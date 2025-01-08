;---------- Dump 16 SSE registers as hex. -------------------------------------;
;                                                                              ;
; INPUT:   SSE registers values for dump.                                      ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpSSE:   ; Entry point for code under debug.
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
mov eax,06000000h   ; EDX.26=SSE2, EDX.25=SSE.
and edx,eax
cmp edx,eax
jne .featureAbsent
; ...
; Place here code under debug.
; ...
call DumpSSE
pop rbx
ret
.featureAbsent:
lea rcx,[MsgAbsentSSE2]
call HelperError
pop rbx
ret

DumpSSE:         ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11
;---------- Store 16 registers. -----------------------------------------------;
sub rsp,256
movups [rsp + 16*00],xmm0
movups [rsp + 16*01],xmm1
movups [rsp + 16*02],xmm2
movups [rsp + 16*03],xmm3
movups [rsp + 16*04],xmm4
movups [rsp + 16*05],xmm5
movups [rsp + 16*06],xmm6
movups [rsp + 16*07],xmm7
movups [rsp + 16*08],xmm8
movups [rsp + 16*09],xmm9
movups [rsp + 16*10],xmm10
movups [rsp + 16*11],xmm11
movups [rsp + 16*12],xmm12
movups [rsp + 16*13],xmm13
movups [rsp + 16*14],xmm14
movups [rsp + 16*15],xmm15
;---------- Initializing dump cycle. ------------------------------------------;
cld
xor ebp,ebp
;---------- Dump cycle with pop 16 registers. ---------------------------------;
.cycleVectors:
;---------- Register name. ----------------------------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea rsi,[NameSSE]
lea rdi,[TEMP_BUFFER]
call StringWrite
mov eax,ebp
mov bl,0
call DecimalPrint32  ; This number at register name, XMM0-XMM15.
cmp ebp,9
ja .formatText
mov al,' '
stosb
.formatText:
mov al,0
stosb
lea rcx,[TEMP_BUFFER]
call ConsoleWriteReport
;---------- Register value. ---------------------------------------------------;
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea rdi,[TEMP_BUFFER]
mov rcx,rdi
mov al,' '
stosb
;---------- XMM[i] data frame start. ------------------------------------------; 
mov rax,[rsp+00]
call HexPrint64  ; first 64-bit scalar as hex.
mov al,' '
stosb
mov rax,[rsp+08] 
call HexPrint64  ; second 64-bit scalar as hex.
add rsp,16
;---------- XMM[i] data frame start. ------------------------------------------;
mov al,0
stosb
call ConsoleWriteReport
;---------- Cycle. ------------------------------------------------------------;
lea rcx,[CrLf]
call ConsoleWriteReport
inc ebp
cmp ebp,16
jnz .cycleVectors    ; Cycle counter for 16 SSE registers.
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn
