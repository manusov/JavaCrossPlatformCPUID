;==============================================================================;
;                                                                              ;
;                      Java cross-platform CPUID Utility.                      ;
;        https://github.com/manusov/JavaCrossPlatformCPUID/tree/master         ;
;                  https://github.com/manusov?tab=repositories                 ;
;                 No copyright. Information belongs to Universe.               ;
;                                                                              ;
;   Native library for Java cross-platform CPUID Utility. Linux x64 variant.   ;
;                 This is FASM (Flat Assembler) source file.                   ;
;                                                                              ; 
;              https://github.com/manusov/JavaCrossPlatformCPUID               ;
;                 https://github.com/manusov?tab=repositories                  ;
;                No copyright. Information belongs to Universe.                ;
;                                                                              ;
;==============================================================================;

format ELF64

public detectBinary   as  'Java_cpuidv3_pal_PAL_detectBinary'
public initBinary     as  'Java_cpuidv3_pal_PAL_initBinary'  
public deinitBinary   as  'Java_cpuidv3_pal_PAL_deinitBinary'
public requestBinary  as  'Java_cpuidv3_pal_PAL_requestBinary'  

NATIVE_ID             EQU  240h
MAX_REQUEST           EQU  8

;---------- Linux API functions ( syscall numbers ). --------------------------; 
SYS_READ              EQU  0       ; Linux API functions ( syscall numbers ).
SYS_WRITE             EQU  1
SYS_OPEN              EQU  2
SYS_CLOSE             EQU  3
SYS_LSEEK             EQU  8
SYS_MMAP              EQU  9
SYS_MUNMAP            EQU  11
SYS_IOCTL             EQU  16
SYS_NANOSLEEP         EQU  35
SYS_GETPID            EQU  39
SYS_EXIT              EQU  60
SYS_UNLINK            EQU  87
SYS_GETTIME           EQU  228
SYS_GETRES            EQU  229
SYS_SETAFFINITY       EQU  203
SYS_GETAFFINITY       EQU  204
SYS_SETMEMPOLICY      EQU  238     ; Note alternative codes.
SYS_GETMEMPOLICY      EQU  239     ; Note alternative codes.

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
;                         This bit not used and always "0" for Linux variant.  ; 
;                EAX[3] = TSC support verified OK by CPUID.                    ;
;                EAX[2] = x87 FPU support verified OK by CPUID.                ; 
;                EAX[1] = CPUID support verified OK by EFLAGS.21 bit.          ;
;                EAX[0] = WoW64 flag, "1" means Win32 application under Win64. ;
;                         This bit not used and always "0" for Linux variant.  ;
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
; INPUT:   Parm#1 = RDI = JNI Environment.                                     ;  
;          Parm#2 = RSI = JNI This Object reference (not used here).           ;
;          Parm#3 = RDX = Object IPB array of qwords (long) reference or NULL. ;
;          Parm#4 = RCX = Object OPB array of qwords (long) reference or NULL. ;
;          Parm#5 = R8  = IPB size, qwords, or function code if IPB=NULL.      ;
;          Parm#6 = R9 = OPB size, qwords, or reserved if OPB=NULL.            ;
;                                                                              ;
; OUTPUT:  Return = EAX = JNI Status: 0=Error, or OS/Library NATIVE_ID = OK.   ;
;                                                                              ;
;------------------------------------------------------------------------------;
requestBinary:    
push rbx r12 r13 r14 r15 rbp
mov rbp,rsp                 ; Save RSP because alignment.
xor eax,eax
push rax rax                ; Reserve stack space for variables.
mov rbx,rdi                 ; RBX = Environment.
mov r12,rdx                 ; R12 = Object: Input Parm. Block.
mov r13,rcx                 ; R13 = Object: Output Parm. Block.
mov r14,r8                  ; R14 = Length of IPB (parm#5).
mov r15,r9                  ; R15 = Length of OPB (parm#6).
and rsp,0FFFFFFFFFFFFFFF0h  ; Stack alignment by calling convention.
xor esi,esi                 ; Pre-blank IPB pointer.
xor edi,edi                 ; Pre-blank OPB pointer.
;---------- Check IPB presence. -----------------------------------------------;
test r12,r12
jz .skipIpb                 ; Go skip IPB extract. if IPB = null.
mov rdi,rbx                 ; Parm#1 = Environment.
mov rsi,r12                 ; Parm#2 = Array reference.
lea rdx,[rbp - 8]           ; Parm#3 = isCopyAddress.
mov rax,[rbx]               ; RAX = Pointer to functions table.
;---------- Get IPB, parms: RDI=env, RSI=IPB Object, RDX=Pointer to flag. -----;
call qword [rax + 188*8]    ; JNI call [GetLongArrayElements].
test rax,rax
jz .statusReturn            ; Go skip if error = NULL pointer.
xchg rsi,rax                ; RSI = Pointer to IPB.
.skipIpb:
;---------- Check OPB presence. -----------------------------------------------;
test r13,r13
jz .skipOpb                 ; Go skip OPB extraction if OPB = null.
push rsi rsi                ; Store IPB, twice for align RSP.
mov rdi,rbx                 ; Parm#1 = Environment.
mov rsi,r13                 ; Parm#2 = Array reference.
lea rdx,[rbp - 16]          ; Parm#3 = isCopyAddress. 
mov rax,[rbx]               ; RAX = Pointer to functions table.
;---------- Get OPB, parms: RDI=env, RSI=OPB Object, RDX=Pointer to flag. -----;
call qword [rax + 188*8]    ; JNI call [GetLongArrayElements].
pop rsi rsi
test rax,rax
jz .statusReturn            ; Go skip if error = NULL pointer.
xchg rdi,rax                ; RDI = Pointer to OPB.
.skipOpb: 
;---------- Target operation --------------------------------------------------;
test rsi,rsi
jz .absentIpb               ; Go spec. case, IPB size = function.
;---------- Handling IPB present. ---------------------------------------------;
xor eax,eax
mov r10d,[rsi]              ; DWORD IPB[0] = Function selector. 
cmp r10d,MAX_REQUEST
ja .releaseReturn
lea rcx,[RequestHandlers]   ; Relocatable address in the RCX must be
call qword [rcx + r10*8]    ; adjustable by *.SO maker. 
;---------- Return point ------------------------------------------------------;
.releaseReturn:
;---------- Check IPB release requirement flag and IPB presence. --------------;
cmp qword [rbp - 8],0
je .skipReleaseIpb          ; Go skip if IPB release not requered.
test r12,r12
jz .skipReleaseIpb          ; Go skip IPB extract. if IPB = null.
push rdi rdi                ; Store OPB, twice for align RSP.
mov rdi,rbx                 ; Parm#1 = Environment. 
mov rdx,rsi                 ; Parm#3 = Copy address, note RSI.
mov rsi,r12                 ; Parm#2 = Object reference.
xor ecx,ecx                 ; Parm#4 = Release mode.
mov rax,[rbx]               ; RAX = Pointer to functions table.
;---------- Release IPB, parms: RDI=env, RSI=obj, RDX=P, RCX=Mode. ------------; 
call qword [rax + 196*8]    ; call [ReleaseLongArrayElements].
pop rdi rdi
.skipReleaseIpb:
;---------- Check OPB release requirement flag and OPB presence. --------------;
cmp qword [rbp - 16],0
je .skipReleaseOpb          ; Go skip if OPB release not requered.
test r13,r13
jz .skipReleaseOpb          ; Go skip OPB extract. if OPB = null.
mov rdx,rdi                 ; Parm#3 = Copy address, note RDI.
mov rdi,rbx                 ; Parm#1 = Environment. 
mov rsi,r13                 ; Parm#2 = Object reference.
xor ecx,ecx                 ; Parm#4 = Release mode.
mov rax,[rbx]               ; RAX = Pointer to functions table.
;---------- Release OPB, parms: RDI=env, RSI=obj, RDX=P, RCX=Mode. ------------; 
call qword [rax + 196*8]    ; call [ReleaseLongArrayElements].
.skipReleaseOpb:
;---------- Return with status = RAX. -----------------------------------------;
mov eax,NATIVE_ID        ; EAX = NATIVE_ID (true) means OK from Linux64 *.SO.
.statusReturn:
leave                    ; Restore stack
pop r15 r14 r13 r12 rbx
ret
;---------- Special fast case, no Input Parameters Block. ---------------------;
.absentIpb:
xor eax,eax
cmp r14,MAX_REQUEST         ; QWORD R14 = Function selector. 
jae @f
lea rcx,[RequestHandlers]   ; Relocatable address in the RCX must be
call qword [rcx + r14*8]    ; adjustable by *.SO maker.
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

TimespecWait:        ; Seconds=1 and Nanoseconds=0 values, time for wait.
DQ  1, 0         
TimespecRemain:      ; Seconds and Nanoseconds, remain stored if wait interrupted.
DQ  0, 0
TimespecResolution:  ; Seconds and Nanoseconds, timer resolution.
DQ  0, 0 
