;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; DLL library for Windows x64.                                                 ; 
; Handler for java request ID = REQUEST_GET_EXTENDED_TOPOLOGY.                 ;
;                                                                              ;
; Get platform topology information by WinAPI function:                        ;
; GetLogicalProcessorInformationEx(). See MSDN for details and buffer layout.  ;
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
;           2 DWORDs from [RDI + 08] = Reserved for status and alignment.      ;
;           Topology data located at OPB from [RDI + 16].                      ;
;                                                                              ;
; This function gets info by separate calls for 5 entries types (0-4).         ;
; This required for ordering text strings in the report.                       ;
;                                                                              ;
;------------------------------------------------------------------------------;

GetExtendedTopology:
push r13
mov dword [rdi + 00],0
mov dword [rdi + 04],ID_ERROR_TOPOLOGY_NO_API

mov r15,[_GetLogicalProcessorInformationEx]
test r15,r15
jz .exit

lea r14,[rdi + 16]
xor r13,r13
mov al,0                  ; 0 = RelationProcessorCore.
call HelperTopologyEx
jc .exit
mov al,2                  ; 2 = RelationCache. 
call HelperTopologyEx
jc .exit
mov al,3                  ; 3 = RelationProcessorPackage.
call HelperTopologyEx
jc .exit
mov al,1                  ; 1 = RelationNumaNode.
call HelperTopologyEx
jc .exit
mov al,4                  ; 4 = RelationGroup.
call HelperTopologyEx

mov dword [rdi + 00],r13d
mov dword [rdi + 04],0

.exit:
pop r13
ret

;---------- Helper for get topological information by selected type. ----------;
;                                                                              ;
; INPUT:   AL   = Relation type by WinAPI encoding.                            ;   
;          R15  = WinAPI function address.                                     ;
;          R14  = Pointer to buffer for topology information.                  ;
;          R13D = Buffer used size accumulator.                                ;
;          DWORD [RSI + 08] = Topology data buffer maximum length, bytes.      ;
;          DWORD [RDI + 00] = Pointer to temporary DWORD variable at OPB.      ;
;          DWORD [RDI + 04] = Pointer to output status DWORD variable at OPB.  ;
;                                                                              ;
; OUTPUT:  CF flag: error status: 0(NC)=No errors, 1(C)=Error.                 ;
;          R14 = Updated buffer pointer.                                       ;
;          R13 = Updated buffer total size accumulator.                        ;
;------------------------------------------------------------------------------;             
HelperTopologyEx:
push rbx rbp
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32

movzx ebx,al
mov dword [rdi + 00],0   ; Dword [rdi]  = Buffer size variable, OPB.
mov dword [rdi + 04],ID_ERROR_TOPOLOGY_API
mov r8,rdi               ; R8  = Parm#3 = Pointer to size.
mov rdx,r14              ; RDX = Parm#2 = Pointer to buffer.
mov ecx,ebx              ; RCX = Parm#1 = Relation type.
call r15
test rax,rax             ; check RAX plus clear CF, required if return.
jnz .exitOk              ; This variant means no data return, but no errors.
call [GetLastError]
cmp eax,WINAPI_ERROR_BUFFER_LIMIT
jne .error               ; Go if other errors detected.

mov dword [rdi + 04],ID_ERROR_BUFFER_LIMIT
mov eax,[rdi + 00]
cmp eax,[rsi + 08]
ja .error                ; Go if required buffer size too big.
add r13d,eax
cmp r13d,[rsi + 08]
ja .error                ; Go if required buffer size too big.

mov dword [rdi + 04],ID_ERROR_TOPOLOGY_API
mov r8,rdi               ; R8  = Parm#3 = Pointer to size.
mov rdx,r14              ; RDX = Parm#2 = Pointer to buffer.
mov ecx,ebx              ; RCX = Parm#1 = Relation type.
call r15
test rax,rax
jz .error

mov dword [rdi + 04],0
mov eax,[rdi + 00]
add r14,rax
.exitOk:
clc
.exit:
leave
pop rbx
ret
.error:
stc
jmp .exit

