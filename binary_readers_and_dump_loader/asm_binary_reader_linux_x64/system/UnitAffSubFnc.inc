;---------- Unit test handler: dump affinized CPUID (sub)function info. -------;
; This variant for one (sub)function from SELECTED thread,                     ;
; with affinization. Use cycle for scan all logical processors.                ; 
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitAffSubFnc:
call CheckCpuid
lea rcx,[MsgErrorGetCpuid]
jc HelperError
push rbx rsi rdi rbp
;---------- Initializing IPB, OPB. Blank header. ------------------------------;
lea rsi,[TEMP_BUFFER + 8192]
lea rdi,[TEMP_BUFFER + 16384]
mov ecx,8
xor eax,eax
cld
rep stosd
;---------- Execute CPUID and store results to 32-byte entry. -----------------;
mov dword [rsi + 0],TEST_FUNCTION_AFFINIZED      ; CPUID function
mov dword [rsi + 4],TEST_SUB_FUNCTION_AFFINIZED  ; CPUID sub-function
mov dword [rsi + 8],TEST_LOGICAL_PROCESSOR       ; Logical processor number. 
call GetCpuidSubFunctionAffinized                ; RSI = IPB, RDI = OPB.
;---------- Check status. -----------------------------------------------------;
lea rcx,[MsgErrorOSAPI]
cmp eax,-1
je .error 
lea rcx,[MsgErrorAffin]
cmp eax,-2
je .error 
lea rcx,[MsgErrorAffFailed]
cmp eax,-3
je .error 
;---------- Console output. ---------------------------------------------------;
lea rcx,[TEMP_BUFFER]
lea rdx,[rcx + 16384]
mov r8d,1
xor r9d,r9d
call ConsoleWriteDumpCPUID
;---------- Exit if no errors. ------------------------------------------------;
pop rbp rdi rsi rbx
ret
;---------- Exit with error message. ------------------------------------------;
.error:
pop rbp rdi rsi rbx
jmp HelperError

;---------- Constants for debug -----------------------------------------------;
TEST_FUNCTION_AFFINIZED     = 0Bh  ; 1 ; 80000008h
TEST_SUB_FUNCTION_AFFINIZED = 01h  ; 0 ; 00000000h
TEST_LOGICAL_PROCESSOR      = 3


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


;---------- Execute one CPUID (sub)function. ----------------------------------;
; Assume CPUID support already validated (EFLAGS.21)                           ;
; before call this function.                                                   ;
;                                                                              ;
; INPUT:   RSI = Pointer to IPB (Input Parameters Block).                      ;
;          RDI = Pointer to OPB (Output Parameters Block).                     ;
;          DWORD [RSI + 00] = CPUID function.                                  ;
;          DWORD [RSI + 04] = CPUID sub-function, optional for some            ; 
;                             CPUID functions.                                 ;
;          DWORD [RSI + 08] = Logical processor number for affinization.       ;
;                                                                              ;
; OUTPUT:  32 bytes [RDI + 00] = CPUID sub(function) result entry.             ;
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
push rsi rdi rbx rbp r12 r13
mov rbp,rsp 
and rsp,0FFFFFFFFFFFFFFF0h

;---------- Set thread affinity mask. -----------------------------------------;
; TODO. Error check required after system calls.
push rsi rdi 0 0
mov ecx,[rsi + 8]
mov rdx,1
rol rdx,cl
mov [rsp + 0],rdx

mov eax,SYS_GETPID
syscall
mov r12d,eax

mov edi,eax                ; Parm#1 = RDI = PID.
mov esi,8                  ; Parm#2 = RSI = Mask size.
lea rdx,[rsp + 8]          ; Parm#3 = RDX = Pointer for store mask.
mov eax,SYS_GETAFFINITY    ; System call code = RAX.
syscall

mov edi,r12d               ; Parm#1 = RDI = PID.
mov esi,8                  ; Parm#2 = RSI = Mask size.
mov rdx,rsp                ; Parm#3 = RDX = Pointer for store mask.
mov eax,SYS_SETAFFINITY    ; System call code = RAX.
syscall

pop rdi r13 rdi rsi        ; Make R12 = still PID, R13 = Original mask.

;---------- Get CPUID sub(function) results. ----------------------------------;
call GetCpuidSubFunction

;---------- Restore thread affinity mask. -------------------------------------;
; TODO. Error check required after system calls.

push rsi rdi 0 r13
mov edi,r12d               ; Parm#1 = RDI = PID.
mov esi,8                  ; Parm#2 = RSI = Mask size.
mov rdx,rsp                ; Parm#3 = RDX = Pointer for store mask.
mov eax,SYS_SETAFFINITY    ; System call code = RAX.
syscall
pop rdi rdi rdi rsi

;---------- Done. -------------------------------------------------------------;
xor eax,eax
.exit:
mov rsp,rbp
pop r13 r12 rbp rbx rdi rsi
ret
.exitAffinityFailed:
mov eax,-3
jmp .exit

