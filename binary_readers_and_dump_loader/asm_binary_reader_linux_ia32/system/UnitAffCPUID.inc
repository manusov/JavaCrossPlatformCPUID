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
lea ecx,[MsgErrorOSAPI]
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
push esi edi ebx ebp
;---------- Pre-blank results. ------------------------------------------------;
xor eax,eax
mov [edi + 0],eax
mov [edi + 4],eax
mov [edi + 8],eax
;---------- Get PID. ----------------------------------------------------------;
mov eax,SYS_GETPID
int 80h
;---------- Get affinity mask. ------------------------------------------------;
; TODO. Error check required after system calls.
mov ebx,eax                ; Parm#1 = EBX = PID.
mov ecx,8                  ; Parm#2 = ESI = Mask size.
lea edx,[edi + 8]          ; Parm#3 = EDX = Pointer for store mask.
mov eax,SYS_GETAFFINITY    ; System call code = EAX.
int 80h
;---------- Logical CPUs count = F(affinity mask). ----------------------------;
xor eax,eax
mov ecx,32
mov esi,[edi + 8]
.L0:
shr esi,1
jnc .L1
inc eax
.L1:
loop .L0
mov [edi + 0],eax
;---------- Done. -------------------------------------------------------------;
pop ebp ebx edi esi
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
push esi edi ebx ebp
;---------- Set thread affinity mask. -----------------------------------------;

;---------- Set thread affinity mask. -----------------------------------------;
; TODO. Error check required after system calls.
push esi edi 0 0 0 0
mov ecx,[esi + 0]
mov edx,1
rol edx,cl
mov [esp + 0],edx
mov dword [esp + 4],0
mov eax,SYS_GETPID
int 80h
mov ebx,eax               ; Parm#1 = EBX = PID.
mov ecx,8                 ; Parm#2 = ECX = Mask size.
lea edx,[esp + 8]         ; Parm#3 = EDX = Pointer for store mask.
mov eax,SYS_GETAFFINITY   ; System call code = EAX.
int 80h
                          ; Parm#1 = EBX = PID.
mov ecx,8                 ; Parm#2 = ECX = Mask size.
mov edx,esp               ; Parm#3 = EDX = Pointer for store mask.
mov eax,SYS_SETAFFINITY   ; System call code = EAX.
int 80h
pop eax eax ebp eax edi esi   ; Make R12 = still PID, R13 = Original mask.

;---------- Get CPUID dump. ---------------------------------------------------;
call GetCpuidDumpWithHeader

;---------- Restore thread affinity mask. -------------------------------------;
; TODO. Error check required after system calls.

push 0 ebp
                          ; Parm#1 = EBX = PID.
mov ecx,8                 ; Parm#2 = ECX = Mask size.
mov edx,esp               ; Parm#3 = EDX = Pointer for store mask.
mov eax,SYS_SETAFFINITY   ; System call code = EAX.
int 80h
pop eax eax

;---------- Done. -------------------------------------------------------------;
.exit:
pop ebp ebx edi esi
ret
.exitAffinityFailed:
mov dword [edi],-3
jmp .exit

