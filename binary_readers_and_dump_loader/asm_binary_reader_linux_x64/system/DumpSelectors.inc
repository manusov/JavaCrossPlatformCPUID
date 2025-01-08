;---------- Dump 6 16-bit segment selectors registers. ------------------------;
;                                                                              ;
; INPUT:   Segment selectors registers values for dump.                        ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpSelectors:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers.
; ...
DumpSelectors:         ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11
;---------- Push 6 selectors. -------------------------------------------------;
xor eax,eax
mov ax,gs
push rax   ; PUSH #1.
mov ax,fs
push rax   ; PUSH #2.
mov ax,ss
push rax   ; PUSH #3.
mov ax,es
push rax   ; PUSH #4.
mov ax,ds
push rax   ; PUSH #5.
mov ax,cs
push rax   ; PUSH #6.
;---------- Initializing dump cycle. ------------------------------------------;
cld
mov ebx,6
lea rsi,[NamesSelectors]
;---------- Dump cycle with pop 6 selectors. ----------------------------------;
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
pop rax    ; POP #[6-1]. 
call HexPrint16
mov al,0
stosb
call ConsoleWriteReport
lea rcx,[CrLf]
call ConsoleWriteReport
add rsi,3   ; Select next text string for register name.
dec ebx     ; Cycle counter for 6 segment selectors registers.
jnz .cycle
;---------- Global entry point for return. ------------------------------------;
DumpReturn:
;---------- Restore original color. -------------------------------------------;
call GetColor
xchg ecx,eax
call SetColor
;---------- Insert empty string. ----------------------------------------------;
lea rcx,[CrLf]
call ConsoleWriteReport
;---------- Restore registers and return. -------------------------------------;
pop r11 r10 r9 r8 rbp rdi rsi rdx rcx rbx rax
ret
