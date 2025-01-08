;---------- Create report file. --------------------------------------------;
; Input / Output parameters and Volatile / Non volatile registers           ;
; compatible with Microsoft x64 calling convention.                         ;
;                                                                           ;
; After this function successfully call, function ConsoleWrite              ;
; starts save output information to report file.                            ;
;                                                                           ;
; INPUT:  RCX = Pointer to report file name, 0-terminated ASCII string.     ;
;         RDX = Pointer to report file handle, return handle = 0 if error.  ;  
;                                                                           ;
; OUTPUT: RAX = Status code.                                                ;
;               Variable report handle at [input RCX] = Temporary handle,   ; 
;               used as flag for write report file enable.                  ;
;---------------------------------------------------------------------------;
CreateReport:
push rbx rbp
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h  ; Align stack for call WinAPI by convention.
mov rbx,rdx                 ; RBX = Non volatile copy of handle pointer. 
;--- Create file, input parameter RCX = Parm #1 = Pointer to file name. -------;
xor eax,eax                 ; RAX = 0 for store result = 0 if ReportName = 0.
jrcxz @f
mov edx,GENERIC_WRITE       ; RDX = Parm #2 = Desired access. 
xor r8d,r8d                 ; R8  = Parm #3 = Share mode, not used.
xor r9d,r9d                 ; R9  = Parm #4 = Security attributes, not used.
xor eax,eax
push rax                    ; This push for stack alignment.
push rax                    ; Parm #7 = Template file, not used.
push FILE_ATTRIBUTE_NORMAL  ; Parm #6 = File attributes.
push CREATE_ALWAYS          ; Parm #5 = Creation disposition.
sub rsp,32                  ; Create parameters shadow.
call [CreateFileA]
add rsp,32+32               ; Remove parameters shadow and parameters.
@@:
;---------- Store result. -----------------------------------------------------;
mov [rbx],rax               ; RAX = Returned handle.
;---------- Close file. -------------------------------------------------------;
xchg rcx,rax
jrcxz @f
sub rsp,32
call [CloseHandle]
@@:
mov rsp,rbp   ; This for restore after alignment and also instead ADD RSP,32.
pop rbp rbx
ret
