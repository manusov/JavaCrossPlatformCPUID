;---------- Unit test handler: read platform extended topology by WinAPI. -----;
; Use WinAPI function GetLogicalProcessorInformationEx().                      ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitExTopology:
push ebx esi edi
;---------- Blank buffer for load topological information at next step. -------;
lea edi,[TEMP_BUFFER + 8192]
push edi
mov ecx,VALUE_BUFFER_LIMIT / 4
xor eax,eax
rep stosd
pop edi
;---------- Get extended topological information, use WinAPI. -----------------;
call GetExtendedTopology
;--- Write buffer dump for ext. topological information, returned by WinAPI. --;
lea esi,[TEMP_BUFFER + 8192]
mov ebx,ecx
call DumpMemory
;---------- Done. -------------------------------------------------------------;
pop edi esi ebx
ret


;==============================================================================;
;                                                                              ;
;              Subroutines portable to JNI (Java Native Interface)             ;
;                         library of CPUID utility.                            ;
;          Accept ESI = optional input data array (or no input data).          ;
;                       IPB = Input Parameters Block.                          ;
;                 EDI = optional output data array (or no output data).        ;
;                       OPB = Output Parameters Block.                         ;
;                 EAX = optional output result or status info.                 ;
;                 CF flag = status.                                            ;
;                                                                              ;
;==============================================================================;


;---------- Get extended topology information by WinAPI. ----------------------;
;                                                                              ;
; INPUT:   ESI = Pointer to IPB (Input Parameters Block). Not used here.       ;
;          EDI = Pointer to OPB (Output Parameters Block).                     ;
;                                                                              ;
; OUTPUT:  OPB at [EDI] = Extended topology information, returned by WinAPI.   ;
;          ECX = Used buffer size.                                             ;
;          CF, AL = Reserved for status reporting.                             ;
;------------------------------------------------------------------------------;
; Get info with separate calls for 5 entries types (0-4).
; This required for ordering text strings in the report.
GetExtendedTopology:
push ebx esi ebp
push 0
mov ebp,esp
mov ebx,[_GetLogicalProcessorInformationEx]
test ebx,ebx
jz .exit
xor esi,esi
mov al,0                  ; 0 = RelationProcessorCore.
call HelperTopologyEx
jc .errorTopologyEx
mov al,2                  ; 2 = RelationCache. 
call HelperTopologyEx
jc .errorTopologyEx
mov al,3                  ; 3 = RelationProcessorPackage.
call HelperTopologyEx
jc .errorTopologyEx
mov al,1                  ; 1 = RelationNumaNode.
call HelperTopologyEx
jc .errorTopologyEx
mov al,4                  ; 4 = RelationGroup.
call HelperTopologyEx
.errorTopologyEx:
jc .error
clc
.exit:
pop ecx
mov ecx,esi
pop ebp esi ebx
ret
.error:
mov al,MSG_ERROR_TOPOLOGY_API
stc
jmp .exit

;---------- Get topological information. --------------------------------------;
;                                                                              ;
; INPUT:   EBX = WinAPI function address.                                      ;
;          EDI = Pointer to buffer for topology information.                   ;
;          ESI = Buffer used size accumulator.                                 ;
;          EBP = Pointer to temporary DWORD variable, located at stack frame.  ;
;          AL  = Relation type by WinAPI encoding.                             ;
;                                                                              ;
; OUTPUT:  ESI = Updated buffer size accumulator.                              ;
;                                                                              ;
;------------------------------------------------------------------------------;             
HelperTopologyEx:
push edi ebp eax
mov dword [ebp],0       ; dword [ebp] = Buffer size variable, stack.
push ebp                ; Parm#3 = Pointer to size.
lea edx,[edi + esi]
push edx                ; Parm#2 = Pointer to buffer.
movzx eax,al
push eax                ; Parm#1 = Relation type.
call ebx
test eax,eax              ; check EAX plus clear CF, required if return.
jnz .exit  ;  jnz .error  ; this variant means no data return, but no errors.
call [GetLastError]
cmp eax,ERROR_BUFFER_LIMIT
jne .error                   ; Go if other errors detected.
mov eax,[ebp]
cmp eax,VALUE_BUFFER_LIMIT
ja .error                    ; Go if required buffer size too big. 
lea edx,[edi + esi]
add esi,[ebp]
cmp esi,VALUE_BUFFER_LIMIT
ja .error                   ; Go if required buffer size too big. 
push ebp                    ; Parm#3 = Pointer to size.
push edx                    ; RDX = Parm#2 = Pointer to buffer.
movzx eax,byte [esp + 8]
push eax                    ; RCX = Parm#1 = Relation type.
call ebx
test eax,eax
jz .error
.exit:
pop eax ebp edi
ret
.error:
stc
jmp .exit
