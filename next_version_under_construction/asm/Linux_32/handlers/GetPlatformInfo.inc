;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; *.SO library for Linux ia32.                                                 ; 
; Handler for java request ID = REQUEST_GET_PLATFORM_INFO.                     ;
;                                                                              ;
; Get number of logical processors, platform affinity mask                     ; 
; and optional OS-specific data.                                               ; 
; Operation executed for current thread without affinization.                  ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see LINUX32JNI.asm), then works this  ;
; *.SO library JNI gate and calls this subroutine, selected by request ID.     ; 
; Non-volatile registers for this subroutine: EBP, ESI, EDI.                   ;
;                                                                              ;
; INPUT:    ESI = Pointer to IPB (Input Parameters Block).                     ;
;           EDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [ESI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [ESI + 04] = Reserved for request options.                   ;
;           DWORD [ESI + 08] = Output data buffer maximum length, bytes.       ;
;           DWORD [ESI + 12] = Reserved for big data buffer length extension.  ;  
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result. Output buffer located    ;
;           from [ EDI ] to [ EDI + data buffer maximum length ] exclusive.    ;
;           DWORD [EDI + 00] = Status, 0 if OK (output valid) or error code.   ;
;           DWORD [EDI + 04] = Offset of OS-specific data block #1. 0 if none. ;
;           DWORD [EDI + 08] = Offset of OS-specific data block #2. 0 if none. ;
;           DWORD [EDI + 12] = Platform logical processors count.              ;
;           DWORD [EDI + 16] = Platform affinity mask QWORDs count = N.        ;
;           DWORD [EDI + 20] = Reserved for affinization mode(s) report.       ;
;           N QWORDS from [EDI + 24] = Affinity mask, N x 64-bit values.       ;                       
;                                                                              ;
;------------------------------------------------------------------------------;

GetPlatformInfo:
push esi edi ebx ebp
;---------- Pre-blank results. ------------------------------------------------;
xor eax,eax
mov dword [edi + 00],eax
mov dword [edi + 04],eax
mov dword [edi + 08],eax
mov dword [edi + 16],1
mov dword [edi + 20],eax
;---------- Get PID. ----------------------------------------------------------;
mov eax,SYS_GETPID
int 80h
;---------- Get affinity mask. ------------------------------------------------;
; TODO. Error check required after system calls.
mov ebx,eax                ; Parm#1 = EBX = PID.
mov ecx,8                  ; Parm#2 = ESI = Mask size.
lea edx,[edi + 24]         ; Parm#3 = EDX = Pointer for store mask.
mov eax,SYS_GETAFFINITY    ; System call code = EAX.
int 80h
;---------- Logical CPUs count = F(affinity mask). ----------------------------;
; TODO. Required support >32 (>64) logical processors.
xor eax,eax
mov ecx,32          ; Yet limit = 32 processors (ECX=32).
mov esi,[edi + 24]  ; Yet limit = 32 processors (ESI=mask).
.L0:
shr esi,1
jnc .L1
inc eax
.L1:
loop .L0
mov dword [edi + 12],eax
mov dword [edi + 00],0
;---------- Done. -------------------------------------------------------------;
pop ebp ebx edi esi
ret

