;---------- Unit test handler: read CPU affinity mask by Linux API. -----------;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitAffinity:
push rbx rsi rdi
lea rdi,[TEMP_BUFFER + 8192]
mov rsi,rdi
call LoadSystemInfo
;---------- Write string: number of logical processors. -----------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea rcx,[MsgCpuCount]
call ConsoleWriteReport 
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea rdi,[TEMP_BUFFER]
mov rcx,rdi
mov eax,[rsi + 0]
mov bl,0
call DecimalPrint32
mov ax,'.'
stosw
call ConsoleWriteReport
lea rcx,[CrLf]
call ConsoleWriteReport 
;---------- Write string: system affinity map. --------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea rcx,[MsgAffinityMask]
call ConsoleWriteReport 
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea rdi,[TEMP_BUFFER]
mov rcx,rdi
mov rax,[rsi + 8]
call HexPrint64
mov ax,'h.'
stosw
mov al,0
stosb
call ConsoleWriteReport
lea rcx,[CrLf2]
call ConsoleWriteReport 
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

; See affinized CPUID support: LoadSystemInfo subroutine.

