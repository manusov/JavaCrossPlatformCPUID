;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; *.SO library for Linux x64.                                                  ; 
; Handler for java request ID = REQUEST_MEASURE_TSC_FREQUENCY.                 ;
;                                                                              ;
; Measure TSC (Time Stamp Counter) clock frequency and returns as Hz.          ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see LINUX64JNI.asm), then works this  ;
; *.SO library JNI gate and calls this subroutine, selected by request ID.     ; 
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

;------------------------------------------------------------------------;
; Measure CPU TSC (Time Stamp Counter) clock frequency, store results    ;
; F=Frequency=[Hz].                                                      ;
;                                                                        ;
; INPUT:   None                                                          ;
;                                                                        ;
; OUTPUT:  CF flag = Status: 0(NC)=Measured OK, 1(C)=Measurement error	 ;
;          Output RAX valid only if CF=0(NC)                             ;
;          RAX = TSC Frequency, Hz, F = Delta TSC per 1 second           ;
;------------------------------------------------------------------------;
MeasureCpuClk:
push rbp
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h   ; Stack alignment by calling convention.
push rsi rdi                 ; Number of pushes here must be even, for stack alignment.
;---------- Prepare parameters, early to minimize dTSC. ---
lea rdi,[TimespecWait]  ; RDI = Pointer to loaded wait time: DQ sec, ns.
lea rsi,[rdi+16]        ; RSI = Pointer to stored remain time: DQ sec, ns.
;---------- Get TSC value before 1 second pause. ---
rdtsc                   ; EDX:EAX = TSC, EDX = High , EAX = Low.
push rax rdx
;---------- Wait 1 second. ----------------------------------------------------;
mov eax,SYS_NANOSLEEP   ; EAX = Linux API function (syscall number).
push rsi
syscall
pop rsi
xchg r8,rax
;---------- Get TSC value after 1 second pause. -------------------------------;
rdtsc                 ; EDX:EAX = TSC, EDX = High , EAX = Low , BEFORE 1 second pause.
pop rcx rdi           ; ECX:EDI = TSC, ECX = High , EBX = Low , AFTER 1 second pause.
;---------- Check results. ----------------------------------------------------;
test r8,r8
jnz TimerFailed       ; Go if error returned by OS or wait interrupted.
mov r8,[rsi+00]       ; R8 = Time remain, seconds.
or  r8,[rsi+08]       ; R8 = Disjunction with TIme remain, nanoseconds.
jnz TimerFailed       ; Go if remain time stored by function.
;---------- Calculate delta-TSC per 1 second = TSC frequency. -----------------;
sub eax,edi           ; Subtract: DeltaTSC.Low  = EndTSC.Low - StartTSC.Low.
sbb edx,ecx           ; Subtract: DeltaTSC.High = EndTSC.High - StartTSC.High - Borrow.
;---------- Extract TSC frequency as 64-bit value. ----------------------------;
shl rdx,32
add rax,rdx
jz TimerFailed
;---------- Exit points. ------------------------------------------------------;
clc
TimerDone:
pop rdi rsi
leave                 ; Remove stack alignment.
ret
TimerFailed:
stc
jmp TimerDone


