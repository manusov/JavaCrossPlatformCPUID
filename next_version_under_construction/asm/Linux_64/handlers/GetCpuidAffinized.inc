;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; *.SO library for Linux x64.                                                  ; 
; Handler for java request ID = REQUEST_GET_CPUID_AFFINIZED.                   ;
;                                                                              ;
; Get CPUID information as Standard/Extended/Virtual/Vendor CPUID functions    ;
; with subfunctions. Dump for one selected logical processor (thread).         ;
; Logical processor selected by affinization Linux API.                        ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see LINUX64JNI.asm), then works this  ;
; *.SO library JNI gate and calls this subroutine, selected by request ID.     ; 
; Non-volatile registers for this subroutine: RBX, RBP, RSI, RDI, R12, R13.    ;
;                                                                              ;
; INPUT:    RSI = Pointer to IPB (Input Parameters Block).                     ;
;           RDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [RSI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [RSI + 04] = Reserved for request options.                   ;
;           DWORD [RSI + 08] = CPUID data buffer maximum length, bytes.        ;
;           DWORD [RSI + 12] = Reserved for big data buffer length extension.  ;
;           DWORD [RSI + 16] = Logical processor number for affinization,      ;
;                              this function internally creates affinity       ;
;                              mask as left shift "1" by this number.          ;
;           DWORD [RSI + 20] = Reserved for affinization options.              ; 
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result. Output buffer located    ;
;           from [ RDI ] to [ RDI + data buffer maximum length ] exclusive.    ;
;           DWORD [RDI + 00] = Number of 32-byte entries returned.             ;
;           DWORD [RDI + 04] = Status, 0 if OK (output valid) or error code.   ;
;           6 DWORDs from [RDI + 08] = Reserved for status and alignment.      ;
;           Entries located at OPB from [RDI + 32].                            ;
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
push rbx rbp
mov rbp,rsp 
and rsp,0FFFFFFFFFFFFFFF0h
sub rsp,32
;---------- Clear output buffer, pre-set error status. ------------------------;
cld
push rdi
xor eax,eax
stosd          ; Return size = 0, no entries returned yet.
mov eax,1
stosd          ; Status = 1 means CPUID not supported or locked.
mov ecx,6
xor eax,eax
rep stosd
pop rdi
;---------- Verify CPUID instruction supported and non-locked. ----------------;
call CheckCpuid
jc .exit
;---------- Verify output buffer size. ----------------------------------------;
mov dword [rdi + 04],2      ; Status = 2 means insufficient output buffer size.
mov eax,[rsi + 08]
cmp eax,32 + 32
jb .exit
sub eax,32
shr eax,5
xchg r15d,eax               ; R15 = Limit for output entries count.
;---------- Set thread affinity mask. -----------------------------------------;
push rsi rdi 0 0
mov ecx,[rsi + 16]
mov rdx,1
rol rdx,cl
mov [rsp + 0],rdx
xor edi,edi                ; Parm#1 = RDI = PID (Old variant = GET_SYSPID).
mov esi,8                  ; Parm#2 = RSI = Mask size.
lea rdx,[rsp + 8]          ; Parm#3 = RDX = Pointer for store mask.
mov eax,SYS_GETAFFINITY    ; System call code = RAX.
syscall
test eax,eax               ; Error check after system call.
js .skip
xor edi,edi                ; Parm#1 = RDI = PID (Old variant = GET_SYSPID).
mov esi,8                  ; Parm#2 = RSI = Mask size.
mov rdx,rsp                ; Parm#3 = RDX = Pointer for store mask.
mov eax,SYS_SETAFFINITY    ; System call code = RAX.
syscall
test eax,eax               ; Error check after system call.
.skip:
pop rdi rbx rdi rsi        ; Make RBX = Original mask.
js .exit
;---------- Get CPUID dump, all functions/subfunctions per selected CPU. ------;
push rdi
add rdi,32
call HelperGetCpuid
pop rdi
mov dword [rdi + 00],eax    ; Return size = eax 
mov dword [rdi + 04],3      ; Status = 3 means output buffer overflow. 
jc .bufferOverflow
.bufferOverflow:
;---------- Restore thread affinity mask. -------------------------------------;
push rsi rdi 0 rbx
xor edi,edi                ; Parm#1 = RDI = PID (Old variant = GET_SYSPID).
mov esi,8                  ; Parm#2 = RSI = Mask size.
mov rdx,rsp                ; Parm#3 = RDX = Pointer for store mask.
mov eax,SYS_SETAFFINITY    ; System call code = RAX.
syscall
test eax,eax               ; Error check after system call.
pop rdi rdi rdi rsi
;---------- Done. -------------------------------------------------------------;
js .exit
mov dword [rdi + 04],0      ; Status = 0 means no errors.
.exit:
leave
pop rbx
ret
