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
;         Buffer at [input ECX] updated.                                       ;
;------------------------------------------------------------------------------;
ConsoleReadChar:
push ebx esi edi ebp
;---------- Pre-load and check OS API parameters. -----------------------------;
mov edi,ecx         ; EDI = Buffer pointer backup.
mov esi,edx         ; ESI = Buffer size limit.
mov ebp,[StdIn]     ; EBP = Device = STDIN.
test ebp,ebp            
jz .L0              ; Go skip console write if handle = 0.
;---------- Disable terminal echo. --------------------------------------------;
call DisableEcho
push ecx
;---------- Read char. --------------------------------------------------------;
mov ebx,ebp         ; Parm#1 = EBX = Device = STDIN.
mov ecx,edi         ; Parm#2 = ECX = Pointer to message string.
mov edx,esi         ; Parm#3 = EDX = Length.
mov eax,SYS_READ    ; EAX = Linux API function (syscall number).
int 80h
;---------- Restore terminal options after echo disable. ----------------------;
pop ecx
call RestoreEcho
;---------- Done. -------------------------------------------------------------;
.L0:
pop ebp edi esi ebx
ret


