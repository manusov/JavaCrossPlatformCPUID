;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; DLL library for Windows x64.                                                 ; 
; Handler for java request ID = REQUEST_GET_PLATFORM_INFO.                     ;
;                                                                              ;
; Get number of logical processors, platform affinity mask                     ; 
; and optional OS-specific data.                                               ; 
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
;           DWORD [RSI + 08] = Output data buffer maximum length, bytes.       ;
;           DWORD [RSI + 12] = Reserved for big data buffer length extension.  ;  
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result. Output buffer located    ;
;           from [ RDI ] to [ RDI + data buffer maximum length ] exclusive.    ;
;           DWORD [RDI + 00] = Status, 0 if OK (output valid) or error code.   ;
;           DWORD [RDI + 04] = Offset of OS-specific data block #1. 0 if none. ;
;           DWORD [RDI + 08] = Offset of OS-specific data block #2. 0 if none. ;
;           DWORD [RDI + 12] = Platform logical processors count.              ;
;           DWORD [RDI + 16] = Platform affinity mask QWORDs count = N.        ;
;           DWORD [RDI + 20] = Reserved for affinization mode(s) report.       ;
;           N QWORDS from [RDI + 24] = Affinity mask, N x 64-bit values.       ;                       
;                                                                              ;
;------------------------------------------------------------------------------;

GetPlatformInfo:
push rbp
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32

mov dword [rdi + 00],2     ; Status = 2 means insufficient output buffer size.
cmp dword [rsi + 08],sizeof.SYSTEM_INFO + 32
jb .exit
mov dword [rdi + 00],0     ; Status = 0 means no errors.
mov eax,32
mov dword [rdi + 04],eax
mov dword [rdi + 08],0
lea rcx,[rdi + rax]
call [GetSystemInfo]
mov eax,[rdi + 32 + SYSTEM_INFO.dwNumberOfProcessors]
mov dword [rdi + 12],eax
mov dword [rdi + 16],1
mov dword [rdi + 20],0
mov rax,[rdi + 32 + SYSTEM_INFO.dwActiveProcessorMask]
mov qword [rdi + 24],rax

.exit:
leave
ret

