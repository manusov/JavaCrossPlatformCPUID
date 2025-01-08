;---------- Unit test handler: read platform topology by WinAPI. --------------;
; Use WinAPI function GetLogicalProcessorInformation().                        ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitTopology:
push rbx rsi rdi
;---------- Blank buffer for load topological information at next step. -------;
lea rdi,[TEMP_BUFFER + 8192]
push rdi
mov ecx,VALUE_BUFFER_LIMIT / 4
xor eax,eax
rep stosd
pop rdi
;---------- Get topological information, use WinAPI. --------------------------;
call GetTopology
;--- Write buffer dump for topological information, returned by WinAPI. -------;
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
; TODO. Verify all PUSH/POP, parameter shadow and stack alignment.


;---------- Local used equations. ---------------------------------------------;

MSG_ERROR_TOPOLOGY_API  = 6 
ERROR_BUFFER_LIMIT      = 07Ah
VALUE_BUFFER_LIMIT      = 16384 

;---------- Get topology information by WinAPI. -------------------------------;
;                                                                              ;
; INPUT:   RSI = Pointer to IPB (Input Parameters Block). Not used here.       ;
;          RDI = Pointer to OPB (Output Parameters Block).                     ;
;                                                                              ;
; OUTPUT:  OPB at [RDI] = Topology information, returned by WinAPI.            ;
;          ECX = Used buffer size.                                             ;
;          CF, AL = Reserved for status reporting.                             ;
;------------------------------------------------------------------------------;
GetTopology:
push rbx r13 r14             ; TODO. Optimize usage RBP, RDI, RSI, R13.
push 0
mov r13,rsp
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32
mov rbx,[_GetLogicalProcessorInformation]
test rbx,rbx
jz .exit
mov dword [r13],0            ; dword [r13]  = Buffer size variable, stack.
mov rdx,r13                  ; RDX = Parm#2 = Pointer to buffer size variable. 
mov rcx,rdi                  ; RCX = Parm#1 = Pointer to buffer. 
call rbx
test rax,rax
jnz .error                   ; Go if unexpected error: zero-sized buffer.
call [GetLastError]
cmp rax,ERROR_BUFFER_LIMIT
jne .error                   ; Go if other errors detected.
mov r14d,[r13]
cmp r14d,VALUE_BUFFER_LIMIT
ja .error                    ; Go if required buffer size too big. 
mov rdx,r13                  ; RDX = Parm#2 = Pointer to buffer size variable. 
mov rcx,rdi                  ; RCX = Parm#1 = Pointer to buffer. 
call rbx
test rax,rax
jz .error          ; Go if unexpected error: zero-sized buffer.
clc
.exit:
mov rsp,r13
pop rcx            ; This POP for remove stack variable.
pop r14 r13 rbx
ret
.error:
mov al,MSG_ERROR_TOPOLOGY_API
stc
jmp .exit 
