;---------- Unit test handler: detect WoW. ------------------------------------;
; Note. Reserved (not used) at Linux version.                                  ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitWoW64:
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea ecx,[MsgWarningWoW64]
jmp ConsoleWriteReport 
