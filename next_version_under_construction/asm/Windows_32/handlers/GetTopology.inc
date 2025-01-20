;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; DLL library for Windows ia32.                                                ; 
; Handler for java request ID = REQUEST_GET_TOPOLOGY.                          ;
;                                                                              ;
; Get platform topology information by WinAPI function:                        ;
; GetLogicalProcessorInformation(). See MSDN for details and buffer layout.    ;
; Returned binary data at output buffer must be interpreted                    ;
; by caller at java class level.                                               ;  
;                                                                              ;
; JVM calls "requestBinary" entry point (see WIN32JNI.asm), then works this    ;
; DLL library JNI gate and calls this subroutine, selected by request ID.      ; 
; Non-volatile registers for this subroutine: EBP, ESI, EDI.                   ;
;                                                                              ;
; INPUT:    ESI = Pointer to IPB (Input Parameters Block).                     ;
;           EDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [ESI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [ESI + 04] = Reserved for request options.                   ;
;           DWORD [ESI + 08] = Topology data buffer maximum length, bytes.     ;
;           DWORD [ESI + 12] = Reserved for big data buffer length extension.  ;  
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result. Output buffer located    ;
;           from [ EDI ] to [ RDI + data buffer maximum length ] exclusive.    ;
;           DWORD [EDI + 00] = Number of bytes returned.                       ;
;           DWORD [EDI + 04] = Status, 0 if OK (output valid) or error code.   ;
;           8 BYTES [EDI + 08] = Reserved for status and alignment.            ;
;           Topology data located at OPB from [EDI + 16].                      ;
;                                                                              ;
;------------------------------------------------------------------------------;

ID_ERROR_TOPOLOGY_NO_API  = 10
ID_ERROR_TOPOLOGY_API     = 11
ID_ERROR_BUFFER_LIMIT     = 2 
WINAPI_ERROR_BUFFER_LIMIT = 07Ah

GetTopology:
mov dword [edi + 00],0
mov dword [edi + 04],ID_ERROR_TOPOLOGY_NO_API
cmp [_GetLogicalProcessorInformation],0
je .exit
mov dword [edi + 04],ID_ERROR_TOPOLOGY_API
push edi               ; Parm#2 = Pointer to buffer size variable. 
lea ecx,[edi + 16]
push ecx               ; Parm#1 = Pointer to buffer.
call [_GetLogicalProcessorInformation]  
test eax,eax
jnz .exit              ; Go if unexpected error: zero-sized buffer cause OK.

call [GetLastError]
cmp eax,WINAPI_ERROR_BUFFER_LIMIT
jne .exit              ; Go if other errors detected.
mov dword [edi + 04],ID_ERROR_BUFFER_LIMIT
mov eax,[edi]
add eax,16
cmp eax,[esi + 08]
ja .exit               ; Go if required buffer size too big. 

mov dword [edi + 04],ID_ERROR_TOPOLOGY_API
push edi               ; Parm#2 = Pointer to buffer size variable. 
lea ecx,[edi + 16]
push ecx               ; Parm#1 = Pointer to buffer. 
call [_GetLogicalProcessorInformation]
test eax,eax
jz .exit               ; Go if unexpected error: zero-sized buffer.

mov dword [edi + 04],0

.exit:
ret

