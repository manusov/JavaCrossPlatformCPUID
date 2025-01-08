;---------- Dump 8 32-bit general purpose registers. --------------------------;
;                                                                              ;
; INPUT:   GPR registers values for dump.                                      ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpGPR32:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers.
; ...
DumpGPR32:         ; Entry point for dump.
;---------- Save registers for non-volatile and for dump. ---------------------;
push eax ebx ecx edx esi edi ebp
lea eax,[esp + 7*4 + 4]
push eax
;---------- Initializing dump cycle. ------------------------------------------;
cld
mov ebx,8
lea esi,[NamesGPR32]
lea ebp,[esp + 7*4]
;---------- Dump cycle with 8 Read instructions. ------------------------------;
.cycle:
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
mov ecx,esi
call ConsoleWriteReport 
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea edi,[TEMP_BUFFER]
mov ecx,edi
mov al,' '
stosb
mov eax,[ebp]
call HexPrint32
mov al,0
stosb
call ConsoleWriteReport
lea ecx,[CrLf]
call ConsoleWriteReport
sub ebp,4       ; Select next register at stack frame.
add esi,4       ; Select next text string for register name.
dec ebx         ; Cycle counter for 16 general-purpose registers.
jnz .cycle
;---------- Restore original color. -------------------------------------------;
call GetColor
xchg ecx,eax
call SetColor
;---------- Insert empty string. ----------------------------------------------;
lea ecx,[CrLf]
call ConsoleWriteReport
;---------- Restore registers and return. -------------------------------------;
pop eax ebp edi esi edx ecx ebx eax
ret

