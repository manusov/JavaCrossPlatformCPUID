;---------------------- Linux32 console input/output notes. -------------------;
; Used functions:
; 
; write(), Function code = EAX = SYS_WRITE = 4.
; Input:  Parm#1 = EBX = Handle of STDOUT device, typical = 1.
;         Parm#2 = ECX = Pointer to buffer.
;         Parm#3 = EDX = Number of characters to write.
; Output: Status = EAX, >0 chars count, OK, 0 = No data, Negative = Error.
;
; read(), Function code = EAX = SYS_READ = 3.
; Input:  Parm#1 = EBX = Handle of STDIN device, typical = 2.
;         Parm#2 = ECX = Pointer to buffer.
;         Parm#3 = EDX = Number of characters to write.
; Output: Status = EAX, >0 chars count, OK, 0 = No data, Negative = Error.
;
; exit(), Function code = EAX = SYS_EXIT = 1.
; Input:  Parm#1 = EBX = Exit code for parent process, low byte only used.
; No output, because not return control to caller.
;
;------------------------------------------------------------------------------;

;---------- Wait for press any key. (Wait for press CHARACTER key). -----------;
;                                                                              ;
; Used simplified variant of read(), Number of chars to Read can be = 1,       ;
; but buffer used for extra input support and clear.                           ;
; Echo disabled.                                                               ;
;                                                                              ;
; INPUT:  ECX = Pointer to output buffer, for single char in this variant.     ;
;         EDX = Buffer size limit.                                             ;
;                                                                              ;
; OUTPUT: EAX = Status.                                                        ;
;         Buffer at [input RCX] updated.                                       ;
;------------------------------------------------------------------------------;
ConsoleReadAnyEvent:
; ... UNDER CONSTRUCTION ...
jmp ConsoleReadChar

