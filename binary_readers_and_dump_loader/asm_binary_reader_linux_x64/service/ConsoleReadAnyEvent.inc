;---------------------- Linux64 console input/output notes. -------------------;
; Used functions:
; 
; write(), Function code = EAX = SYS_WRITE = 1.
; Input:  Parm#1 = RDI = Handle of STDOUT device, typical = 1.
;         Parm#2 = RSI = Pointer to buffer.
;         Parm#3 = RDX = Number of characters to write.
; Output: Status = RAX, >0 chars count, OK, 0 = No data, Negative = Error.
;
; read(), Function code = EAX = SYS_READ = 0.
; Input:  Parm#1 = RDI = Handle of STDIN device, typical = 2.
;         Parm#2 = RSI = Pointer to buffer.
;         Parm#3 = RDX = Number of characters to write.
; Output: Status = RAX, >0 chars count, OK, 0 = No data, Negative = Error.
;
; exit(), Function code = EAX = SYS_EXIT = 60.
; Input:  Parm#1 = EDI = Exit code for parent process, low byte only used.
; No output, because not return control to caller.
;
;------------------------------------------------------------------------------;

;---------- Wait for press any key. (Wait for press CHARACTER key). -----------;
;                                                                              ;
; Used simplified variant of read(), Number of chars to Read can be = 1,       ;
; but buffer used for extra input support and clear.                           ;
; Echo disabled.                                                               ;
;                                                                              ;
; INPUT:  RCX = Pointer to output buffer, for single char in this variant.     ;
;         EDX = Buffer size limit.                                             ;
;                                                                              ;
; OUTPUT: RAX = Status.                                                        ;
;         Buffer at [input RCX] updated.                                       ;
;------------------------------------------------------------------------------;
ConsoleReadAnyEvent:
; ...
; UNDER CONSTRUCTION.
; ...
jmp ConsoleReadChar


