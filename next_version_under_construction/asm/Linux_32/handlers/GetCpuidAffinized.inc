;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; *.SO library for Linux ia32.                                                 ; 
; Handler for java request ID = REQUEST_GET_CPUID_AFFINIZED.                   ;
;                                                                              ;
; Get CPUID information as Standard/Extended/Virtual/Vendor CPUID functions    ;
; with subfunctions. Dump for one selected logical processor (thread).         ;
; Logical processor selected by affinization Linux API.                        ;
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
;           DWORD [ESI + 16] = Logical processor number for affinization,      ;
;                              this function internally creates affinity       ;
;                              mask as left shift "1" by this number.          ;
;           DWORD [ESI + 20] = Reserved for affinization options.              ; 
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result. Output buffer located    ;
;           from [ EDI ] to [ RDI + data buffer maximum length ] exclusive.    ;
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

GetCpuidAffinized:
push ebx ebp
mov ebp,esp
xor eax,eax
push eax eax

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

;---------- Verify output buffer size. ----------------------------------------;
mov dword [edi + 04],2      ; Status = 2 means insufficient output buffer size.
mov eax,[esi + 08]
cmp eax,32 + 32
jb .exit
sub eax,32
shr eax,5
mov [ebp - 4],eax     ; DWORD [EBP - 4] = Limit for output entries count.

;---------- Set thread affinity mask. -----------------------------------------;
; TODO. Error check required after system calls.
push esi edi 0 0 0 0
mov ecx,[esi + 16]
mov edx,1
rol edx,cl
mov [esp + 0],edx
mov dword [esp + 4],0
xor ebx,ebx               ; Parm#1 = EBX = PID (Old variant = GET_SYSPID).
mov ecx,8                 ; Parm#2 = ECX = Mask size.
lea edx,[esp + 8]         ; Parm#3 = EDX = Pointer for store mask.
mov eax,SYS_GETAFFINITY   ; System call code = EAX.
int 80h
                          ; Parm#1 = EBX = PID (Old variant = GET_SYSPID).
mov ecx,8                 ; Parm#2 = ECX = Mask size.
mov edx,esp               ; Parm#3 = EDX = Pointer for store mask.
mov eax,SYS_SETAFFINITY   ; System call code = EAX.
int 80h
pop eax eax ebx eax edi esi   ; Make EBX = Original mask. TODO. Yet 32-bit (32 CPU) only.

;---------- Get CPUID dump, all functions/subfunctions per selected CPU. ------;
push edi
add edi,32
mov eax,[ebp - 4]
call HelperGetCpuid
pop edi
mov dword [edi + 00],eax  ; Return size = eax 
mov dword [edi + 04],3    ; Status = 3 means output buffer overflow. 
jc .bufferOverflow
mov dword [edi + 04],0    ; Status = 0 means no errors.
.bufferOverflow:

;---------- Restore thread affinity mask. -------------------------------------;
; TODO. Error check required after system calls.
push 0 ebx
xor ebx,ebx               ; Parm#1 = EBX = PID (Old variant = GET_SYSPID)..
mov ecx,8                 ; Parm#2 = ECX = Mask size.
mov edx,esp               ; Parm#3 = EDX = Pointer for store mask.
mov eax,SYS_SETAFFINITY   ; System call code = EAX.
int 80h
pop eax eax

;---------- Done. -------------------------------------------------------------;
.exit:
leave
pop ebx
ret

