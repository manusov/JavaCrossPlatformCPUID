;---------- Create report file. -----------------------------------------------;
;                                                                              ;
; After this function successfully call, function ConsoleWrite                 ;
; starts save output information to report file.                               ;
;                                                                              ;
; INPUT:  RCX = Pointer to report file name, 0-terminated ASCII string.        ;
;         RDX = Pointer to report file handle, return handle = 0 if error.     ;  
;                                                                              ;
; OUTPUT: RAX = Status code                                                    ;
;               Variable report handle at [input RCX] =                        ; 
;               Temporary handle, used as flag for write report file enable.   ;
;------------------------------------------------------------------------------;
CreateReport:
push rbp
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h  ; Stack alignment by calling convention.
push rbx rsi rdi rbp        ; Number of pushes here must be even, for stack alignment.
mov rbx,rdx                 ; RBX = Non volatile copy of handle pointer. 
;--- Create file, input parameter RCX = Parm #1 = Pointer to file name. -------;
xor eax,eax               ; RAX = 0 for store result = 0 if ScenarioName = 0.
jrcxz .L1                 ; Skip operation if file name pointer = 0.
mov rdi,rcx               ; RDI = Parm#1 = Pointer to source file path.
mov esi,FILE_FLAGS_BLANK + FILE_FLAG_TRUNCATE  ; RSI = Parm#2 = File operation mode flags.
mov edx,FILE_SHARE_MODE   ; RDX = Parm#3 = File access read-write mode.
mov eax,SYS_OPEN          ; RAX = Function code, open file.
syscall
mov [rbx],rax          ; Save scenario file handle.
.L1:
;---------- Store result. -----------------------------------------------------;
mov [rbx],rax          ; RAX = Returned handle.
;---------- Close file. -------------------------------------------------------;
xor eax,eax            ; RAX = Status, make 0 if handle invalid.
mov rdi,[rbx]          ; RDI = Parm#1 = Source file descriptor, handle.
test rdi,rdi
jz .L0                 ; Go skip close if handle = 0.
js .L0                 ; Go skip close if handle < 0.
mov eax,SYS_CLOSE
syscall
test rax,rax
setz al                ; RAX = make status = 1 if no errors (RAX=0).
.L0:
;---------- Exit. -------------------------------------------------------------;
pop rbp rdi rsi rbx
leave                  ; Remove stack alignment.
ret




