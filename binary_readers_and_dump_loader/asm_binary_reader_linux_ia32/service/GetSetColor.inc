;---------- Get console color, saved at start. --------------------------------;
;                                                                              ;
; INPUT:  None.                                                                ;
;                                                                              ;
; OUTPUT: EAX = Color code.                                                    ;
;------------------------------------------------------------------------------;
GetColor:
mov eax,[OriginalColor]
ret

;---------- Set console color. ------------------------------------------------;
;                                                                              ;
; INPUT:   ECX = New color code.                                               ;
;          Use global variables [StdOut], [CurrentColor], [SequenceBuffer].    ;
;                                                                              ;
; OUTPUT:  EAX = OS Status.                                                    ;
;------------------------------------------------------------------------------;
SetColor:
xor eax,eax          ; Bit EAX.31 = Internal flag, 1 = skip background.
SetColorEntry:
push ebx esi edi ebp
cld
lea esi,[SequenceBuffer]   ; ESI = Pointer to buffer for API function.
mov edi,esi                ; EDI = Pointer to buffer for build sequence.
mov edx,2                  ; EDX = String size for API function.
mov ax,'[' SHL 8 + 1Bh     ; Build 2 chars = "\x1B[".
stosw
;---------- Check for reset defaults. -----------------------------------------;
test ecx,ecx               ; ECX = Color value from caller.
js .Default
;---------- Foreground color. -------------------------------------------------;
add edx,2
mov ah,cl
and ax,0700h
or ax,3033h               ; Build 2 chars = "3x", x = foreground color.
stosw
;---------- Background color. -------------------------------------------------;
test eax,eax
js .SkipBackground
add edx,3
mov al,';'
stosb
mov ah,ch
and ax,0700h
or ax,3034h               ; Build 2 chars = "4x", x = foreground color.
stosw
.SkipBackground:
;---------- Foreground intensity. ---------------------------------------------;
test ecx,FOREGROUND_INTENSITY
jz .Terminate
add edx,2
mov ax,';1'
stosw
jmp .Terminate
;---------- Reset to default colors. ------------------------------------------;
.Default:
inc edx
mov al,'0'
stosb
;---------- Terminate escape sequence. ----------------------------------------;
.Terminate:
inc edx
mov al,'m'
stosb
;---------- Write string to console, conditional, if handle not zero. ---------;
.Write:
mov ebx,[StdOut]           ; Parm#1 = EBX = Device = STDOUT.
test edi,edi
jz .Done                   ; Go skip console write if handle = 0.
mov ecx,esi                ; ECX = Pointer to string.
mov eax,SYS_WRITE          ; EAX = Linux API function (syscall number).
int 80h                    ; EDX = Length.
.Done:
pop ebp edi esi ebx
ret

;--- Set console foreground color, background color as saved at start. --------;
;                                                                              ;
; INPUT:   ECX = New foreground color code.                                    ;
;          Use global variables [StdOut], [CurrentColor], [SequenceBuffer].    ;
;                                                                              ;
; OUTPUT:  EAX = OS Status.                                                    ;
;------------------------------------------------------------------------------;
SetFgColor:
mov eax,-1          ; Bit EAX.31 = Internal flag, 1 = skip background.
jmp SetColorEntry

