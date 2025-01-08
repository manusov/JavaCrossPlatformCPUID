;---------- Read scenario file. --------------------------------------------;
; Input / Output parameters and Volatile / Non volatile registers           ;
; compatible with Microsoft x64 calling convention.                         ;
;                                                                           ;
; INPUT: RCX = Pointer to scenario file path and name,                      ;
;              0-terminated ASCII string.                                   ;
;        RDX = Pointer to scenario handle.                                  ;
;        R8  = Pointer to loaded scenario base address variable,            ; 
;              this variable is buffer base address for file read.          ;
;        R9  = Pointer to scenario size variable,                           ; 
;              this variable is size limit for this buffer.                 ;   
;                                                                           ;
; OUTPUT: RAX = OS API last operation status code.                          ;
;         Variable scenario handle at [input RDX] = updated by file open.   ;
;         Variable scenario size at [input R9] = Read size, 0 if error.     ;
;---------------------------------------------------------------------------;
ReadScenario:
push rbx rsi rdi rbp r12 r13 r14
mov rbx,rdx                ; RBX = non volatile pointer to scenario handle.
mov rsi,r8                 ; RSI = non volatile pointer to loaded scenario.
mov rdi,r9                 ; RDI = non volatile pointer to scenario size. 
mov rbp,rsp                ; RBP = Storage for RSP before alignment.
and rsp,0FFFFFFFFFFFFFFF0h ; Align stack for call WinAPI by convention.
;--- Open file, by input parameters: RCX = Parm #1 = Pointer to file name. ----; 
xor eax,eax                ; RAX = 0 for store result = 0 if ScenarioName = 0.
jrcxz .error               ; Skip operation if file name pointer = 0.
mov edx,GENERIC_READ       ; RDX = Parm #2 = Desired access. 
xor r8d,r8d                ; R8  = Parm #3 = Share mode, not used.
xor r9d,r9d                ; R9  = Parm #4 = Security attributes, not used.
xor eax,eax
push rax                   ; This push for stack alignment.
push rax                   ; Parm #7 = Template file, not used.
push FILE_ATTRIBUTE_NORMAL ; Parm #6 = File attributes.
push OPEN_EXISTING         ; Parm #5 = Creation/Open disposition.
sub rsp,32                 ; Create parameters shadow.
call [CreateFileA]
add rsp,32+32              ; Remove parameters shadow and parameters.
mov [rbx],rax              ; Save scenario file handle.
;---------- Initializing for read file. ---------------------------------------;
xor r12,r12                ; R12 = 0, clear read size counter.
mov r13,[rsi]              ; R13 = Base address of memory buffer.
mov r14,[rdi]              ; R14 = Size limit of memory buffer.
;---------- Read file. --------------------------------------------------------;
.read:
mov rcx,[rbx]            ; RCX = Parm #1 = File handle.
jrcxz .error             ; Skip read and close if handle = 0 (if open error).
mov rdx,r13              ; RDX = Parm #2 = Buffer base address for read.
mov r8,r14               ; R8  = Parm #3 = Buffer size limit.
xor eax,eax
push rax                 ; This space for output variable plus stack align.
mov r9,rsp               ; R9  = Parm #4 = Pointer to output size.
push rax                 ; Parm #5 = Pointer to overlapped str., not used.
sub rsp,32
call [ReadFile]
add rsp,32+8
pop rcx                  ; RCX = Output size, RAX = Output status.
;---------- Analusing read results. -------------------------------------------;
test rax,rax
jz .error        ; Go error if OS status = 0.
jrcxz .result    ; Go normal read termination if returned size = 0.
test ecx,ecx     ; Note bits RCX.63-32 cleared at PUSH RAX=0.
js .error        ; Go error if size negative, note for 32-bit only.
add r12,rcx      ; R12 = accumulate read size.
add r13,rcx      ; R13 = advance read pointer by returned size.
sub r14,rcx      ; R14 = subtract current read size from size limit. 
ja .read         ; Repeat read if return size > 0 and limit not reached. 
jb .error        ; Error if read size > size limit, if R14 = 0, means read OK. 
;---------- Write result size. ------------------------------------------------;
.result:
mov [rdi],r12    ; Write scenario size = file size if read OK. 
jmp .close
.error:
mov qword [rdi],0  ; Write scenario size = 0 if read error.
; Close file.
.close:
mov rcx,[rbx]    ; RCX = Parm #1 = File handle.
jrcxz .exit
sub rsp,32
call [CloseHandle]
.exit:
mov rsp,rbp   ; This for restore after alignment and also instead ADD RSP,32.
pop r14 r13 r12 rbp rdi rsi rbx
ret
