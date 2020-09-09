;------------------------------------------------------------------------------;
;                Native Binary Library for Windows ia32                        ;
;            JNI DLL (Java Native Interface Dynamical Load Library)            ;
; Note. Kernel Mode Support functions removed, see previous library versions.  ;
;                                                                              ;
;                   Updated at CPUID v1.00.00 refactoring.                     ; 
;------------------------------------------------------------------------------;

include 'win32a.inc'
format PE GUI 4.0 DLL
entry DllMain

;---------- Code section -------------------------------------------------------
section '.text' code readable executable

DllMain:        ; This called by Operating System when load/unload DLL
mov eax,1       ; Return status to OS caller (actual when load)
ret

;--- This simple entry point for debug native call mechanism ---

checkBinary:              ; also differentiate between Win32 and Win32/WOW64
;--- Detect WOW64 ---
push ebx
push LibName
call [GetModuleHandle]
test eax,eax
jz @f
push FncName eax
call [GetProcAddress]
test eax,eax
jz @f
xchg ebx,eax
call [GetCurrentProcess]
push 0
mov ecx,esp
push ecx eax
call ebx
pop eax
@@:
pop ebx
;--- Send result ---
test eax,eax
mov eax,32
jz @f
inc eax
@@:
ret

;--- Entry point for binary services, Java Native Interface (JNI) ---
; Parm#1 = [ESP+04] = JNI Environment  
; Parm#2 = [ESP+08] = JNI This Object reference (not used by this routine)
; Parm#3 = [ESP+12] = Object IPB array of qwords (long) reference or NULL
; Parm#4 = [ESP+16] = Object OPB array of qwords (long) reference or NULL
; Parm#5 = [ESP+20] = IPB size, qwords, or function code if IPB=NULL
; Parm#6 = [ESP+24] = OPB size, qwords, or reserved if OPB=NULL
; Return = EAX = JNI Status, 0=Error, 1=IA32 OK, 2=x64 OK
; Remember about 6*4=24 bytes must be removed from stack when return (RET 24),
; because required by IA32 calling convention.

entryBinary:
push ebx esi edi ebp                   ; Save non-volatile registers
xor eax,eax
push eax eax                           ; Reserve space for variables
mov ebp,esp                            ; EBP=Frame, parm #1 at [ebp+28]
xor esi,esi                            ; Pre-blank IPB pointer
xor edi,edi                            ; Pre-blank OPB pointer
;--- Check IPB presence ---
mov ecx,[ebp+36]                       ; ECX = IPB object
jecxz @f                               ; Go skip IPB extraction if IPB=null
mov ebx,[ebp+28]                       ; EBX = environment
mov eax,[ebx]                          ; EAX = Pointer to functions table
push ebp ecx ebx  
;--- Get IPB, parms: env, IPB Object, Pointer to flag ---
call dword [eax+188*4]                 ; JNI call [GetLongArrayElements]
test eax,eax
jz StatusRet                           ; Go skip if error = NULL pointer
xchg esi,eax                           ; ESI = Pointer to IPB
@@:
;--- Check OPB presence ---
mov ecx,[ebp+40]                       ; ECX = OPB object
jecxz @f                               ; Go skip IPB extraction if OPB=null
mov ebx,[ebp+28]                       ; EBX = environment
mov eax,[ebx]                          ; EAX = Pointer to functions table
lea edx,[ebp+4]
push edx ecx ebx  
;--- Get OPB, parms: env, OPB Object, Pointer to flag ---
call dword [eax+188*4]                 ; JNI call [GetLongArrayElements]
test eax,eax
jz StatusRet                           ; Go skip if error = NULL pointer
xchg edi,eax                           ; EDI = Pointer to OPB
@@:
;--- Target operation ---
test esi,esi
jz IPB_null
;--- Handling IPB present ---
xor eax,eax
mov ecx,[esi]
cmp ecx,iFunctionCount
jae @f
call dword [iFunctionSelector+ecx*4]
@@:
;--- Return point ---
ReleaseRet:
;--- Check IPB release requirement flag and IPB presence ---
cmp dword [ebp],0
je @f                                  ; Go skip if IPB release not required
mov ecx,[ebp+36]                       ; ECX = IPB object
jecxz @f                               ; Go skip IPB release if IPB=null
mov ebx,[ebp+28]                       ; EBX = environment
mov eax,[ebx]                          ; EAX = Pointer to functions table
push 0 esi ecx ebx  
;--- Release IPB, parms: env, obj, Pointer, Release mode --- 
call dword [eax+196*4]                 ; call [ReleaseLongArrayElements]
@@:
;--- Check OPB release requirement flag and OPB presence ---
cmp dword [ebp+4],0
je @f                                  ; Go skip if OPB release not required
mov ecx,[ebp+40]                       ; EDX = OPB object
jecxz @f                               ; Go skip OPB release if OPB=null
mov ebx,[ebp+28]                       ; EBX = environment
mov eax,[ebx]                          ; EAX = Pointer to functions table
push 0 edi ecx ebx  
;--- Release OPB, parms: env, obj, Pointer, Release mode --- 
call dword [eax+196*4]                 ; call [ReleaseLongArrayElements]
@@:
;--- Return with status = EAX ---
mov eax,1                              ; RAX=1 (true) means OK from Win32 DLL 
StatusRet:                             ; Entry point with RAX=0 (error)
pop ecx ecx ebp edi esi ebx            ; Restore non-volatile registers
ret 24                                 ; Return to Java JNI service caller 
;--- Special fast case, no Input Parameters Block ---
IPB_null:
xor eax,eax
mov ecx,[ebp+44]
cmp ecx,FunctionCount
jae @f
call dword [FunctionSelector+ecx*4]
@@:
jmp ReleaseRet

;---------- Get CPUID dump ----------------------------------------------------;
; Parm#1 = EDI = Pointer to buffer for status and dump data                    ;
; Output = EAX = Status: 0=Error, Non-Zero=OK, set external. at transit caller ;
;          Buffer DWORD[0] = Number of entries returned                        ;
;          Bytes [4-31] = Reserved for alignment                               ;
;          Bytes [32-16383] = Buffer, maximum (16384-32)/32 = 511 entries ret. ;  
;------------------------------------------------------------------------------;
GetCPUID:
push edi                 ; (+1)
cld
mov ecx,8
xor eax,eax
rep stosd
push edi                 ; Parm#1
call Internal_GetCPUID
pop edi                  ; (-1)
mov [edi],eax
ret

ENTRIES_LIMIT = 511    ; Maximum number of output buffer 16352 bytes = 511*32
;---------- Target subroutine -------------------------------------------------;
; INPUT:  Parameter#1 = [esp+4] = Pointer to output buffer
; OUTPUT: RAX = Number of output entries
;         Output buffer updated
;---
; Output buffer maximum size is 16352 bytes, 511 entries * 32 bytes
; Each entry is 32 bytes, 8 dwords:
; dword   offset in entry(hex)   comments
;--------------------------------------------------------------------------
;   0     00-03                  Information type tag, 0 for CPUID info                  
;   1     04-07                  CPUID function number
;   2     08-0B                  CPUID subfunction number
;   3     0C-0F                  CPUID pass number (as for function #2)
;   4     10-13                  Result EAX after CPUID
;   5     14-17                  Result EBX after CPUID
;   6     18-1B                  Result ECX after CPUID
;   7     1C-1F                  Result EDX after CPUID
;---
Internal_GetCPUID:
;---------- Initializing ------------------------------------------------------;

temp_r8   EQU  dword [ebp+00]  ; this for porting from x64 code
temp_r9   EQU  dword [ebp+04]
temp_r10  EQU  dword [ebp+08]
temp_ebp  EQU  dword [ebp+12]

cld
;--- Store registers ---
push ebx ebp esi edi
;--- Variables pool ---
xor eax,eax
push eax eax eax eax
mov ebp,esp
;--- Start ---
mov edi,[esp+32+4]
mov temp_ebp,0            ; xor ebp,ebp ; EBP = Global output entries counter
call CheckCPUID            ; Return CF=Error flag, EAX=Maximum standard function
jc NoCpuId

;---------- Get standard CPUID results ----------------------------------------;
mov temp_r9,0             ; xor r9d,r9d  ; R9D  = standard functions start
cmp eax,ENTRIES_LIMIT/2   ; EAX = maximum supported standard function number
ja ErrorCpuId             ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction

;---------- Get virtual CPUID results -----------------------------------------;
mov temp_r9,40000000h     ; R9D = virtual functions start
mov eax,temp_r9           ; EAX = Function
xor ecx,ecx               ; ECX = Subfunction
cpuid
and eax,0FFFFFF00h
cmp eax,040000000h
jne NoVirtual             ; Skip virtual CPUID if not supported
mov eax,temp_r9           ; EAX = Limit, yet 1 function 40000000h
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction
NoVirtual:

;---------- Get extended CPUID results ----------------------------------------;
mov temp_r9,80000000h     ; mov r9d,80000000h ; R9D  = extended functions start
mov eax,temp_r9           ; r9d
cpuid
cmp eax,80000000h + ENTRIES_LIMIT/2  ; EAX = maximum extended function number
ja ErrorCpuId                        ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId                        ; Exit if output buffer overflow

;---------- Return points -----------------------------------------------------;
mov eax,temp_ebp          ; Normal exit point, return EAX = number of entries
ExitCpuId:
add esp,16
pop edi esi ebp ebx
ret 4
NoCpuId:                  ; Exit for CPUID not supported, RAX=0  
xor eax,eax
jmp ExitCpuId
ErrorCpuId:               ; Exit for CPUID error, RAX=-1=FFFFFFFFFFFFFFFFh
mov eax,-1
jmp ExitCpuId 

;---------- Subroutine, sequence of CPUID functions ---------------------------;
; INPUT:  R9D = Start CPUID function number
;         EAX = Limit CPUID function number (inclusive)
;         RDI = Pointer to memory buffer
; OUTPUT: RDI = Modified by store CPUID input parms + output parms entry
;         Flags condition code: Carry (C) = means entries count limit
;------------------------------------------------------------------------------;
SequenceCpuId:
mov temp_r10,eax   ; mov r10d,eax ; R10D = standard or extended functions limit 
CycleCpuId:
;--- Specific handling for functions with subfunctions ---
mov eax,temp_r9    ; r9d ; EAX = function number, input at R9D
cmp eax,04h
je Function04
cmp eax,07h
je Function07
cmp eax,0Bh
je Function0B
cmp eax,0Dh
je Function0D
cmp eax,0Fh
je Function0F
cmp eax,10h
je Function10
cmp eax,14h
je Function14
;- Locked for debug -
; cmp eax,17h
; je Function17
; cmp eax,18h
; je Function18
;- End of locked for debug -
cmp eax,8000001Dh
je Function04
cmp eax,80000020h
je Function10
;--- Default handling for functions without subfunctions ---
xor esi,esi               ; ESI = sub-function number for CPUID
xor ecx,ecx               ; ECX = sub-function number for save entry 
call StoreCpuId
ja OverSubFunction
AfterSubFunction:         ; Return point after sub-function specific handler
mov eax,temp_r9
inc eax
mov temp_r9,eax
cmp eax,temp_r10
jbe CycleCpuId            ; Cycle for CPUID standard functions
ret
OverSubFunction:
stc
ret 

;---------- Subroutine, one CPUID function execution --------------------------;
; INPUT:  EAX = CPUID function number
;         R9D = EAX (R8-R15 emulated in memory, because port from x64)
;         ECX = CPUID subfunction number
;         ESI = ECX
;         RDI = Pointer to memory buffer
; OUTPUT: RDI = Modified by store CPUID input parms + output parms entry
;         Flags condition code: Above (A) = means entries count limit
;------------------------------------------------------------------------------;
StoreCpuId:
cpuid
StoreCpuId_Entry:     ; Entry point for CPUID results (EAX,EBX,ECX,EDX) ready 
push eax
xor eax,eax
stosd                 ; Store tag dword[0] = Information type
mov eax,temp_r9       ; r9d
stosd                 ; Store argument dword [1] = CPUID function number 
mov eax,esi
stosd                 ; Store argument dword [2] = CPUID sub-function number
xor eax,eax
stosd                 ; Store argument dword [3] = CPUID pass number (see fn.2)
pop eax
stosd                 ; Store result dword [4] = output EAX 
xchg eax,ebx
stosd                 ; Store result dword [5] = output EBX
xchg eax,ecx
stosd                 ; Store result dword [6] = output ECX
xchg eax,edx
stosd                 ; Store result dword [7] = output EDX
inc temp_ebp          ; ebp ; Global counter +1
cmp temp_ebp,ENTRIES_LIMIT  ; ebp ; Limit for number of output entries
ret
;---------- CPUID function 04h = Deterministic cache parameters ---------------;
Function04:
xor esi,esi           ; ESI = Storage for sub-function number
.L0:
mov eax,temp_r9       ; r9d ; EAX = function number
mov ecx,esi           ; ECX = subfunction number
cpuid
test al,00011111b     ; Check for subfunction list end
jz AfterSubFunction   ; Go if reach first not valid subfunction
call StoreCpuId_Entry
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
jmp .L0               ; Go repeat for next subfunction
;---------- CPUID function 07h = Structured extended feature flags ------------;   
Function07:
xor esi,esi           ; ESI = Storage for sub-function number
mov ecx,esi
mov eax,temp_r9       ; r9d ; EAX = function number (BUGGY DUPLICATED)
cpuid
mov temp_r8,eax       ; r8d,eax ; R8D = Maximal sub-function number
.L0:
mov eax,temp_r9       ; r9d
mov ecx,esi           ; ECX = Current sub-function number
call StoreCpuId
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
cmp esi,temp_r8       ; r8d 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction
;---------- CPUID function 0Bh = Extended topology enumeration ----------------;
Function0B:
xor esi,esi           ; ESI = Storage for sub-function number
.L0:
mov eax,temp_r9       ; r9d ; EAX = function number
mov ecx,esi           ; ECX = subfunction number
cpuid
test eax,eax          ; Check for subfunction list end
jz AfterSubFunction   ; Go if reach first not valid subfunction
call StoreCpuId_Entry
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
jmp .L0               ; Go repeat for next subfunction
;---------- CPUID function 0Dh = Processor extended state enumeration ---------;
Function0D:
mov eax,temp_r9       ; r9d ; EAX = function number
xor ecx,ecx           ; ECX = sub-function number
cpuid
xor esi,esi           ; ESI = Storage for sub-function number
.L2:
rcr edx,1
rcr eax,1
jnc .L3
push eax edx
mov eax,temp_r9       ; r9d
mov ecx,esi           ; ECX = Sub-function number
call StoreCpuId
pop edx eax
ja OverSubFunction    ; Go if output buffer overflow
.L3:
inc esi               ; Sunfunctions number +1
cmp esi,63            ;  
jbe .L2               ; Go cycle if next sub-function exist
jmp AfterSubFunction 
;---------- CPUID function 0Fh = Platform QoS monitoring enumeration ----------;
Function0F:
;---------- CPUID function 10h = L3 cache QoS enforcement enumeration (same) --;
Function10:
xor esi,esi           ; ESI = sub-function number for CPUID
xor ecx,ecx           ; ECX = sub-function number for save entry 
push eax temp_r9      ; r9       
call StoreCpuId       ; Subfunction 0 of fixed list [0,1]
pop temp_r9 eax       ; r9
ja OverSubFunction    ; Go if output buffer overflow
mov esi,1
mov ecx,esi
call StoreCpuId       ; Subfunction 1 of fixed list [0,1]
ja OverSubFunction    ; Go if output buffer overflow
jmp AfterSubFunction
;---------- CPUID function 14h = Intel Processor Trace Enumeration ------------;
;---------- CPUID function 17h = System-On-Chip Vendor Attribute Enumeration --;
Function14:
Function17:
xor esi,esi           ; ESI = Storage for sub-function number
mov ecx,esi
mov eax,temp_r9       ; r9d ; EAX = function number (BUGGY DUPLICATED)
cpuid
mov temp_r8,eax       ; r8d,eax ; R8D = Maximal sub-function number
.L0:
mov eax,temp_r9       ; r9d
mov ecx,esi           ; ECX = Current sub-function number
call StoreCpuId
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
cmp esi,temp_r8       ; r8d 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction
;---------- CPUID function 18h = Deterministic Address Translation Parms. -----;
Function18:
xor esi,esi           ; ESI = Storage for sub-function number
mov ecx,esi
mov eax,temp_r9       ; r9d ; EAX = function number (BUGGY DUPLICATED)
cpuid
mov temp_r8,eax       ; r8d,eax ; R8D = Maximal sub-function number
.L0:
mov eax,temp_r9       ; r9d
mov ecx,esi           ; ECX = Current sub-function number
cpuid
test dl,00011111b     ; Check TLB deterministic data validity
jz @f                 ; Go skip if subfunction invalid, can be unordered
call StoreCpuId_Entry
ja OverSubFunction    ; Go if output buffer overflow
@@:
inc esi               ; Sunfunctions number +1
cmp esi,temp_r8       ; r8d 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction

;------------------------------------------------------------------------;
; Measure CPU Clock frequency by Time Stamp Counter (TSC)                ;
;                                                                        ;
; INPUT:   EDI = Pointer to OPB (Output Parameters Block)                ;
;                                                                        ;
; OUTPUT:  QWORD OPB[00] = Frequency, Hz, 0 if measurement error         ;
;------------------------------------------------------------------------;
GetCPUCLK:
push eax ebx ecx edx
mov dword [edi+0],0
mov dword [edi+4],0
call CheckCPUID
jc @f
cmp eax,1
jb @f
mov eax,1
cpuid
test dl,10h
jz @f
call MeasureCpuClk
jc @f
mov [edi+0],eax
mov [edi+4],edx
@@:
pop edx ecx ebx eax
ret

;------------------------------------------------------------------------;
; Get CPU context management state for context save-restore subsystem    ;
;                                                                        ;
; INPUT:   EDI = Pointer to OPB (Output Parameters Block)                ;
;                                                                        ;
; OUTPUT:  QWORD OPB[00] = CPU validation mask                           ;
;          QWORD OPB[08] = OS validation mask                            ;
;------------------------------------------------------------------------;
GetCPUCTX:
push eax ebx ecx edx
xor eax,eax
mov [edi+00],eax     ; Pre-clear output data
mov [edi+04],eax
mov [edi+08],eax
mov [edi+12],eax
call CheckCPUID
jc @f                ; Skip if CPUID not supported
xor eax,eax
cpuid
cmp eax,0Dh
jb @f                ; Skip if CPUID context declaration not supported
mov eax,1
cpuid
bt ecx,27
jnc @f               ; Skip if CPU context management not supported
mov eax,0Dh
xor ecx,ecx
cpuid
mov [edi+00],eax     ; QWORD OPB[00] = CPU validation mask
mov [edi+04],edx
xor ecx,ecx
xgetbv
mov [edi+08],eax     ; QWORD OPB[08] = OS validation mask 
mov [edi+12],edx
@@:
pop edx ecx ebx eax
ret

;------------------------------------------------------------------------;
; Measure CPU TSC (Time Stamp Counter) clock frequency, Hz               ;
;                                                                        ;
; INPUT:   None                                                          ;
;                                                                        ;
; OUTPUT:  CF flag = Status: 0(NC)=Measured OK, 1(C)=Measurement error	 ;
;          Output EAX, EBX, ECX, EDX valid only if CF=0(NC)              ;
;          EDX:EAX = TSC Frequency, Hz, F = Delta TSC per 1 second       ;
;------------------------------------------------------------------------;
MeasureCpuClk:
cld                             ; Clear direction, because STOS used
xor eax,eax
push esi edi ebp eax eax        ; Last EAX = Variable
;--- Start measure frequency, wait toggle ---
push ebp
call [GetSystemTimeAsFileTime]  ; Get current count
mov esi,[ebp]
@@:
push ebp
call [GetSystemTimeAsFileTime]  ; Get next count for wait 100 ns
cmp esi,[ebp]
je @b
;--- Start time point ---
mov esi,[ebp+0]
mov edi,[ebp+4]
add esi,10000000                
adc edi,0                       ; EDI:ESI = 10^7 * 100ns = 1 second
rdtsc
push eax edx                    ; Stack qword = 64-bit TSC at operation start
;--- Delay 1 second ---
@@:
push ebp
call [GetSystemTimeAsFileTime]  ; Get count for wait 1 second
cmp edi,[ebp+4]                 ; Compare high: target=edi and returned=[ebp+4] 
ja @b                           ; Go wait if target > returned, must 1 second
jb @f
cmp esi,[ebp+0]                 ; Compare low: target=esi and returned=[ebp+0] 
ja @b                           ; Go wait if target > returned, must 1 second
@@:
;--- Stop time point ---
rdtsc                           ; EDX:EAX = 64-bit TSC at operation end
pop ecx ebx
sub eax,ebx
sbb edx,ecx
;--- Exit ---
ExitCpuClk:
pop ebp ebp ebp edi esi          ; First POP EBP for RSP-8 only 
ret

;------------------------------------------------------------------------;
; Check CPUID instruction support.                                       ;
;                                                                        ;
; INPUT:   None                                                          ;
;                                                                        ;
; OUTPUT:  CF = Error flag,                                              ; 
;          0(NC) = Result in EAX valid, 1(C) = Result not valid          ;
;          EAX = Maximum supported standard function, if no errors       ;
;------------------------------------------------------------------------;
CheckCPUID:
mov ebx,21
pushf                     ; In the 32-bit mode, push EFLAGS
pop eax
bts eax,ebx               ; Set EAX.21=1
push eax
popf                      ; Load EFLAGS with EFLAGS.21=1
pushf                     ; Store EFLAGS
pop eax                   ; Load EFLAGS to EAX
btr eax,ebx               ; Check EAX.21=1, Set EAX.21=0
jnc .L0                   ; Go error branch if cannot set EFLAGS.21=1
push eax
popf                      ; Load EFLAGS with EFLAGS.21=0
pushf                     ; Store EFLAGS
pop eax                   ; Load EFLAGS to EAX
btr eax,ebx               ; Check EAX.21=0
jc .L0                    ; Go if cannot set EFLAGS.21=0
xor eax,eax
cpuid
ret
.L0:
stc
ret

;---------- Data section -------------------------------------------------------
section '.data' data readable writeable
;--- Functions pointers, for IPB absent ---
FunctionCount      =   3
FunctionSelector   DD  GetCPUID        ; 0 = Get CPUID dump
                   DD  GetCPUCLK       ; 1 = Measure CPU TSC Clock
                   DD  GetCPUCTX       ; 2 = Get CPU context management flags

;--- Functions pointers, for IPB present ---
iFunctionCount     =   0
iFunctionSelector  DD  0

;--- Data for detect WOW64 ---
LibName  DB  'KERNEL32',0
FncName  DB  'IsWow64Process',0

;---------- Export section -----------------------------------------------------
section '.edata' export data readable
export 'WIN32JNI.dll' ,\
checkBinary  , 'Java_cpuidrefactoring_system_PAL_checkBinary', \
entryBinary  , 'Java_cpuidrefactoring_system_PAL_entryBinary'

;---------- Import section ----------------------------------------------------;
section '.idata' import data readable writeable
library kernel32 , 'KERNEL32.DLL' , advapi32 , 'ADVAPI32.DLL'
include 'api\kernel32.inc'
include 'api\advapi32.inc'

;---------- Relocations section ------------------------------------------------ 
data fixups
end data

