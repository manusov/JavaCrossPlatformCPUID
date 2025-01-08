;---------- Unit test handler: read platform extended topology by WinAPI. -----;
; Use WinAPI function GetLogicalProcessorInformationEx().                      ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitExTopology:
push rbx rsi rdi
;---------- Blank buffer for load topological information at next step. -------;
lea rdi,[TEMP_BUFFER + 8192]
push rdi
mov ecx,VALUE_BUFFER_LIMIT / 4
xor eax,eax
rep stosd
pop rdi
;---------- Get extended topological information, use WinAPI. -----------------;
call GetExtendedTopology
;--- Write buffer dump for ext. topological information, returned by WinAPI. --;
lea rsi,[TEMP_BUFFER + 8192]
mov ebx,ecx
call DumpMemory
;---------- Done. -------------------------------------------------------------;
pop rdi rsi rbx
ret


;==============================================================================;
;                                                                              ;
;              Subroutines portable to JNI (Java Native Interface)             ;
;                         library of CPUID utility.                            ;
;          Accept RSI = optional input data array (or no input data).          ;
;                       IPB = Input Parameters Block.                          ;
;                 RDI = optional output data array (or no output data).        ;
;                       OPB = Output Parameters Block.                         ;
;                 RAX = optional output result or status info.                 ;
;                 CF flag = status.                                            ;
;                                                                              ;
;==============================================================================;


; TODO. Optimize usage RBP, RDI, RSI, R13.
; TODO. Bug with R12 corruption, 
; verify all PUSH/POP, parameter shadow and stack alignment.


;---------- Get extended topology information by WinAPI. ----------------------;
;                                                                              ;
; INPUT:   RSI = Pointer to IPB (Input Parameters Block). Not used here.       ;
;          RDI = Pointer to OPB (Output Parameters Block).                     ;
;                                                                              ;
; OUTPUT:  OPB at [RDI] = Extended topology information, returned by WinAPI.   ;
;          ECX = Used buffer size.                                             ;
;          CF, AL = Reserved for status reporting.                             ;
;------------------------------------------------------------------------------;
; Get info with separate calls for 5 entries types (0-4).
; This required for ordering text strings in the report.
GetExtendedTopology:
push rbx r13 r14
push 0
mov r13,rsp
mov rbx,[_GetLogicalProcessorInformationEx]
test rbx,rbx
jz .exit
xor r14d,r14d
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
pop rcx
mov ecx,r14d
pop r14 r13 rbx
ret
.error:
mov al,MSG_ERROR_TOPOLOGY_API
stc
jmp .exit

;---------- Get topological information. --------------------------------------;
;                                                                              ;
; INPUT:   RBX = WinAPI function address.                                      ;
;          RBP = Pointer to buffer for topology information.                   ;
;          R14 = Buffer used size accumulator.                                 ;
;          R13 = Pointer to temporary DWORD variable, located at stack frame.  ;
;          AL  = Relation type by WinAPI encoding.                             ;
;                                                                              ;
; OUTPUT:  R14 = Updated buffer size accumulator.                              ;
;                                                                              ;
;------------------------------------------------------------------------------;             
HelperTopologyEx:
push rsi rdi rbp
mov rbp,rdi                 ; TODO. Optimize usage RBP, RDI, RSI, R13.
mov rsi,rsp
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32
movzx edi,al
mov dword [r13],0            ; dword [r13]  = Buffer size variable, stack.
mov r8,r13                   ; R8  = Parm#3 = Pointer to size.
lea rdx,[rbp + r14]          ; RDX = Parm#2 = Pointer to buffer.
mov ecx,edi                  ; RCX = Parm#1 = Relation type.
call rbx
test rax,rax              ; check RAX plus clear CF, required if return.
jnz .exit  ;  jnz .error  ; this variant means no data return, but no errors.
call [GetLastError]
cmp rax,ERROR_BUFFER_LIMIT
jne .error                   ; Go if other errors detected.
mov r12d,[r13]
cmp r12d,VALUE_BUFFER_LIMIT
ja .error                    ; Go if required buffer size too big.
mov r8,r13                   ; R8  = Parm#3 = Pointer to size.
lea rdx,[rbp + r14]          ; RDX = Parm#2 = Pointer to buffer.
mov ecx,edi                  ; RCX = Parm#1 = Relation type.
add r14d,r12d
cmp r14d,VALUE_BUFFER_LIMIT
ja .error                    ; Go if required buffer size too big.
call rbx
test rax,rax
jz .error
.exit:
mov rsp,rsi
pop rbp rdi rsi
ret
.error:
stc
jmp .exit
