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

;---------- String write to console, in ASCII. --------------------------------;
;                                                                              ;
; INPUT:   ECX = Pointer to 0-terminated ASCII string, string output           ; 
;                to console and optional to report file (if RDX non zero).     ;
;          EDX = Report file handle, used as report validity flag only,        ;
;                report file must be re-opened before write.                   ;
;          ESI = Pointer to report file name and path,                         ;
;                0-terminated ASCII string.                                    ;
;                                                                              ;
; OUTPUT:  EAX = OS Status.                                                    ;
;------------------------------------------------------------------------------;
ConsoleWriteReport:
push ebx ecx edx esi edi ebp
mov edx,[ReportHandle]
mov esi,[ReportName]
ConsoleWrite:
mov ebp,edx             ; EBP = Report file handle.
;---------- Calculate string length. ------------------------------------------;
xor edi,edi             ; EDI  = Number of chars ( length ).
@@:
cmp byte [ecx + edi],0  ; Check current char from string.
je @f                   ; Exit cycle if terminator (byte=0) found.
inc edi                 ; Chars counter + 1.
jmp @b                  ; Go next iteration.
@@:
;---------- Write string to console, conditional, if handle not zero. ---------;
mov ebx,[StdOut]        ; Parm#1 = EBX = Device = STDOUT.
test ebx,ebx
jz @f                   ; Go skip console write if handle = 0.
                        ; Parm#2 = ECX = Pointer to message string.
mov edx,edi             ; Parm#3 = EDX = Length.
mov eax,SYS_WRITE       ; EAX = Linux API function (syscall number).
int 80h
@@:
;---------- Check criteria for write report file - optional. ------------------;
mov eax,1              ; EAX = Status = OK, if report save disabled.
test ebp,ebp           ; EBP = Report temp. handle used as flag.
jz .Exit               ; Skip file output if handle = 0.
js .Exit               ; Skip file output if handle = Invalid < 0.
test esi,esi           ; ESI = Parm #1 = Pointer to name string.
jz .Exit               ; Skip file output if name pointer = 0.
;---------- Open. -------------------------------------------------------------;
mov ebx,esi              ; EBX = Parm#1 = Pointer to Report file name.
mov ecx,FILE_FLAGS_BLANK ; ECX = Parm#2 = File operation mode flags.
mov edx,FILE_SHARE_MODE  ; EDX = Parm#3 = File access read-write mode.
mov eax,SYS_OPEN         ; EAX = Function code, open file.
int 80h
test eax,eax
jz .Exit                ; Go if open file error.
js .Exit
mov ebp,eax             ; EBP = Now used handle.
;---------- Positioning pointer to end of file. -------------------------------;
mov ebx,ebp             ; EBX = Parm#1 = File descriptor, handle.
xor ecx,ecx             ; ECX = Parm#2 = File offset = 0.
mov edx,2               ; EDX = Parm#3 = Whence, 2 means SEEK_END = from file begin.
mov eax,SYS_LSEEK       ; EAX = Function code = seek.
int 80h
test eax,eax            ; Check RAX = Status.
js .L11                 ; Go if error seek file.
;---------- Write. ------------------------------------------------------------;
.L10:
mov ebx,ebp            ; Parm#1 = EBX = File handle.
test ebx,ebx
jz @f                  ; Go skip console write if handle = 0.
mov ecx,[esp + 16]     ; Parm#2 = ECX = Pointer to message string.
mov edx,edi            ; Parm#3 = EDX = Length.
mov eax,SYS_WRITE      ; EAX = Linux API function (syscall number).
int 80h
@@:
test eax,eax           ; EAX = write size (if >= 0) of status (if < 0).
jz .L0                 ; Go exit if returned size = 0.
js .L0                 ; Go exit if error.
add dword [esp + 16],eax  ; Advance read pointer by returned size.
sub edi,eax            ; EDI = subtract current read size from size limit.
ja .L10                ; Repeat read if return size > 0 and limit not reached. 
;---------- Close file. -------------------------------------------------------;
.L11:
xor eax,eax            ; EAX = Status, make 0 if handle invalid.
mov ebx,ebp            ; EBX = Parm#1 = File descriptor, handle.
test ebx,ebx
jz .L0                 ; Go skip close if handle = 0.
js .L0                 ; Go skip close if handle < 0.
mov eax,SYS_CLOSE
int 80h
test eax,eax
setz al                ; EAX = make status = 1 if no errors (EAX=0).
.L0:
;---------- Exit point, RAX = Status actual here. -----------------------------;
.Exit:
pop ebp edi esi edx ecx ebx
ret


