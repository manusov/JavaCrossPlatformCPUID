;---------- Unit test handler: read CPU affinity mask by WinAPI. --------------;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitAffinity:
push ebx esi edi
lea edi,[TEMP_BUFFER + 8192]
mov esi,edi
call LoadSystemInfo
;---------- Write string: number of logical processors. -----------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea ecx,[MsgCpuCount]
call ConsoleWriteReport 
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea edi,[TEMP_BUFFER]
mov ecx,edi
mov eax,[esi + 0]
mov bl,0
call DecimalPrint32
mov ax,'.'
stosw
call ConsoleWriteReport
lea ecx,[CrLf]
call ConsoleWriteReport 
;---------- Write string: system affinity map. --------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea ecx,[MsgAffinityMask]
call ConsoleWriteReport 
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea edi,[TEMP_BUFFER]
mov ecx,edi
mov eax,[esi + 8]
call HexPrint32
mov ax,'h.'
stosw
mov al,0
stosb
call ConsoleWriteReport
lea ecx,[CrLf2]
call ConsoleWriteReport 
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


; See affinized CPUID support: LoadSystemInfo subroutine.
