;------------------------------------------------------------------------------
; Native library for Java cross-platform CPUID Utility.
; This is FASM source file.
; This source (Java CPUID v2.xx.xx) repository: 
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
; Previous source (Java CPUID v1.xx.xx) repository: 
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
; All repositories: 
; https://github.com/manusov?tab=repositories
; (C) Manusov I.V. Refactoring at 2024.
;------------------------------------------------------------------------------
; Native Binary Library for Windows x64.
; JNI DLL ( Java Native Interface module as Dynamical Load Library ).
;------------------------------------------------------------------------------

include 'win64a.inc'
format PE64 GUI 4.0 DLL
entry DllMain

;---------- Code section ------------------------------------------------------;
section '.text' code readable executable

DllMain:        ; This called by Operating System when load/unload DLL
mov eax,1       ; Return status to OS caller (actual when load)
ret

;--- This simple entry point for debug native call mechanism ---
checkBinary:    
mov eax,64
ret

;--- Entry point for binary services, Java Native Interface (JNI) ---
; Parm#1 = RCX = JNI Environment  
; Parm#2 = RDX = JNI This Object reference (not used by this routine)
; Parm#3 = R8  = Object IPB array of qwords (long) reference or NULL
; Parm#4 = R9  = Object OPB array of qwords (long) reference or NULL
; Parm#5 = [RSP+40] = IPB size, qwords, or function code if IPB=NULL
; Parm#6 = [RSP+48] = OPB size, qwords, or reserved if OPB=NULL
; Return = RAX = JNI Status: 0=Error, 1=IA32 OK, 2=x64 OK
;---

entryBinary:
push rbx rsi rdi rbp r12 r13 r14 r15   ; Save non-volatile registers
mov rbp,rsp                            ; Save RSP because stack alignment
xor eax,eax
push rax rax                           ; Storage for variable
mov rbx,rcx                            ; RBX = Environment
mov r12,r8                             ; R12 = Object: Input Parm. Block
mov r13,r9                             ; R13 = Object: Output Parm. Block 
mov r14,[rbp+64+8+32+0]                ; R14 = Length of IPB (parm#5)
mov r15,[rbp+64+8+32+8]                ; R15 = Length of OPB (parm#6)
and rsp,0FFFFFFFFFFFFFFF0h             ; Stack alignment by calling convention
sub rsp,32                             ; Parm. shadow by calling convention
xor esi,esi                            ; Pre-blank IPB pointer
xor edi,edi                            ; Pre-blank OPB pointer
;--- Check IPB presence ---
test r12,r12
jz @f                                  ; Go skip IPB extraction if IPB=null
mov rdx,r12
lea r8,[rbp-8]
mov rax,[rbx]                          ; RAX = Pointer to functions table
;--- Get IPB, parms: RCX=env, RDX=IPB Object, R8=Pointer to flag ---
call qword [rax+188*8]                 ; JNI call [GetLongArrayElements]
test rax,rax
jz StatusRet                           ; Go skip if error = NULL pointer
xchg rsi,rax                           ; RSI = Pointer to IPB
@@:
;--- Check OPB presence ---
test r13,r13
jz @f                                  ; Go skip IPB extraction if OPB=null
mov rcx,rbx
mov rdx,r13
lea r8,[rbp-16]
mov rax,[rbx]                          ; RAX = Pointer to functions table
;--- Get OPB, parms: RCX=env, RDX=OPB Object, R8=Pointer to flag ---
call qword [rax+188*8]                 ; JNI call [GetLongArrayElements]
test rax,rax
jz StatusRet                           ; Go skip if error = NULL pointer
xchg rdi,rax                           ; RDI = Pointer to OPB
@@: 
;--- Target operation ---
test rsi,rsi
jz IPB_null                            ; Go special case, IPB size = function
;--- Handling IPB present ---
xor eax,eax
mov r10d,[rsi]                         ; DWORD IPB[0] = Function selector 
cmp r10d,iFunctionCount
jae @f
call qword [iFunctionSelector+r10*8]
@@:
;--- Return point ---
ReleaseRet:
;--- Check IPB release requirement flag and IPB presence ---
cmp qword [rbp-8],0
je @f                                  ; Go skip if IPB release not required
test r12,r12
jz @f                                  ; Go skip IPB extraction if IPB=null
mov rcx,rbx
mov rdx,r12
mov r8,rsi
xor r9d,r9d
mov rax,[rbx]                          ; RAX = Pointer to functions table
;--- Release IPB, parms: RCX=env, RDX=obj, R8=Pointer, R9=Release mode --- 
call qword [rax+196*8]                 ; call [ReleaseLongArrayElements]
@@:
;--- Check OPB release requirement flag and OPB presence ---
cmp qword [rbp-16],0
je @f                                  ; Go skip if OPB release not required
test r13,r13
jz @f                                  ; Go skip OPB extraction if OPB=null
mov rcx,rbx
mov rdx,r13
mov r8,rdi
xor r9d,r9d
mov rax,[rbx]                          ; RAX = Pointer to functions table
;--- Release OPB, parms: RCX=env, RDX=obj, R8=Pointer, R9=Release mode --- 
call qword [rax+196*8]                 ; call [ReleaseLongArrayElements]
@@:
;--- Return with status = RAX ---
mov eax,2                              ; RAX=2 (true) means OK from Win64 DLL 
StatusRet:                             ; Entry point with RAX=0 (error)
mov rsp,rbp                            ; Restore RSP after alignment
pop r15 r14 r13 r12 rbp rdi rsi rbx    ; Restore non-volatile registers
ret                                    ; Return to Java JNI service caller 
;--- Special fast case, no Input Parameters Block ---
IPB_null:
xor eax,eax
cmp r14,FunctionCount                  ; QWORD R14 = Function selector 
jae @f
call qword [FunctionSelector+r14*8]
@@:
jmp ReleaseRet

;---------- Get CPUID dump ----------------------------------------------------;
; Parm#1 = RDI = Pointer to buffer for status and dump data                    ;
; Output = RAX = Status: 0=Error, Non-Zero=OK, set external. at transit caller ;
;          Buffer DWORD[0] = Number of entries returned                        ;
;          Bytes [4-31] = Reserved for alignment                               ;
;          Bytes [32-16383] = Buffer, maximum (16384-32)/32 = 511 entries ret. ;  
;------------------------------------------------------------------------------;
GetCPUID:
push rdi
cld
mov ecx,8
xor eax,eax
rep stosd      ; blank first 8 * 4 = 32 bytes
mov rcx,rdi
call Internal_GetCPUID
pop rdi
mov [rdi],eax
ret

; Output buffer restrictions, for prevent buffer overflow
; when invalid CPUID data returned
; 511 (not 512) because entry 0 used for data size return
; -1 because, for example, Limit=1 means only function 0 supported
; -2 because 511 entries, not 512
ENTRIES_LIMIT  = 511      ; Output buffer maximum size = 16352 bytes = 511*32
STANDARD_LIMIT = 192 - 1  ; Limit for standard CPUID functions 000000xxh
EXTENDED_LIMIT = 192 - 1  ; Limit for extended CPUID functions 800000xxh
VIRTUAL_LIMIT  = 128 - 2  ; Limit for virtual CPUID functions  400000xxh

;---------- Target subroutine -------------------------------------------------;
; INPUT:  Parameter#1 = RCX = Pointer to output buffer
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
;--------------------------------------------------------------------------
Internal_GetCPUID:
;---------- Initializing ------------------------------------------------------;
cld
push rbx rbp rsi rdi
mov rdi,rcx
xor ebp,ebp               ; EBP = Global output entries counter
call CheckCPUID           ; Return CF=Error flag, EAX=Maximum standard function
jc NoCpuId

;---------- Get standard CPUID results ----------------------------------------;
xor r9d,r9d               ; R9D  = standard functions start
cmp eax,STANDARD_LIMIT    ; EAX = maximum supported standard function number
ja ErrorCpuId             ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction

;---------- Get virtual CPUID results -----------------------------------------;
mov r9d,040000000h        ; R9D = virtual functions start number
mov eax,r9d               ; EAX = Function number for CPUID instruction
xor ecx,ecx               ; ECX = Subfunction, here redundant
cpuid
mov ecx,eax
and ecx,0FFFFFF00h        ; ECX = Pattern bits for check virtual CPUID support
cmp ecx,r9d               ; Compare pattern bits
jne NoVirtual             ; Skip virtual CPUID if not supported
cmp eax,40000000h + VIRTUAL_LIMIT  ; EAX = maximum extended function number
ja ErrorCpuId             ; Go if invalid limit, too big function number
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction
NoVirtual:                ; This label for skip virtual functions

;---------- Get extended CPUID results ----------------------------------------;
mov r9d,80000000h         ; R9D  = extended functions start
mov eax,r9d
cpuid
test eax,eax
jns NoExtended            ; Go skip extended functions if bit EAX.31 = 0
cmp eax,80000000h + EXTENDED_LIMIT  ; EAX = maximum extended function number
ja ErrorCpuId             ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow
NoExtended:

;---------- Return points -----------------------------------------------------;
xchg eax,ebp              ; Normal exit point, return RAX = number of entries
ExitCpuId:
pop rdi rsi rbp rbx
ret
NoCpuId:                  ; Exit for CPUID not supported, RAX=0  
xor eax,eax
jmp ExitCpuId
ErrorCpuId:               ; Exit for CPUID error, RAX=-1=FFFFFFFFFFFFFFFFh
mov rax,-1
jmp ExitCpuId 

;---------- Subroutine, sequence of CPUID functions ---------------------------;
; INPUT:  R9D = Start CPUID function number
;         EAX = Limit CPUID function number (inclusive)
;         RDI = Pointer to memory buffer
; OUTPUT: RDI = Modified by store CPUID input parms + output parms entry
;         Flags condition code: Carry (C) = means entries count limit
;------------------------------------------------------------------------------;
SequenceCpuId:
mov r10d,eax              ; R10D = standard or extended functions limit 
CycleCpuId:
;--- Specific handling for functions with subfunctions ---
mov eax,r9d           ; EAX = function number, input at R9D
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
cmp eax,17h
je Function17
cmp eax,18h
je Function18
cmp eax,1Dh
je Function1D
cmp eax,1Fh
je Function1F
cmp eax,8000001Dh
je Function04
cmp eax,80000020h
je Function10
;--- Default handling for functions without subfunctions ---
xor esi,esi               ; ESI = sub-function number for save entry
xor ecx,ecx               ; ECX = sub-function number for CPUID  
call StoreCpuId
ja OverSubFunction
AfterSubFunction:         ; Return point after sub-function specific handler
inc r9d
cmp r9d,r10d
jbe CycleCpuId            ; Cycle for CPUID standard functions
ret
OverSubFunction:
stc
ret 

;---------- CPUID function 04h = Deterministic cache parameters ---------------;
Function04:
xor esi,esi           ; ESI = Storage for sub-function number
.L0:
mov eax,r9d           ; EAX = function number
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
mov eax,r9d           ; EAX = function number (BUGGY DUPLICATED)
cpuid
mov r8d,eax           ; R8D = Maximal sub-function number
.L0:
mov eax,r9d
mov ecx,esi           ; ECX = Current sub-function number
call StoreCpuId
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
cmp esi,r8d           ; 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction
;---------- CPUID function 0Bh = Extended topology enumeration ----------------;
;---------- CPUID function 1Fh = V2 Extended topology enumeration -------------;
Function0B:
Function1F:
xor esi,esi           ; ESI = Storage for sub-function number
.L0:
mov eax,r9d           ; EAX = function number
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
mov eax,r9d           ; EAX = function number
xor ecx,ecx           ; ECX = sub-function number
cpuid
shl rdx,32
lea r8,[rdx+rax]
xor esi,esi           ; ESI = Storage for sub-function number
.L2:
shr r8,1
jnc .L3
mov eax,r9d
mov ecx,esi           ; ECX = Sub-function number
call StoreCpuId
ja OverSubFunction    ; Go if output buffer overflow
.L3:
inc esi               ; Sunfunctions number +1
cmp esi,63            ; 
jbe .L2               ; Go cycle if next sub-function exist
jmp AfterSubFunction 
;---------- CPUID function 0Fh = Platform QoS monitoring enumeration ----------;
;---------- CPUID function 10h = L3 cache QoS enforcement enumeration (same) --;
Function0F:
Function10:
xor esi,esi           ; ESI = sub-function number for save entry 
xor ecx,ecx           ; ECX = sub-function number for CPUID 
push rax r9       
call StoreCpuId       ; Subfunction 0 of fixed list [0,1]
pop r9 rax
ja OverSubFunction    ; Go if output buffer overflow
mov esi,1
mov ecx,esi
call StoreCpuId       ; Subfunction 1 of fixed list [0,1]
ja OverSubFunction    ; Go if output buffer overflow
jmp AfterSubFunction
;---------- CPUID function 14h = Intel Processor Trace Enumeration ------------;
;---------- CPUID function 17h = System-On-Chip Vendor Attribute Enumeration --;
;---------- CPUID function 1Dh = Intel AMX Tile Information -------------------;
Function14:
Function17:
Function1D:
xor esi,esi           ; ESI = Storage for sub-function number
mov ecx,esi
mov eax,r9d           ; EAX = function number (BUGGY DUPLICATED)
cpuid
mov r8d,eax           ; R8D = Maximal sub-function number
.L0:
mov eax,r9d
mov ecx,esi           ; ECX = Current sub-function number
call StoreCpuId
ja OverSubFunction    ; Go if output buffer overflow
inc esi               ; Sunfunctions number +1
cmp esi,r8d           ; 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction
;---------- CPUID function 18h = Deterministic Address Translation Parms. -----;
Function18:
xor esi,esi           ; ESI = Storage for sub-function number
mov ecx,esi
mov eax,r9d           ; EAX = function number (BUGGY DUPLICATED)
cpuid
mov r8d,eax           ; R8D = Maximal sub-function number
jmp .L2
.L0:
mov eax,r9d
mov ecx,esi           ; ECX = Current sub-function number
cpuid
test dl,00011111b     ; Check TLB deterministic data validity
jz .L1                ; Go skip if subfunction invalid, can be unordered
.L2:
call StoreCpuId_Entry
ja OverSubFunction    ; Go if output buffer overflow
.L1:
inc esi               ; Sunfunctions number +1
cmp esi,r8d           ; 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction

;---------- Subroutine, one CPUID function execution --------------------------;
; INPUT:  EAX = CPUID function number
;         R9D = EAX
;         ECX = CPUID subfunction number
;         ESI = ECX
;         RDI = Pointer to memory buffer
; OUTPUT: RDI = Modified by store CPUID input parms + output parms entry
;         Flags condition code: Above (A) = means entries count limit
;------------------------------------------------------------------------------;
StoreCpuId:
cpuid
StoreCpuId_Entry:     ; Entry point for CPUID results (EAX,EBX,ECX,EDX) ready 
push rax
xor eax,eax
stosd                 ; Store tag dword[0] = Information type
mov eax,r9d
stosd                 ; Store argument dword [1] = CPUID function number 
mov eax,esi
stosd                 ; Store argument dword [2] = CPUID sub-function number
xor eax,eax
stosd                 ; Store argument dword [3] = CPUID pass number (see fn.2)
pop rax
stosd                 ; Store result dword [4] = output EAX 
xchg eax,ebx
stosd                 ; Store result dword [5] = output EBX
xchg eax,ecx
stosd                 ; Store result dword [6] = output ECX
xchg eax,edx
stosd                 ; Store result dword [7] = output EDX
inc ebp               ; Global counter +1
cmp ebp,ENTRIES_LIMIT ; Limit for number of output entries
ret

;------------------------------------------------------------------------;
; Measure CPU Clock frequency by Time Stamp Counter (TSC)                ;
;                                                                        ;
; INPUT:   RDI = Pointer to OPB (Output Parameters Block)                ;
;                                                                        ;
; OUTPUT:  QWORD OPB[00] = Frequency, Hz, 0 if measurement error         ;
;------------------------------------------------------------------------;
GetCPUCLK:
push rax rbx rcx rdx
mov qword [rdi],0
call CheckCPUID
;--- Make subroutine for optimization --- end ---
jc @f
cmp eax,1
jb @f
mov eax,1
cpuid
test dl,10h
jz @f
call MeasureCpuClk
jc @f
mov [rdi],rax
@@:
pop rdx rcx rbx rax
ret

;------------------------------------------------------------------------;
; Get CPU context management state for context save-restore subsystem    ;
;                                                                        ;
; INPUT:   RDI = Pointer to OPB (Output Parameters Block)                ;
;                                                                        ;
; OUTPUT:  QWORD OPB[00] = CPU validation mask                           ;
;          QWORD OPB[08] = OS validation mask                            ;
;------------------------------------------------------------------------;
GetCPUCTX:
push rax rbx rcx rdx
xor eax,eax
mov [rdi+00],rax     ; Pre-clear output data
mov [rdi+08],rax
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
mov [rdi+00],eax     ; QWORD OPB[00] = CPU validation mask
mov [rdi+04],edx
xor ecx,ecx
xgetbv
mov [rdi+08],eax     ; QWORD OPB[08] = OS validation mask 
mov [rdi+12],edx
@@:
pop rdx rcx rbx rax
ret

;------------------------------------------------------------------------;
; Measure CPU TSC (Time Stamp Counter) clock frequency, Hz               ;
;                                                                        ;
; INPUT:   None                                                          ;
;                                                                        ;
; OUTPUT:  CF flag = Status: 0(NC)=Measured OK, 1(C)=Measurement error	 ;
;          Output RAX valid only if CF=0(NC)                             ;
;          RAX = TSC Frequency, Hz, F = Delta TSC per 1 second           ;
;------------------------------------------------------------------------;
; MeasureCpuClk:
; cld                         ; Clear direction, because STOSQ used
; push rbx rcx rdx rsi rbp r8 r9 r10 r11 rax	 ; R8-R11 because API, RAX = Var
; mov rbp,rsp                 ; RBP used for restore RSP and addressing variables
; and rsp,0FFFFFFFFFFFFFFF0h  ; Align stack (16)
; sub rsp,32                  ; Make parameters shadow
; ;--- Start measure frequency, wait toggle ---
; mov rcx,rbp
; call [GetSystemTimeAsFileTime]  ; Get current count
; mov rsi,[rbp]
; @@:
; mov rcx,rbp
; call [GetSystemTimeAsFileTime]  ; Get next count for wait 100 ns
; cmp rsi,[rbp]
; je @b
; ;--- Start time point ---
; mov rsi,[rbp]
; add rsi,10000000                ; RSI = 10^7 * 100ns = 1 second
; rdtsc
; shl rdx,32
; lea rbx,[rax+rdx]               ; RBX = 64-bit TSC at operation start
; ;--- Delay 1 second ---
; @@:
; mov rcx,rbp
; call [GetSystemTimeAsFileTime]  ; Get count for wait 1 second
; cmp rsi,[rbp]                   ; Compare target=rsi and returned=[rbp] 
; ja @b                           ; Go wait if target > returned, must 1 second
; ;--- Stop time point ---
; rdtsc
; shl rdx,32
; or rax,rdx                      ; RAX = 64-bit TSC at operation end
; sub rax,rbx                     ; RAX = Delta TSC = frequency (1 second)
; ;--- Restore RSP, pop extra registers, exit ---
; ExitCpuClk:
; mov rsp,rbp                            ; Restore RSP after alignment and shadow
; pop rbx r11 r10 r9 r8 rbp rsi rdx rcx rbx  ; First POP RBX for RSP + 8 only 
; ret

MeasureCpuClk:
cld
push rbx rcx rdx rsi rbp r8 r9 r10 r11
xor ecx,ecx
push rcx rcx               ; 16 bytes for 2 Qword stack variables
mov rbp,rsp                ; RBP used for restore RSP and addressing variables
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32                 ; Make parameters shadow
;---------- This branch use Performance Counter, high precision ---------------;
;---------- Detect performance counter status and frequency -------------------;
lea rcx,[rbp + 08]         ; RCX = Parm#1 = pointer to output 64-bit variable
call [QueryPerformanceFrequency]  ; Qword [rbp + 08] = performance frequency
test rax,rax
jz .tryFileTime            ; Go File Time branch if status = FALSE
;---------- Get current ticks counter value -----------------------------------;
mov rcx,rbp                ; RCX = Parm#1 = pointer to output 64-bit variable
call [QueryPerformanceCounter]  ; Qword [rbp + 00] = performance counter now
test rax,rax
jz .tryFileTime            ; Go File Time branch if status = FALSE
mov rsi,[rbp]              ; RSI = Performance counter now
;---------- Wait next tick for synchronization --------------------------------;
@@:
mov rcx,rbp                ; RCX = Parm#1 = pointer to output 64-bit variable
call [QueryPerformanceCounter]
test rax,rax
jz .tryFileTime            ; Go File Time branch if status = FALSE
cmp rsi,[rbp]
je @b                      ; Go wait cycle if counter value = previous
;---------- Start measurement -------------------------------------------------;
mov rsi,[rbp + 00]         ; RSI = Current value of counter
add rsi,[rbp + 08]         ; RSI = Current + Ticks per second = target value
rdtsc                      ; Get start TSC
shl rdx,32
lea rbx,[rax + rdx]        ; RBX = 64-bit TSC at operation start
@@:
mov rcx,rbp                ; RCX = Parm#1 = pointer to output 64-bit variable
call [QueryPerformanceCounter]
test rax,rax
jz .tryFileTime            ; Go File Time branch if status = FALSE
cmp rsi,[rbp]
jae @b                     ; Go wait cycle if target value >= current value
rdtsc                      ; Get end TSC for calculate delta-TSC
shl rdx,32
or rax,rdx            ; RAX = 64-bit TSC at operation end
sub rax,rbx           ; RAX = Delta TSC, also set CF flag for error status
jbe .tryFileTime      ; Go File Time branch if delta TSC <= 0
.exit:                ; Here CF = 0 (NC) if no errors
mov rsp,rbp           ; Flags must be not changed, because here CF = status
pop rcx rcx           ; Remove 2 qwords from stack frame 
pop r11 r10 r9 r8 rbp rsi rdx rcx rbx
ret
;---------- This branch use File Time if Performance Counter failed -----------;
.tryFileTime:
;---------- Start measure frequency -------------------------------------------;
mov rcx,rbp
call [GetSystemTimeAsFileTime]    ; Get current count
mov rsi,[rbp]
@@:
mov rcx,rbp
call [GetSystemTimeAsFileTime]    ; Get next count for wait 100 ns
cmp rsi,[rbp]
je @b
mov rsi,[rbp]
add rsi,10000000                  ; 10^7 * 100ns = 1 second
rdtsc
shl rdx,32
lea rbx,[rax + rdx]               ; RBX = 64-bit TSC at operation start
@@:
mov rcx,rbp
call [GetSystemTimeAsFileTime]    ; Get count for wait 1 second
cmp rsi,[rbp]
ja @b
rdtsc
shl rdx,32
or rax,rdx                        ; RAX = 64-bit TSC at operation end
sub rax,rbx                       ; RAX = Delta TSC
ja .exit
stc
jmp .exit


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
pushf                     ; In the 64-bit mode, push RFLAGS
pop rax
bts eax,ebx               ; Set EAX.21=1
push rax
popf                      ; Load RFLAGS with RFLAGS.21=1
pushf                     ; Store RFLAGS
pop rax                   ; Load RFLAGS to RAX
btr eax,ebx               ; Check EAX.21=1, Set EAX.21=0
jnc .L0                   ; Go error branch if cannot set EFLAGS.21=1
push rax
popf                      ; Load RFLAGS with RFLAGS.21=0
pushf                     ; Store RFLAGS
pop rax                   ; Load RFLAGS to RAX
btr eax,ebx               ; Check EAX.21=0
jc .L0                    ; Go if cannot set EFLAGS.21=0
xor eax,eax
cpuid                     ; CPUID function 0, here used output EAX = max. std.
ret
.L0:                      ; This point for errors handling
stc
ret

;---------- Data section ------------------------------------------------------;
section '.data' data readable writeable
;--- Functions pointers, for IPB absent ---
FunctionCount      =   3
FunctionSelector   DQ  GetCPUID        ; 0 = Get CPUID dump
                   DQ  GetCPUCLK       ; 1 = Measure CPU TSC frequency
                   DQ  GetCPUCTX       ; 2 = Get CPU context management flags
;--- Functions pointers, for IPB present ---
iFunctionCount     =   0
iFunctionSelector  DQ  0

;---------- Export section ----------------------------------------------------;
; This path strings depends on java source path:
; source file at package:  ...\src\cpuidv2\platforms\, 
; class: Detector.java, 
; native methods: checkBinary, entryBinary.

section '.edata' export data readable
export 'WIN64JNI.dll' ,\
checkBinary  , 'Java_cpuidv2_platforms_Detector_checkBinary', \
entryBinary  , 'Java_cpuidv2_platforms_Detector_entryBinary'

;---------- Import section ----------------------------------------------------;
section '.idata' import data readable writeable
library kernel32 , 'KERNEL32.DLL' , advapi32 , 'ADVAPI32.DLL'
include 'api\kernel32.inc'
include 'api\advapi32.inc'

;---------- Relocations section -----------------------------------------------; 
data fixups
end data

