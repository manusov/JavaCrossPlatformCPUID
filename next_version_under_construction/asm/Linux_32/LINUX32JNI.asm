;==============================================================================;
;                                                                              ;
;                      Java cross-platform CPUID Utility.                      ;
;        https://github.com/manusov/JavaCrossPlatformCPUID/tree/master         ;
;                  https://github.com/manusov?tab=repositories                 ;
;                 No copyright. Information belongs to Universe.               ;
;                                                                              ;
;  Native library for Java cross-platform CPUID Utility. Linux ia32 variant.   ;
;                 This is FASM (Flat Assembler) source file.                   ;
;                                                                              ; 
;              https://github.com/manusov/JavaCrossPlatformCPUID               ;
;                 https://github.com/manusov?tab=repositories                  ;
;                No copyright. Information belongs to Universe.                ;
;                                                                              ;
;==============================================================================;

format ELF

public detectBinary   as  'Java_cpuidv3_services_PAL_detectBinary'
public initBinary     as  'Java_cpuidv3_services_PAL_initBinary'  
public deinitBinary   as  'Java_cpuidv3_services_PAL_deinitBinary'
public requestBinary  as  'Java_cpuidv3_services_PAL_requestBinary'  

NATIVE_ID             EQU  220h
MAX_REQUEST           EQU  8

;---------- Linux API functions ( syscall numbers ). --------------------------; 
SYS_EXIT              EQU  1       ; Linux API functions ( syscall numbers ).
SYS_READ              EQU  3
SYS_WRITE             EQU  4
SYS_OPEN              EQU  5
SYS_CLOSE             EQU  6
SYS_MMAP              EQU  9
SYS_UNLINK            EQU  10
SYS_MUNMAP            EQU  11
SYS_LSEEK             EQU  19
SYS_GETPID            EQU  20
SYS_IOCTL             EQU  54
SYS_NANOSLEEP         EQU  162
SYS_GETTIME           EQU  228
SYS_GETRES            EQU  229
SYS_SETAFFINITY       EQU  241
SYS_GETAFFINITY       EQU  242
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
push ebx
mov ebx,055AA0000h
call CheckCpuid
jc .skipCpuid
mov bl,00000010b
cmp eax,1
jb .skipCpuid 
mov eax,1
push ebx
cpuid
pop ebx
test dl,00000001b    ; CPUID.1.EDX.0 = FPU.
jz .skipFpu
or bl,00000100b
.skipFpu:
test dl,00010000b    ; CPUID.1.EDX.4 = TSC.
jz .skipCpuid
or bl,00001000b
.skipCpuid:
xchg eax,ebx         ; EAX = result, note this XCHG compact than MOV.
pop ebx
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
; INPUT:   Parm#1 = DWORD [ESP + 04] = JNI Environment.                        ;
;          Parm#2 = DWORD [ESP + 08] = JNI This Object reference               ;
;                                      (not used here).                        ;
;          Parm#3 = DWORD [ESP + 12] = Object IPB array of qwords (long)       ;
;                                      reference or NULL.                      ;
;          Parm#4 = DWORD [ESP + 16] = Object OPB array of qwords (long)       ;
;                                      reference or NULL.                      ;
;          Parm#5 = QWORD [ESP + 20] = IPB size, qwords,                       ;
;                                      or function code if IPB = NULL.         ;
;          Parm#6 = QWORD [ESP + 28] = OPB size, qwords,                       ;
;                                      or reserved if OPB = NULL.              ;
;                                                                              ;
; OUTPUT:  Return = EAX = JNI Status: 0=Error, or OS/Library NATIVE_ID = OK.   ;
;                                                                              ;
;------------------------------------------------------------------------------;
; Stack layout at function call, as DWORDS.      ;
; [ESP + 00] = IP for return from subroutine.    ;
; [ESP + 04] = JNI Environment.                  ;
; [ESP + 08] = JNI This Object reference.        ;
; [ESP + 12] = Object IPB array of QWORDs.       ;
; [ESP + 16] = Object OPB array of QWORDs.       ;
; [ESP + 20] = IPB size, low dword.              ;
; [ESP + 24] = IPB size, high dword, usually 0.  ;
; [ESP + 28] = OPB size, low dword.              ;
; [ESP + 32] = OPB size, high dword, usually 0.  ;
;------------------------------------------------;
requestBinary:    
push ebx ecx edx esi edi ebp
mov ebp,esp
xor eax,eax
push eax eax             ; Reserve stack space for variables.
and esp,0FFFFFFF0h       ; Align stack.
xor esi,esi              ; Pre-blank IPB pointer.
xor edi,edi              ; Pre-blank OPB pointer.
;---------- Check IPB presence. -----------------------------------------------;
mov ecx,[ebp + 12 + 24]  ; Parm#2 = Array reference.
jecxz .skipIpb           ; Go skip IPB extract. if IPB = null.
mov ebx,[ebp + 04 + 24]  ; Parm#1 = Environment.
lea edx,[ebp - 4]        ; Parm#3 = isCopyAddress.
mov eax,[ebx]            ; EAX = Pointer to functions table.
;--- Get IPB, parms: EBX=environment, ECX=IPB Object, EDX=Pointer to flag. ----;
push edx ecx ebx
call dword [eax+188*4]   ; JNI call [GetLongArrayElements].
add esp,12
test eax,eax
jz .statusReturn         ; Go skip if error = NULL pointer.
xchg esi,eax             ; ESI = Pointer to IPB.
.skipIpb:
;---------- Check OPB presence. -----------------------------------------------;
mov ecx,[ebp + 16 + 24]   ; Parm#2 = Array reference.
jecxz .skipOpb            ; Go skip OPB extract. if OPB = null.
mov ebx,[ebp + 04 + 24]   ; Parm#1 = Environment.
lea edx,[ebp-8]           ; Parm#3 = isCopyAddress.
mov eax,[ebx]             ; EAX = Pointer to functions table.
;--- Get OPB, parms: EBX=environment, ECX=IPB Object, EDX=Pointer to flag -----;
push ebp ebp esi esi      ; Push twice for alignment 16.
push edx ecx ebx
call dword [eax + 188*4]  ; JNI call [GetLongArrayElements].
add esp,12
pop esi esi ebp ebp
test eax,eax
jz .statusReturn          ; Go skip if error = NULL pointer.
xchg edi,eax              ; EDI = Pointer to OPB.
.skipOpb:
;---------- Target operation. -------------------------------------------------;
test esi,esi
jz .absentIpb             ; Go spec. case, IPB size = function.
;---------- Handling IPB present. ---------------------------------------------;
xor eax,eax
mov edx,[esi]                 ; DWORD IPB[0] = Function selector. 
cmp edx,MAX_REQUEST
jae .releaseReturn
lea ecx,[RequestHandlers]     ; Relocatable address in the RCX must be
call dword [ecx + edx*4]      ; adjustable by *.SO maker.
;---------- Return point. -----------------------------------------------------;
.releaseReturn:
;---------- Check IPB release requirement flag and IPB presence. --------------;
cmp dword [ebp - 4],0
je .skipReleaseIpb            ; Go skip if IPB release not required.
mov ecx,[ebp + 12 + 24]       ; Parm#2 = Array reference.
jecxz .skipReleaseIpb         ; Go skip IPB extract. if IPB=null.
mov ebx,[ebp + 04 + 24]       ; Parm#1 = Environment.
mov edx,esi                   ; Parm#3 = Copy address, note ESI.
xor esi,esi                   ; Parm#4 = Release mode.
mov eax,[ebx]                 ; EAX = Pointer to functions table.
;--- Release IPB, parms: EBX=environment, ECX=obj, EDX=P, ESI=Mode ------------; 
push ebp ebp edi edi          ; Twice for align ESP.
push esi edx ecx ebx 
call dword [eax + 196*4]      ; call [ReleaseLongArrayElements].
add esp,16
pop edi edi ebp ebp
.skipReleaseIpb:
;---------- Check OPB release requirement flag and OPB presence. --------------;
cmp dword [ebp - 8],0
je .skipReleaseOpb           ; Go skip if OPB release not required.
mov ecx,[ebp + 16 + 24]      ; Parm#2 = Array reference.
jecxz .skipReleaseOpb        ; Go skip IPB extract. if IPB = null.
mov ebx,[ebp + 04 + 24]      ; Parm#1 = Environment.
mov edx,edi                  ; Parm#3 = Copy address, note EDI.
xor esi,esi                  ; Parm#4 = Release mode.
mov eax,[ebx]                ; EAX = Pointer to functions table.
;---------- Release OPB, parms: EBX=environment, ECX=obj, EDX=P, ESI=Mode. ----; 
push esi edx ecx ebx 
call dword [eax + 196*4]     ; call [ReleaseLongArrayElements].
add esp,16
.skipReleaseOpb:
;---------- Return with status = EAX. -----------------------------------------;
mov eax,NATIVE_ID        ; EAX = NATIVE_ID (true) means OK from Linux32 *.SO.
.statusReturn:
leave                    ; Restore stack.
pop edi esi edx ecx ebx
ret
;---------- Special fast case, no Input Parameters Block. ---------------------;
.absentIpb:
xor eax,eax
mov edx,[ebp+20+24]          ; EDX = Selector / IPB size place.
cmp edx,MAX_REQUEST          ; DWORD EDX = Function selector. 
jae @f
lea ecx,[RequestHandlers]    ; Relocatable address in the RCX must be
call dword [ecx + edx*4]     ; adjustable by *.SO maker.
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

align 4
RequestHandlers:
DD  GetCpuid
DD  GetCpuidSubfunction
DD  GetCpuidAffinized
DD  GetCpuidSubfunctionAffinized
DD  GetOsContext
DD  MeasureTscFrequency
DD  GetPlatformInfo
DD  GetTopology
DD  GetExtendedTopology

TimespecWait:        ; Seconds=1 and Nanoseconds=0 values, time for wait.
DQ  1, 0         
TimespecRemain:      ; Seconds and Nanoseconds, remain stored if wait interrupted.
DQ  0, 0
TimespecResolution:  ; Seconds and Nanoseconds, timer resolution.
DQ  0, 0 
