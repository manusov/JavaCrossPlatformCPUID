;---------- Show scenario options settings. -----------------------------------;
;                                                                              ;
; INPUT:  RCX = Pointer to options descriptors list.                           ;
;         RDX = Pointer to work buffer for prepare text data                   ;
;               no limits provided, caller must associate buffer size and      ;
;               text output size, typically additional space available.        ;
;                                                                              ;
; OUTPUT: None.                                                                ;
;         Use memory at [input RDX].                                           ;
;------------------------------------------------------------------------------;
ShowScenario:
;--- Stack alignment not used because no system calls in this subroutine. -----;
cld
push rbx rsi rdi rbp 
;---------- Initializing cycle for show options. ------------------------------; 
mov rbx,rcx        ; RBX = Pointer to options descriptors list. 
mov rdi,rdx        ; RDI = Pointer to work buffer for prepare text data. 
push rdi
;---------- Start cycle for show options, build text block in the buffer. -----;
.OpInterpreting:
mov al,[rbx + X0]  ; AL = Option type from descriptor.
cmp al,XEND               
je .OpDone         ; Go exit cycle if terminator detected.
cmp al,XLAST
ja .OpDone         ; Go exit cycle if unknown option code.
;---------- Write option name. ------------------------------------------------;
push rax
mov rdx,rdi
mov ecx,OPTION_NAME_FORMAT
mov al,' '
rep stosb
xchg rdi,rdx
mov esi,[rbx + X1]
add rsi,OpDesc
call StringWrite   ; Write option name, left part of string.
mov rdi,rdx
mov ax,'= '
stosw                 ; Write "= " between left and right parts of string. 
pop rax               ; Restore option type, AL = Type.
mov esi,[rbx + X2]    ; RSI = Pointer to option value, size is option-specific.
add rsi,OpDesc
;---------- Detect option type = AL. ------------------------------------------;
cmp al,XKEY
je .OpKeys
cmp al,XDEC
je .OpDecimal32
cmp al,XHEX
je .OpHex64
cmp al,XSIZE
je .OpSize64
;---------- Option handler = string. ------------------------------------------;
.OpString:
mov rsi,[rsi]         ; RSI = Pointer to raw string.
call StringWrite      ; Write option value after " = ", raw string.
.OpInterpretingP25:
add rbx,XSMALL        ; RBX = Pointer, go to next option descriptor.
mov ax,0A0Dh
stosw                 ; Make next string, write CR, LF. 
jmp .OpInterpreting
;---------- Option handler = keys. --------------------------------------------;
.OpKeys:
mov al,[rsi]          ; AL = Index for sequence of 0-terminated strings.
mov ah,0FFh
mov esi,[rbx + X4]
add rsi,OpDesc
call StringWriteSelected   ; Write option value after " = ", selected keyword.
add rbx,XBIG               ; RBX = Pointer, go to next option descriptor.
mov ax,0A0Dh
stosw                      ; Make next string, write CR, LF. 
jmp .OpInterpreting
;---------- Option handler = decimal 32. --------------------------------------;
.OpDecimal32:
mov eax,[rsi]         ; EAX = Value for visual as 32-bit decimal number.
push rbx
mov bl,0              ; BL = Template for print.
call DecimalPrint32   ; Write option value after " = ", decimal number.
pop rbx
jmp .OpInterpretingP25
;---------- Option handler = hex 64. ------------------------------------------;
.OpHex64:
mov rax,[rsi]         ; RAX = Value for visual as 64-bit hex number.
call HexPrint64       ; Write option value after " = ", hex number.
mov al,'h'
stosb
jmp .OpInterpretingP25
;---------- Option handler = size 64. -----------------------------------------;
.OpSize64:
mov rax,[rsi]         ; RAX = Value for visual as 64-bit size, can use K/M/G.
push rbx
mov bl,0FFh
call SizePrint64      ; Write option value after " = ", size.
pop rbx
jmp .OpInterpretingP25
;---------- Termination. ------------------------------------------------------;
.OpDone:
mov ax,0A0Dh
stosw                 ; Make next string, write CR, LF. 
mov al,0
stosb                 ; Terminate all sequence of strings, write 0 byte.
pop rcx
;---------- Read data from prepared buffer and display to console. ------------; 
; Optionally save to report file.
call ConsoleWriteReport
;---------- Exit. -------------------------------------------------------------;
pop rbp rdi rsi rbx 
ret
