;---------- Unit test handler: read platform topology. ------------------------;
; This function is reserved (not used) at Linux edition.                       ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitTopology:
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea rcx,[MsgWarningNoTop]
jmp ConsoleWriteReport 


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

;---------- Local used equations. ---------------------------------------------;

VALUE_BUFFER_LIMIT      = 16384 

; Reserved.
