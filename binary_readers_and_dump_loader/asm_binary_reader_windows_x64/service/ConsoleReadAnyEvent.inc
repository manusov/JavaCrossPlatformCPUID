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

;---------- Wait for press ANY key. ----------------------------------------;
; Input / Output parameters and Volatile / Non volatile registers,          ;
; compatible with Microsoft x64 calling convention.                         ;
; Echo and edit string mode disabled.                                       ;
; Used simplified variant of [ReadConsole], Number of chars to Read = 1.    ;
;                                                                           ;
; INPUT:  RCX = Pointer to output buffer,                                   ; 
;               for single structure INPUT_RECORD in this variant.          ;
;                                                                           ;
; OUTPUT: RAX = Status                                                      ;
;         Buffer at [input RCX] updated.                                    ;
;---------------------------------------------------------------------------;
ConsoleReadAnyEvent:
push rbx rsi rbp rax rax    ; RBX, RSI, RBP = non-volatile, RAX = for storage.
mov rbp,rsp                 ; RBP = storage for RSP and pointer to frame.
and rsp,0FFFFFFFFFFFFFFF0h  ; Align stack for call WinAPI by convention.
sub rsp,32                  ; Create parameters shadow.
mov rbx,[StdIn]             ; RBX = Storage for input device handle.
mov rsi,rcx                 ; RSI = Non volatile copy of pointer.
;--- Exit with status = OK if input handle=0, wait key disabled by options. ---;
mov eax,1                   ; RAX = Status = OK, if wait key disabled.
test rbx,rbx                ; RBX = Input handle.
jz .exit                    ; Skip keyboard input if handle = 0.
;---------- Get current console mode. -----------------------------------------;
mov rcx,rbx                 ; RCX = Parm#1 = Input device handle.
mov rdx,rbp                 ; RDX = Parm#2 = Pointer to output variable. 
call [GetConsoleMode]       ; Get current console mode.
test rax,rax                ; RAX = Status, 0 if error.
jz .exit                    ; Go exit function if error.
;---------- Change current console mode. --------------------------------------;
mov rcx,rbx                 ; RCX = Parm#1 = Input device handle.
mov edx,[rbp]               ; RDX = Parm#2 = Console mode.
and dl,DISABLE_ECHO_ALL     ; Disable echo and string in. (ret. after 1 char).
call [SetConsoleMode]       ; Get current console mode.
test rax,rax                ; RAX = Status, 0 if error.
jz .exit                    ; Go exit function if error.

;---------- Read console (wait only without echo). ----------------------------;
@@:
mov rcx,rbx                  ; RCX = Parm#1 = Input device handle
mov rdx,rsi                  ; RDX = Parm#2 = Pointer to input buffer. 
mov r8d,1                    ; R8  = Parm#3 = Number of INPUT_RECORDs to Read. 
lea r9,[rsi + sizeof.INPUT_RECORD_KEY_EVENT] ; R9 = Parm#4 = Pointer to count. 
xor eax,eax
mov [rdx],rax
mov [rdx + 8],rax
mov [rdx + 16],eax
mov [r9],eax
call [ReadConsoleInput]
cmp [rsi + INPUT_RECORD_KEY_EVENT.EventType],KEY_EVENT
jne @b         ; Go repeat input if not a key event.
cmp [rsi + INPUT_RECORD_KEY_EVENT.bKeyDown],0
je @b          ; Go repeat input if not a press event (maybe key release).

;---------- Restore current console mode. -------------------------------------;
; Use parameters shadow created at subroutine entry.
mov rcx,rbx                 ; RCX = Parm#1 = Input device handle.
xchg rbx,rax                ; RBX = Save error code after input char.
mov edx,[rbp]               ; RDX = Parm#2 = Console mode.
call [SetConsoleMode]       ; Set current console mode.
;---------- Error code = F( restore, input ). ---------------------------------;
test rbx,rbx                ; Check status after console input. 
setnz bl                    ; BL=0 if input error, BL=1 if input OK.
test rax,rax                ; Check status after restore console mode.
setnz al                    ; AL=0 if mode error, AL=1 if mode OK.
and al,bl                   ; AL=1 only if both operations status OK.
and eax,1                   ; Bit RAX.0=Valid, bits RAX.[63-1]=0.
;---------- Exit point, RAX = Status actual here. -----------------------------;
.exit:
mov rsp,rbp
pop rcx rcx rbp rsi rbx     ; Note. RCX pops for clear stack.
ret
