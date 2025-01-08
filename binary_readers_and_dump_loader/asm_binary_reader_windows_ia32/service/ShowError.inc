;---------- Show details about detected error and wait key press. -------------;
;                                                                              ;
; INPUT:  ECX = Pointer to error description first string, 0 means skip.       ;
;         EDX = Pointer to error description second string, 0 means skip.      ;
;         EAX = Windows style error code for decoding by WinAPI and            ;
;               show string "<Error name> (code)", 0 means skip.               ;
;         EDI = Pointer to work ( transit ) buffer for prepare text data       ;
;               no limits provided, caller must associate buffer size and      ;
;               text output size, typically additional space available.        ;
;                                                                              ;
; OUTPUT: None.                                                                ;
;         Use memory at [input EDI].                                           ;
;                                                                              ;         
;------------------------------------------------------------------------------;
ShowError:
cld
push ebx esi edi ebp eax edx ecx
mov ebp,esp 
;---------- Set color and write "Error: " message part. -----------------------;
mov ecx,FOREGROUND_RED + FOREGROUND_INTENSITY
call SetFgColor               ; Color for "Error: " message part.
lea ecx,[MsgError]
call ConsoleWriteReport
;---------- Set color and conditionally write first string.
mov ecx, FOREGROUND_RED + FOREGROUND_GREEN + FOREGROUND_BLUE + FOREGROUND_INTENSITY
call SetFgColor
mov ecx,[ebp + 00]           ; ECX = Input ECX = string 1.
jecxz @f                     ; Go skip if string pointer = 0.
call ConsoleWriteReport      ; First string about error.
@@:
;---------- Conditionally write second string with alignment. -----------------;
; For "Error: " part.
mov ebx,[ebp + 04]           ; EBX = Input EDX = string 2.
test ebx,ebx
jz @f                        ; Go skip if string pointer = 0.
lea ecx,[CrLf]
call ConsoleWriteReport      ; Next string.
lea ecx,[MsgErrorTab]
call ConsoleWriteReport      ; Tabulation for alignment for "Error: " part.
mov ecx,ebx
call ConsoleWriteReport      ; Second string about error.
@@:
;--- Conditionally write third string with alignment for "Error: " part. ------;
mov ebx,[ebp + 08]           ; EBX = Input EAX = WinAPI error code.
test ebx,ebx
jz @f                        ; Go skip if error code = 0.
lea ecx,[CrLf]
call ConsoleWriteReport      ; Next string.
lea ecx,[MsgErrorTab]
call ConsoleWriteReport      ; Tabulation for alignment for "Error: " part. 
lea esi,[MsgErrorOS]         ; ESI = Pointer to string, EDI = To buffer.
call StringWrite             ; Write "OS error" to buffer.
xchg eax,ebx                 ; EAX = WinAPI error code.
mov bl,0                     ; BL  = Numeric template control.
call DecimalPrint32          ; Write error code decimal number to buffer.
mov ax,' ='
stosw
stosb
mov eax,[ebp + 08]           ; EBX = Input EAX = WinAPI error code.
call DecodeError             ; Write OS error description string to buffer.
mov al,0
stosb
mov ecx,[ebp + 16]           ; ECX = Input EDI = buffer pointer.
call ConsoleWriteReport      ; Write from buffer to console. 
@@:
;---------- Restore console color, skip string and write done message. --------;
; "Press any key..."
call GetColor
xchg ecx,eax
call SetColor                ; Restore original color.
lea ecx,[CrLf2]
call ConsoleWriteReport
lea ecx,[DoneMsgNoWait]      ; ECX = Pointer to message 1.
cmp [Option_Waitkey],0
je  @f
lea ecx,[DoneMsgWait]        ; ECX = Pointer to message 2.
@@:
call ConsoleWriteReport
;---------- Wait key press, after key pressed skip string. --------------------;
lea ecx,[TEMP_BUFFER]      ; ECX = Pointer to buffer for char.
call ConsoleReadAnyEvent   ; Console input.
;---------- Next line at console. ---------------------------------------------;
lea ecx,[CrLf2]
call ConsoleWriteReport
pop ebp ebp ebp ebp edi esi ebx 
ret

;---------- Write error string to console and wait any key. -------------------;
; Variant for ONE string parameter.                                            ;
;                                                                              ;
; INPUT:   ECX = Pointer to error name string. ASCII, 0-terminated.            ; 
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
HelperError:
push edi
xor edx,edx
xor eax,eax
lea edi,[TEMP_BUFFER]
call ShowError
pop edi
ret
