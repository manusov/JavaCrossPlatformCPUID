;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; DLL library for Windows x64.                                                 ; 
; Handler for java request ID = REQUEST_MEASURE_TSC_FREQUENCY.                 ;
;                                                                              ;
; Measure TSC (Time Stamp Counter) clock frequency and returns as Hz.          ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see WIN64JNI.asm), then works this    ;
; DLL library JNI gate and calls this subroutine, selected by request ID.      ; 
; Non-volatile registers for this subroutine: RBX, RBP, RSI, RDI, R12, R13.    ;
;                                                                              ;
; INPUT:    RSI = Pointer to IPB (Input Parameters Block).                     ;
;           RDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [RSI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [RSI + 04] = Reserved for request options.                   ;
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result.                          ;
;           DWORD [RDI + 00] = Number of clocks values returned.               ;
;                              must be maximum 1 for this function.            ;                              
;           DWORD [RDI + 04] = Status, 0 if OK (output valid) or error code.   ;
;           QWORD [RDI + 08] = TSC frequency value, Hz (periods per second).   ;
;                                                                              ;
;------------------------------------------------------------------------------;

MeasureTscFrequency:
push rbx
xor eax,eax
mov dword [rdi + 00],eax   ; Return size = 0, no entries returned yet.
mov dword [rdi + 04],1     ; Status = 1 means CPUID not supported or locked.
mov qword [rdi + 08],rax
call CheckCpuid
jc .exit          ; Error if CPUID not supported or locked by VMM.
cmp eax,1
jb .exit          ; Error if CPUID function 1 not supported. 
mov eax,1
cpuid
test dl,10h
jz .exit          ; Error if TSC not supported.
;---------- This executed if operation supported. -----------------------------;
mov dword [rdi + 04],6     ; Status = 6 means clock measurement failed.
call MeasureCpuClk
jc .exit
mov dword [rdi + 00],1     ; Return size = 1 frequency value returned.
mov dword [rdi + 04],0     ; Status = 0 means operation OK.
mov qword [rdi + 08],rax
.exit:
pop rbx
ret

;---------- This entry point used after verification, returns RAX = Hz. -------;
;                                                             CF = error flag.
; TODO. Don't save volatile registers, optimize for use LEAVE instruction.
MeasureCpuClk:
cld
push rbx rcx rdx rsi rbp r8 r9 r10 r11
xor ecx,ecx
push rcx rcx               ; 16 bytes for 2 Qword stack variables.
mov rbp,rsp                ; RBP used for restore RSP and addressing variables.
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32                 ; Make parameters shadow.
;---------- This branch use Performance Counter, high precision. --------------;
;---------- Detect performance counter status and frequency. ------------------;
lea rcx,[rbp + 08]         ; RCX = Parm#1 = pointer to output 64-bit variable.
call [QueryPerformanceFrequency]  ; Qword [rbp + 08] = performance frequency.
test rax,rax
jz .tryFileTime            ; Go File Time branch if status = FALSE.
;---------- Get current ticks counter value. ----------------------------------;
mov rcx,rbp                ; RCX = Parm#1 = pointer to output 64-bit variable.
call [QueryPerformanceCounter]  ; Qword [rbp + 00] = performance counter now.
test rax,rax
jz .tryFileTime            ; Go File Time branch if status = FALSE.
mov rsi,[rbp]              ; RSI = Performance counter now.
;---------- Wait next tick for synchronization. -------------------------------;
@@:
mov rcx,rbp                ; RCX = Parm#1 = pointer to output 64-bit variable.
call [QueryPerformanceCounter]
test rax,rax
jz .tryFileTime            ; Go File Time branch if status = FALSE.
cmp rsi,[rbp]
je @b                      ; Go wait cycle if counter value = previous.
;---------- Start measurement -------------------------------------------------;
mov rsi,[rbp + 00]         ; RSI = Current value of counter.
add rsi,[rbp + 08]         ; RSI = Current + Ticks per second = target value.
rdtsc                      ; Get start TSC.
shl rdx,32
lea rbx,[rax + rdx]        ; RBX = 64-bit TSC at operation start.
@@:
mov rcx,rbp                ; RCX = Parm#1 = pointer to output 64-bit variable.
call [QueryPerformanceCounter]
test rax,rax
jz .tryFileTime            ; Go File Time branch if status = FALSE.
cmp rsi,[rbp]
jae @b                     ; Go wait cycle if target value >= current value.
rdtsc                      ; Get end TSC for calculate delta-TSC.
shl rdx,32
or rax,rdx            ; RAX = 64-bit TSC at operation end.
sub rax,rbx           ; RAX = Delta TSC, also set CF flag for error status.
jbe .tryFileTime      ; Go File Time branch if delta TSC <= 0.
.exit:                ; Here CF = 0 (NC) if no errors.
mov rsp,rbp           ; Flags must be not changed, because here CF = status.
pop rcx rcx           ; Remove 2 qwords from stack frame. 
pop r11 r10 r9 r8 rbp rsi rdx rcx rbx
ret
;---------- This branch use File Time if Performance Counter failed. ----------;
.tryFileTime:
;---------- Start measure frequency. ------------------------------------------;
mov rcx,rbp
call [GetSystemTimeAsFileTime]    ; Get current count.
mov rsi,[rbp]
@@:
mov rcx,rbp
call [GetSystemTimeAsFileTime]    ; Get next count for wait 100 ns.
cmp rsi,[rbp]
je @b
mov rsi,[rbp]
add rsi,10000000                  ; 10^7 * 100ns = 1 second.
rdtsc
shl rdx,32
lea rbx,[rax + rdx]               ; RBX = 64-bit TSC at operation start.
@@:
mov rcx,rbp
call [GetSystemTimeAsFileTime]    ; Get count for wait 1 second.
cmp rsi,[rbp]
ja @b
rdtsc
shl rdx,32
or rax,rdx                        ; RAX = 64-bit TSC at operation end.
sub rax,rbx                       ; RAX = Delta TSC.
ja .exit
stc
jmp .exit
