;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; DLL library for Windows ia32.                                                ; 
; Handler for java request ID = REQUEST_MEASURE_TSC_FREQUENCY.                 ;
;                                                                              ;
; Measure TSC (Time Stamp Counter) clock frequency and returns as Hz.          ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see WIN32JNI.asm), then works this    ;
; DLL library JNI gate and calls this subroutine, selected by request ID.      ; 
; Non-volatile registers for this subroutine: EBP, ESI, EDI.                   ;
;                                                                              ;
; INPUT:    ESI = Pointer to IPB (Input Parameters Block).                     ;
;           EDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [ESI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [ESI + 04] = Reserved for request options.                   ;
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result.                          ;
;           DWORD [EDI + 00] = Number of clocks values returned.               ;
;                              must be maximum 1 for this function.            ;                              
;           DWORD [EDI + 04] = Status, 0 if OK (output valid) or error code.   ;
;           QWORD [EDI + 08] = TSC frequency value, Hz (periods per second).   ;
;                                                                              ;
;------------------------------------------------------------------------------;

MeasureTscFrequency:
push ebx
xor eax,eax
mov dword [edi + 00],eax   ; Return size = 0, no entries returned yet.
mov dword [edi + 04],1     ; Status = 1 means CPUID not supported or locked.
mov dword [edi + 08],eax
mov dword [edi + 12],eax
call CheckCpuid
jc .exit          ; Error if CPUID not supported or locked by VMM.
cmp eax,1
jb .exit          ; Error if CPUID function 1 not supported. 
mov eax,1
cpuid
test dl,10h
jz .exit          ; Error if TSC not supported.
;---------- This executed if operation supported. -----------------------------;
mov dword [edi + 04],6     ; Status = 6 means clock measurement failed.
call MeasureCpuClk
jc .exit
mov dword [edi + 00],1     ; Return size = 1 frequency value returned.
mov dword [edi + 04],0     ; Status = 0 means operation OK.
mov dword [edi + 08],eax
mov dword [edi + 12],edx
.exit:
pop ebx
ret

;--- This entry point used after verification, returns EDX:EAX = Hz. ----------;
;                                                      CF = error flag.
; TODO. Don't save volatile registers, optimize for use LEAVE instruction.

MeasureCpuClk:
cld
push ebx ecx esi ebp
xor ecx,ecx
push ecx ecx ecx ecx  ; 16 bytes for 2 Qword stack variables.
mov ebp,esp           ; EBP = pointer to local variables.
;---------- Detect performance counter status and frequency -------------------;
lea eax,[ebp + 08]
push eax              ; Parm#1 = pointer to output 64-bit variable.
call [QueryPerformanceFrequency]  ; Qword [ebp + 08] = performance frequency.
test eax,eax
jz .tryFileTime       ; Go File Time branch if status = FALSE.
;---------- Get current ticks counter value -----------------------------------;
push ebp              ; Parm#1 = pointer to output 64-bit variable.
call [QueryPerformanceCounter]  ; Qword [ebp + 00] = performance counter now.
test eax,eax
jz .tryFileTime       ; Go File Time branch if status = FALSE.
mov ebx,[ebp + 00]    ; EBX:ESI = Performance counter now.
mov esi,[ebp + 04]
;---------- Wait next tick for synchronization --------------------------------;
@@:
push ebp              ; Parm#1 = pointer to output 64-bit variable.
call [QueryPerformanceCounter]
test eax,eax
jz .tryFileTime       ; Go File Time branch if status = FALSE.
cmp ebx,[ebp + 00]
jne @f
cmp esi,[ebp + 04]
je @b                 ; Go wait cycle if counter value = previous.
@@:
;---------- Start measurement -------------------------------------------------;
mov ebx,[ebp + 00]    ; EBX:ESI = Current value of counter.
mov esi,[ebp + 04]
add ebx,[ebp + 08]    ; EBX:ESI = Current + Ticks per second = target value.
adc esi,[ebp + 12]
rdtsc                 ; Get start TSC.
push edx eax
@@:
push ebp              ; Parm#1 = pointer to output 64-bit variable.
call [QueryPerformanceCounter]
test eax,eax
jz .tryFileTime       ; Go File Time branch if status = FALSE.
cmp esi,[ebp + 04]
ja @b
jb @f
cmp ebx,[ebp + 00]
jae @b                ; Go wait cycle if target value >= current value.
@@:
rdtsc                 ; Get end TSC, calculate delta-TSC.
pop esi ebx
sub eax,esi
sbb edx,ebx           ; EDX:EAX = Delta TSC per 1 second = frequency, Hz.
jb .tryFileTime       ; Go File Time branch if error: frequency < 0.               
mov ecx,eax
or ecx,edx
jz .tryFileTime       ; Go File Time branch if error: frequency = 0,
;---------- Restore ESP, pop extra registers, exit. ---------------------------;
.exit:                    ; Here CF flag = status, NC=No errors, C=Error.
pop ecx ecx ecx ecx       ; Cannot ADD ESP,16 because CF flag. 
pop ebp esi ecx ebx
ret
;---------- This branch use File Time if Performance Counter failed. ----------;
.tryFileTime:
;---------- Start measure frequency. ------------------------------------------;
push ebp                         ; Parm#1 = pointer to output 64-bit variable.
call [GetSystemTimeAsFileTime]   ; Get current count.
mov esi,[ebp]
@@:                              ; Wait for start 1 second interval.
push ebp
call [GetSystemTimeAsFileTime]   ; Get next count for wait 100 ns.
cmp esi,[ebp]
je @b
mov esi,[ebp + 0]                ; Set time interval = 1 second.
mov ebx,[ebp + 4]
add esi,10000000                 ; 10^7 * 100ns = 1 second.
adc ebx,0
rdtsc                            ; Get start TSC.
push edx eax
@@:                              ; Wait for end 1 second interval.
push ebp
call [GetSystemTimeAsFileTime]   ; Get count for wait 1 second.
cmp [ebp + 4],ebx
jb @b
ja @f
cmp [ebp + 0],esi
jb @b
@@:
rdtsc                ; Get end TSC, calculate delta-TSC.
pop esi ebx
sub eax,esi
sbb edx,ebx          ; EDX:EAX = Delta TSC per 1 second = frequency, Hz.
jb .fileTimeError    ; Go error if frequency < 0.               
mov ecx,eax
or ecx,edx
jnz .exit            ; Go NO error if frequency > 0.
.fileTimeError:
stc
jmp .exit


