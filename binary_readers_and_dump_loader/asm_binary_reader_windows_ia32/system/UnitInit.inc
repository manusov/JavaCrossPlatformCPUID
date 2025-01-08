;---------- Application initialization and compatibility checking. ------------;
;                                                                              ;
; INPUT:   ECX = Pointer to status block: 3 DWORDS (msg1, msg2, WinAPI error). ;
;                                                                              ;
; OUTPUT:  EAX = Status, 0  (FALSE) = Error, status block valid,               ;
;                        >0 (TRUE)  = No errors, status block not used.        ;
;          3 DWORDs at input RCX = Status block.                               ;
;------------------------------------------------------------------------------;
SHELL_Init:
push ebx
;---------- Clear status. -----------------------------------------------------;
mov ebx,ecx
xor eax,eax
mov [ebx + 00],eax
mov [ebx + 04],eax
mov [ebx + 12],eax
;---------- Check CPUID support, get maximum standard function. ---------------;
call CheckCpuid
jc .fpuTscAbsent
cmp eax,1
jb .fpuTscAbsent
mov eax,1
push ebx
cpuid
pop ebx
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
pop ebx
ret
;---------- Exit points for errors. -------------------------------------------;
.cpuidAbsent:
lea eax,[MsgAbsentCpuid]
.error:
mov [ebx],eax
xor eax,eax
jmp .exit
.fpuTscAbsent:
lea eax,[MsgAbsentFPUTSC]
jmp .error
.errorSystem:
lea eax,[MsgErrorWinAPI]
jmp .error


;==============================================================================;
;                                                                              ;
;              Subroutines portable to JNI (Java Native Interface)             ;
;                         library of CPUID utility.                            ;
;          Accept ESI = optional input data array (or no input data).          ;
;                       IPB = Input Parameters Block.                          ;
;                 EDI = optional output data array (or no output data).        ;
;                       OPB = Output Parameters Block.                         ;
;                 EAX = optional output result or status info.                 ;
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
push ebx
mov ebx,21
pushf
pop eax
bts eax,ebx
push eax
popf
pushf
pop eax
btr eax,ebx
jnc .cpuidAbsent
push eax
popf
pushf
pop eax
bt eax,ebx
jc .cpuidAbsent
xor eax,eax
cpuid
pop ebx
ret
.cpuidAbsent:
stc
pop ebx
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
push ebx esi edi
push NameDll       ; Parm#1 = Pointer to library (DLL) name.
call [GetModuleHandle]
test eax,eax
jz .error
xchg ebx,eax       ; This XCHG compact than MOV. 
cld
lea esi,[NamesFunctions]
lea edi,[LoadPointers]
.load:
cmp byte [esi],0   ; Check for end of list.
jz .exit           ; Go if end of functions list.
push esi           ; Parm#2 = Pointer to function name.       
push ebx           ; Parm#1 = Library (DLL) handle.
call [GetProcAddress]
stosd
.skip:
lodsb             ; Skip loaded function name string.
cmp al,0
jne .skip
jmp .load
.exit:
pop edi esi ebx
ret
.error:
stc
jmp .exit
