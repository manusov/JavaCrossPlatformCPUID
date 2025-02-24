;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; DLL library for Windows x64.                                                 ; 
; Handler for java request ID = REQUEST_GET_TOPOLOGY.                          ;
;                                                                              ;
; Get platform topology information by WinAPI function:                        ;
; GetLogicalProcessorInformation(). See MSDN for details and buffer layout.    ;
; Returned binary data at output buffer must be interpreted                    ;
; by caller at java class level.                                               ;  
;                                                                              ;
; JVM calls "requestBinary" entry point (see WIN64JNI.asm), then works this    ;
; DLL library JNI gate and calls this subroutine, selected by request ID.      ; 
; Non-volatile registers for this subroutine: RBX, RBP, RSI, RDI, R12, R13.    ;
;                                                                              ;
; INPUT:    RSI = Pointer to IPB (Input Parameters Block).                     ;
;           RDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [RSI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [RSI + 04] = Reserved for request options.                   ;
;           DWORD [RSI + 08] = Topology data buffer maximum length, bytes.     ;
;           DWORD [RSI + 12] = Reserved for big data buffer length extension.  ;  
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result. Output buffer located    ;
;           from [ RDI ] to [ RDI + data buffer maximum length ] exclusive.    ;
;           DWORD [RDI + 00] = Number of bytes returned.                       ;
;           DWORD [RDI + 04] = Status, 0 if OK (output valid) or error code.   ;
;           8 BYTES [RDI + 08] = Reserved for status and alignment.            ;
;           Topology data located at OPB from [RDI + 16].                      ;
;                                                                              ;
;------------------------------------------------------------------------------;

ID_ERROR_TOPOLOGY_NO_API  = 10
ID_ERROR_TOPOLOGY_API     = 11
ID_ERROR_BUFFER_LIMIT     = 2 
WINAPI_ERROR_BUFFER_LIMIT = 07Ah

GetTopology:
push rbp
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32

mov dword [rdi + 00],0
mov dword [rdi + 04],ID_ERROR_TOPOLOGY_NO_API
mov r15,[_GetLogicalProcessorInformation]
test r15,r15
jz .exit
mov dword [rdi + 04],ID_ERROR_TOPOLOGY_API
mov rdx,rdi            ; RDX = Parm#2 = Pointer to buffer size variable. 
lea rcx,[rdi + 16]     ; RCX = Parm#1 = Pointer to buffer.
call r15 
test rax,rax
jnz .exit              ; Go if unexpected error: zero-sized buffer cause OK.

call [GetLastError]
cmp rax,WINAPI_ERROR_BUFFER_LIMIT
jne .exit              ; Go if other errors detected.
mov dword [rdi + 04],ID_ERROR_BUFFER_LIMIT
mov eax,[rdi]
add eax,16
cmp eax,[rsi + 08]
ja .exit               ; Go if required buffer size too big. 

mov dword [rdi + 04],ID_ERROR_TOPOLOGY_API
mov rdx,rdi            ; RDX = Parm#2 = Pointer to buffer size variable. 
lea rcx,[rdi + 16]     ; RCX = Parm#1 = Pointer to buffer. 
call r15
test rax,rax
jz .exit               ; Go if unexpected error: zero-sized buffer.

mov dword [rdi + 04],0

.exit:
leave
ret

