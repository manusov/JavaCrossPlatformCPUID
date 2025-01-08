;---------- Dump memory region. -----------------------------------------------;
;                                                                              ;
; INPUT:   ESI = Pointer to region for dump.                                   ;
;          EBX = Region length, bytes.                                         ;
;          Memory [ESI] = data for dump.                                       ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpMemory:
; ...
; Place here code under debug, include save-restore registers.
; ...
push ebx esi
lea esi,[TEMP_BUFFER]  ; ESI = Memory block base address, used for debug.
mov ebx,256            ; EBX = Memory block size, used for debug.  
call DumpMemory
pop esi ebx
ret
; ...
DumpMemory:
;---------- Push registers include volatile for API. --------------------------;
push eax ebx ecx edx esi edi ebp
push 0
;---------- Start cycle for dump lines. ---------------------------------------;
.cycleDump:
test dword [esp],0Fh
jnz .skipAddressPrint
mov ecx,DUMP_ADDRESS_COLOR
call SetFgColor
lea edi,[TEMP_BUFFER]
mov eax,[esp]
call HexPrint32
mov ax,'  '
stosw
mov al,0
stosb
lea ecx,[TEMP_BUFFER]
call ConsoleWriteReport
.skipAddressPrint:
mov ecx,DUMP_DATA_COLOR
call SetFgColor
mov ecx,16
mov ebp,ebx
push dword [esp] ecx 
lea edi,[TEMP_BUFFER]
;---------- Start cycle for bytes in the line. --------------------------------;
.cycle16bytes:
dec ebp
js .lineStop
mov eax,dword [esp+4]
mov al,[esi+eax]
call HexPrint8
mov al,' '
stosb
jmp .lineDone
.lineStop:
mov ax,'  '
stosw
stosb
.lineDone:
inc dword [esp+4]
loop .cycle16bytes
;---------- End cycle for bytes in the line. ----------------------------------;
mov al,' '
stosb
pop ecx eax
cmp ecx,ebx
jbe .lengthLimited
mov ecx,ebx
.lengthLimited:
;---------- Start cycle for ASCII chars in the line (right block). ------------;
.cycleAscii:
mov eax,dword [esp]
mov al,[esi+eax]
cmp al,' '
jb .belowSpace
cmp al,'z'
jbe .charLimited
.belowSpace:
mov al,'.'
.charLimited:
stosb
inc dword [esp]
loop .cycleAscii
;---------- End cycle for ASCII chars in the line (right block). --------------;
mov al,0
stosb
lea ecx,[TEMP_BUFFER]
call ConsoleWriteReport
lea ecx,[CrLf]
call ConsoleWriteReport
sub ebx,16
ja .cycleDump 
;---------- End cycle for dump lines. -----------------------------------------;
;--- Go to restore original color, insert empty string, restore registers. ----;
pop eax
jmp DumpReturn

