;---------- Unit test handler: measure TSC clock frequency. -------------------;
; Note. TSC is Time Stamp Counter.                                             ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitTSC:
push rbx rsi rdi
;---------- Write left string (parameter name) to console. --------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea rcx,[MsgMeasureTSC]
call ConsoleWriteReport 
;---------- Measure TSC frequency. --------------------------------------------;
lea rdi,[TEMP_BUFFER + 8192]
call MeasureCpuClkVerified
mov rax,[rdi]
test rax,rax
jz .errorTsc 
;---------- Calculate value in MHz. -------------------------------------------;
finit
fild qword [rdi]
push 1000000
fidiv dword [rsp]
fstp qword [rsp]
pop rax
;---------- Write string with value in MHz. -----------------------------------;
lea rdi,[TEMP_BUFFER]
push rdi
mov bx,0200h
call DoublePrint
lea rsi,[NameMHz]
call StringWrite
mov al,0
stosb
;---------- Write right string (parameter value) to console. ------------------;
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
pop rcx
call ConsoleWriteReport
;---------- Exit if no errors. ------------------------------------------------;
pop rdi rsi rbx
ret
;---------- Exit with error messages. -----------------------------------------;
.errorTsc:
lea rcx,[CrLf]           ; Parm#1 = RCX = Pointer to 0Dh, 0Ah ( CR, LF ).
call ConsoleWriteReport  ; Console output.
lea rcx,[MsgErrorTSC]
call HelperError
pop rdi rsi rbx
ret


;==============================================================================;
;                                                                              ;
;              Subroutines portable to JNI (Java Native Interface)             ;
;                         library of CPUID utility.                            ;
;          Accept RSI = optional input data array (or no input data).          ;
;                       IPB = Input Parameters Block.                          ;
;                 RDI = optional output data array (or no output data).        ;
;                       OPB = Output Parameters Block.                         ;
;                 RAX = optional output result or status info.                 ;
;                 CF flag = status.                                            ;
;                                                                              ;
;==============================================================================;


;---------- Measure CPU Clock frequency by Time Stamp Counter (TSC). ----------;
;                                                                              ;
; INPUT:   RSI = Pointer to IPB (Input Parameters Block). Not used here.       ;
;          RDI = Pointer to OPB (Output Parameters Block).                     ;
;                                                                              ;
; OUTPUT:  QWORD OPB[00] = [RDI + 00] = Frequency, Hz, 0 if measurement error. ;
;------------------------------------------------------------------------------;
MeasureCpuClkVerified:
push rax rbx rcx rdx
mov qword [rdi],0
;---------- This support already verified during Hardware Shell start. --------;
;---------- But required for JNI porting. -------------------------------------;
call CheckCpuid
jc @f
cmp eax,1
jb @f
mov eax,1
cpuid
test dl,10h
jz @f
call MeasureCpuClk
jc @f
mov [rdi],rax
@@:
pop rdx rcx rbx rax
ret

;---------- This entry point used after verification, returns RAX = Hz. -------;

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




