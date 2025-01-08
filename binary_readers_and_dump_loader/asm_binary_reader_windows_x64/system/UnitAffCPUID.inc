;---------- Unit test handler: dump affinized CPUID information. --------------;
; This variant for all functions/subfunctions from SELECTED thread,            ;
; with affinization. Use cycle for scan all logical processors.                ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitAffCPUID:
call CheckCpuid
lea rcx,[MsgErrorGetCpuid]
jc HelperError
push rbx rsi rdi rbp
;---------- Get system info and logical processors count. ---------------------;
lea rsi,[TEMP_BUFFER + 8192]
lea rdi,[TEMP_BUFFER + 16384]
call LoadSystemInfo
mov ebx,[rdi]
lea rcx,[MsgErrorCpuNone]
test ebx,ebx
jz .error
lea rcx,[MsgErrorCpuLimit]
cmp ebx,PROCESSORS_LIMIT
ja .error
;---------- Cycle for detected logical processors. ----------------------------;
xor ebp,ebp                  ; EBP = Logical processor number.
.scanLogicalProcessor:
mov [esi],ebp
call GetCpuidDumpWithHeaderAffinized
mov r8d,[rdi]                ; R8D = CPUID functions (32B entries) count. 
lea rcx,[MsgErrorWinAPI]
cmp r8d,-1
je .error 
lea rcx,[MsgErrorAffin]
cmp r8d,-2
je .error 
lea rcx,[MsgErrorAffFailed]
cmp r8d,-3
je .error 
lea rcx,[MsgErrorZeroCpuid]
test r8d,r8d
jz .error
lea rcx,[MsgErrorOverCpuid]
cmp r8d,ENTRIES_LIMIT
ja .error 
lea rcx,[TEMP_BUFFER]         ; RCX = Pointer to text buffer.
mov rdx,rdi                   ; RDX = Pointer to CPUID data buffer.
mov r9d,ebp                   ; R9D = Logical processor number.
call ConsoleWriteDumpCPUID
inc ebp
cmp ebp,ebx
jb .scanLogicalProcessor 
;---------- Exit if no errors. ------------------------------------------------;
pop rbp rdi rsi rbx
ret
;---------- Exit with error message. ------------------------------------------;
.error:
pop rbp rdi rsi rbx
jmp HelperError


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


;---------- Load system information. ------------------------------------------;
;                                                                              ;
; INPUT:   RSI = Pointer to IPB (Input Parameters Block). Not used here.       ;
;          RDI = Pointer to OPB (Output Parameters Block).                     ;
;                                                                              ;
; OUTPUT:  DWORD  [RDI + 00] = Logical processors count.                       ;
;          DWORD  [RDI + 04] = Reserved.                                       ;
;          QWORD  [RDI + 08] = Platform affinity mask (yet for 64 CPUs).       ;
;------------------------------------------------------------------------------;
LoadSystemInfo:
push rdi rbp
mov rbp,rsp 
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32
lea rcx,[SystemInfo]
call [GetSystemInfo]                       ; Get system info. 
cld
mov eax,[SystemInfo.dwNumberOfProcessors]  ; Get number of logical processors.
stosd
xor eax,eax
stosd
mov rax,[SystemInfo.dwActiveProcessorMask]  ; Get platform affinity mask.
stosq
mov rsp,rbp
pop rbp rdi
ret

;---------- Get CPUID dump with 32-byte header, for selected logical CPU. -----;
;                                                                              ;
; INPUT:   RSI = Pointer to IPB (Input Parameters Block).                      ;
;                DWORD [RSI + 0] = Logical CPU number.                         ;
;          RDI = Pointer to OPB (Output Parameters Block).                     ;
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
push rbx rbp r12
mov rbp,rsp 
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32
;---------- Set thread affinity mask. -----------------------------------------;
call [GetCurrentThread]
mov dword [rdi],-1
test rax,rax
jz .exit
xchg rbx,rax
mov dword [rdi],-2
cmp [_SetThreadAffinityMask],0
jz .exit
mov ecx,[rsi]
mov rdx,1
rol rdx,cl
mov rcx,rbx
call [_SetThreadAffinityMask]
test rax,rax
jz .exitAffinityFailed
xchg r12,rax                  ; R12 = original affinity mask, XCHG is compact.
;---------- Get CPUID dump. ---------------------------------------------------;
call GetCpuidDumpWithHeader
;---------- Restore thread affinity mask. -------------------------------------;
mov rcx,rbx
mov rdx,r12
call [_SetThreadAffinityMask]
test rax,rax
jz .exitAffinityFailed
;---------- Done. -------------------------------------------------------------;
.exit:
mov rsp,rbp
pop r12 rbp rbx
ret
.exitAffinityFailed:
mov dword [rdi],-3
jmp .exit
