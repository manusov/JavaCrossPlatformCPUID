;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; DLL library for Windows x64.                                                 ; 
; Handler for java request ID = REQUEST_GET_OS_CONTEXT.                        ;
;                                                                              ;
; Get CPU-supported saved-restored context features bitmap (by CPUID function  ;
; 0Dh) and OS-enabled context features bitmap (by XGETBV instruction).         ;
; Operation executed for current thread without affinization.                  ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see WIN64JNI.asm), then works this    ;
; DLL library JNI gate and calls this subroutine, selected by request ID.      ; 
; Non-volatile registers for this subroutine: RBX, RBP, RSI, RDI, R12, R13.    ;
;                                                                              ;
; INPUT:    RSI = Pointer to IPB (Input Parameters Block).                     ;
;           RDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [RSI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [RSI + 04] = Reserved for request options.                   ;
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result.                          ;
;           DWORD [RDI + 00] = Number of 8-byte (QWORDs) bitmaps returned.     ;
;                              Maximum 2 for this function.                    ;
;           DWORD [RDI + 04] = Status, 0 if OK (output valid) or error code.   ;
;           2 DWORDs from [RDI + 08] = Reserved for status and alignment.      ;
;           Bitmaps located at OPB from [RDI + 16].                            ;
;           QWORD [RDI + 16] = CPU-supported context features bitmap.          ;
;           QWORD [RDI + 24] = OS-enabled context features bitmap.             ;
;                                                                              ;
;------------------------------------------------------------------------------;

GetOsContext:
push rbx
xor eax,eax
mov dword [rdi + 00],eax   ; Return size = 0, no entries returned yet.
mov dword [rdi + 04],1     ; Status = 1 means CPUID not supported or locked.
mov qword [rdi + 08],rax
call CheckCpuid
jc .exit          ; Error if CPUID not supported or locked by VMM.
cmp eax,0Dh
jb .exit          ; Error if CPUID context declaration not supported.
mov eax,1
cpuid
bt ecx,27
jnc .exit         ; Error if CPU context management not supported.
;---------- This executed if operation supported. -----------------------------;
mov dword [rdi + 00],2     ; Return size = 2 entries returned.
mov dword [rdi + 04],0     ; Status = 0 means operation OK.
mov eax,0Dh
xor ecx,ecx
cpuid
mov [rdi + 16],eax         ; QWORD OPB[16] = CPU validation mask.
mov [rdi + 20],edx
xor ecx,ecx
xgetbv
mov [rdi + 24],eax         ; QWORD OPB[24] = OS validation mask. 
mov [rdi + 28],edx
.exit:
pop rbx
ret

