;---------- Unit test handler: dump affinized CPUID information. --------------;
; This variant for all functions/subfunctions from SELECTED thread,            ;
; with affinization. Use cycle for scan all logical processors.                ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitAffCPUID:
call CheckCpuid
lea ecx,[MsgErrorGetCpuid]
jc HelperError
push ebx esi edi ebp
;---------- Get system info and logical processors count. ---------------------;
lea esi,[TEMP_BUFFER + 8192]
lea edi,[TEMP_BUFFER + 16384]
call LoadSystemInfo
mov ebx,[edi]
lea ecx,[MsgErrorCpuNone]
test ebx,ebx
jz .error
lea ecx,[MsgErrorCpuLimit]
cmp ebx,PROCESSORS_LIMIT
ja .error
;---------- Cycle for detected logical processors. ----------------------------;
xor ebp,ebp                 ; EBP = Logical processor number.
.scanLogicalProcessor:
mov [esi],ebp
call GetCpuidDumpWithHeaderAffinized
mov eax,[edi]               ; EAX = CPUID functions (32B entries) count. 
lea ecx,[MsgErrorWinAPI]
cmp eax,-1
je .error 
lea ecx,[MsgErrorAffin]
cmp eax,-2
je .error 
lea ecx,[MsgErrorAffFailed]
cmp eax,-3
je .error 
lea ecx,[MsgErrorZeroCpuid]
test eax,eax
jz .error
lea ecx,[MsgErrorOverCpuid]
cmp eax,ENTRIES_LIMIT
ja .error 
;---------- Console write functions dump per logical processor = EBP. ---------;
push ebx
xchg ebx,eax                ; EBX = CPUID functions (32B entries) count. 
lea ecx,[TEMP_BUFFER]       ; ECX = Pointer to text buffer.
mov edx,edi                 ; EDX = Pointer to CPUID data buffer.
mov eax,ebp                 ; EAX = Logical processor number.
call ConsoleWriteDumpCPUID
pop ebx
;---------- Cycle for logical processors. -------------------------------------;
inc ebp
cmp ebp,ebx
jb .scanLogicalProcessor 
;---------- Exit if no errors. ------------------------------------------------;
pop ebp edi esi ebx
ret
;---------- Exit with error message. ------------------------------------------;
.error:
pop ebp edi esi ebx
jmp HelperError


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


;---------- Load system information. ------------------------------------------;
;                                                                              ;
; INPUT:   ESI = Pointer to IPB (Input Parameters Block). Not used here.       ;
;          EDI = Pointer to OPB (Output Parameters Block).                     ;
;                                                                              ;
; OUTPUT:  DWORD  [EDI + 00] = Logical processors count.                       ;
;          DWORD  [EDI + 04] = Reserved.                                       ;
;          QWORD  [EDI + 08] = Platform affinity mask (yet for 64 CPUs).       ;
;------------------------------------------------------------------------------;
LoadSystemInfo:
push edi ebp
push SystemInfo
call [GetSystemInfo]                       ; Get system info. 
cld
mov eax,[SystemInfo.dwNumberOfProcessors]  ; Get number of logical processors.
stosd
xor eax,eax
stosd
mov eax,[SystemInfo.dwActiveProcessorMask]  ; Get platform affinity mask.
stosd
xor eax,eax
stosd
pop ebp edi
ret

;---------- Get CPUID dump with 32-byte header, for selected logical CPU. -----;
;                                                                              ;
; INPUT:   ESI = Pointer to IPB (Input Parameters Block).                      ;
;                DWORD [ESI + 0] = Logical CPU number.                         ;
;          EDI = Pointer to OPB (Output Parameters Block).                     ;
;                Here used for status and dump data.                           ;
;                                                                              ;
; OUTPUT:  EAX = Reserved. (Previous: number of entries returned).             ; 
;          Buffer DWORD[0] = Number of entries returned or error code:         ;
;                            -1 : Invalid current thread handle,               ;
;                            -2 : Affinization API not found,                  ;
;                            -3 : Affinization failed.                         ;                            
;                                                                              ;
;          Bytes [4-31] = Reserved for alignment.                              ;
;          Bytes [32-16383] = Buffer, maximum (16384-32)/32 = 511 entries ret. ;
;------------------------------------------------------------------------------;
GetCpuidDumpWithHeaderAffinized:
push ebx ebp
;---------- Set thread affinity mask. -----------------------------------------;
call [GetCurrentThread]
mov dword [edi],-1
test eax,eax
jz .exit
xchg ebx,eax
mov dword [edi],-2
cmp [_SetThreadAffinityMask],0
jz .exit
mov ecx,[esi]
mov edx,1
rol edx,cl
push edx        ; Parm#2 = Affinity mask.
push ebx        ; Parm#1 = Thread handle.
call [_SetThreadAffinityMask]
test eax,eax
jz .exitAffinityFailed
xchg ebp,eax    ; EBP = original affinity mask, XCHG is compact.
;---------- Get CPUID dump. ---------------------------------------------------;
call GetCpuidDumpWithHeader
;---------- Restore thread affinity mask. -------------------------------------;
push ebp        ; Parm#2 = Affinity mask.
push ebx        ; Parm#1 = Thread handle.
call [_SetThreadAffinityMask]
test eax,eax
jz .exitAffinityFailed
;---------- Done. -------------------------------------------------------------;
.exit:
pop ebp ebx
ret
.exitAffinityFailed:
mov dword [edi],-3
jmp .exit
