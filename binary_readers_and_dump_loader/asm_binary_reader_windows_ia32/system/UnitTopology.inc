;---------- Unit test handler: read platform topology by WinAPI. --------------;
; Use WinAPI function GetLogicalProcessorInformation().                        ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitTopology:
push ebx esi edi
;---------- Blank buffer for load topological information at next step. -------;
lea edi,[TEMP_BUFFER + 8192]
push edi
mov ecx,VALUE_BUFFER_LIMIT / 4
xor eax,eax
rep stosd
pop edi
;---------- Get topological information, use WinAPI. --------------------------;
call GetTopology
;--- Write buffer dump for topological information, returned by WinAPI. -------;
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


;---------- Local used equations. ---------------------------------------------;

MSG_ERROR_TOPOLOGY_API  = 6 
ERROR_BUFFER_LIMIT      = 07Ah
VALUE_BUFFER_LIMIT      = 16384 

;---------- Get topology information by WinAPI. -------------------------------;
;                                                                              ;
; INPUT:   ESI = Pointer to IPB (Input Parameters Block). Not used here.       ;
;          EDI = Pointer to OPB (Output Parameters Block).                     ;
;                                                                              ;
; OUTPUT:  OPB at [EDI] = Topology information, returned by WinAPI.            ;
;          ECX = Used buffer size.                                             ;
;          CF, AL = Reserved for status reporting.                             ;
;------------------------------------------------------------------------------;
GetTopology:
push ebx esi ebp    ; TODO. Optimize usage EBP, EDI, ESI.
push 0
mov ebp,esp
mov ebx,[_GetLogicalProcessorInformation]
test ebx,ebx
jz .exit
mov dword [ebp],0   ; dword [ebp]  = Buffer size variable, stack.
push ebp            ; Parm#2 = Pointer to buffer size variable. 
push edi            ; Parm#1 = Pointer to buffer. 
call ebx
test eax,eax
jnz .error          ; Go if unexpected error: zero-sized buffer.
call [GetLastError]
cmp eax,ERROR_BUFFER_LIMIT
jne .error          ; Go if other errors detected.
mov esi,[ebp]
cmp esi,VALUE_BUFFER_LIMIT
ja .error           ; Go if required buffer size too big. 
push ebp            ; Parm#2 = Pointer to buffer size variable. 
push edi            ; Parm#1 = Pointer to buffer. 
call ebx
test eax,eax
jz .error           ; Go if unexpected error: zero-sized buffer.
clc
.exit:
pop ecx             ; This POP for remove stack variable.
pop ebp esi ebx
ret
.error:
mov al,MSG_ERROR_TOPOLOGY_API
stc
jmp .exit 
