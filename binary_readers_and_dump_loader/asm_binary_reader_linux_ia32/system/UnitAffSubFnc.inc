;---------- Unit test handler: dump affinized CPUID (sub)function info. -------;
; This variant for one (sub)function from SELECTED thread,                     ;
; with affinization. Use cycle for scan all logical processors.                ; 
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitAffSubFnc:
call CheckCpuid
lea ecx,[MsgErrorGetCpuid]
jc HelperError
push ebx esi edi ebp
;---------- Initializing IPB, OPB. Blank header. ------------------------------;
lea esi,[TEMP_BUFFER + 8192]
lea edi,[TEMP_BUFFER + 16384]
mov ecx,8
xor eax,eax
cld
rep stosd
;---------- Execute CPUID and store results to 32-byte entry. -----------------;
mov dword [esi + 0],TEST_FUNCTION_AFFINIZED      ; CPUID function
mov dword [esi + 4],TEST_SUB_FUNCTION_AFFINIZED  ; CPUID sub-function
mov dword [esi + 8],TEST_LOGICAL_PROCESSOR       ; Logical processor number. 
call GetCpuidSubFunctionAffinized                ; RSI = IPB, RDI = OPB.
;---------- Check status. -----------------------------------------------------;
lea ecx,[MsgErrorOSAPI]
cmp eax,-1
je .error 
lea ecx,[MsgErrorAffin]
cmp eax,-2
je .error 
lea ecx,[MsgErrorAffFailed]
cmp eax,-3
je .error 
;---------- Console output. ---------------------------------------------------;
lea ecx,[TEMP_BUFFER]
lea edx,[ecx + 16384]
mov ebx,1
xor eax,eax
call ConsoleWriteDumpCPUID
;---------- Exit if no errors. ------------------------------------------------;
pop ebp edi esi ebx
ret
;---------- Exit with error message. ------------------------------------------;
.error:
pop ebp edi esi ebx
jmp HelperError

;---------- Constants for debug -----------------------------------------------;
TEST_FUNCTION_AFFINIZED     = 0Bh  ; 1 ; 80000008h
TEST_SUB_FUNCTION_AFFINIZED = 01h  ; 0 ; 00000000h
TEST_LOGICAL_PROCESSOR      = 3


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


;---------- Execute one CPUID (sub)function. ----------------------------------;
; Assume CPUID support already validated (EFLAGS.21)                           ;
; before call this function.                                                   ;
;                                                                              ;
; INPUT:   ESI = Pointer to IPB (Input Parameters Block).                      ;
;          EDI = Pointer to OPB (Output Parameters Block).                     ;
;          DWORD [ESI + 00] = CPUID function.                                  ;
;          DWORD [ESI + 04] = CPUID sub-function, optional for some            ; 
;                             CPUID functions.                                 ;
;          DWORD [ESI + 08] = Logical processor number for affinization.       ;
;                                                                              ;
; OUTPUT:  32 bytes [EDI + 00] = CPUID sub(function) result entry.             ;
;          EAX = Status:                                                       ;
;                 0 : No errors,                                               ;
;                -1 : Invalid current thread handle,                           ;
;                -2 : Affinization API not found,                              ;
;                -3 : Affinization failed.                                     ;                            
;------------------------------------------------------------------------------;
; Output CPUID sub(function) result entry is 32 bytes, 8 dwords:               ;
; dword   offset in entry(hex)   comments                                      ;
;------------------------------------------------------------------------------;
;   0     00-03                  Information type tag, 0 for CPUID info.       ;                  
;   1     04-07                  CPUID function number.                        ;
;   2     08-0B                  CPUID subfunction number.                     ;
;   3     0C-0F                  CPUID pass number (as for function #2).       ;
;   4     10-13                  Result EAX after CPUID.                       ;
;   5     14-17                  Result EBX after CPUID.                       ;
;   6     18-1B                  Result ECX after CPUID.                       ;
;   7     1C-1F                  Result EDX after CPUID.                       ;
;------------------------------------------------------------------------------;
GetCpuidSubFunctionAffinized:
push ebx ebp

;---------- Set thread affinity mask. -----------------------------------------;
;---------- Set thread affinity mask. -----------------------------------------;
; TODO. Error check required after system calls.
push esi edi 0 0 0 0
mov ecx,[esi + 8]
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

;---------- Get CPUID sub(function) results. ----------------------------------;
call GetCpuidSubFunction

;---------- Restore thread affinity mask. -------------------------------------;
push 0 ebp
                          ; Parm#1 = EBX = PID.
mov ecx,8                 ; Parm#2 = ECX = Mask size.
mov edx,esp               ; Parm#3 = EDX = Pointer for store mask.
mov eax,SYS_SETAFFINITY   ; System call code = EAX.
int 80h
pop eax eax

;---------- Done. -------------------------------------------------------------;
xor eax,eax
.exit:
pop ebp ebx
ret
.exitAffinityFailed:
mov eax,-3
jmp .exit
