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
