;==============================================================================;
;                                                                              ;
;                      Java cross-platform CPUID Utility.                      ;
;        https://github.com/manusov/JavaCrossPlatformCPUID/tree/master         ;
;                  https://github.com/manusov?tab=repositories                 ;
;                 No copyright. Information belongs to Universe.               ;
;                                                                              ;
;  Native library for Java cross-platform CPUID Utility. Windows x64 variant.  ;
;                 This is FASM (Flat Assembler) source file.                   ;
;                                                                              ; 
;==============================================================================;

include 'win64a.inc'
format PE64 GUI 4.0 DLL
entry DllMain
section '.text' code readable executable

NATIVE_ID    EQU  140h
MAX_REQUEST  EQU  8

;---------- DLL entry point. --------------------------------------------------;
; This entry point not used here, see:                                         ;
; https://learn.microsoft.com/ru-ru/windows/win32/dlls/dllmain                 ;
;------------------------------------------------------------------------------;
DllMain:
mov eax,1
ret

;---------- Get platform and OS constant properties. --------------------------;  
; This subroutine is handler for JNI call "detectBinary" from Java code.       ;
;                                                                              ;
; INPUT:   None.                                                               ;
;                                                                              ;
; OUTPUT:  EAX = Detection status, bits usage:                                 ;
;                EAX[31-24] = Reserved = 0.                                    ;
;                EAX[23-20] = This library version, major value.               ;
;                EAX[19-16] = This library version, minor value.               ;
;                EAX[15-8]  = OS ID, can be 1=MS Windows, 2=Linux.             ;                
;                EAX[7-0]   = Native width, can be 32=20h or 64=40h.           ;
;------------------------------------------------------------------------------; 
detectBinary:    
mov eax,NATIVE_ID
ret

;---------- Initializing platform and OS functions, returns status. -----------;  
; This subroutine is handler for JNI call "initBinary" from Java code.         ;
;                                                                              ;
; INPUT:   None.                                                               ;
;                                                                              ;
; OUTPUT:  EAX = Initialization status, bits usage:                            ;
;                EAX[31-16] = Signature for verification, 55AAh.               ;
;                EAX[15-5]  = Reserved = 0.                                    ;                
;                EAX[4] = No fatal errors at WinAPI loader.                    ; 
;                EAX[3] = TSC support verified OK by CPUID.                    ;
;                EAX[2] = x87 FPU support verified OK by CPUID.                ; 
;                EAX[1] = CPUID support verified OK by EFLAGS.21 bit.          ;
;                EAX[0] = WoW64 flag, "1" means Win32 application under Win64. ;
;------------------------------------------------------------------------------;
initBinary:    
push rbx
mov ebx,055AA0000h
call CheckCpuid
jc .skipCpuid
mov bl,00000010b
cmp eax,1
jb .skipCpuid 
mov eax,1
push rbx
cpuid
pop rbx
test dl,00000001b    ; CPUID.1.EDX.0 = FPU.
jz .skipFpu
or bl,00000100b
.skipFpu:
test dl,00010000b    ; CPUID.1.EDX.4 = TSC.
jz .skipCpuid
or bl,00001000b
.skipCpuid:
call LoadWinApi
jc .skipApi
or bl,00010000b
.skipApi:
xchg eax,ebx         ; EAX = result, note this XCHG compact than MOV.
pop rbx
ret
;---------- Check CPUID instruction support. ------------------------;
;                                                                    ;
; INPUT:   None.                                                     ;
;                                                                    ;
; OUTPUT:  CF flag: 0(NC) = CPUID supported.                         ;
;                   1(C)  = CPUID not supported.                     ;
;          EAX = Maximum standard function supported.                ;
;--------------------------------------------------------------------;
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
;---------- Dynamical import required WinAPI functions. -------------;  
;                                                                    ;
; INPUT:   None.                                                     ;  
;                                                                    ;
; OUTPUT:  CF flag: 0(NC) = Dynamical import executed OK,            ;
;                   but pointers requires checks: can be zero.       ;
;                   1(C)  = Dynamical import fatal error.            ;
;--------------------------------------------------------------------;
LoadWinApi:
push rbx rsi rdi rbp
mov rbp,rsp 
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32
lea rcx,[NameDll]        ; RCX = Parm#1 = Pointer to library (DLL) name.
call [GetModuleHandle]
test rax,rax
jz .error
xchg rbx,rax      ; This XCHG compact than MOV.
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

;---------- De-initializing platform and OS functions, returns status. --------;  
; This subroutine is handler for JNI call "deinitBinary" from Java code.       ;
;                                                                              ;
; Yet not used, reserved for de-initialization operations.                     ;  
;------------------------------------------------------------------------------;
deinitBinary:    
mov eax,0AA550000h + NATIVE_ID
ret

;---------- Entry point for requests with IPB and OPB data arrays. ------------;  
; This subroutine is handler for JNI call "requestBinary" from Java code.      ;
; Note. IPB = Input Parameters Block, OPB = Output Parameters Block.           ;
; Note. Parm#1, Parm#2 provided by JVM. User parameters starts from Parm#3.    ;
;                                                                              ;
; INPUT:   Parm#1 = RCX = JNI Environment.                                     ;  
;          Parm#2 = RDX = JNI This Object reference (not used here).           ;
;          Parm#3 = R8  = Object IPB array of qwords (long) reference or NULL. ;
;          Parm#4 = R9  = Object OPB array of qwords (long) reference or NULL. ;
;          Parm#5 = QWORD [ RSP + 40] = IPB size, qwords,                      ; 
;                   or function code if IPB=NULL.                              ;
;          Parm#6 = QWORD [ RSP + 48] = OPB size, qwords,                      ; 
;                   or reserved if OPB=NULL.                                   ;
;                                                                              ;
; OUTPUT:  Return = EAX = JNI Status: 0=Error, or OS/Library NATIVE_ID = OK.   ;
;                                                                              ;
;------------------------------------------------------------------------------;
requestBinary:    
push rbx rsi rdi r12 r13 r14 r15 rbp  ; Save non-volatile registers.
mov rbp,rsp                           ; Save RSP because stack alignment.
xor eax,eax
push rax rax                          ; Storage for variables.
mov rbx,rcx                           ; RBX = Environment.
mov r12,r8                            ; R12 = Object: Input Parm. Block.
mov r13,r9                            ; R13 = Object: Output Parm. Block.
mov r14,[rbp + 64 + 8 + 32 + 0]       ; R14 = Length of IPB (parm#5).
mov r15,[rbp + 64 + 8 + 32 + 8]       ; R15 = Length of OPB (parm#6).
and rsp,0FFFFFFFFFFFFFFF0h            ; Stack alignment by calling convention.
sub rsp,32                            ; Parm. shadow by calling convention.
xor esi,esi                           ; Pre-blank IPB pointer.
xor edi,edi                           ; Pre-blank OPB pointer.
;---------- Check IPB presence. -----------------------------------------------;
test r12,r12
jz .skipIpb                       ; Go skip IPB extraction if IPB = null.
mov rdx,r12
lea r8,[rbp - 8]
mov rax,[rbx]                     ; RAX = Pointer to functions table.
;---------- Get IPB, parms: RCX=env, RDX=IPB Object, R8=Pointer to flag. ------;
call qword [rax + 188*8]          ; JNI call [GetLongArrayElements]
test rax,rax
jz .statusReturn                  ; Go skip if error = NULL pointer
xchg rsi,rax                      ; RSI = Pointer to IPB.
.skipIpb:
;---------- Check OPB presence. -----------------------------------------------;
test r13,r13
jz .skipOpb                       ; Go skip IPB extraction if OPB=null.
mov rcx,rbx
mov rdx,r13
lea r8,[rbp - 16]
mov rax,[rbx]                     ; RAX = Pointer to functions table.
;---------- Get OPB, parms: RCX=env, RDX=OPB Object, R8=Pointer to flag. ------;
call qword [rax + 188*8]          ; JNI call [GetLongArrayElements].
test rax,rax
jz .statusReturn                  ; Go skip if error = NULL pointer.
xchg rdi,rax                      ; RDI = Pointer to OPB.
.skipOpb: 
;---------- Prepare for target operation. -------------------------------------;
test rsi,rsi
jz .absentIpb                     ; Go special case, IPB size = function.
;---------- Handling IPB present. ---------------------------------------------;
xor eax,eax
mov r10d,[rsi]                    ; DWORD IPB[0] = Function selector. 
cmp r10d,MAX_REQUEST
ja .releaseReturn
call qword [RequestHandlers + r10*8]
;---------- Return point. -----------------------------------------------------;
.releaseReturn:
;---------- Check IPB release requirement flag and IPB presence. --------------;
cmp qword [rbp - 8],0
je .skipReleaseIpb                ; Go skip if IPB release not required.
test r12,r12
jz .skipReleaseIpb                ; Go skip IPB extraction if IPB=null.
mov rcx,rbx
mov rdx,r12
mov r8,rsi
xor r9d,r9d
mov rax,[rbx]                     ; RAX = Pointer to functions table.
;--- Release IPB, parms: RCX=env, RDX=obj, R8=Pointer, R9=Release mode. -------; 
call qword [rax + 196*8]          ; call [ReleaseLongArrayElements]
.skipReleaseIpb:
;---------- Check OPB release requirement flag and OPB presence. --------------;
cmp qword [rbp - 16],0
je .skipReleaseOpb                ; Go skip if OPB release not required.
test r13,r13
jz .skipReleaseOpb                ; Go skip OPB extraction if OPB=null.
mov rcx,rbx
mov rdx,r13
mov r8,rdi
xor r9d,r9d
mov rax,[rbx]                     ; RAX = Pointer to functions table.
;--- Release OPB, parms: RCX=env, RDX=obj, R8=Pointer, R9=Release mode. -------; 
call qword [rax + 196*8]          ; call [ReleaseLongArrayElements]
.skipReleaseOpb:
;---------- Return with status = RAX ------------------------------------------;
mov eax,NATIVE_ID            ; EAX = NATIVE_ID (true) means OK from Win64 DLL. 
.statusReturn:                    ; Entry point with RAX = 0 (error).
leave                             ; Restore RSP after alignment.
pop r15 r14 r13 r12 rdi rsi rbx   ; Restore non-volatile registers.
ret                               ; Return to Java JNI service caller. 
;---------- Special fast case, no Input Parameters Block. ---------------------;
.absentIpb:
xor eax,eax
cmp r14,MAX_REQUEST               ; QWORD R14 = Function selector. 
ja @f
call qword [RequestHandlers + r14*8]
@@:
jmp .releaseReturn

;------------------------------------------------------------------------------;
;        Requests handlers, runs under JNI gate, can use IPB and OPB.          ;
;      Note. IPB = Input Parameters Block, OPB = Output Parameters Block.      ;
;------------------------------------------------------------------------------;
include 'handlers\GetCpuid.inc'
include 'handlers\GetCpuidSubfunction.inc'
include 'handlers\GetCpuidAffinized.inc'
include 'handlers\GetCpuidSubfunctionAffinized.inc'
include 'handlers\GetOsContext.inc'
include 'handlers\MeasureTscFrequency.inc'
include 'handlers\GetPlatformInfo.inc'
include 'handlers\GetTopology.inc'
include 'handlers\GetExtendedTopology.inc'

section '.data' data readable writeable
align 8
RequestHandlers:
DQ  GetCpuid
DQ  GetCpuidSubfunction
DQ  GetCpuidAffinized
DQ  GetCpuidSubfunctionAffinized
DQ  GetOsContext
DQ  MeasureTscFrequency
DQ  GetPlatformInfo
DQ  GetTopology
DQ  GetExtendedTopology

NameDll                             DB  'KERNEL32.DLL'                     , 0
NamesFunctions:
FnSetThreadAffinityMask             DB  'SetThreadAffinityMask'            , 0
FnGetLogicalProcessorInformation    DB  'GetLogicalProcessorInformation'   , 0
FnGetLogicalProcessorInformationEx  DB  'GetLogicalProcessorInformationEx' , 0
                                    DB  0  ; This 0 means end of list.
align 8
LoadPointers:
_SetThreadAffinityMask              DQ  ?
_GetLogicalProcessorInformation     DQ  ?
_GetLogicalProcessorInformationEx   DQ  ?

section '.edata' export data readable
export 'WIN64JNI.dll' , \
detectBinary   , 'Java_cpuidv3_pal_PAL_detectBinary'  , \
initBinary     , 'Java_cpuidv3_pal_PAL_initBinary'    , \
deinitBinary   , 'Java_cpuidv3_pal_PAL_deinitBinary'  , \
requestBinary  , 'Java_cpuidv3_pal_PAL_requestBinary'

section '.idata' import data readable writeable
library kernel32 , 'KERNEL32.DLL' , advapi32 , 'ADVAPI32.DLL'
include 'api\kernel32.inc'
include 'api\advapi32.inc'
 
data fixups
end data

