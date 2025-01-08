;---------- Dump memory region. -----------------------------------------------;
;                                                                              ;
; INPUT:   RSI = Pointer to region for dump.                                   ;
;          EBX = Region length, bytes.                                         ;
;          Memory [RSI] = data for dump.                                       ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpMemory:
; ...
; Place here code under debug, include save-restore registers.
; ...
push rbx rsi
lea rsi,[TEMP_BUFFER]  ; RSI = Memory block base address, used for debug.
mov ebx,256            ; EBX = Memory block size, used for debug.  
call DumpMemory
pop rsi rbx
ret
; ...
DumpMemory:
;---------- Push registers include volatile for API. --------------------------;
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11 r15
xor r15,r15
;---------- Start cycle for dump lines. ---------------------------------------;
.cycleDump:
test r15,0Fh
jnz .skipAddressPrint
mov ecx,DUMP_ADDRESS_COLOR
call SetFgColor
lea rdi,[TEMP_BUFFER]
mov eax,r15d
call HexPrint32
mov ax,'  '
stosw
mov al,0
stosb
lea rcx,[TEMP_BUFFER]
call ConsoleWriteReport
.skipAddressPrint:
mov ecx,DUMP_DATA_COLOR
call SetFgColor
mov ecx,16
mov ebp,ebx
push rcx r15
lea rdi,[TEMP_BUFFER]
;---------- Start cycle for bytes in the line. --------------------------------;
.cycle16bytes:
dec ebp
js .lineStop
mov al,[rsi+r15]
call HexPrint8
mov al,' '
stosb
jmp .lineDone
.lineStop:
mov ax,'  '
stosw
stosb
.lineDone:
inc r15
loop .cycle16bytes
;---------- End cycle for bytes in the line. ----------------------------------;
mov al,' '
stosb
pop r15 rcx
cmp ecx,ebx
jbe .lengthLimited
mov ecx,ebx
.lengthLimited:
;---------- Start cycle for ASCII chars in the line (right block). ------------;
.cycleAscii:
mov al,[rsi + r15]
cmp al,' '
jb .belowSpace
cmp al,'z'
jbe .charLimited
.belowSpace:
mov al,'.'
.charLimited:
stosb
inc r15
loop .cycleAscii
;---------- End cycle for ASCII chars in the line (right block). --------------;
mov al,0
stosb
lea rcx,[TEMP_BUFFER]
call ConsoleWriteReport
lea rcx,[CrLf]
call ConsoleWriteReport
sub ebx,16
ja .cycleDump 
;---------- End cycle for dump lines. -----------------------------------------;
;--- Go to restore original color, insert empty string, restore registers. ----;
pop r15
jmp DumpReturn

