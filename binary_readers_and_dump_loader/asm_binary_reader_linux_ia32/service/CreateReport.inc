;---------- Create report file. -----------------------------------------------;
;                                                                              ;
; After this function successfully call, function ConsoleWrite                 ;
; starts save output information to report file.                               ;
;                                                                              ;
; INPUT:  ECX = Pointer to report file name, 0-terminated ASCII string.        ;
;         EDX = Pointer to report file handle, return handle = 0 if error.     ;  
;                                                                              ;
; OUTPUT: EAX = Status code                                                    ;
;               Variable report handle at [input ECX] =                        ; 
;               Temporary handle, used as flag for write report file enable.   ;
;------------------------------------------------------------------------------;
CreateReport:
push ebx edi esi ebp
mov ebp,edx               ; EBP = Non volatile copy of handle pointer.
;--- Create file, input parameter ECX = Parm #1 = Pointer to file name. -------;
xor eax,eax               ; EAX = 0 for store result = 0 if ScenarioName = 0.
jecxz .L1                 ; Skip operation if file name pointer = 0.
mov ebx,ecx               ; EBX = Parm#1 = Pointer to source file path.
mov ecx,FILE_FLAGS_BLANK + FILE_FLAG_TRUNCATE  ; ECX = Parm#2 = File operation mode flags.
mov edx,FILE_SHARE_MODE   ; EDX = Parm#3 = File access read-write mode.
mov eax,SYS_OPEN          ; EAX = Function code, open file.
int 80h
;---------- Store result. -----------------------------------------------------;
.L1:
mov edx,ebp
mov [edx],eax       ; Save EAX = returned scenario file handle or error code.
;---------- Close file. -------------------------------------------------------;
mov ebx,eax         ; EBX = Parm#1 = Source file descriptor, handle.
xor eax,eax
test ebx,ebx
jz .L0              ; Go skip close if handle = 0.
js .L0              ; Go skip close if handle < 0.
mov eax,SYS_CLOSE
int 80h
test eax,eax
setz al             ; EAX = make status = 1 if no errors (RAX=0).
.L0:
pop ebp esi edi ebx
ret

