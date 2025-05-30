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
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11
;---------- Store 8 registers. ------------------------------------------------;
sub rsp,64
fstp qword [rsp + 8*0]
fstp qword [rsp + 8*1]
fstp qword [rsp + 8*2]
fstp qword [rsp + 8*3]
fstp qword [rsp + 8*4]
fstp qword [rsp + 8*5]
fstp qword [rsp + 8*6]
fstp qword [rsp + 8*7]
;---------- Initializing dump cycle. ------------------------------------------;
cld
mov ebp,8
lea rsi,[NamesFPU]
;---------- Dump cycle with pop 8 registers. ----------------------------------;
.cycle:
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
mov rcx,rsi
call ConsoleWriteReport 
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea rdi,[TEMP_BUFFER]
mov rcx,rdi
mov al,' '
stosb
pop rax        ; POP #[8-1]. 
mov bx,0700h
call DoublePrint
mov al,0
stosb
call ConsoleWriteReport
lea rcx,[CrLf]
call ConsoleWriteReport
add rsi,4      ; Select next text string for register name.
dec ebp        ; Cycle counter for 8 FPU selectors registers.
jnz .cycle
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn
