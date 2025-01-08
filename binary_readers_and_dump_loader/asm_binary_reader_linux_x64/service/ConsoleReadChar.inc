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
ConsoleReadChar:
push rbp
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h    ; Stack alignment by calling convention.
push rbx rbp rsi rdi r12 r13  ; Number of pushes here must be even, for stack alignment.
;---------- Pre-load and check OS API parameters. -----------------------------;
mov r12,rcx               ; R12 = Buffer pointer backup.
mov r13d,edx              ; R13 = Buffer size limit, bits [63-32] cleared.
mov rbp,[StdIn]           ; RBP = Device = STDIN.
test rbp,rbp            
jz .L0                    ; Go skip console write if handle = 0.
;---------- Disable terminal echo. --------------------------------------------;
call DisableEcho
mov ebx,ecx
;---------- Write string to console, conditional, if handle not zero. ---------;
mov rdi,rbp               ; Parm#1 = RDI = Device = STDIN.
mov rsi,r12               ; Parm#2 = RSI = Pointer to message string.
mov rdx,r13               ; Parm#3 = RDX = Length.
mov eax,SYS_READ          ; EAX = Linux API function (syscall number).
syscall
;---------- Restore terminal options after echo disable. ----------------------;
mov ecx,ebx
call RestoreEcho
;---------- Done. -------------------------------------------------------------;
.L0:
pop r13 r12 rdi rsi rbp rbx
leave                     ; Remove stack alignment.
ret
