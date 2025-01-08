;---------- Dump 8 MMX registers. ---------------------------------------------;
;                                                                              ;
; INPUT:   MMX registers values for dump.                                      ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpMMX:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push rbx
xor eax,eax
cpuid
cmp eax,1
jb .featureAbsent
mov eax,1
cpuid
bt edx,23        ; EDX.23=MMX.
jnc .featureAbsent
; ...
; Place here code under debug.
; ...
call DumpMMX
pop rbx
ret
.featureAbsent:
lea rcx,[MsgAbsentMMX]
call HelperError
pop rbx
ret

DumpMMX:         ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11
;---------- Store 8 registers. ------------------------------------------------;
sub rsp,64
movq [rsp + 8*0],mm0
movq [rsp + 8*1],mm1
movq [rsp + 8*2],mm2
movq [rsp + 8*3],mm3
movq [rsp + 8*4],mm4
movq [rsp + 8*5],mm5
movq [rsp + 8*6],mm6
movq [rsp + 8*7],mm7
;---------- Initializing dump cycle. ------------------------------------------;
cld
mov ebp,8
lea rsi,[NamesMMX]
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
pop rax         ; POP #[8-1]. 
call HexPrint64
mov al,0
stosb
call ConsoleWriteReport
lea rcx,[CrLf]
call ConsoleWriteReport
add rsi,4       ; Select next text string for register name.
dec ebp         ; Cycle counter for 8 MMX registers.
jnz .cycle
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn
