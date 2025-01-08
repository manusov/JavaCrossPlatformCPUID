;---------------------- Win32 console functions notes -------------------------;
; Used functions:
;
; GetStdHandle()
; Input:  Parm#1 = Handle type code for retrieve.
; Output: Handle, if error 0 or INVALID_HANDLE_VALUE (32-bit EAX). 
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

;---------- Wait for press CHARACTER key. -------------------------------------;
; Echo and edit string mode disabled.                                          ;
; Used simplified variant of [ReadConsole], Number of chars to Read = 1.       ;
;                                                                              ;
; INPUT:  ECX = Pointer to output buffer, for single char in this variant.     ;
;                                                                              ;
; OUTPUT: EAX = Status.                                                        ;
;         Buffer at [input ECX] updated.                                       ;
;------------------------------------------------------------------------------;
ConsoleReadChar:
xor eax,eax
push ebx esi ebp eax eax    ; EAX = For variables storage.
mov ebp,esp                 ; EBP = Pointer to stack frame.
mov ebx,[StdIn]             ; EBX = Storage for input device handle.
mov esi,ecx                 ; ESI = Non volatile copy of pointer.
;--- Exit with status = OK if input handle = 0, wait key disabled by options. -;
mov eax,1                   ; EAX = Status = OK, if wait key disabled.
test ebx,ebx                ; EBX = Input handle.
jz .exit                    ; Skip keyboard input if handle = 0.
;---------- Get current console mode. -----------------------------------------;
push ebp                    ; Parm#2 = Pointer to output variable. 
push ebx                    ; Parm#1 = Input device handle.
call [GetConsoleMode]       ; Get current console mode.
test eax,eax                ; EAX = Status, 0 if error.
jz .exit                    ; Go exit function if error.
;---------- Change current console mode. --------------------------------------;
mov eax,[ebp]               ; EAX = Console mode.
and al,DISABLE_ECHO_ALL     ; Disable echo and string in. (ret. after 1 char).
push eax                    ; Parm#2 = Console mode. 
push ebx                    ; Parm#1 = Input device handle.
call [SetConsoleMode]       ; Get current console mode.
test eax,eax                ; EAX = Status, 0 if error.
jz .exit                    ; Go exit function if error.
;---------- Blank buffer (usually redundant). ---------------------------------;
mov byte [esi],BLANK_KEY
;---------- Read console ( wait only without echo ). --------------------------;
push 0                      ; Parm#5 = InputControl.
lea eax,[ebp + 4]
push eax                    ; Parm#4 = Pointer to output variable, chars count.
push 1                      ; Parm#3 = Number of chars to Read.
push esi                    ; Parm#2 = Pointer to input buffer.
push ebx                    ; Parm#1 = Input device handle.
call [ReadConsole]          ; Keyboard input.
;---------- Restore current console mode. -------------------------------------;
push dword [ebp]            ; Parm#2 = Console mode.
push ebx                    ; Parm#1 = Input device handle.
xchg ebx,eax                ; EBX = Save error code after input char.
call [SetConsoleMode]       ; Set current console mode.
;---------- Error code = F( restore, input ). ---------------------------------;
test ebx,ebx                ; Check status after console input. 
setnz bl                    ; BL=0 if input error, BL=1 if input OK.
test eax,eax                ; Check status after restore console mode.
setnz al                    ; AL=0 if mode error, AL=1 if mode OK.
and al,bl                   ; AL=1 only if both operations status OK.
and eax,1                   ; Bit EAX.0 = Valid, bits EAX.[31-1] = 0.
;---------- Exit point, EAX = Status actual here. -----------------------------;
.exit:
pop ecx ecx ebp esi ebx
ret
