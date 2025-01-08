;---------- Unit test handler: detect WoW. ------------------------------------;
; Note. Reserved for use in the ia32 application, not used by this             ;
; x64 application. WoW64 mode can be used for ia32 application only.           ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitWoW64:
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea rcx,[MsgWarningWoW64]
jmp ConsoleWriteReport 

 