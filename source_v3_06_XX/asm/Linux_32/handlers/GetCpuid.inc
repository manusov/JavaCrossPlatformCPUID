;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; *.SO library for Linux ia32.                                                 ; 
; Handler for java request ID = REQUEST_GET_CPUID.                             ;
;                                                                              ;
; Get CPUID information as Standard/Extended/Virtual/Vendor CPUID functions    ;
; with subfunctions. Dump for one logical processor (current thread).          ;
; This variant without affinization.                                           ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see LINUX32JNI.asm), then works this  ;
; *.SO library JNI gate and calls this subroutine, selected by request ID.     ; 
; Non-volatile registers for this subroutine: EBP, ESI, EDI.                   ;
;                                                                              ;
; INPUT:    ESI = Pointer to IPB (Input Parameters Block).                     ;
;           EDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [ESI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [ESI + 04] = Reserved for request options.                   ;
;           DWORD [ESI + 08] = CPUID data buffer maximum length, bytes.        ;
;           DWORD [ESI + 12] = Reserved for big data buffer length extension.  ;  
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result. Output buffer located    ;
;           from [ EDI ] to [ EDI + data buffer maximum length ] exclusive.    ;
;           DWORD [EDI + 00] = Number of 32-byte entries returned.             ;
;           DWORD [EDI + 04] = Status, 0 if OK (output valid) or error code.   ;
;           6 DWORDs from [EDI + 08] = Reserved for status and alignment.      ;
;           Entries located at OPB from [EDI + 32].                            ;
;                                                                              ;
;------------------------------------------------------------------------------;
; Maximum entries count = integer part of COUNT = ( buffer length - 32 ) / 32. ;
; Each entry is 32 bytes, 8 dwords:                                            ;
;------------------------------------------------------------------------------;
; Dword   Offset in entry(hex)   Comments                                      ;
;------------------------------------------------------------------------------;
;   0     00-03                  Information type tag, 0 for CPUID info.       ;                  
;   1     04-07                  CPUID function number.                        ;
;   2     08-0B                  CPUID subfunction number.                     ;
;   3     0C-0F                  CPUID pass number (as for function #2),       ;
;                                      pass number is deprecated.              ;
;   4     10-13                  Result EAX after CPUID.                       ;
;   5     14-17                  Result EBX after CPUID.                       ;
;   6     18-1B                  Result ECX after CPUID.                       ;
;   7     1C-1F                  Result EDX after CPUID.                       ;
;                                                                              ;
;------------------------------------------------------------------------------;

GetCpuid:
;---------- Clear output buffer, pre-set error status. ------------------------;
cld
push edi
xor eax,eax
stosd          ; Return size = 0, no entries returned yet.
mov eax,1
stosd          ; Status = 1 means CPUID not supported or locked.
mov ecx,6
xor eax,eax
rep stosd
pop edi
;---------- Verify CPUID instruction supported and non-locked. ----------------;
call CheckCpuid
jc .exit
;---------- Get CPUID dump, all functions/subfunctions per selected CPU. ------;
mov dword [edi + 04],2      ; Status = 2 means insufficient output buffer size.
mov eax,[esi + 08]
cmp eax,32 + 32
jb .exit
sub eax,32
shr eax,5
push edi
add edi,32
call HelperGetCpuid         ; EDI = Buffer pointer, EAX = Entries count limit.
pop edi
;---------- Done. -------------------------------------------------------------;
mov dword [edi + 00],eax    ; Return size = eax 
mov dword [edi + 04],3      ; Status = 3 means output buffer overflow. 
jc .exit
mov dword [edi + 04],0      ; Status = 0 means no errors.
.exit:
ret

;---------- Limits by expected counts of CPUID functions. ---------------------;
STANDARD_LIMIT = 255  ; Limit for standard CPUID functions 000000xxh.
EXTENDED_LIMIT = 255  ; Limit for extended CPUID functions 800000xxh.
VIRTUAL_LIMIT  = 255  ; Limit for virtual CPUID functions  400000xxh.

;---------- Get CPUID dump. ---------------------------------------------------;
;                                                                              ;
; INPUT:   EDI = Pointer to output buffer.                                     ;
;          EAX = CPUID entries count limit.                                    ;
;                                                                              ;
; OUTPUT:  CF flag = status: 0(NC) no errors, 1(C) = error.                    ; 
;          EAX = Number of output entries.                                     ;
;          Output buffer updated.                                              ;
;------------------------------------------------------------------------------;
; Output buffer maximum size is 16352 bytes, 511 entries * 32 bytes.           ;
; Each entry is 32 bytes, 8 dwords:                                            ;
; dword   offset in entry(hex)   comments                                      ;
;------------------------------------------------------------------------------;
;   0     00-03                  Information type tag, 0 for CPUID info.       ;                  
;   1     04-07                  CPUID function number.                        ;
;   2     08-0B                  CPUID subfunction number.                     ;
;   3     0C-0F                  CPUID pass number (as for function #2).       ;
;   4     10-13                  Result EAX after CPUID.                       ;
;   5     14-17                  Result EBX after CPUID.                       ;
;   6     18-1B                  Result ECX after CPUID.                       ;
;   7     1C-1F                  Result EDX after CPUID.                       ;
;------------------------------------------------------------------------------;

temp_r8d   EQU  dword [ebp + 00]  ; This for porting from x64 code.
temp_r8up  EQU  dword [ebp + 04]   
temp_r9d   EQU  dword [ebp + 08]
temp_r10d  EQU  dword [ebp + 12]
temp_ebp   EQU  dword [ebp + 16]
temp_r15d  EQU  dword [ebp + 20]

HelperGetCpuid:
;---------- Initializing. -----------------------------------------------------;
cld
push ebx ebp esi edi  ; TODO. Use LEAVE instruction at exit.
push eax
xor eax,eax
push eax eax eax eax eax
mov ebp,esp
mov temp_ebp,0         ; EBP = Global output entries counter.

;- call CheckCpuid     ; Return CF=Error flag, EAX=Maximum standard function.
;- jc .noCpuId

;---------- Get standard CPUID results. ---------------------------------------;
xor eax,eax            ; EAX = Function number for CPUID instruction.
mov temp_r9d,eax       ; R9D = Standard functions start.
xor ecx,ecx            ; ECX = Subfunction, here redundant (function 0).
cpuid
;--- end of added for HardwareShell variant. ---
cmp eax,STANDARD_LIMIT ; EAX = maximum supported standard function number.
ja .errorCpuId         ; Go if invalid limit.
call SequenceCpuId
jc .errorCpuId         ; Exit if output buffer overflow at subfunction.
;---------- Get virtual CPUID results. ----------------------------------------;
mov eax,040000000h     ; EAX = Function number for CPUID instruction. 
mov temp_r9d,eax       ; R9D = virtual functions start number.
xor ecx,ecx            ; ECX = Subfunction, here redundant (function 40000000h).
cpuid
mov ecx,eax
and ecx,0FFFFFF00h     ; ECX = Pattern bits for check virtual CPUID support.
cmp ecx,temp_r9d       ; Compare pattern bits.
jne .noVirtual         ; Skip virtual CPUID if not supported.
cmp eax,40000000h + VIRTUAL_LIMIT  ; EAX = maximum extended function number.
ja .errorCpuId         ; Go if invalid limit, too big function number.
call SequenceCpuId
jc .errorCpuId         ; Exit if output buffer overflow at subfunction.
.noVirtual:            ; This label for skip virtual functions.
;---------- Get extended CPUID results. ---------------------------------------;
mov eax,80000000h
mov temp_r9d,eax       ; R9D  = extended functions start (function 80000000h).
cpuid
test eax,eax
jns .noExtended        ; Go skip extended functions if bit EAX.31 = 0.
cmp eax,80000000h + EXTENDED_LIMIT  ; EAX = maximum extended function number.
ja .errorCpuId         ; Go if invalid limit.
call SequenceCpuId
jc .errorCpuId         ; Exit if output buffer overflow.
.noExtended:
;---------- Return points. ----------------------------------------------------;
mov eax,temp_ebp       ; Normal exit point, return EAX = number of entries.
.okCpuId:
add esp,24
clc
.exit:                 ; Exit point for errors.
pop edi esi ebp ebx
ret
.errorCpuId:
add esp,24
stc
jmp .exit

;---------- Subroutine, sequence of CPUID functions. Local helper. ------------;
;                                                                              ;
; INPUT:   R9D (temp_r9) = Start CPUID function number.                        ;
;          EAX = Limit CPUID function number (inclusive).                      ;
;          EDI = Pointer to memory buffer.                                     ;
;          EBP = CPUID functions (32B entries) counter.                        ;
;                                                                              ;
; OUTPUT:  EDI = Modified by store CPUID input parms + output parms entry.     ;
;          EBP = CPUID functions (32B entries) counter, updated.               ;
;          Flags condition code: Carry (C) = means entries count limit.        ;
;------------------------------------------------------------------------------;
SequenceCpuId:
mov temp_r10d,eax     ; R10D = standard or extended functions limit, 
.cycleCpuid:          ; Note XCHG compact than MOV if AX/EAX/RAX.
;--- Specific handling for functions with subfunctions. ---
mov eax,temp_r9d      ; EAX = function number, input at R9D.
cmp eax,07Fh
ja .skipAbove7F
cmp al,04h
je .function04
cmp al,07h
je .function07
cmp al,0Bh
je .function0B
cmp al,0Dh
je .function0D
cmp al,0Fh
je .function0F
cmp al,10h
je .function10
cmp al,12h
je .function12
cmp al,14h
je .function14
cmp al,17h
je .function17
cmp al,18h
je .function18
cmp al,1Bh
je .function1B
cmp al,1Dh
je .function1D
cmp al,1Fh
je .function1F
cmp al,20h
je .function20
cmp al,23h
je .function23
cmp al,24h
je .function24
.skipAbove7F:
cmp eax,8000001Dh
je .function8000001D
cmp eax,80000020h
je .function80000020
cmp eax,80000026h
je .function80000026
;---------- Default handling for functions without subfunctions. --------------;
xor esi,esi           ; ESI = sub-function number for save entry.
xor ecx,ecx           ; ECX = sub-function number for CPUID.  
call StoreCpuid
ja .overSubFunction
.afterSubFunction:    ; Return point after sub-function specific handler.
inc temp_r9d
mov eax,temp_r9d
cmp eax,temp_r10d
jbe .cycleCpuid       ; Cycle for CPUID standard functions.
ret
.overSubFunction:
stc
ret 
;---------- CPUID function 00000004h = Deterministic cache parameters. --------;
;---------- CPUID function 8000001Dh = Deterministic cache parameters (AMD). --;
; Subfunctions enumeration logic: 
; incremental execution of subfunctions, break if EAX.[4-0] = Cache type = 0. 
.function04:
.function8000001D:
xor esi,esi           ; ESI = Storage for sub-function number.
@@:
mov eax,temp_r9d      ; EAX = function number, restore from R9D because cycle.
mov ecx,esi           ; ECX = subfunction number.
cpuid
test al,00011111b     ; Check for subfunction list end.
jz .afterSubFunction  ; Go if reach first not valid subfunction.
call StoreCpuid_Entry
ja .overSubFunction   ; Go if output buffer overflow.
inc esi               ; Sunfunctions number +1.
jmp @b                ; Go repeat for next subfunction.
;-------- CPUID function 07h = Structured extended feature flags. -------------;   
;-------- CPUID function 14h = Intel processor trace enumeration. -------------;
;-------- CPUID function 17h = System-On-Chip vendor attribute enumeration. ---;
;-------- CPUID function 1Dh = Intel AMX tile information. --------------------;
;-------- CPUID function 20h = Processor history reset. -----------------------;
;-------- CPUID function 24h = Converged vector ISA. --------------------------;
; Subfunctions enumeration logic:
; LIMIT = EAX after subfunction 0, execute subfunctions from 0 to LIMIT. 
.function07:
.function14:
.function17:
.function1D:
.function20:
.function24:
xor esi,esi           ; ESI = Storage for sub-function number.
mov ecx,esi
cpuid
mov temp_r8d,eax      ; R8D = Maximum sub-function number.
.entryMaxSub:
mov eax,temp_r9d      ; EAX = function number, restore from R9D because cycle.
mov ecx,esi           ; ECX = Current sub-function number.
call StoreCpuid
ja .overSubFunction   ; Go if output buffer overflow.
inc esi               ; Sunfunctions number +1.
cmp esi,temp_r8d      ; Compare next subfunction number and limit. 
jbe .entryMaxSub      ; Go cycle if next sub-function exist.
jmp .afterSubFunction
;-------- CPUID function 80000026h = Extended CPU topology. -------------------;
; Subfunctions enumeration logic:
; LIMIT = const = 3, execute subfunctions from 0 to LIMIT. 
.function80000026:
mov temp_r8d,3        ; R8D = Maximum sub-function number (inclusive).
jmp .entryMaxSub
;---------- CPUID function 0Bh = Extended topology enumeration. ---------------;
;---------- CPUID function 1Fh = V2 Extended topology enumeration. ------------;
; Subfunctions enumeration logic:
; incremental execution of subfunctions, break if
; bits ECX.[15-8] = Domain type = 0. 
.function0B:
.function1F:
xor esi,esi           ; ESI = Storage for sub-function number.
.L12:
mov eax,temp_r9d      ; EAX = function number, restore from R9D because cycle.
mov ecx,esi           ; ECX = subfunction number.
cpuid
test esi,esi
jz .L13               ; Save results and skip check if subfunction 0.
test ch,ch            ; Check for subfunction list end. Note CH = ECX.[15-8].
jz .afterSubFunction  ; Go if reach first not valid subfunction.
.L13:
call StoreCpuid_Entry
ja .overSubFunction   ; Go if output buffer overflow.
inc esi               ; Sunfunctions number +1.
jmp .L12              ; Go repeat for next subfunction.
;---------- CPUID function 0Dh = Processor extended state enumeration. --------;
; Subfunctions enumeration logic:
; Selective execution subfunctions from INDEX=0 to INDEX=63 (inclusive) if
; BITMAP[INDEX]=1. 
; BITMAP[H,L] = registers EDX:EAX after subfunction 0.    
.function0D:
xor ecx,ecx           ; ECX = sub-function number.
cpuid
mov temp_r8up,edx
mov temp_r8d,eax      ; R8 = 64-bit bitmap.
xor esi,esi           ; ESI = Storage for sub-function number.
.L0:
shr temp_r8up,1
rcr temp_r8d,1
jnc .L1
mov eax,temp_r9d      ; EAX = function number, restore from R9D because cycle.
mov ecx,esi           ; ECX = Sub-function number.
call StoreCpuid
ja .overSubFunction   ; Go if output buffer overflow.
.L1:
inc esi               ; Sunfunctions number +1.
cmp esi,63            ; Highest mask bit for check = D63. 
jbe .L0               ; Go cycle if next sub-function exist.
jmp .afterSubFunction 
;-------- CPUID function 0Fh = Platform QoS monitoring enumeration. -----------;
; Subfunctions enumeration logic:
; Subfunction 1 exists if subfunction 0 EDX.1 = 1. 
.function0F:
xor esi,esi            ; ESI = Storage for sub-function number.
mov ecx,esi            ; ECX = subfunction number.
cpuid
mov temp_r8d,edx
call StoreCpuid_Entry  ; Store results of subfunction 0. 
ja .overSubFunction    ; Go if output buffer overflow.
test temp_r8d,0010b
jz .afterSubFunction   ; Go if subfunction 1 not exists.
inc esi                ; Sunfunctions number +1.
mov eax,temp_r9d
mov ecx,esi            ; ECX = Sub-function number.
call StoreCpuid        ; Execute and store results of subfunction 1.
ja .overSubFunction    ; Go if output buffer overflow.
jmp .afterSubFunction
;-------- CPUID function 00000010h = L3 cache QoS enforcement enumeration. ----;
;-------- CPUID function 80000020h = Platform QoS ext. features (AMD). --------; 
; Subfunctions enumeration logic:
; Selective execution subfunctions from INDEX=0 to INDEX=5 (inclusive) if
; BITMAP[INDEX]=1. 
; BITMAP = register bits EBX.[3-1] or EDX[3-1] after subfunction 0.    
.function10:
.function80000020:
xor ecx,ecx           ; ECX = sub-function number.
cpuid
mov temp_r8d,ebx
or temp_r8d,edx
or temp_r8d,0001b     ; D0=1 because subfunction 0 execute unconditionally.
xor esi,esi           ; ESI = Storage for sub-function number.
.L5:
shr temp_r8d,1
jnc .L6
mov eax,temp_r9d
mov ecx,esi           ; ECX = Sub-function number.
call StoreCpuid
ja .overSubFunction   ; Go if output buffer overflow.
.L6:
inc esi               ; Sunfunctions number +1.
cmp esi,5             ; Highest mask bit for check = D5. 
jbe .L5               ; Go cycle if next sub-function exist.
jmp .afterSubFunction 
;-------- CPUID function 12h = Intel SGX capability/attributes enumeration. ---;
; Subfunctions enumeration logic:
; Subfunctions 0-1 called and stored unconditionally.
; Subfunctions 2-5(limit=5=?) called, but stored if result bits 
; EAX.[3-0] = sub-leaf type is non zero.     
.function12:
xor esi,esi           ; ESI = Storage for sub-function number.
mov ecx,esi           ; ECX = subfunction number.
call StoreCpuid       ; Execute and store results of subfunction 0. 
ja .overSubFunction   ; Go if output buffer overflow.
inc esi               ; Sunfunctions number +1.
mov eax,temp_r9d
mov ecx,esi           ; ECX = Sub-function number.
call StoreCpuid       ; Execute and store results of subfunction 1.
ja .overSubFunction   ; Go if output buffer overflow.
inc esi
.L7:
mov eax,temp_r9d      ; EAX = function number, restore from R9D because cycle.
mov ecx,esi           ; ECX = subfunction number.
cpuid
test eax,00001111b    ; Check for subfunction valid.
jz .L8                ; Go for skip not valid subfunction.
call StoreCpuid_Entry
ja .overSubFunction   ; Go if output buffer overflow.
.L8:
inc esi
cmp esi,5
jbe .L7
jmp .afterSubFunction
;---------- CPUID function 18h = Deterministic Address Translation Parms. -----;
; Subfunctions enumeration logic:
; LIMIT = EAX after subfunction 0, execute subfunctions from 0 to LIMIT. 
; Plus, for subfunctions >0,
; skip subfunction if EDX.[4-0] = Translation cache type = 0.
.function18:
xor esi,esi           ; ESI = Storage for sub-function number.
mov ecx,esi
cpuid
mov temp_r8d,eax      ; R8D = Maximum sub-function number.
jmp .L4               ; Skip check EDX.[4-0] bits if subfunction 0.
.L2:
mov eax,temp_r9d
mov ecx,esi           ; ECX = Current sub-function number.
cpuid
test dl,00011111b     ; Check TLB deterministic data validity.
jz .L3                ; Go skip if subfunction invalid, can be unordered.
.L4:
call StoreCpuid_Entry
ja .overSubFunction   ; Go if output buffer overflow.
.L3:
inc esi               ; Sunfunctions number +1.
cmp esi,temp_r8d 
jbe .L2               ; Go cycle if next sub-function exist.
jmp .afterSubFunction
;---------- CPUID function 1Bh = PCONFIG information. -------------------------;
; Subfunctions enumeration logic:
; executie subfunctions from INDEX=0 to INDEX=3 (inclusive, 3=?),
; store results if output EAX = subleaf type > 0.  
.function1B:
xor esi,esi           ; ESI = Storage for sub-function number.
.L10:
mov eax,temp_r9d      ; EAX = function number, restore from R9D because cycle.
mov ecx,esi           ; ECX = Subfunction number.
cpuid
test esi,esi
jz .L11               ; Save results unconditionally if subfunction 0.
test eax,eax
jz .L9
.L11:
call StoreCpuid_Entry
ja .overSubFunction   ; Go if output buffer overflow.
.L9:
inc esi               ; Sunfunctions number +1.
cmp esi,3             ; Highest mask bit for check = D63. 
jbe .L10              ; Go cycle if next sub-function exist.
jmp .afterSubFunction 
;---------- CPUID function 23h = Architectural performance monitoring. --------;
; Subfunctions enumeration logic:
; Selective execution subfunctions from INDEX=0 to INDEX=31 (inclusive) if
; BITMAP[INDEX]=1. But subfunction 0 execution is unconditionally. 
; BITMAP = register EAX after subfunction 0.
.function23:
xor ecx,ecx           ; ECX = sub-function number.
cpuid
mov temp_r8d,eax
or temp_r8d,1         ; Subfunction 0 execution is unconditionally. 
xor esi,esi           ; ESI = Storage for sub-function number.
.L14:
shr temp_r8d,1
jnc .L15
mov eax,temp_r9d      ; EAX = function number, restore from R9D because cycle.
mov ecx,esi           ; ECX = Sub-function number.
call StoreCpuid
ja .overSubFunction   ; Go if output buffer overflow.
.L15:
inc esi               ; Sunfunctions number +1.
cmp esi,31            ; Highest mask bit for check = D31. 
jbe .L14              ; Go cycle if next sub-function exist.
jmp .afterSubFunction 

;---------- Subroutine, one CPUID function execution. Local helper. -----------;
;                                                                              ;
; INPUT:  EAX = CPUID function number.                                         ;
;         R9D (temp_r9) = EAX.                                                 ;
;         ECX = CPUID subfunction number.                                      ;
;         ESI = ECX.                                                           ;
;         EDI = Pointer to memory buffer.                                      ;
;         EBP = CPUID functions (32B entries) counter.                         ;
;                                                                              ;
; OUTPUT: EDI = Modified by store CPUID input parms + output parms entry.      ;
;         Flags condition code: Above (A) = means entries count limit.         ;
;------------------------------------------------------------------------------;
StoreCpuid:
cpuid
StoreCpuid_Entry:   ; Entry point for CPUID results (EAX,EBX,ECX,EDX) ready. 
cld
push eax
xor eax,eax
stosd               ; Store tag dword[0] = Information type.
mov eax,temp_r9d
stosd               ; Store argument dword [1] = CPUID function number. 
mov eax,esi
stosd               ; Store argument dword [2] = CPUID sub-function number.
xor eax,eax
stosd               ; Store argument dword [3] = CPUID pass number (see fn.2).
pop eax
stosd               ; Store result dword [4] = output EAX. 
xchg eax,ebx
stosd               ; Store result dword [5] = output EBX.
xchg eax,ecx
stosd               ; Store result dword [6] = output ECX.
xchg eax,edx
stosd               ; Store result dword [7] = output EDX.
inc temp_ebp        ; Global counter +1.

mov eax,temp_ebp
cmp eax,temp_r15d   ; Compare EBP with limit for number of output entries.

;- cmp temp_ebp,ENTRIES_LIMIT  ; Compare EBP with limit for number of output entries.

ret

