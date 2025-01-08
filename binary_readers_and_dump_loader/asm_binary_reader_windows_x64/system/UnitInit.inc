;---------- Application initialization and compatibility checking. ------------;
;                                                                              ;
; INPUT:   RCX = Pointer to status block: 3 QWORDS (msg1, msg2, WinAPI error). ;
;                                                                              ;
; OUTPUT:  RAX = Status, 0  (FALSE) = Error, status block valid,               ;
;                        >0 (TRUE)  = No errors, status block not used.        ;
;          3 QWORDs at input RCX = Status block.                               ;
;------------------------------------------------------------------------------;
SHELL_Init:
push rbx
;---------- Clear status. -----------------------------------------------------;
mov rbx,rcx
xor eax,eax
mov [rbx + 00],rax
mov [rbx + 08],rax
mov [rbx + 16],rax
;---------- Check CPUID support, get maximum standard function. ---------------;
call CheckCpuid
jc .fpuTscAbsent
cmp eax,1
jb .fpuTscAbsent
mov eax,1
push rbx
cpuid
pop rbx
mov al,00010001b  ; EDX.4 = TSC, EDX.0 = FPU.
and dl,al
cmp dl,al
jne .fpuTscAbsent
;---------- Load dynamically imported WinAPI. ---------------------------------;
call LoadWinApi
jc .errorSystem
;---------- Exit with no errors, EAX = Status. --------------------------------;
mov eax,1
.exit:
pop rbx
ret
;---------- Exit points for errors. -------------------------------------------;
.cpuidAbsent:
lea rax,[MsgAbsentCpuid]
.error:
mov [rbx],rax
xor eax,eax
jmp .exit
.fpuTscAbsent:
lea rax,[MsgAbsentFPUTSC]
jmp .error
.errorSystem:
lea rax,[MsgErrorWinAPI]
jmp .error


;==============================================================================;
;                                                                              ;
;              Subroutines portable to JNI (Java Native Interface)             ;
;                         library of CPUID utility.                            ;
;          Accept RSI = optional input data array (or no input data).          ;
;                       IPB = Input Parameters Block.                          ;
;                 RDI = optional output data array (or no output data).        ;
;                       OPB = Output Parameters Block.                         ;
;                 RAX = optional output result or status info.                 ;
;                 CF flag = status.                                            ;
;                                                                              ;
;==============================================================================;

;---------- Check CPUID instruction support. ----------------------------------;
;                                                                              ;
; INPUT:   None.                                                               ;
;                                                                              ;
; OUTPUT:  CF flag: 0(NC) = CPUID supported.                                   ;
;                   1(C)  = CPUID not supported.                               ;
;          EAX = Maximum standard function supported.                          ;
;------------------------------------------------------------------------------;
; This support already verified during Hardware Shell start.
; But required for JNI porting.
CheckCpuid:
push rbx
mov ebx,21
pushf
pop rax
bts eax,ebx
push rax
popf
pushf
pop rax
btr eax,ebx
jnc .cpuidAbsent
push rax
popf
pushf
pop rax
bt eax,ebx
jc .cpuidAbsent
xor eax,eax
cpuid
pop rbx
ret
.cpuidAbsent:
stc
pop rbx
ret

;---------- Dynamical import required WinAPI functions. -----------------------;  
;                                                                              ;
; INPUT:  None.                                                                ;  
;                                                                              ;
; OUTPUT:  CF flag: 0(NC) = Dynamical import executed OK,                      ;
;                           but pointers requires checks: can be zero.         ;
;                   1(C)  = Dynamical import fatal error.                      ;
;------------------------------------------------------------------------------;
LoadWinApi:
push rbx rsi rdi rbp
mov rbp,rsp 
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32
lea rcx,[NameDll]        ; RCX = Parm#1 = Pointer to library (DLL) name.
call [GetModuleHandle]
test rax,rax
jz .error
xchg rbx,rax   ; This XCHG compact than MOV.
cld
lea rsi,[NamesFunctions]
lea rdi,[LoadPointers]
.load:
cmp byte [rsi],0  ; Check for end of list. 
je .exit          ; Go if end of functions list if string starts with zero byte.
mov rcx,rbx       ; RCX = Parm#1 = Library (DLL) handle. 
mov rdx,rsi       ; RDX = Parm#2 = Pointer to function name.
call [GetProcAddress]
stosq
.skip:
lodsb             ; Skip loaded function name string.
cmp al,0
jne .skip
jmp .load
.exit:
leave             ; LEAVE = MOV RSP,RBP and POP RBP.
pop rdi rsi rbx
ret
.error:
stc
jmp .exit
