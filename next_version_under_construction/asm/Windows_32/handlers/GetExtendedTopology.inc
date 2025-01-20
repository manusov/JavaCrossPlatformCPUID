;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; DLL library for Windows ia32.                                                ; 
; Handler for java request ID = REQUEST_GET_EXTENDED_TOPOLOGY.                 ;
;                                                                              ;
; Get platform topology information by WinAPI function:                        ;
; GetLogicalProcessorInformationEx(). See MSDN for details and buffer layout.  ;
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
;           from [ EDI ] to [ EDI + data buffer maximum length ] exclusive.    ;
;           DWORD [EDI + 00] = Number of bytes returned.                       ;
;           DWORD [EDI + 04] = Status, 0 if OK (output valid) or error code.   ;
;           2 DWORDs from [EDI + 08] = Reserved for status and alignment.      ;
;           Topology data located at OPB from [EDI + 16].                      ;
;                                                                              ;
;------------------------------------------------------------------------------;

GetExtendedTopology:
mov dword [edi + 00],0
mov dword [edi + 04],ID_ERROR_TOPOLOGY_NO_API

mov ebx,[_GetLogicalProcessorInformationEx]
test ebx,ebx
jz .exit

lea ecx,[edi + 16]        ; ECX = Buffer pointer, added after each subroutine.
xor edx,edx               ; EDX = Buffer size, , added after each subroutine.
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

mov dword [edi + 00],edx
mov dword [edi + 04],0

.exit:
ret

;---------- Helper for get topological information by selected type. ----------;
;                                                                              ;
; INPUT:   AL   = Relation type by WinAPI encoding.                            ;   
;          EBX  = WinAPI function address.                                     ;
;          ECX  = Pointer to buffer for topology information.                  ;
;          EDX  = Buffer used size accumulator.                                ;
;          DWORD [ESI + 08] = Topology data buffer maximum length, bytes.      ;
;          DWORD [EDI + 00] = Temporary DWORD variable at OPB.                 ;
;          DWORD [EDI + 04] = Output status DWORD variable at OPB.             ;
;                                                                              ;
; OUTPUT:  CF flag: error status: 0(NC)=No errors, 1(C)=Error.                 ;
;          ECX = Updated buffer pointer.                                       ;
;          EDX = Updated buffer total size accumulator.                        ;
;------------------------------------------------------------------------------;             
HelperTopologyEx:
movzx eax,al
push ebp eax ecx edx
mov ebp,esp

mov dword [edi + 00],0   ; Dword [edi]  = Buffer size variable, OPB.
mov dword [edi + 04],ID_ERROR_TOPOLOGY_API

push edi                 ; Parm#3 = Pointer to size.
push dword [ebp + 04]    ; Parm#2 = Pointer to buffer.
push dword [ebp + 08]    ; Parm#1 = Relation type.
call ebx
test eax,eax             ; check EAX plus clear CF, required if return.
jnz .exitOk              ; This variant means no data return, but no errors.

call [GetLastError]
cmp eax,WINAPI_ERROR_BUFFER_LIMIT
jne .error               ; Go if other errors detected.

mov dword [edi + 04],ID_ERROR_BUFFER_LIMIT
mov eax,[edi + 00]
cmp eax,[esi + 08]
ja .error                ; Go if required buffer size too big.

add [ebp + 00],eax
mov eax,[ebp + 00]
cmp eax,[esi + 08]
ja .error                ; Go if required buffer size too big.

mov dword [edi + 04],ID_ERROR_TOPOLOGY_API

push edi                 ; Parm#3 = Pointer to size.
push dword [ebp + 04]    ; Parm#2 = Pointer to buffer.
push dword [ebp + 08]    ; Parm#1 = Relation type.
call ebx
test eax,eax
jz .error

mov dword [edi + 04],0
mov eax,[edi]
add [ebp + 04],eax 

.exitOk:
clc

.exit:
pop edx ecx eax ebp
ret

.error:
stc
jmp .exit
