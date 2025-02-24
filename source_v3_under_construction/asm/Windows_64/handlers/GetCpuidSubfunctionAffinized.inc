;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; DLL library for Windows x64.                                                 ; 
; Handler for java request ID = REQUEST_GET_CPUID_SUBFUNCTION_AFFINIZED.       ;
;                                                                              ;
; Get CPUID information for one selected Standard/Extended/Virtual/Vendor      ;
; CPUID function with optional subfunction selection. Executed for one         ;
; selected logical processor (thread), selected by affinization WinAPI.        ;  
;                                                                              ;
; JVM calls "requestBinary" entry point (see WIN64JNI.asm), then works this    ;
; DLL library JNI gate and calls this subroutine, selected by request ID.      ; 
; Non-volatile registers for this subroutine: RBX, RBP, RSI, RDI, R12, R13.    ;
;                                                                              ;
; INPUT:    RSI = Pointer to IPB (Input Parameters Block).                     ;
;           RDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [RSI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [RSI + 04] = Reserved for request options.                   ;
;           DWORD [RSI + 08] = CPUID function, used as CPUID input EAX.        ;
;           DWORD [RSI + 12] = CPUID sub-function, used as CPUID input ECX.    ;
;           DWORD [RSI + 16] = Logical processor number for affinization,      ;
;                              this function internally creates affinity       ;
;                              mask as left shift "1" by this number.          ;
;           DWORD [RSI + 20] = Reserved for affinization options.              ; 
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

GetCpuidSubfunctionAffinized:
push r13 rbx rbp
mov rbp,rsp 
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32
;---------- Clear output buffer, pre-set error status. ------------------------;
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
;---------- Verify CPUID instruction supported and non-locked. ----------------;
call CheckCpuid
jc .exit
;---------- Set thread affinity mask. -----------------------------------------;
call [GetCurrentThread]
mov dword [rdi + 04],4
test rax,rax
jz .exit
xchg rbx,rax            ; RBX = Thread handle, used for affinization functions.
mov dword [rdi + 04],5
mov r13,[_SetThreadAffinityMask]   ; R13 = Pointer to affinization function.
test r13,r13
jz .exit
mov ecx,[rsi + 16]
mov rdx,1
rol rdx,cl
mov rcx,rbx
call r13         ; This means call [SetThreadAffinityMask].
test rax,rax
jz .exit
xchg r14,rax     ; R14 = original affinity mask, XCHG is compact.
;--- Get CPUID one function and subfunction (optional) per selected CPU. ------;
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
pop rsi rbp rbx
;---------- Restore thread affinity mask. -------------------------------------;
mov rcx,rbx
mov rdx,r14
call r13                ; This means call [SetThreadAffinityMask].
test rax,rax
jz .exit                ; Go if error.
mov dword [rdi + 04],0  ; Status = 0 if no errors.
;---------- Done. -------------------------------------------------------------;
.exit:
leave
pop rbx r13
ret

