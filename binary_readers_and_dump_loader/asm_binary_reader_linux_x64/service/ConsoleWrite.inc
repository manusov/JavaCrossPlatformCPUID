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

;---------- String write to console, in ASCII. --------------------------------;
;                                                                              ;
; INPUT:   RCX = Pointer to 0-terminated ASCII string, string output           ; 
;                to console and optional to report file (if RDX non zero).     ;
;          RDX = Report file handle, used as report validity flag only,        ;
;                report file must be re-opened before write.                   ;
;          R8  = Pointer to report file name and path,                         ;
;                0-terminated ASCII string.                                    ;
;                                                                              ;
; OUTPUT:  RAX = OS Status.                                                    ;
;------------------------------------------------------------------------------;
ConsoleWriteReport:
mov rdx,[ReportHandle]
mov r8,[ReportName]
ConsoleWrite:
push rbp
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h    ; Stack alignment by calling convention.
push rbx rsi rdi rbp r12 r13  ; Number of pushes here must be even, for stack alignment.
mov rbx,rcx            ; RBX = Save pointer to string.
mov rbp,rdx            ; RBP = Save report file handle.
mov r13,r8             ; R13 = Save pointer to report file name and path.
;---------- Calculate string length. ------------------------------------------;
xor r12,r12            ; R12  = Number of chars ( length ).
@@:
cmp byte [rbx+r12],0   ; Check current char from string.
je @f                  ; Exit cycle if terminator (byte=0) found.
inc r12                ; Chars counter + 1.
jmp @b                 ; Go next iteration.
@@:
;---------- Write string to console, conditional, if handle not zero. ---------;
mov rdi,[StdOut]       ; Parm#1 = RDI = Device = STDOUT.
test rdi,rdi
jz @f                  ; Go skip console write if handle = 0.
mov rsi,rbx            ; Parm#2 = RSI = Pointer to message string.
mov rdx,r12            ; Parm#3 = RDX = Length.
mov eax,SYS_WRITE      ; EAX = Linux API function (syscall number).
syscall
@@:
;---------- Check criteria for write report file - optional. ------------------;
mov eax,1              ; RAX = Status = OK, if report save disabled.
test rbp,rbp           ; RBP = Report temp. handle used as flag.
jz .Exit               ; Skip file output if handle = 0.
js .Exit               ; Skip file output if handle = Invalid < 0.
mov rdi,r13            ; RDI = Parm #1 = Pointer to name string.
test rdi,rdi
jz .Exit               ; Skip file output if name pointer = 0.
;---------- Open. -------------------------------------------------------------;
mov esi,FILE_FLAGS_BLANK ; RSI = Parm#2 = File operation mode flags.
mov edx,FILE_SHARE_MODE  ; RDX = Parm#3 = File access read-write mode.
mov eax,SYS_OPEN         ; RAX = Function code, open file.
syscall
test rax,rax
jz .Exit               ; Go if open file error.
js .Exit
mov rbp,rax            ; RBP = Now used handle.
;---------- Positioning pointer to end of file. -------------------------------;
mov rdi,rbp            ; RDI = Parm#1 = File descriptor, handle.
xor esi,esi            ; RSI = Parm#2 = File offset = 0.
mov edx,2              ; RDX = Parm#3 = Whence, 2 means SEEK_END = from file begin.
mov eax,SYS_LSEEK      ; RAX = Function code = seek.
syscall
test rax,rax           ; Check RAX = Status.
js .L11                ; Go if error seek file.
;---------- Write. ------------------------------------------------------------;
.L10:
mov rdi,rbp            ; Parm#1 = RDI = File handle.
test rdi,rdi
jz @f                  ; Go skip console write if handle = 0.
mov rsi,rbx            ; Parm#2 = RSI = Pointer to message string.
mov rdx,r12            ; Parm#3 = RDX = Length.
mov eax,SYS_WRITE      ; EAX = Linux API function (syscall number).
syscall
@@:
test rax,rax           ; RAX = write size (if >= 0) of status (if < 0).
jz .L0                 ; Go exit if returned size = 0.
js .L0                 ; Go exit if error.
add rbx,rax            ; RSI = advance read pointer by returned size.
sub r12,rax            ; RDI = subtract current read size from size limit.
ja .L10                ; Repeat read if return size > 0 and limit not reached. 
;---------- Close file. -------------------------------------------------------;
.L11:
xor eax,eax            ; RAX = Status, make 0 if handle invalid.
mov rdi,rbp            ; RDI = Parm#1 = File descriptor, handle.
test rdi,rdi
jz .L0                 ; Go skip close if handle = 0.
js .L0                 ; Go skip close if handle < 0.
mov eax,SYS_CLOSE
syscall
test rax,rax
setz al                ; RAX = make status = 1 if no errors (RAX=0).
.L0:
;---------- Exit point, RAX = Status actual here. -----------------------------;
.Exit:
pop r13 r12 rbp rdi rsi rbx
leave                  ; Remove stack alignment.
ret
