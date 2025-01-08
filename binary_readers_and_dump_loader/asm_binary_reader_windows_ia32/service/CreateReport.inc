;---------- Create report file. -----------------------------------------------;
; After this function successfully call, function ConsoleWrite                 ;
; starts save output information to report file.                               ;
;                                                                              ;
; INPUT:  ECX = Pointer to report file name, 0-terminated ASCII string.        ;
;         EDX = Pointer to report file handle, return handle = 0 if error.     ;
;                                                                              ;
; OUTPUT: EAX = Status code.                                                   ;
;               Variable report handle at [input ECX] =                        ;
;               Temporary handle, used as flag for write report file enable.   ;
;------------------------------------------------------------------------------;
CreateReport:
push ebx
mov ebx,edx                 ; EBX = Non volatile copy of handle pointer. 
;---------- Create file, input parameter RCX = Pointer to file name. ----------;
xor eax,eax                 ; EAX = 0 for store result = 0 if ReportName = 0.
jecxz @f
push eax                    ; Parm #7 = Template file, not used.
push FILE_ATTRIBUTE_NORMAL  ; Parm #6 = File attributes.
push CREATE_ALWAYS          ; Parm #5 = Creation disposition.
push eax                    ; Parm #4 = Security attributes, not used.
push eax                    ; Parm #3 = Share mode, not used.
push GENERIC_WRITE          ; Parm #2 = Desired access.
push ecx                    ; Parm #1 = Pointer to file name.
call [CreateFileA]
@@:
;---------- Store result. -----------------------------------------------------;
mov [ebx],eax               ; EAX = Returned handle.
;---------- Close file. -------------------------------------------------------;
test eax,eax
jz @f
push eax                    ; Parm#1 = Handle.
call [CloseHandle]
@@:
pop ebx
ret
