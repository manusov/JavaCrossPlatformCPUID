;---------- Show scenario options settings. -----------------------------------;
;                                                                              ;
; INPUT:  ECX = Pointer to options descriptors list.                           ;
;         EDX = Pointer to work buffer for prepare text data,                  ;
;               no limits provided, caller must associate buffer size and      ;
;               text output size, typically additional space available.        ;
;                                                                              ;
; OUTPUT: None.                                                                ;
;         Use memory at [input EDX].                                           ;
;                                                                              ;         
;------------------------------------------------------------------------------;
ShowScenario:
cld
push ebx esi edi ebp 
;---------- Initializing cycle for show options. ------------------------------; 
mov ebx,ecx          ; EBX = Pointer to options descriptors list. 
mov edi,edx          ; EDI = Pointer to work buffer for prepare text data. 
push edi
;---------- Start cycle for show options, build text block in the buffer. -----;
.opInterpreting:
mov al,[ebx + X0]    ; AL = Option type from descriptor.
cmp al,XEND               
je .opDone           ; Go exit cycle if terminator detected.
cmp al,XLAST
ja .opDone           ; Go exit cycle if unknown option code.
;---------- Write option name. ------------------------------------------------;
push eax
mov edx,edi
mov ecx,OPTION_NAME_FORMAT
mov al,' '
rep stosb
xchg edi,edx
mov esi,[ebx + X1]
call StringWrite     ; Write option name, left part of string.
mov edi,edx
mov ax,'= '
stosw                ; Write "= " between left and right parts of string. 
pop eax              ; Restore option type, AL = Type.
mov esi,[ebx + X2]   ; RSI = Pointer to option value, size is option-specific.
;---------- Detect option type = AL. ------------------------------------------;
cmp al,XKEY
je .opKeys
cmp al,XDEC
je .opDecimal32
cmp al,XHEX
je .opHex64
cmp al,XSIZE
je .opSize64
;---------- Option handler = string. ------------------------------------------;
.opString:
mov esi,[esi]           ; ESI = Pointer to raw string.
call StringWrite        ; Write option value after " = ", raw string.
.opInterpretingP25:
add ebx,XSMALL          ; RBX = Pointer, go to next option descriptor.
mov ax,0A0Dh
stosw                   ; Make next string, write CR, LF. 
jmp .opInterpreting
;---------- Option handler = keys. --------------------------------------------;
.opKeys:
mov al,[esi]            ; AL = Index for sequence of 0-terminated strings.
mov ah,0FFh
mov esi,[ebx + X4]
call StringWriteSelected  ; Write option value after " = ", selected keyword.
add ebx,XBIG              ; RBX = Pointer, go to next option descriptor.
mov ax,0A0Dh
stosw                     ; Make next string, write CR, LF. 
jmp .opInterpreting
;---------- Option handler = decimal 32. --------------------------------------;
.opDecimal32:
mov eax,[esi]             ; EAX = Value for visual as 32-bit decimal number.
push ebx
mov bl,0                  ; BL = Template for print.
call DecimalPrint32       ; Write option value after " = ", decimal number.
pop ebx
jmp .opInterpretingP25
;---------- Option handler = hex 64. ------------------------------------------;
.opHex64:
mov eax,[esi + 0]  ; EAX = Value for visual as 64-bit hex number.
mov edx,[esi + 4]
call HexPrint64    ; Write option value after " = ", hex number.
mov al,'h'
stosb
jmp .opInterpretingP25
;---------- Option handler = size 64. -----------------------------------------;
.opSize64:
mov eax,[esi + 0]  ; EDX:EAX = Value for visual as 64-bit size, can use K/M/G.
mov edx,[esi + 4]
push ebx
mov bl,0FFh
call SizePrint64   ; Write option value after " = ", size.
pop ebx
jmp .opInterpretingP25
;---------- Termination. ------------------------------------------------------;
.opDone:
mov ax,0A0Dh
stosw              ; Make next string, write CR, LF. 
mov al,0
stosb              ; Terminate all sequence of strings, write 0 byte.
pop ecx
;---------- Read data from prepared buffer and display to console. ------------; 
; Optionally save to report file.
call ConsoleWriteReport
pop ebp edi esi ebx 
ret
