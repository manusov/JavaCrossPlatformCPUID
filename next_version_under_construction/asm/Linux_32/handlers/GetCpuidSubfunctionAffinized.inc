;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; *.SO library for Linux ia32.                                                 ; 
; Handler for java request ID = REQUEST_GET_CPUID_SUBFUNCTION_AFFINIZED.       ;
;                                                                              ;
; Get CPUID information for one selected Standard/Extended/Virtual/Vendor      ;
; CPUID function with optional subfunction selection. Executed for one         ;
; selected logical processor (thread), selected by affinization Linux API.     ;  
;                                                                              ;
; JVM calls "requestBinary" entry point (see LINUX32JNI.asm), then works this  ;
; *.SO library JNI gate and calls this subroutine, selected by request ID.     ; 
; Non-volatile registers for this subroutine: EBP, ESI, EDI.                   ;
;                                                                              ;
; INPUT:    ESI = Pointer to IPB (Input Parameters Block).                     ;
;           EDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [ESI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [ESI + 04] = Reserved for request options.                   ;
;           DWORD [ESI + 08] = CPUID function, used as CPUID input EAX.        ;
;           DWORD [ESI + 12] = CPUID sub-function, used as CPUID input ECX.    ;
;           DWORD [ESI + 16] = Logical processor number for affinization,      ;
;                              this function internally creates affinity       ;
;                              mask as left shift "1" by this number.          ;
;           DWORD [ESI + 20] = Reserved for affinization options.              ; 
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result.                          ;
;           DWORD [EDI + 00] = Number of 32-byte entries returned.             ;
;                              must be maximum 1 for this function.            ;
;           DWORD [EDI + 04] = Status, 0 if OK (output valid) or error code.   ; 
;           6 DWORDs from [EDI + 08] = Reserved for status and alignment.      ;
;           One entry returned by this function, it located at OPB [EDI + 32]. ; 
;                                                                              ;
;------------------------------------------------------------------------------;
; Output data entry is 32 bytes, 8 dwords:                                     ;
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

temp_handle  EQU  dword [ebp + 24]
temp_mask    EQU  dword [ebp + 28]

GetCpuidSubfunctionAffinized:
push ebp
xor eax,eax
push eax eax eax eax eax eax eax eax
mov ebp,esp

;---------- Verify CPUID instruction supported and non-locked. ----------------;
cld
push edi
stosd          ; Return size = 0, no entries returned yet.
mov eax,1
stosd          ; Status = 1 means CPUID not supported or locked.
mov ecx,6
xor eax,eax
rep stosd
pop edi

call CheckCpuid
jc .exit

;---------- Set thread affinity mask. -----------------------------------------;
; call [GetCurrentThread]
; mov dword [edi + 04],4
; test eax,eax
; jz .exit
; mov temp_handle,eax     ; Save thread handle, used for affinization functions.
; mov dword [edi + 04],5
; cmp [_SetThreadAffinityMask],0   ; Check pointer to affinization function.
; jz .exit
; mov ecx,[esi + 16]
; mov edx,1
; rol edx,cl
; push edx                  ; Parm#2 = mask.
; push ebx                  ; Parm#1 = handle.
; call [_SetThreadAffinityMask]
; test eax,eax
; jz .exit
; mov temp_mask,eax         ; Save original affinity mask.

; TODO. Error check required after system calls.
; push esi edi 0 0 0 0
; mov ecx,[esi + 0]
; mov edx,1
; rol edx,cl
; mov [esp + 0],edx
; mov dword [esp + 4],0
; mov eax,SYS_GETPID
; int 80h
; mov ebx,eax               ; Parm#1 = EBX = PID.
; mov ecx,8                 ; Parm#2 = ECX = Mask size.
; lea edx,[esp + 8]         ; Parm#3 = EDX = Pointer for store mask.
; mov eax,SYS_GETAFFINITY   ; System call code = EAX.
; int 80h
;                           ; Parm#1 = EBX = PID.
; mov ecx,8                 ; Parm#2 = ECX = Mask size.
; mov edx,esp               ; Parm#3 = EDX = Pointer for store mask.
; mov eax,SYS_SETAFFINITY   ; System call code = EAX.
; int 80h
; pop eax eax ebp eax edi esi   ; Make R12 = still PID, R13 = Original mask.

;--- Get CPUID one function and subfunction (optional) per selected CPU. ------;
push ebx esi edi
cld
add esi,8
add edi,32
lodsd
mov temp_r9d,eax
lodsd
xchg ecx,eax
mov eax,temp_r9d   ; EAX  = R9D = Function, select by this line. 
mov esi,ecx        ; ECX  = ESI = Sub-function, select by this line.
mov temp_ebp,0     ; EBP  = Entries counter (redundant here).
mov temp_r15d,1    ; R15D = Entries count limit (redundant here). 
call StoreCpuid
pop edi esi ebx 

;---------- Restore thread affinity mask. -------------------------------------;
; push temp_mask            ; Parm#2 = mask.
; push temp_handle          ; Parm#1 = handle.
; call [_SetThreadAffinityMask]
; test eax,eax
; mov dword [edi + 04],5
; jz .exit
; mov eax,temp_ebp
; mov dword [edi + 00],eax
; mov dword [edi + 04],0

; TODO. Error check required after system calls.
; push 0 ebp
;                           ; Parm#1 = EBX = PID.
; mov ecx,8                 ; Parm#2 = ECX = Mask size.
; mov edx,esp               ; Parm#3 = EDX = Pointer for store mask.
; mov eax,SYS_SETAFFINITY   ; System call code = EAX.
; int 80h
; pop eax eax

;---------- Done. -------------------------------------------------------------;
.exit:
add esp,32
pop ebp
ret

