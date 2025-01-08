;---------- Unit test handler: detect WoW. ------------------------------------;
; This function is reserved (not used) at Linux edition.                       ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitWoW64:
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea rcx,[MsgWarningWoW64]
jmp ConsoleWriteReport 
