;---------- Unit test handler: dump CPUID (sub)function information. ----------;
; This variant for one (sub)function from current thread,                      ;
; without affinization.                                                        ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitSubFnc:
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
mov dword [rsi + 0],TEST_FUNCTION      ; CPUID function (for input EAX).
mov dword [rsi + 4],TEST_SUB_FUNCTION  ; CPUID sub-function (for input ECX). 
call GetCpuidSubFunction               ; RSI = IPB, RDI = OPB.
;---------- Console output. ---------------------------------------------------;
lea rcx,[TEMP_BUFFER]
lea rdx,[rcx + 16384]
mov r8d,1
xor r9d,r9d
call ConsoleWriteDumpCPUID
pop rbp rdi rsi rbx
ret

;---------- Constants for debug -----------------------------------------------;
TEST_FUNCTION     =  1   ; 0Bh  ; 1  ; 80000008h
TEST_SUB_FUNCTION =  0   ; 01h  ; 0  ; 00000000h


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
;                                                                              ;
; OUTPUT:  32 bytes [RDI + 00] = CPUID sub(function) result entry.             ;   
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
GetCpuidSubFunction:
push rbx rsi rdi rbp
cld
lodsd
xchg r9d,eax
lodsd
xchg ecx,eax
mov eax,r9d      ; EAX = R9D = Function, select by this line. 
mov esi,ecx      ; ECX = ESI = Sub-function, select by this line.
xor ebp,ebp      ; EBP = Entries count limit (redundant here).
call StoreCpuid
pop rbp rdi rsi rbx
ret


 