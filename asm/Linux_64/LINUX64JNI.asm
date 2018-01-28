;------------------------------------------------------------------------------;
;                    Native Binary Library for Linux x64                       ;
;       JNI ELF (Java Native Interface Executable Linkable Format 64           ;
;                                                                              ;
;                     Updated at CPUID v0.52 refactoring.                      ;
;------------------------------------------------------------------------------;

format ELF64

;--- Binary services entry points ---

public checkBinary  as  'Java_cpuid_kernel_PAL_checkBinary'
public entryBinary  as  'Java_cpuid_kernel_PAL_entryBinary'  

;--- This simple entry point for debug native call mechanism ---

checkBinary:
mov eax,64   ; 164
ret

;--- Entry point for binary services, Java Native Interface (JNI) -------------;
; Parm#1 = RDI = JNI Environmet                                                ;
; Parm#2 = RSI = JNI This Object reference (not used here)                     ;
; Parm#3 = RDX = Object IPB array of qwords (long) ref. or NULL                ;
; Parm#4 = RCX = Object OPB array of qwords (long) ref. or NULL                ;
; Parm#5 = R8  = IPB size, qwords, or function code if IPB=NULL                ;
; Parm#6 = R9  = OPB size, qwords, or reserved if OPB=NULL                     ;
; Return = RAX = JNI Status: 0=Error, 1=IA32 OK, 2=x64 OK                      ;
;------------------------------------------------------------------------------;

entryBinary:
push rbp rbx r12 r13 r14 r15
mov rbp,rsp                     ; Save RSP because alignment
xor eax,eax
push rax rax                    ; Reserve stack space for variables
mov rbx,rdi                     ; RBX = Environment
mov r12,rdx                     ; R12 = Object: Input Parm. Block
mov r13,rcx                     ; R13 = Object: Output Parm. Block
mov r14,r8                      ; R14 = Length of IPB (parm#5)
mov r15,r9                      ; R15 = Length of OPB (parm#6)
and rsp,0FFFFFFFFFFFFFFF0h      ; Stack alignment by calling conv.
xor esi,esi                     ; Pre-blank IPB pointer
xor edi,edi                     ; Pre-blank OPB pointer
;--- Check IPB presence ---
test r12,r12
jz @f                           ; Go skip IPB extract. if IPB=null
mov rdi,rbx                     ; Parm#1 = Environment
mov rsi,r12                     ; Parm#2 = Array reference
lea rdx,[rbp-8]                 ; Parm#3 = isCopyAddress
mov rax,[rbx]                   ; RAX = Pointer to functions table
;--- Get IPB, parms: RDI=env, RSI=IPB Object, RDX=Pointer to flag --
call qword [rax+188*8]          ; JNI call [GetLongArrayElements]
test rax,rax
jz StatusRet                    ; Go skip if error = NULL pointer
xchg rsi,rax                    ; RSI = Pointer to IPB
@@:
;--- Check OPB presence ---
test r13,r13
jz @f                           ; Go skip OPB extraction if OPB=null
push rsi rsi                    ; Store IPB, twice for align RSP
mov rdi,rbx                     ; Parm#1 = Environment
mov rsi,r13                     ; Parm#2 = Array reference
lea rdx,[rbp-16]                ; Parm#3 = isCopyAddress 
mov rax,[rbx]                   ; RAX = Pointer to functions table
;--- Get OPB, parms: RDI=env, RSI=OPB Object, RDX=Pointer to flag --
call qword [rax+188*8]          ; JNI call [GetLongArrayElements]
pop rsi rsi
test rax,rax
jz StatusRet                    ; Go skip if error = NULL pointer
xchg rdi,rax                    ; RSI = Pointer to OPB
@@: 
;--- Target operation ---
test rsi,rsi
jz IPB_null                     ; Go spec. case, IPB size = function
;--- Handling IPB present ---
xor eax,eax
mov r10d,[rsi]               ; DWORD IPB[0] = Function selector 
cmp r10d,iFunctionCount
jae @f
lea rcx,[iFunctionSelector]  ; RCX must be adjustable by *.SO maker
call qword [rcx+r10*8]
@@:
;--- Return point ---
ReleaseRet:
;--- Check IPB release requirement flag and IPB presence ---
cmp qword [rbp-8],0
je @f                           ; Go skip if IPB release not req.
test r12,r12
jz @f                           ; Go skip IPB extract. if IPB=null
push rdi rdi                    ; Store OPB, twice for align RSP
mov rdi,rbx                     ; Parm#1 = Environment 
mov rdx,rsi                     ; Parm#3 = Copy address, note RSI
mov rsi,r12                     ; Parm#2 = Object reference
xor ecx,ecx                     ; Parm#4 = Release mode
mov rax,[rbx]                   ; RAX = Pointer to functions table
;--- Release IPB, parms: RDI=env, RSI=obj, RDX=P, RCX=Mode --- 
call qword [rax+196*8]          ; call [ReleaseLongArrayElements]
pop rdi rdi
@@:
;--- Check OPB release requirement flag and OPB presence ---
cmp qword [rbp-16],0
je @f                           ; Go skip if OPB release not req.
test r13,r13
jz @f                           ; Go skip OPB extract. if OPB=null
mov rdx,rdi                     ; Parm#3 = Copy address, note RDI
mov rdi,rbx                     ; Parm#1 = Environment 
mov rsi,r13                     ; Parm#2 = Object reference
xor ecx,ecx                     ; Parm#4 = Release mode
mov rax,[rbx]                   ; RAX = Pointer to functions table
;--- Release OPB, parms: RDI=env, RSI=obj, RDX=P, RCX=Mode --- 
call qword [rax+196*8]          ; call [ReleaseLongArrayElements]
@@:
;--- Return with status = RAX ---
mov eax,2                       ; RAX=2 (true) means OK from JNI
StatusRet:
mov rsp,rbp                     ; Restore stack
pop r15 r14 r13 r12 rbx rbp
ret
;--- Special fast case, no Input Parameters Block ---
IPB_null:
xor eax,eax
cmp r14,FunctionCount        ; QWORD R14 = Function selector 
jae @f
lea rcx,[FunctionSelector]   ; RCX must be adjustable by *.SO maker
call qword [rcx+r14*8]
@@:
jmp ReleaseRet

;---------- Get CPUID dump ----------------------------------------------------;
; Parm#1 = RDI = Pointer to buffer for status and dump data                    ;
; Output = RAX = Status: 0=Error, Non-Zero=OK, set external. at transit caller ;
;          Buffer DWORD[0] = Number of entries returned                        ;
;          Bytes [4-31] = Reserved for alignment                               ;
;          Bytes [32-16383] = Buffer, maximum (16384-32)/32 = 511 entries ret. ;  
;------------------------------------------------------------------------------;
Get_CPUID:
push rdi
cld
mov ecx,8
xor eax,eax
rep stosd
mov rcx,rdi
call Internal_GetCPUID
pop rdi
mov [rdi],eax
ret

ENTRIES_LIMIT = 511    ; Maximum number of output buffer 16352 bytes = 511*32

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
;---
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
cmp eax,ENTRIES_LIMIT/2   ; EAX = maximum supported standard function number
ja ErrorCpuId             ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction

;---------- Get virtual CPUID results -----------------------------------------;
mov r9d,40000000h         ; R9D = virtual functions start
mov eax,r9d               ; EAX = Function
xor ecx,ecx               ; ECX = Subfunction
cpuid
and eax,0FFFFFF00h
cmp eax,040000000h
jne NoVirtual             ; Skip virtual CPUID if not supported
mov eax,r9d               ; EAX = Limit, yet 1 function 40000000h
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction
NoVirtual:

;---------- Get extended CPUID results ----------------------------------------;
mov r9d,80000000h         ; R9D  = extended functions start
mov eax,r9d
cpuid
cmp eax,80000000h + ENTRIES_LIMIT/2  ; EAX = maximum extended function number
ja ErrorCpuId                        ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId                        ; Exit if output buffer overflow

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
;- Locked for debug -
; cmp eax,17h
; je Function17
; cmp eax,18h
; je Function18
;- End of locked for debug -
cmp eax,8000001Dh
je Function04
;--- Default handling for functions without subfunctions ---
xor esi,esi               ; ESI = sub-function number for CPUID
xor ecx,ecx               ; ECX = sub-function number for save entry 
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
Function0B:
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
Function0F:
;---------- CPUID function 10h = L3 cache QoS enforcement enumeration (same) --;
Function10:
xor esi,esi           ; ESI = sub-function number for CPUID
xor ecx,ecx           ; ECX = sub-function number for save entry 
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
Function14:
Function17:
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
.L0:
mov eax,r9d
mov ecx,esi           ; ECX = Current sub-function number
cpuid
test dl,00011111b     ; Check TLB deterministic data validity
jz @f                 ; Go skip if subfunction invalid, can be unordered
call StoreCpuId_Entry
ja OverSubFunction    ; Go if output buffer overflow
@@:
inc esi               ; Sunfunctions number +1
cmp esi,r8d           ; 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction

;------------------------------------------------------------------------------;
; Measure CPU Clock frequency by Time Stamp Counter (TSC)                      ;
;                                                                              ;
; INPUT:   RDI = Pointer to OPB (Output Parameters Block)                      ;
;                                                                              ;
; OUTPUT:  QWORD OPB[00] = Frequency, Hz, 0 if measurement error               ;
;------------------------------------------------------------------------------;
Get_CPUCLK:
push rax rbx rcx rdx
mov qword [rdi+0],0
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
mov qword [rdi+0],rax
@@:
pop rdx rcx rbx rax
ret

;------------------------------------------------------------------------------;
; Get CPU context management state for context save-restore subsystem          ;
;                                                                              ;
; INPUT:   RDI = Pointer to OPB (Output Parameters Block)                      ;
;                                                                              ;
; OUTPUT:  QWORD OPB[00] = CPU validation mask                                 ;
;          QWORD OPB[08] = OS validation mask                                  ;
;------------------------------------------------------------------------------;
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

;--- Measure CPU TSC (Time Stamp Counter) clock frequency ---------------------;
;                                                                              ;
; INPUT:   None                                                                ;
;                                                                              ;
; OUTPUT:  CF flag = Status: 0(NC)=Measured OK, 1(C)=Measurement error	       ;
;          Output RAX,RDX valid only if CF=0(NC)                               ;
;          RAX = TSC Frequency, Hz, F = Delta TSC per 1 second                 ;
;------------------------------------------------------------------------------;
MeasureCpuClk:
push rcx rsi rdi r8 r9 r10 r11
;--- Prepare parameters, early to minimize dTSC ---
sub rsp,32
mov rdi,rsp
lea rsi,[rdi+16]
xor eax,eax
mov qword [rdi+00],1
mov qword [rdi+08],rax
mov qword [rsi+00],rax
mov qword [rsi+08],rax
;--- Get TSC value before 1 second pause ---
rdtsc                     ; EDX:EAX = TSC, EDX = High , EAX = Low
push rax rdx
;--- Wait 1 second ---
mov eax,35   ; SYS_NANOSLEEP  ; EAX = Linux API function (syscall number)
push rsi
syscall
pop rsi
xchg r8,rax
;--- Get TSC value after 1 second pause ---
rdtsc           ; EDX:EAX = TSC, EDX = High , EAX = Low , BEFORE 1 second pause
pop rcx rdi     ; ECX:EDI = TSC, ECX = High , EBX = Low , AFTER 1 second pause
;--- Check results ---
test r8,r8
jnz TimerFailed   ; Go if error returned or wait interrupted
mov r8,[rsi+00]   ; RAX = Time remain, seconds
or  r8,[rsi+08]   ; RAX = Disjunction with Time remain, nanoseconds
jnz TimerFailed   ; Go if remain time stored by function
;--- Calculate delta-TSC per 1 second = TSC frequency ---
sub eax,edi    ; Subtract: DeltaTSC.Low  = EndTSC.Low - StartTSC.Low
sbb edx,ecx    ; Subtract: DeltaTSC.High = EndTSC.High - StartTSC.High - Borrow
;--- Extract TSC frequency as 64-bit value ---
shl rdx,32
add rax,rdx
;--- Exit points ---
add rsp,32
clc
TimerDone:
pop r11 r10 r9 r8 rdi rsi rcx
ret
TimerFailed:
add rsp,32
stc
jmp TimerDone

;------------------------------------------------------------------------------;
; Check CPUID instruction support.                                             ;
;                                                                              ;
; INPUT:   None                                                                ;
;                                                                              ;
; OUTPUT:  CF = Error flag,                                                    ; 
;          0(NC) = Result in EAX valid, 1(C) = Result not valid                ;
;          EAX = Maximum supported standard function, if no errors             ;
;------------------------------------------------------------------------------;
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
cpuid
ret
.L0:
stc
ret

;--- Functions pointers, for IPB absent ---
FunctionCount      =   3
FunctionSelector   DQ  Get_CPUID      ; 0 = Get CPUID dump
                   DQ  Get_CPUCLK     ; 1 = Measure CPU TSC frequency
                   DQ  GetCPUCTX      ; 2 = Get CPU context management flags

;--- Functions pointers, for IPB present ---
iFunctionCount     =   0
iFunctionSelector  DQ  0

