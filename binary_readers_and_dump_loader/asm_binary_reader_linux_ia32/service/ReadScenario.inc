;---------- Read scenario file. -----------------------------------------------;
;                                                                              ;
; INPUT: ECX = Pointer to scenario file path and name,                         ;
;              0-terminated ASCII string.                                      ;
;        EDX = Pointer to scenario handle.                                     ;
;        ESI = Pointer to loaded scenario base address variable,               ;
;              this variable is buffer base address for file read.             ;
;        EDI = Pointer to scenario size variable,                              ;
;              this variable is size limit for this buffer.                    ;
;                                                                              ;
; OUTPUT: EAX = OS API last operation status code.                             ;
;         Variable scenario handle at [input EDX] = updated by file open.      ;
;         Variable scenario size at [input EDI] = Read size, 0 if error.       ;
;------------------------------------------------------------------------------;

;---------- This equations for porting from x64 to ia32. ----------------------;
rs_temp_r12  equ  dword [esp + 0]
rs_temp_r13  equ  dword [esp + 4]
rs_temp_r14  equ  dword [esp + 8]

ReadScenario:
push ebx esi edi ebp 0 0 0
mov ebp,edx               ; EBP = non volatile pointer to scenario handle.
;--- Open file, by input parameters: ECX = Parm #1 = Pointer to file name. ----;
xor eax,eax               ; RAX = 0 for store result = 0 if ScenarioName = 0.
jecxz .L2                 ; Skip operation if file name pointer = 0.
mov ebx,ecx               ; EBX = Parm#1 = Pointer to source file path.
mov ecx,FILE_FLAGS_BLANK  ; ECX = Parm#2 = File operation mode flags.
mov edx,FILE_SHARE_MODE   ; EDX = Parm#3 = File access read-write mode.
mov eax,SYS_OPEN          ; RAX = Function code, open file
int 80h
mov edx,ebp
mov [edx],eax             ; Save scenario file handle. 
;---------- Initializing for read file. ---------------------------------------;
                          ; R12 = 0, clear read size counter.
mov eax,[esi]
mov rs_temp_r13,eax       ; R13 = Base address of memory buffer.
mov eax,[edi]       
mov rs_temp_r14,eax       ; R14 = Size limit of memory buffer.
;---------- Read file. --------------------------------------------------------;
.L10:
mov edx,ebp
mov ebx,[edx]             ; Parm#1 = EBX = Handle of storage device.
test ebx,ebx
jz .L2                    ; Go skip close if handle = 0.
js .L2                    ; Go skip close if handle < 0. 
mov ecx,rs_temp_r13       ; Parm#2 = ECX = Pointer to buffer.
mov edx,rs_temp_r14       ; Parm#3 = RDX = Number of characters to read.
mov eax,SYS_READ          ; EAX = Function code = Read file.
int 80h
;---------- Analusing read results. -------------------------------------------;
test eax,eax
jz .L3                    ; Go normal read termination if returned size = 0.
js .L2                    ; Go error if OS status < 0.
add rs_temp_r12,eax       ; R12 = accumulate read size.
add rs_temp_r13,eax       ; R13 = advance read pointer by returned size.
sub rs_temp_r14,eax       ; R14 = subtract current read size from size limit. 
ja .L10                   ; Repeat read if return size > 0 and limit not reached. 
jb .L2                    ; Error if read size > size limit.

;;---------- Write result size. ------------------------------------------------;
.L3:
mov eax,rs_temp_r12
mov [edi],eax           ; Write scenario size = file size if read OK.
jmp .L1
.L2:
mov dword [edi],0       ; Write scenario size = 0 if read error.
.L1:
;---------- Close file. -------------------------------------------------------;
xor eax,eax             ; EAX = Status, make 0 if handle invalid.
mov edx,ebp
mov ebx,[edx]           ; EBX = Parm#1 = Source file descriptor, handle.
test ebx,ebx
jz .L0                  ; Go skip close if handle = 0.
js .L0                  ; Go skip close if handle < 0.
mov eax,SYS_CLOSE
int 80h
test eax,eax
setz al         ; Output EAX = make status = 1 if no errors ( input EAX = 0 ).
.L0:
;---------- Exit. -------------------------------------------------------------;
pop ecx ecx ecx ebp edi esi ebx
ret


