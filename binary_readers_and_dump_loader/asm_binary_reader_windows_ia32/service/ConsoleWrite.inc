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

;---------- ASCII string write to console. ------------------------------------;
;                                                                              ;
; INPUT:   ECX = Pointer to 0-terminated ASCII string, string output           ; 
;                to console and optional to report file (if EDX non zero).     ;
;          EDX = Report file handle, used as report validity flag only,        ;
;                report file must be re-opened before write.                   ;
;          ESI = Pointer to report file name and path,                         ;
;                0-terminated ASCII string.                                    ;
;                                                                              ;
; OUTPUT:  EAX = OS Status.                                                    ;
;------------------------------------------------------------------------------;
;---------- This special entry point not required input EDX, ESI. -------------;
ConsoleWriteReport:
push edx esi
mov edx,[ReportHandle]
mov esi,[ReportName]
call ConsoleWrite
pop esi edx
ret
;---------- This normal entry point required input EDX, ESI. ------------------;
ConsoleWrite:
push ebx esi edi ebp 0      ; push 0 for variable.
mov ebp,esp                 ; EBP = Pointer to stack frame.
mov esi,ecx                 ; ESI = Non volatile copy of buffer pointer.
mov ebx,edx                 ; EBX = Non volatile copy of report handle.
;---------- Calculate string length. ------------------------------------------;
xor edi,edi                 ; EDI = Number of chars ( length ).
@@:
cmp byte [esi + edi],0      ; Check current char from string.
je @f                       ; Exit cycle if terminator (byte=0) found.
inc edi                     ; Chars counter + 1.
jmp @b                      ; Go next iteration.
@@:
;---------- Write console - optional. -----------------------------------------;
mov eax,1                   ; EAX = Status = OK, if display output disabled.
mov ecx,[StdOut]            ; ECX = Parm#1 = Input device handle.
jecxz @f                    ; Skip console output if handle = 0.
push 0                      ; Parm#5 = Overlapped, not used.
push ebp                    ; Parm#4 = Pointer to output variable, count.
push edi                    ; Parm#3 = Number of chars ( length ).
push esi                    ; Parm#2 = Pointer to string ( buffer ).
push ecx                    ; Parm#1 = Input device handle.
call [WriteFile]
@@:
;---------- Check criteria for write report file - optional. ------------------;
mov eax,1                   ; EAX = Status = OK, if report save disabled.
test ebx,ebx                ; EBX = Report temp. handle used as flag.
jz .exit                    ; Skip file output if handle = 0.
cmp ebx,INVALID_HANDLE_VALUE
je .exit                    ; Skip file output if handle = Invalid = -1.
mov ecx,[ebp + 12]          ; ECX = Pointer to name string.
jecxz .exit                 ; Skip file output if name pointer = 0.
;---------- Open. -------------------------------------------------------------;
xor eax,eax
push eax                    ; Parm #7 = Template file, not used.
push FILE_ATTRIBUTE_NORMAL  ; Parm #6 = File attributes.
push OPEN_EXISTING          ; Parm #5 = Creation disposition.
push eax                    ; Parm #4 = Security attributes, not used.
push eax                    ; Parm #3 = Share mode, not used.
push GENERIC_WRITE          ; Parm #2 = Desired access.
push ecx                    ; Parm #1 = Pointer to name string.
call [CreateFileA]
test eax,eax
jz .exit                    ; Go if open file error.
mov ebx,eax                 ; EBX = Save file handle.
;---------- Positioning pointer to end of file. -------------------------------;
push FILE_END          ; Parm #4 = Move method.
push 0                 ; Parm #3 = Position, high dword.
push 0                 ; Parm #2 = Position, low dword.
push eax               ; Parm #1 = File handle.
call [SetFilePointer]
;---------- Write. ------------------------------------------------------------;
.write:
push 0                 ; Parm#5 = Overlapped, not used.
push ebp               ; Parm#4 = Pointer to output variable, count.
push edi               ; Parm#3 = Number of chars ( length ).
push esi               ; Parm#2 = Pointer to string ( buffer ).
push ebx               ; Parm#1 = File handle.
call [WriteFile]
mov ecx,[ebp]          ; ECX = Returned size.
test eax,eax           ; EAX = status, 0 means error.
jz .close              ; Go exit if error.
jecxz .close           ; Go exit if returned size = 0.
add esi,ecx            ; ESI = advance read pointer by returned size.
sub edi,ecx            ; EDI = subtract current read size from size limit.
ja .write              ; Repeat read if return size > 0 and limit not reached. 
;---------- Close. ------------------------------------------------------------;
.close:
test ebx,ebx
jz .exit
push ebx               ; Parm#1 = Handle.
call [CloseHandle]     ; Close report file after write.
;---------- Exit point, EAX = Status actual here. -----------------------------;
.exit:
pop ebp ebp edi esi ebx
ret

