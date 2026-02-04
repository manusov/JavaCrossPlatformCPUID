;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; DLL library for Windows ia32.                                                ; 
; Handler for java request ID = REQUEST_GET_PLATFORM_INFO.                     ;
;                                                                              ;
; Get number of logical processors, platform affinity mask                     ; 
; and optional OS-specific data.                                               ; 
; Operation executed for current thread without affinization.                  ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see WIN32JNI.asm), then works this    ;
; DLL library JNI gate and calls this subroutine, selected by request ID.      ; 
; Non-volatile registers for this subroutine: EBP, ESI, EDI.                   ;
;                                                                              ;
; INPUT:    ESI = Pointer to IPB (Input Parameters Block).                     ;
;           EDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [ESI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [ESI + 04] = Reserved for request options.                   ;
;           DWORD [ESI + 08] = Output data buffer maximum length, bytes.       ;
;           DWORD [ESI + 12] = Reserved for big data buffer length extension.  ;  
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result. Output buffer located    ;
;           from [ EDI ] to [ EDI + data buffer maximum length ] exclusive.    ;
;           DWORD [EDI + 00] = Status, 0 if OK (output valid) or error code.   ;
;           DWORD [EDI + 04] = Offset of OS-specific data block #1. 0 if none. ;
;           DWORD [EDI + 08] = Offset of OS-specific data block #2. 0 if none. ;
;           DWORD [EDI + 12] = Platform logical processors count.              ;
;           DWORD [EDI + 16] = Platform affinity mask QWORDs count = N.        ;
;           DWORD [EDI + 20] = Reserved for affinization mode(s) report.       ;
;           N QWORDS from [EDI + 24] = Affinity mask, N x 64-bit values.       ;                       
;                                                                              ;
;------------------------------------------------------------------------------;

GetPlatformInfo:
mov dword [edi + 00],2     ; Status = 2 means insufficient output buffer size.
cmp dword [esi + 08],sizeof.SYSTEM_INFO + 32
jb .exit
mov dword [edi + 00],0     ; Status = 0 means no errors.
mov eax,32
mov dword [edi + 04],eax
mov dword [edi + 08],0
lea ecx,[edi + eax]
push ecx
call [GetSystemInfo]
mov eax,[edi + 32 + SYSTEM_INFO.dwNumberOfProcessors]
mov dword [edi + 12],eax
mov dword [edi + 16],1
mov dword [edi + 20],0
mov eax,[edi + 32 + SYSTEM_INFO.dwActiveProcessorMask]
mov dword [edi + 24],eax
mov dword [edi + 28],0
.exit:
ret

