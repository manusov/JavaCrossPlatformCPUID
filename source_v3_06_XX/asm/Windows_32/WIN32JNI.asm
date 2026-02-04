;==============================================================================;
;                                                                              ;
;                      Java cross-platform CPUID Utility.                      ;
;        https://github.com/manusov/JavaCrossPlatformCPUID/tree/master         ;
;                  https://github.com/manusov?tab=repositories                 ;
;                 No copyright. Information belongs to Universe.               ;
;                                                                              ;
; Native library for Java cross-platform CPUID Utility. Windows ia32 variant.  ;
;                 This is FASM (Flat Assembler) source file.                   ;
;                                                                              ; 
;==============================================================================;

include 'win32a.inc'
format PE GUI 4.0 DLL
entry DllMain
section '.text' code readable executable

NATIVE_ID    EQU  120h
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
call LoadWinApi
jc .skipApi
or bl,00010000b
.skipApi:
;---------- For Win32 application only: check WoW64 mode. ---------------------;
call [GetCurrentProcess]
test eax,eax
jz .skipWow
mov ecx,[_IsWow64Process]
jecxz .skipWow  
push 0               ; PUSH variable, updated by function. 
mov edx,esp
push edx             ; Parm#2 = Pointer to flag.
push eax             ; Parm#1 = Process handle.
call ecx
pop ecx              ; POP variable, updated by function.
test eax,eax
jz .skipWow
jecxz .skipWow 
inc ebx
.skipWow:
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
;---------- Dynamical import required WinAPI functions. -------------;  
;                                                                    ;
; INPUT:  None.                                                      ;  
;                                                                    ;
; OUTPUT:  CF flag: 0(NC) = Dynamical import executed OK,            ;
;                   but pointers requires checks: can be zero.       ;
;                   1(C)  = Dynamical import fatal error.            ;
;--------------------------------------------------------------------;
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
je .exit           ; Go if end of functions list.
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
; INPUT:   Parm#1 = [ ESP + 04 ] = JNI Environment.                            ;  
;          Parm#2 = [ ESP + 08 ] = JNI This Object reference,                  ;
;                                  (not used by this routine).                 ;
;          Parm#3 = [ ESP + 12 ] = Object IPB array of qwords (long) reference ;
;                                  or NULL if not used.                        ;
;          Parm#4 = [ ESP + 16 ] = Object OPB array of qwords (long) reference ;
;                                  or NULL if not used.                        ;
;          Parm#5 = [ ESP + 20 ] = IPB size, qwords,                           ; 
;                                  or function code if IPB=NULL.               ;
;          Parm#6 = [ ESP + 24 ] = OPB size, qwords,                           ; 
;                                  or reserved if OPB=NULL.                    ;
;                                                                              ;
; OUTPUT:  Return = EAX = JNI Status: 0=Error, or OS/Library NATIVE_ID = OK.   ;
;                                                                              ;
; Remember about 6*4=24 bytes must be removed from stack when return (RET 24), ;
; because required by IA32 calling convention.                                 ;
;                                                                              ;
;------------------------------------------------------------------------------;
requestBinary:    
push ebx esi edi ebp        ; Save non-volatile registers.
xor eax,eax
push eax eax                ; Reserve space for variables.
mov ebp,esp                 ; EBP = Frame, parm #1 at [ebp + 28].
xor esi,esi                 ; Pre-blank IPB pointer.
xor edi,edi                 ; Pre-blank OPB pointer.
;---------- Check IPB presence. -----------------------------------------------;
mov ecx,[ebp + 36]          ; ECX = IPB object.
jecxz .skipIpb              ; Go skip IPB extraction if IPB = null.
mov ebx,[ebp + 28]          ; EBX = environment.
mov eax,[ebx]               ; EAX = Pointer to functions table.
push ebp ecx ebx  
;---------- Get IPB, parms: environment, IPB Object, Pointer to flag. ---------;
call dword [eax + 188*4]    ; JNI call [GetLongArrayElements].
test eax,eax
jz .statusReturn            ; Go skip if error = NULL pointer.
xchg esi,eax                ; ESI = Pointer to IPB.
.skipIpb:
;---------- Check OPB presence. -----------------------------------------------;
mov ecx,[ebp + 40]          ; ECX = OPB object.
jecxz .skipOpb              ; Go skip OPB extraction if OPB = null.
mov ebx,[ebp + 28]          ; EBX = environment.
mov eax,[ebx]               ; EAX = Pointer to functions table.
lea edx,[ebp + 4]
push edx ecx ebx  
;---------- Get OPB, parms: env, OPB Object, Pointer to flag. -----------------;
call dword [eax + 188*4]    ; JNI call [GetLongArrayElements].
test eax,eax
jz .statusReturn            ; Go skip if error = NULL pointer.
xchg edi,eax                ; EDI = Pointer to OPB.
.skipOpb:
;---------- Target operation. -------------------------------------------------;
test esi,esi
jz .absentIpb
;---------- Handling IPB present. ---------------------------------------------;
xor eax,eax
mov ecx,[esi]
cmp ecx,MAX_REQUEST
ja .releaseReturn
call dword [RequestHandlers + ecx*4]
;---------- Return point. -----------------------------------------------------;
.releaseReturn:
;---------- Check IPB release requirement flag and IPB presence. --------------;
cmp dword [ebp],0
je .skipReleaseIpb          ; Go skip if IPB release not required.
mov ecx,[ebp + 36]          ; ECX = IPB object.
jecxz .skipReleaseIpb       ; Go skip IPB release if IPB = null.
mov ebx,[ebp + 28]          ; EBX = environment.
mov eax,[ebx]               ; EAX = Pointer to functions table.
push 0 esi ecx ebx  
;--- Release IPB, parms: env, obj, Pointer, Release mode --- 
call dword [eax + 196*4]    ; call [ReleaseLongArrayElements].
.skipReleaseIpb:
;---------- Check OPB release requirement flag and OPB presence. --------------;
cmp dword [ebp + 4],0
je .skipReleaseOpb          ; Go skip if OPB release not required.
mov ecx,[ebp + 40]          ; ECX = OPB object.
jecxz .skipReleaseOpb       ; Go skip OPB release if OPB = null.
mov ebx,[ebp + 28]          ; EBX = environment.
mov eax,[ebx]               ; EAX = Pointer to functions table.
push 0 edi ecx ebx  
;---------- Release OPB, parms: env, obj, Pointer, Release mode. --------------; 
call dword [eax + 196*4]    ; call [ReleaseLongArrayElements].
.skipReleaseOpb:
;---------- Return with status = EAX. -----------------------------------------;
mov eax,NATIVE_ID            ; EAX = NATIVE_ID (true) means OK from Win32 DLL. 
.statusReturn:               ; Entry point with EAX=0 (error).
pop ecx ecx ebp edi esi ebx  ; Clear stack and restore non-volatile registers.
ret 24                       ; Return to Java JNI service caller. 
;---------- Special fast case, no Input Parameters Block. ---------------------;
.absentIpb:
xor eax,eax
mov ecx,[ebp+44]
cmp ecx,MAX_REQUEST
jae @f
call dword [RequestHandlers + ecx*4]   ; ECX = Function selector.
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

NameDll                             DB  'KERNEL32.DLL'                     , 0
NamesFunctions:
FnSetThreadAffinityMask             DB  'SetThreadAffinityMask'            , 0
FnGetLogicalProcessorInformation    DB  'GetLogicalProcessorInformation'   , 0
FnGetLogicalProcessorInformationEx  DB  'GetLogicalProcessorInformationEx' , 0
FnIsWow64Process                    DB  'IsWow64Process'                   , 0
                                    DB  0  ; This 0 means end of list.
align 4
LoadPointers:
_SetThreadAffinityMask              DD  ?
_GetLogicalProcessorInformation     DD  ?
_GetLogicalProcessorInformationEx   DD  ?
_IsWow64Process                     DD  ?

section '.edata' export data readable
export 'WIN32JNI.dll' ,\
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

