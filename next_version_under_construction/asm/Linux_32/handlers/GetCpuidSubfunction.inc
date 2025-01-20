;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; *.SO library for Linux ia32.                                                 ; 
; Handler for java request ID = REQUEST_GET_CPUID_SUBFUNCTION.                 ;
;                                                                              ;
; Get CPUID information for one selected Standard/Extended/Virtual/Vendor      ;
; CPUID function with optional subfunction selection. Executed for one         ;
; logical processor (current thread). This variant without affinization.       ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see LINUX32JNI.asm), then works this  ;
; *.SO library JNI gate and calls this subroutine, selected by request ID.     ; 
; Non-volatile registers for this subroutine: EBP, ESI, EDI.                   ;
;                                                                              ;
; INPUT:    ESI = Pointer to IPB (Input Parameters Block).                     ;
;           EDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [ESI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [ESI + 04] = Reserved for request options.                   ;
;           DWORD [ESI + 08] = CPUID function, used as CPUID input EAX.        ;
;           DWORD [ESI + 12] = CPUID sub-function, used as CPUID input ECX.    ;   
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result.                          ;
;           DWORD [EDI + 00] = Number of 32-byte entries returned.             ;
;                              must be maximum 1 for this function.            ;
;           DWORD [EDI + 04] = Status, 0 if OK (output valid) or error code.   ; 
;           6 DWORDs from [EDI + 08] = Reserved for status and alignment.      ;
;           One entry returned by this function, it located at OPB [EDI + 32]. ; 
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
push ebp
xor eax,eax
push eax eax eax eax eax eax
mov ebp,esp

cld
push edi
stosd          ; Return size = 0, no entries returned yet.
mov eax,1
stosd          ; Status = 1 means CPUID not supported or locked.
mov ecx,6
xor eax,eax
rep stosd
pop edi

call CheckCpuid
jc .exit

push ebx esi edi
add esi,8
add edi,32
lodsd
mov temp_r9d,eax
lodsd
xchg ecx,eax
mov eax,temp_r9d   ; EAX  = R9D = Function, select by this line. 
mov esi,ecx        ; ECX  = ESI = Sub-function, select by this line.
mov temp_ebp,0     ; EBP  = Entries counter (redundant here).
mov temp_r15d,1    ; R15D = Entries count limit (redundant here). 
call StoreCpuid
pop edi esi ebx 

mov eax,temp_ebp
mov dword [edi + 00],eax
mov dword [edi + 04],0
.exit:
add esp,24
pop ebp
ret
