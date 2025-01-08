;---------- Dump 8 x87 FPU registers. -----------------------------------------;
;                                                                              ;
; INPUT:   FPU registers values for dump.                                      ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpFPU:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers.
; ...
DumpFPU:         ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push eax ebx ecx edx esi edi ebp
;---------- Store 8 registers. ------------------------------------------------;
sub esp,64
fstp qword [esp + 8*0]
fstp qword [esp + 8*1]
fstp qword [esp + 8*2]
fstp qword [esp + 8*3]
fstp qword [esp + 8*4]
fstp qword [esp + 8*5]
fstp qword [esp + 8*6]
fstp qword [esp + 8*7]
;---------- Initializing dump cycle. ------------------------------------------;
cld
mov ebp,8
lea esi,[NamesFPU]
;---------- Dump cycle with pop 8 registers. ----------------------------------;
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
pop eax edx     ; POP #[8-1]. 
mov bx,0700h
call DoublePrint
mov al,0
stosb
call ConsoleWriteReport
lea ecx,[CrLf]
call ConsoleWriteReport
add esi,4       ; Select next text string for register name.
dec ebp         ; Cycle counter for 8 FPU selectors registers.
jnz .cycle
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn

