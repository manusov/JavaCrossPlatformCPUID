;---------- Unit test handler: read platform extended topology. ---------------;
; This function is reserved (not used) at Linux edition.                       ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitExTopology:
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea ecx,[MsgWarningNoTop]
jmp ConsoleWriteReport 


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

; Reserved.
