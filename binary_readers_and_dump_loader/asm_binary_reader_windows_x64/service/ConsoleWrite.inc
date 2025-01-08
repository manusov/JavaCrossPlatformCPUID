;---------------------- Win64 console functions notes -------------------------;
; Used functions:
;
; GetStdHandle()
; Input:  Parm#1 = Handle type code for retrieve.
; Output: Handle, if error 0 or INVALID_HANDLE_VALUE (64-bit RAX). 
;
; WriteConsole()
; Input:  Parm#1 = Handle of output device.
;         Parm#2 = Pointer to buffer.
;         Parm#3 = Number of characters to write.
;         Parm#4 = Pointer to returned number of successfully chars write.
;         Parm#5 = Reserved parameters must be 0 (NULL).
; Output: Status, Nonzero=OK, 0=Error. 
;
; ReadConsole()
; Input:  Parm#1 = Handle of input device.
;         Parm#2 = Pointer to output buffer.
;         Parm#3 = Number of chars to read (limit, but not for edit).
;         Parm#4 = Pointer to returned number of CHARS read (before ENTER).
;         Parm#5 = Pointer to CONSOLE_READCONSOLE_CONTROL structure, 0=None.
; Output: Status, Nonzero=OK, 0=Error.
;
; ReadConsoleInput()
; Input:  Parm#1 = Handle of input device.
;         Parm#2 = Pointer to output buffer with INPUT_RECORD structure(s).
;         Parm#3 = Number of events to read.
;         Parm#4 = Pointer to returned number of EVENTS read.
; Output: Status, Nonzero=OK, 0=Error.
;
; ExitProcess()
; Input:  Parm#1 = Exit code for parent process.
; No output, because not return control to caller.
;
;------------------------------------------------------------------------------;

;---------- ASCII string write to console. ---------------------------------;
; Input / Output parameters and Volatile / Non volatile registers           ;
; compatible with Microsoft x64 calling convention                          ;
;                                                                           ;
; INPUT:   RCX = Pointer to 0-terminated ASCII string, string output        ; 
;                to console and optional to report file (if RDX non zero)   ;
;          RDX = Report file handle, used as report validity flag only,     ;
;                report file must be re-opened before write                 ;
;          R8  = Pointer to report file name and path,                      ;
;                0-terminated ASCII string                                  ;
;                                                                           ;
; OUTPUT:  RAX = OS Status                                                  ;
;---------------------------------------------------------------------------;
ConsoleWriteReport:      ; This special entry point not required input RDX, R8.
mov rdx,[ReportHandle]
mov r8,[ReportName]
ConsoleWrite:            ; This normal entry point required input RDX, R8.
;---------- Entry. ------------------------------------------------------------;
push rbx rsi rdi rbp r12
mov rbp,rsp                ; RBP = storage for RSP and pointer to frame.
push 0                     ; Scratch pad for output parameter =  write size.
and rsp,0FFFFFFFFFFFFFFF0h ; Align stack for call WinAPI by convention.
mov rbx,rdx                ; RBX = Non volatile copy of report handle.
mov r12,r8                 ; R12 = Non volatile copy of report path pointer.
;---------- Calculate string length. ------------------------------------------;
mov rdx,rcx                ; RDX = Parm#2 = Pointer to string ( buffer ).
xor r8d,r8d                ; R8  = Parm#3 = Number of chars ( length ).
@@:
cmp byte [rcx+r8],0        ; Check current char from string.
je @f                      ; Exit cycle if terminator (byte=0) found.
inc r8d                    ; Chars counter + 1.
jmp @b                     ; Go next iteration.
@@:
;---------- Save input parameters for usage for console and file both. --------;
mov rsi,rdx                ; RSI = Non volatile copy of buffer pointer. 
mov rdi,r8                 ; RDI = Non volatile copy if length.
;---------- Write console - optional. -----------------------------------------;
mov eax,1                  ; RAX = Status = OK, if display output disabled.
mov rcx,[StdOut]           ; RCX = Parm#1 = Input device handle.
jrcxz @f                   ; Skip console output if handle = 0.
lea r9,[rbp-8]             ; R9  = Parm#4 = Pointer to out. variable, count.
xor eax,eax                ; RAX = 0.
push rax rax               ; Align stack + Parm#5 (exist = null) = Reserved.
sub rsp,32                 ; Create parameters shadow.
call [WriteFile]
add rsp,32+16            ; Remove parameters shadow, parm#5, stack align.
@@:
;---------- Check criteria for write report file - optional. ------------------;
mov eax,1               ; RAX = Status = OK, if report save disabled.
test rbx,rbx            ; RBX = Report temp. handle used as flag.
jz .exit                ; Skip file output if handle = 0.
cmp rbx,INVALID_HANDLE_VALUE
je .exit                ; Skip file output if handle = Invalid = -1.
mov rcx,r12             ; RCX = Parm #1 = Pointer to name string.
test rcx,rcx
jz .exit                ; Skip file output if name pointer = 0.
;---------- Open. -------------------------------------------------------------;
mov edx,GENERIC_WRITE   ; RDX = Parm #2 = Desired access. 
xor r8d,r8d             ; R8  = Parm #3 = Share mode, not used.
xor r9d,r9d             ; R9  = Parm #4 = Security attributes, not used.
xor eax,eax
push rax                    ; This push for stack alignment.
push rax                    ; Parm #7 = Template file, not used.
push FILE_ATTRIBUTE_NORMAL  ; Parm #6 = File attributes.
push OPEN_EXISTING          ; Parm #5 = Creation disposition.
sub rsp,32                  ; Create parameters shadow.
call [CreateFileA]
add rsp,32+32           ; Remove parameters shadow and parameters.
test rax,rax
jz .exit                ; Go if open file error.
mov rbx,rax
;---------- Positioning pointer to end of file. -------------------------------;
xchg rcx,rax            ; RCX = Parm #1 = File handle.
xor edx,edx             ; RDX = Parm #2 = Position, low dword.
xor r8d,r8d             ; R8  = Parm #3 = Position, high dword.
mov r9d,FILE_END        ; R9  = Parm #4 = Move method.
sub rsp,32
call [SetFilePointer]
add rsp,32
;---------- Write. ------------------------------------------------------------;
.write:
mov rcx,rbx             ; RCX = Parm#1 = File handle.
mov rdx,rsi             ; RDX = Parm#2 = Pointer to string ( buffer ). 
mov r8,rdi              ; R8  = Parm#3 = Number of chars ( length ). 
xor eax,eax             ; RAX = 0.
push rax                ; This space for output variable plus stack align.
mov r9,rsp              ; R9  = Parm#4 = Pointer to out. variable, count.
push rax                ; Parm#5 (exist = null) = Reserved.
sub rsp,32              ; Create parameters shadow.
call [WriteFile]
add rsp,32+8            ; Remove parameters shadow, parm#5.
pop rcx                 ; RCX = Returned size.
test rax,rax            ; RAX = status, 0 means error.
jz .close               ; Go exit if error.
jrcxz .close            ; Go exit if returned size = 0.
add rsi,rcx             ; RSI = advance read pointer by returned size.
sub rdi,rcx             ; RDI = subtract current read size from size limit.
ja .write               ; Repeat read if return size > 0 and limit not reached. 
;---------- Close. ------------------------------------------------------------;
.close:
mov rcx,rbx
jrcxz .exit
sub rsp,32
call [CloseHandle]       ; Close report file after write.
;---------- Exit point, RAX = Status actual here. -----------------------------;
.exit:
mov rsp,rbp   ; This for restore after alignment and also instead ADD RSP,32.
pop r12 rbp rdi rsi rbx
ret
