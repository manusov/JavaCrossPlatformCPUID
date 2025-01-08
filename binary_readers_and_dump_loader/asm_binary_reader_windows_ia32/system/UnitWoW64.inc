;---------- Unit test handler: detect WoW. ------------------------------------;
; Note. Reserved for use in the ia32 application, not used by this x64         ;
; x64 application. WoW64 mode can be used for ia32 application only.           ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitWoW64:
call [GetCurrentProcess]
test eax,eax
jz .failed
push 0
mov ecx,esp
push ecx    ; Parm#2 = Pointer to flag.
push eax    ; Parm#1 = Process handle.
call [_IsWow64Process]
test eax,eax
pop eax
jz .failed
push eax
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
pop eax
lea ecx,[MsgYesWoW64]
test eax,eax
jnz .write
lea ecx,[MsgNoWoW64]
jmp .write
.failed:
lea ecx,[MsgErrorWoW64]
.write:
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
 