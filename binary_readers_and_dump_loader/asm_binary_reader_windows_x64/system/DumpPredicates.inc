;---------- Dump 8 predicate registers ( AVX512 K0-K7 ). ----------------------;
; Select variant for minimum AVX512F functionality, 16-bit predicates, or      ;
;        variant for AVX512F + AVX512BW functionality, 64-bit predicates.      ;
;                                                                              ;
; INPUT:   K0-K7 predicate registers values for dump.                          ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpPredicates:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push rbx
xor eax,eax
cpuid
cmp eax,7
jb .cpuFeatureAbsent
mov eax,1
cpuid
mov eax,18000000h   ; ECX.28 = AVX, EDX.27 = OSXSV.
and ecx,eax
cmp ecx,eax
jne .cpuFeatureAbsent
mov eax,7
xor ecx,ecx
cpuid
bt ebx,16           ; EBX.16 = AVX512F.
jnc .cpuFeatureAbsent
xor ecx,ecx
xgetbv
and al,11100110b    ; XCR0.7=ZMM[16-31], XCR0.6=ZMM[0-15], XCR0.5=K[0-7].
cmp al,11100110b    ; XCR0.2=YMM(AVX), XCR0.1=XMM(SSE).
jne .osFeatureAbsent 
; ...
; Place here code under debug.
; ...
mov eax,7
xor ecx,ecx
cpuid
bt ebx,30           ; EBX.30 = AVX512BW.
jnc .k64absent 
call DumpPredicates64
pop rbx
ret
.k64absent:
call DumpPredicates16
pop rbx
ret
.cpuFeatureAbsent:
lea rcx,[MsgAbsentAVX512]
call HelperError
pop rbx
ret
.osFeatureAbsent:
lea rcx,[MsgAbsentAVX512os]
call HelperError
pop rbx
ret

;---------- Dump 8 predicate registers ( AVX512 K0-K7 ). ----------------------;
; Variant for minimum AVX512F functionality, 16-bit predicates.                ;
;                                                                              ;
; INPUT:   K0-K7 predicate registers values for dump.                          ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
DumpPredicates16:       ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11
;---------- Store 8 registers. ------------------------------------------------;
sub rsp,64
kmovw [rsp + 8*0],k0
kmovw [rsp + 8*1],k1
kmovw [rsp + 8*2],k2
kmovw [rsp + 8*3],k3
kmovw [rsp + 8*4],k4
kmovw [rsp + 8*5],k5
kmovw [rsp + 8*6],k6
kmovw [rsp + 8*7],k7
;---------- Initializing dump cycle. ------------------------------------------;
cld
mov ebp,8
lea rsi,[NamesK]
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
pop rax          ; POP #[8-1]. 
call HexPrint16
mov al,0
stosb
call ConsoleWriteReport
lea rcx,[CrLf]
call ConsoleWriteReport
add rsi,3        ; Select next text string for register name.
dec ebp          ; Cycle counter for 8 MMX registers.
jnz .cycle
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn

;---------- Dump 8 predicate registers ( AVX512 K0-K7 ). ----------------------;
; Variant for AVX512F + AVX512BW functionality, 64-bit predicates.             ;
;                                                                              ;
; INPUT:   K0-K7 predicate registers values for dump.                          ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
DumpPredicates64:
;---------- Push registers include volatile for API. --------------------------;
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11
;---------- Store 8 registers. ------------------------------------------------;
sub rsp,64
kmovq [rsp + 8*0],k0
kmovq [rsp + 8*1],k1
kmovq [rsp + 8*2],k2
kmovq [rsp + 8*3],k3
kmovq [rsp + 8*4],k4
kmovq [rsp + 8*5],k5
kmovq [rsp + 8*6],k6
kmovq [rsp + 8*7],k7
;---------- Initializing dump cycle. ------------------------------------------;
cld
mov ebp,8
lea rsi,[NamesK]
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
pop rax      ; POP #[8-1]. 
call HexPrint64
mov al,0
stosb
call ConsoleWriteReport
lea rcx,[CrLf]
call ConsoleWriteReport
add rsi,3    ; Select next text string for register name.
dec ebp      ; Cycle counter for 8 MMX registers.
jnz .cycle
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn

