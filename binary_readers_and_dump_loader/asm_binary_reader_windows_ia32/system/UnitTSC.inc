;---------- Unit test handler: measure TSC clock frequency. -------------------;
; Note. TSC is Time Stamp Counter.                                             ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitTSC:
push ebx esi edi
;---------- Write left string (parameter name) to console. --------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea ecx,[MsgMeasureTSC]
call ConsoleWriteReport 
;---------- Measure TSC frequency. --------------------------------------------;
lea edi,[TEMP_BUFFER + 8192]
call MeasureCpuClkVerified
mov eax,[edi + 0]
or eax,[edi + 4]
jz .errorTsc 
;---------- Calculate value in MHz. -------------------------------------------;
finit
fild qword [edi]
push 0
push 1000000
fidiv dword [esp]
fstp qword [esp]
pop eax edx
;---------- Write string with value in MHz. -----------------------------------;
lea edi,[TEMP_BUFFER]
push edi
mov bx,0200h
call DoublePrint
lea esi,[NameMHz]
call StringWrite
mov al,0
stosb
;---------- Write right string (parameter value) to console. ------------------;
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
pop ecx
call ConsoleWriteReport
;---------- Exit if no errors. ------------------------------------------------;
pop edi esi ebx
ret
;---------- Exit with error messages. -----------------------------------------;
.errorTsc:
lea ecx,[CrLf]           ; Parm#1 = RCX = Pointer to 0Dh, 0Ah ( CR, LF ).
call ConsoleWriteReport  ; Console output.
lea ecx,[MsgErrorTSC]
call HelperError
pop edi esi ebx
ret


;==============================================================================;
;                                                                              ;
;              Subroutines portable to JNI (Java Native Interface)             ;
;                         library of CPUID utility.                            ;
;          Accept ESI = optional input data array (or no input data).          ;
;                       IPB = Input Parameters Block.                          ;
;                 EDI = optional output data array (or no output data).        ;
;                       OPB = Output Parameters Block.                         ;
;                 EAX = optional output result or status info.                 ;
;                 CF flag = status.                                            ;
;                                                                              ;
;==============================================================================;


;---------- Measure CPU Clock frequency by Time Stamp Counter (TSC). ----------;
;                                                                              ;
; INPUT:   ESI = Pointer to IPB (Input Parameters Block). Not used here.       ;
;          EDI = Pointer to OPB (Output Parameters Block).                     ;
;                                                                              ;
; OUTPUT:  QWORD OPB[00] = [EDI + 00] = Frequency, Hz, 0 if measurement error. ;
;------------------------------------------------------------------------------;
MeasureCpuClkVerified:
push eax ebx ecx edx
xor eax,eax
mov dword [edi + 0],eax
mov dword [edi + 4],eax
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
mov [edi + 0],eax
mov [edi + 4],edx
@@:
pop edx ecx ebx eax
ret

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
