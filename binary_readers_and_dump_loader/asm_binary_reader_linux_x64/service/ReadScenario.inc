;---------- Read scenario file. -----------------------------------------------;
;                                                                              ;
; INPUT: RCX = Pointer to scenario file path and name,                         ;
;              0-terminated ASCII string.                                      ;
;        RDX = Pointer to scenario handle.                                     ;
;        R8  = Pointer to loaded scenario base address variable,               ;
;              this variable is buffer base address for file read.             ;
;        R9  = Pointer to scenario size variable,                              ;
;              this variable is size limit for this buffer.                    ;
;                                                                              ;
; OUTPUT: RAX = OS API last operation status code.                             ;
;         Variable scenario handle at [input RDX] = updated by file open.      ;
;         Variable scenario size at [input R9] = Read size, 0 if error.        ;
;------------------------------------------------------------------------------;
ReadScenario:
push rbp
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h                  ; Stack alignment by calling convention.
push rbx rbx rsi rdi rbp r12 r13 r14 r8 r9  ; Number of pushes here must be even, for stack alignment.
                                            ; push rbx twice because alignment required.
mov rbp,rsp
mov rbx,rdx               ; RBX = non volatile pointer to scenario handle.
;--- Open file, by input parameters: RCX = Parm #1 = Pointer to file name. ----;
xor eax,eax               ; RAX = 0 for store result = 0 if ScenarioName = 0.
jrcxz .L2                 ; Skip operation if file name pointer = 0.
mov rdi,rcx               ; RDI = Parm#1 = Pointer to source file path.
mov esi,FILE_FLAGS_BLANK  ; RSI = Parm#2 = File operation mode flags.
mov edx,FILE_SHARE_MODE   ; RDX = Parm#3 = File access read-write mode.
mov eax,SYS_OPEN          ; RAX = Function code, open file
syscall
mov [rbx],rax           ; Save scenario file handle.
;---------- Initializing for read file. ---------------------------------------;
xor r12,r12             ; R12 = 0, clear read size counter.
mov rax,[rbp + 8]
mov r13,[rax]           ; R13 = Base address of memory buffer.
mov rax,[rbp + 0]       
mov r14,[rax]           ; R14 = Size limit of memory buffer.
;---------- Read file. --------------------------------------------------------;
.L10:
mov rdi,[rbx]           ; Parm#1 = RDI = Handle of storage device.
test rdi,rdi
jz .L2                  ; Go skip close if handle = 0.
js .L2                  ; Go skip close if handle < 0. 
mov rsi,r13             ; Parm#2 = RSI = Pointer to buffer.
mov rdx,r14             ; Parm#3 = RDX = Number of characters to write.
mov eax,SYS_READ        ; RAX = Function code = Read file.
syscall
;---------- Analusing read results. -------------------------------------------;
test rax,rax
jz .L3                  ; Go normal read termination if returned size = 0.
js .L2                  ; Go error if OS status < 0.
add r12,rax             ; R12 = accumulate read size.
add r13,rax             ; R13 = advance read pointer by returned size.
sub r14,rax             ; R14 = subtract current read size from size limit. 
ja .L10                 ; Repeat read if return size > 0 and limit not reached. 
jb .L2                  ; Error if read size > size limit.
;---------- Write result size. ------------------------------------------------;
.L3:
mov rax,[rbp + 0]
mov [rax],r12           ; Write scenario size = file size if read OK. 
jmp .L1
.L2:
mov rax,[rbp + 0]
mov qword [rax],0       ; Write scenario size = 0 if read error.
.L1:
;---------- Close file. -------------------------------------------------------;
xor eax,eax             ; RAX = Status, make 0 if handle invalid.
mov rdi,[rbx]           ; RDI = Parm#1 = Source file descriptor, handle.
test rdi,rdi
jz .L0                  ; Go skip close if handle = 0.
js .L0                  ; Go skip close if handle < 0.
mov eax,SYS_CLOSE
syscall
test rax,rax
setz al                 ; RAX = make status = 1 if no errors ( RAX = 0 ).
.L0:
;---------- Exit. -------------------------------------------------------------;
pop rcx rcx r14 r13 r12 rbp rdi rsi rbx rbx
leave                   ; Remove stack alignment.
ret


