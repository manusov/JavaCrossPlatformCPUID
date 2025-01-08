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
push eax ebx ecx edx esi edi ebp
; Push 6 selectors.
xor eax,eax
mov ax,gs
push eax      ; PUSH #1.
mov ax,fs
push eax      ; PUSH #2.
mov ax,ss
push eax      ; PUSH #3.
mov ax,es
push eax      ; PUSH #4.
mov ax,ds
push eax      ; PUSH #5.
mov ax,cs
push eax      ; PUSH #6.
;---------- Initializing dump cycle.  -----------------------------------------;
cld
mov ebx,6
lea esi,[NamesSelectors]
;---------- Dump cycle with pop 6 selectors. ----------------------------------;
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
pop eax             ; POP #[6-1]. 
call HexPrint16
mov al,0
stosb
call ConsoleWriteReport
lea ecx,[CrLf]
call ConsoleWriteReport
add esi,3           ; Select next text string for register name.
dec ebx             ; Cycle counter for 6 segment selectors registers.
jnz .cycle
;---------- Entry point for return. -------------------------------------------;
DumpReturn:
;---------- Restore original color. -------------------------------------------;
call GetColor
xchg ecx,eax
call SetColor
;---------- Insert empty string. ----------------------------------------------;
lea ecx,[CrLf]
call ConsoleWriteReport
;---------- Restore registers and return. -------------------------------------;
pop ebp edi esi edx ecx ebx eax
ret
