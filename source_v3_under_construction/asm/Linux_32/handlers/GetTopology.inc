;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; *.SO library for Linux ia32.                                                 ; 
; Handler for java request ID = REQUEST_GET_TOPOLOGY.                          ;
;                                                                              ;
; This function NOT USED for Linux 32/64 variants because topology detected    ;
; by /sysfs operations. Reserved for Windows 32/64 variants.                   ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see LINUX32JNI.asm), then works this  ;
; *.SO library JNI gate and calls this subroutine, selected by request ID.     ; 
; Non-volatile registers for this subroutine: EBP, ESI, EDI.                   ;
;                                                                              ;
; INPUT:    NOT USED FOR LINUX 32/64. USED FOR WINDOWS 32/64 VARIANTS.         ;
;           ESI = Pointer to IPB (Input Parameters Block).                     ;
;           EDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [ESI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [ESI + 04] = Reserved for request options.                   ;
;           DWORD [ESI + 08] = Topology data buffer maximum length, bytes.     ;
;           DWORD [ESI + 12] = Reserved for big data buffer length extension.  ;  
;                                                                              ;
; OUTPUT:   NOT USED FOR LINUX 32/64. USED FOR WINDOWS 32/64 VARIANTS.         ;
;           Memory at OPB updated by function result. Output buffer located    ;
;           from [ EDI ] to [ EDI + data buffer maximum length ] exclusive.    ;
;           DWORD [EDI + 00] = Number of bytes returned.                       ;
;           DWORD [EDI + 04] = Status, 0 if OK (output valid) or error code.   ;
;           8 BYTES [EDI + 08] = Reserved for status and alignment.            ;
;           Topology data located at OPB from [EDI + 16].                      ;
;                                                                              ;
;------------------------------------------------------------------------------;

GetTopology:
; This entry point not used yet. 
ret

