;------------------------------------------------------------------------------;
;                    Native Binary Library for Linux ia32                      ;
;         JNI ELF (Java Native Interface Executable Linkable Format 32         ;
;                                                                              ;
; Updated at CPUID v1.03.00 for support virtual functions 40000000h-400000xxh. ;
;------------------------------------------------------------------------------;

format ELF

;--- Binary services entry points ---

public checkBinary  as  'Java_cpuidrefactoring_system_PAL_checkBinary'
public entryBinary  as  'Java_cpuidrefactoring_system_PAL_entryBinary'  

;--- This simple entry point for debug native call mechanism ---

checkBinary:
mov eax,32   ; 132
ret

;--- Entry point for binary services, Java Native Interface (JNI) -------------;
; Parm#1 = DWORD [esp+04] = JNI Environment                                    ;
; Parm#2 = DWORD [esp+08] = JNI This Object reference (not used here)          ;
; Parm#3 = DWORD [esp+12] = Object IPB array of qwords (long) ref. or NULL     ;
; Parm#4 = DWORD [esp+16] = Object OPB array of qwords (long) ref. or NULL     ;
; Parm#5 = QWORD [esp+20] = IPB size, qwords, or function code if IPB=NULL     ;
; Parm#6 = QWORD [esp+28] = OPB size, qwords, or reserved if OPB=NULL          ;
; Return = EAX = JNI Status: 0=Error, 1=IA32 OK, 2=x64 OK                      ;
;------------------------------------------------------------------------------;
; Stack layout at function call, as DWORDS.  ;
; [esp+00] = IP for return from subroutine   ;
; [esp+04] = JNI Environment                 ;
; [esp+08] = JNI This Object reference       ;
; [esp+12] = Object IPB array of QWORDs      ;
; [esp+16] = Object OPB array of QWORDs      ;
; [esp+20] = IPB size, low dword             ;
; [esp+24] = IPB size, high dword, usually 0 ;
; [esp+28] = OPB size, low dword             ;
; [esp+32] = OPB size, high dword, usually 0 ;
;--------------------------------------------;

entryBinary:
push ebx ecx edx esi edi ebp
mov ebp,esp
xor eax,eax
push eax eax             ; Reserve stack space for variables
and esp,0FFFFFFF0h       ; Align stack
xor esi,esi              ; Pre-blank IPB pointer
xor edi,edi              ; Pre-blank OPB pointer
;--- Check IPB presence ---
mov ecx,[ebp+12+24]             ; Parm#2 = Array reference
jecxz @f                        ; Go skip IPB extract. if IPB=null
mov ebx,[ebp+04+24]             ; Parm#1 = Environment
lea edx,[ebp-4]                 ; Parm#3 = isCopyAddress
mov eax,[ebx]                   ; RAX = Pointer to functions table
;--- Get IPB, parms: EBX=env, ECX=IPB Object, EDX=Pointer to flag --
push edx ecx ebx
call dword [eax+188*4]          ; JNI call [GetLongArrayElements]
add esp,12
test eax,eax
jz StatusRet                    ; Go skip if error = NULL pointer
xchg esi,eax                    ; RSI = Pointer to IPB
@@:
;--- Check OPB presence ---
mov ecx,[ebp+16+24]             ; Parm#2 = Array reference
jecxz @f                        ; Go skip OPB extract. if OPB=null
mov ebx,[ebp+04+24]             ; Parm#1 = Environment
lea edx,[ebp-8]                 ; Parm#3 = isCopyAddress
mov eax,[ebx]                   ; RAX = Pointer to functions table
;--- Get OPB, parms: EBX=env, ECX=IPB Object, EDX=Pointer to flag --
push ebp ebp esi esi            ; Push twice for alignment 16
push edx ecx ebx
call dword [eax+188*4]          ; JNI call [GetLongArrayElements]
add esp,12
pop esi esi ebp ebp
test eax,eax
jz StatusRet                    ; Go skip if error = NULL pointer
xchg edi,eax                    ; RSI = Pointer to IPB
@@:
;--- Target operation ---
test esi,esi
jz IPB_null                     ; Go spec. case, IPB size = function
;--- Handling IPB present ---
xor eax,eax
mov edx,[esi]                ; DWORD IPB[0] = Function selector 
cmp edx,iFunctionCount
jae @f
lea ecx,[iFunctionSelector]  ; RCX must be adjustable by *.SO maker
call dword [ecx+edx*4]
@@:
;--- Return point ---
ReleaseRet:
;--- Check IPB release requirement flag and IPB presence ---
cmp dword [ebp-4],0
je @f                           ; Go skip if IPB release not req.
mov ecx,[ebp+12+24]             ; Parm#2 = Array reference
jecxz @f                        ; Go skip IPB extract. if IPB=null
mov ebx,[ebp+04+24]             ; Parm#1 = Environment
mov edx,esi                     ; Parm#3 = Copy address, note RSI
xor esi,esi                     ; Parm#4 = Release mode
mov eax,[ebx]                   ; EAX = Pointer to functions table
;--- Release IPB, parms: EBX=env, ECX=obj, EDX=P, ESI=Mode --- 
push ebp ebp edi edi            ; Twice for align ESP
push esi edx ecx ebx 
call dword [eax+196*4]          ; call [ReleaseLongArrayElements]
add esp,16
pop edi edi ebp ebp
@@:
;--- Check OPB release requirement flag and OPB presence ---
cmp dword [ebp-8],0
je @f                           ; Go skip if OPB release not req.
mov ecx,[ebp+16+24]             ; Parm#2 = Array reference
jecxz @f                        ; Go skip IPB extract. if IPB=null
mov ebx,[ebp+04+24]             ; Parm#1 = Environment
mov edx,edi                     ; Parm#3 = Copy address, note RSI
xor esi,esi                     ; Parm#4 = Release mode
mov eax,[ebx]                   ; EAX = Pointer to functions table
;--- Release OPB, parms: EBX=env, ECX=obj, EDX=P, ESI=Mode --- 
push esi edx ecx ebx 
call dword [eax+196*4]          ; call [ReleaseLongArrayElements]
add esp,16
@@:
;--- Return with status = EAX ---
mov eax,1                    ; EAX=1 (true) means OK from JNI
StatusRet:
mov esp,ebp                  ; Restore stack
pop ebp edi esi edx ecx ebx
ret
;--- Special fast case, no Input Parameters Block ---
IPB_null:
xor eax,eax
mov edx,[ebp+20+24]          ; EDX = Selector / IPB size place
cmp edx,FunctionCount        ; DWORD EDX = Function selector 
jae @f
lea ecx,[FunctionSelector]   ; RCX must be adjustable by *.SO maker
call dword [ecx+edx*4]
@@:
jmp ReleaseRet

;---------- Get CPUID dump ----------------------------------------------------;
; Parm#1 = EDI = Pointer to buffer for status and dump data                    ;
; Output = EAX = Status: 0=Error, Non-Zero=OK, set external. at transit caller ;
;          Buffer DWORD[0] = Number of entries returned                        ;
;          Bytes [4-31] = Reserved for alignment                               ;
;          Bytes [32-16383] = Buffer, maximum (16384-32)/32 = 511 entries ret. ;  
;------------------------------------------------------------------------------;
Get_CPUID:
push edi                 ; (+1)
cld
mov ecx,8
xor eax,eax
rep stosd                ; blank first 8 * 4 = 32 bytes
push edi                 ; Parm#1
call Internal_GetCPUID
pop edi                  ; (-1)
mov [edi],eax
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
call CheckCPUID           ; Return CF=Error flag, EAX=Maximum standard function
jc NoCpuId

;---------- Get standard CPUID results ----------------------------------------;
mov temp_r9,0             ; xor r9d,r9d  ; R9D  = standard functions start
cmp eax,STANDARD_LIMIT    ; EAX = maximum supported standard function number
ja ErrorCpuId             ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction

;---------- Get virtual CPUID results -----------------------------------------;
mov temp_r9,040000000h    ; R9D = virtual functions start number
mov eax,temp_r9           ; EAX = Function number for CPUID instruction
xor ecx,ecx               ; ECX = Subfunction, here redundant
cpuid
mov ecx,eax
and ecx,0FFFFFF00h        ; ECX = Pattern bits for check virtual CPUID support
cmp ecx,temp_r9           ; Compare pattern bits
jne NoVirtual             ; Skip virtual CPUID if not supported
cmp eax,40000000h + VIRTUAL_LIMIT  ; EAX = maximum extended function number
ja ErrorCpuId             ; Go if invalid limit, too big function number
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow at subfunction
NoVirtual:                ; This label for skip virtual functions

;---------- Get extended CPUID results ----------------------------------------;
mov temp_r9,80000000h     ; mov r9d,80000000h ; R9D  = extended functions start
mov eax,temp_r9           ; r9d
cpuid
test eax,eax
jns NoExtended            ; Go skip extended functions if bit EAX.31 = 0
cmp eax,80000000h + EXTENDED_LIMIT  ; EAX = maximum extended function number
ja ErrorCpuId             ; Go if invalid limit
call SequenceCpuId
jc ErrorCpuId             ; Exit if output buffer overflow
NoExtended:

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
;---------- CPUID function 1Fh = V2 Extended topology enumeration -------------;
Function0B:
Function1F:
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
;---------- CPUID function 10h = L3 cache QoS enforcement enumeration (same) --;
Function0F:
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
;---------- CPUID function 1Dh = Intel AMX Tile Information -------------------;
Function14:
Function17:
Function1D:
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
jmp .L2
.L0:
mov eax,temp_r9       ; r9d
mov ecx,esi           ; ECX = Current sub-function number
cpuid
test dl,00011111b     ; Check TLB deterministic data validity
jz .L1                ; Go skip if subfunction invalid, can be unordered
.L2:
call StoreCpuId_Entry
ja OverSubFunction    ; Go if output buffer overflow
.L1:
inc esi               ; Sunfunctions number +1
cmp esi,temp_r8       ; r8d 
jbe .L0               ; Go cycle if next sub-function exist
jmp AfterSubFunction

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

;------------------------------------------------------------------------------;
; Measure CPU Clock frequency by Time Stamp Counter (TSC)                      ;
;                                                                              ;
; INPUT:   RDI = Pointer to OPB (Output Parameters Block)                      ;
;                                                                              ;
; OUTPUT:  QWORD OPB[00] = Frequency, Hz, 0 if measurement error               ;
;------------------------------------------------------------------------------;
Get_CPUCLK:
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
mov dword [edi+0],eax
mov dword [edi+4],edx
@@:
pop edx ecx ebx eax
ret

;------------------------------------------------------------------------------;
; Get CPU context management state for context save-restore subsystem          ;
;                                                                              ;
; INPUT:   EDI = Pointer to OPB (Output Parameters Block)                      ;
;                                                                              ;
; OUTPUT:  QWORD OPB[00] = CPU validation mask                                 ;
;          QWORD OPB[08] = OS validation mask                                  ;
;------------------------------------------------------------------------------;
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

;--- Measure CPU TSC (Time Stamp Counter) clock frequency ---------------------;
;                                                                              ;
; INPUT:   None                                                                ;
;                                                                              ;
; OUTPUT:  CF flag = Status: 0(NC)=Measured OK, 1(C)=Measurement error	       ;
;          Output RAX,RDX valid only if CF=0(NC)                               ;
;          EDX:EAX = TSC Frequency, Hz, F = Delta TSC per 1 second             ;
;------------------------------------------------------------------------------;
MeasureCpuClk:
push edi esi ebp
;--- Prepare parameters, early to minimize dTSC ---
sub esp,32
mov ebx,esp
lea ecx,[ebx+16]          ; ECX = Pointer to stored remain time: DQ sec, ns
xor eax,eax
mov dword [ebx+00],1
mov dword [ebx+04],eax 
mov dword [ebx+08],eax
mov dword [ebx+12],eax 
mov dword [ecx+00],eax
mov dword [ecx+04],eax 
mov dword [ecx+08],eax
mov dword [ecx+12],eax 
;--- Get TSC value before 1 second pause ---
rdtsc               ; EDX:EAX = TSC, EDX = High , EAX = Low
push eax edx
;--- Wait 1 second ---
mov eax,162         ; EAX = Linux API function (syscall number) = SYS_NANOSLEEP
push ecx
int 80h
pop ecx
xchg ebx,eax
;--- Get TSC value after 1 second pause ---
rdtsc           ; EDX:EAX = TSC, EDX = High , EAX = Low , BEFORE 1 second pause
pop edi esi     ; EDI:ESI = TSC, ECX = High , EBX = Low , AFTER 1 second pause
;--- Check results ---
test ebx,ebx
jnz TimerFailed    ; Go if error returned or wait interrupted
mov ebx,[ecx+00]   ; Time remain, seconds
or ebx,[ecx+04]    
or ebx,[ecx+08]    ; Disjunction with Time remain, nanoseconds
or ebx,[ecx+12]    
jnz TimerFailed    ; Go if remain time stored by function
;--- Calculate delta-TSC per 1 second = TSC frequency ---
sub eax,esi    ; Subtract: DeltaTSC.Low  = EndTSC.Low - StartTSC.Low
sbb edx,edi    ; Subtract: DeltaTSC.High = EndTSC.High - StartTSC.High - Borrow
test edx,edx
jnz TimerFailed   ; This debug 32-bit code not supports > 4GHz
;--- Exit points ---
add esp,32
clc
TimerDone:
pop ebp esi edi
ret
TimerFailed:
add esp,32
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

;--- Functions pointers, for IPB absent ---
FunctionCount      =   3
FunctionSelector   DD  Get_CPUID      ; 0 = Get CPUID dump
                   DD  Get_CPUCLK     ; 1 = Measure CPU TSC Clock
                   DD  GetCPUCTX      ; 2 = Get CPU context management flags

;--- Functions pointers, for IPB present ---
iFunctionCount     =   0
iFunctionSelector  DD  0
