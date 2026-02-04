;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; *.SO library for Linux x64.                                                  ; 
; Handler for java request ID = REQUEST_GET_CPUID_SUBFUNCTION.                 ;
;                                                                              ;
; Get CPUID information for one selected Standard/Extended/Virtual/Vendor      ;
; CPUID function with optional subfunction selection. Executed for one         ;
; logical processor (current thread). This variant without affinization.       ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see LINUX64JNI.asm), then works this  ;
; *.SO library JNI gate and calls this subroutine, selected by request ID.     ; 
; Non-volatile registers for this subroutine: RBX, RBP, RSI, RDI, R12, R13.    ;
;                                                                              ;
; INPUT:    RSI = Pointer to IPB (Input Parameters Block).                     ;
;           RDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [RSI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [RSI + 04] = Reserved for request options.                   ;
;           DWORD [RSI + 08] = CPUID function, used as CPUID input EAX.        ;
;           DWORD [RSI + 12] = CPUID sub-function, used as CPUID input ECX.    ;   
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result.                          ;
;           DWORD [RDI + 00] = Number of 32-byte entries returned.             ;
;                              must be maximum 1 for this function.            ;
;           DWORD [RDI + 04] = Status, 0 if OK (output valid) or error code.   ; 
;           6 DWORDs from [RDI + 08] = Reserved for status and alignment.      ;
;           One entry returned by this function, it located at OPB [RDI + 32]. ; 
;                                                                              ;
;------------------------------------------------------------------------------;
; Output data entry is 32 bytes, 8 dwords:                                     ;
;------------------------------------------------------------------------------;
; Dword   Offset in entry(hex)   Comments                                      ;
;------------------------------------------------------------------------------;
;   0     00-03                  Information type tag, 0 for CPUID info.       ;                  
;   1     04-07                  CPUID function number.                        ;
;   2     08-0B                  CPUID subfunction number.                     ;
;   3     0C-0F                  CPUID pass number (as for function #2),       ;
;                                      pass number is deprecated.              ;
;   4     10-13                  Result EAX after CPUID.                       ;
;   5     14-17                  Result EBX after CPUID.                       ;
;   6     18-1B                  Result ECX after CPUID.                       ;
;   7     1C-1F                  Result EDX after CPUID.                       ;
;                                                                              ;
;------------------------------------------------------------------------------;

GetCpuidSubfunction:
cld
push rdi
xor eax,eax
stosd          ; Return size = 0, no entries returned yet.
mov eax,1
stosd          ; Status = 1 means CPUID not supported or locked.
mov ecx,6
xor eax,eax
rep stosd
pop rdi

call CheckCpuid
jc .exit

push rbx rbp rsi rdi
add rsi,8
add rdi,32
lodsd
xchg r9d,eax
lodsd
xchg ecx,eax
mov eax,r9d      ; EAX  = R9D = Function, select by this line. 
mov esi,ecx      ; ECX  = ESI = Sub-function, select by this line.
xor ebp,ebp      ; EBP  = Entries counter (redundant here).
mov r15d,1       ; R15D = Entries count limit (redundant here). 
call StoreCpuid
pop rdi 
mov dword [rdi + 00],ebp
mov dword [rdi + 04],0
pop rsi rbp rbx
.exit:
ret

