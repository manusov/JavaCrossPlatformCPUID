;------------------------------------------------------------------------------;
;                                                                              ;
; Java cross-platform CPUID Utility.                                           ;
; https://github.com/manusov/JavaCrossPlatformCPUID/tree/master                ;
; https://github.com/manusov?tab=repositories                                  ;
; No copyright. Information belongs to Universe.                               ;
;                                                                              ;
; This subroutine is part of JNI (Java Native Interface)                       ;
; *.SO library for Linux x64.                                                  ; 
; Handler for java request ID = REQUEST_GET_PLATFORM_INFO.                     ;
;                                                                              ;
; Get number of logical processors, platform affinity mask                     ; 
; and optional OS-specific data.                                               ; 
; Operation executed for current thread without affinization.                  ;
;                                                                              ;
; JVM calls "requestBinary" entry point (see LINUX64JNI.asm), then works this  ;
; *.SO library JNI gate and calls this subroutine, selected by request ID.     ; 
; Non-volatile registers for this subroutine: RBX, RBP, RSI, RDI, R12, R13.    ;
;                                                                              ;
; INPUT:    RSI = Pointer to IPB (Input Parameters Block).                     ;
;           RDI = Pointer to OPB (Output Parameters Block).                    ;
;           DWORD [RSI + 00] = Request ID, used by JNI gate, not used here.    ;
;           DWORD [RSI + 04] = Reserved for request options.                   ;
;           DWORD [RSI + 08] = Output data buffer maximum length, bytes.       ;
;           DWORD [RSI + 12] = Reserved for big data buffer length extension.  ;  
;                                                                              ;
; OUTPUT:   Memory at OPB updated by function result. Output buffer located    ;
;           from [ RDI ] to [ RDI + data buffer maximum length ] exclusive.    ;
;           DWORD [RDI + 00] = Status, 0 if OK (output valid) or error code.   ;
;           DWORD [RDI + 04] = Offset of OS-specific data block #1. 0 if none. ;
;           DWORD [RDI + 08] = Offset of OS-specific data block #2. 0 if none. ;
;           DWORD [RDI + 12] = Platform logical processors count.              ;
;           DWORD [RDI + 16] = Platform affinity mask QWORDs count = N.        ;
;           DWORD [RDI + 20] = Reserved for affinization mode(s) report.       ;
;           N QWORDS from [RDI + 24] = Affinity mask, N x 64-bit values.       ;                       
;                                                                              ;
;------------------------------------------------------------------------------;

GetPlatformInfo:
push rsi rdi rbx rbp
mov rbp,rsp 
and rsp,0FFFFFFFFFFFFFFF0h
;---------- Pre-blank results, pre-set error status. --------------------------;
mov qword [rdi + 00],5
mov qword [rdi + 08],0
mov qword [rdi + 16],1
mov rbx,rdi
;---------- Get PID. ----------------------------------------------------------;
mov eax,SYS_GETPID
syscall
;---------- Get affinity mask. ------------------------------------------------;
mov edi,eax                ; Parm#1 = RDI = PID.
mov esi,8                  ; Parm#2 = RSI = Mask size.
lea rdx,[rbx + 24]         ; Parm#3 = RDX = Pointer for store mask.
mov eax,SYS_GETAFFINITY    ; System call code = RAX.
syscall
;---------- Error check after system call. ------------------------------------;
test eax,eax
js .exit                   ; Go if error. Output < 0 error, byte count if OK ? 
mov dword [rbx + 00],0     ; Status = 0 if no errors.
;---------- Logical CPUs count = F(affinity mask). ----------------------------;
; TODO. Required support >64 logical processors, means mask size >8 bytes.
xor eax,eax
mov ecx,64
mov rdi,[rbx + 24]
.L0:
shr rdi,1
jnc .L1
inc eax
.L1:
loop .L0
mov [rbx + 12],eax
;---------- Done. -------------------------------------------------------------;
.exit:
mov rsp,rbp
pop rbp rbx rdi rsi
ret

