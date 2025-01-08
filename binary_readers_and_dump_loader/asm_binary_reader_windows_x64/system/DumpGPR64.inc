;---------- Dump 16 64-bit general purpose registers. -------------------------;
;                                                                              ;
; INPUT:   GPR registers values for dump.                                      ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpGPR64:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers.
; ...
DumpGPR64:         ; Entry point for dump.
;---------- Save registers for non-volatile and for dump. ---------------------;
push rax rbx rcx rdx rsi rdi rbp
lea rax,[rsp + 7*8 + 8]
push rax
push r8 r9 r10 r11 r12 r13 r14 r15
;---------- Initializing dump cycle. ------------------------------------------;
cld
mov ebx,16
lea rsi,[NamesGPR64]
lea rbp,[rsp + 15*8 ]
;---------- Dump cycle with 16 Read instructions. -----------------------------;
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
mov rax,[rbp]
call HexPrint64
mov al,0
stosb
call ConsoleWriteReport
lea rcx,[CrLf]
call ConsoleWriteReport
sub rbp,8    ; Select next register at stack frame.
add rsi,4    ; Select next text string for register name.
dec ebx      ; Cycle counter for 16 general-purpose registers.
jnz .cycle
;---------- Restore original color. -------------------------------------------;
call GetColor
xchg ecx,eax
call SetColor
;---------- Insert empty string. ----------------------------------------------;
lea rcx,[CrLf]
call ConsoleWriteReport
;---------- Restore registers and return. -------------------------------------;
pop r15 r14 r13 r12 r11 r10 r9 r8
pop rax rbp rdi rsi rdx rcx rbx rax
ret
