;---------- Unit test handler: dump OS context management bitmaps. ------------;
; Dump CPU-supported and OS-enabled context save-restore features.             ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitOsContext:
push rbx rsi rdi rbp
lea rdi,[TEMP_BUFFER + 8192]
;---------- Get context features bitmaps to OPB at RDI. -----------------------; 
mov rbx,rdi
call GetContextBitmaps
;---------- Write string: CPU-supported context features bitmap. --------------;
lea rbp,[MsgContextCpu]
xor esi,esi
call HelperContextBitmap
lea rcx,[CrLf]
call ConsoleWriteReport 
;---------- Write string: OS-enabled context features bitmap. -----------------;
lea rbp,[MsgContextOs]
mov esi,8
call HelperContextBitmap
lea rcx,[CrLf2]
call ConsoleWriteReport 
;---------- Done. -------------------------------------------------------------;
pop rbp rdi rsi rbx
ret

;--- Helper for console output context features bitmaps as hex. ---------------;
;                                                                              ;
; INPUT:   RBP = Pointer to parameter name text string.                        ;
;          RSI = Offset of parameter in the OPB.                               ;
;          RBX = Pointer to OPB. OPB data is input data for this subroutine.   ;       
;                                                                              ;
; OUTPUT:  None.                                                               ; 
;------------------------------------------------------------------------------;
HelperContextBitmap:
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
mov rcx,rbp
call ConsoleWriteReport 
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea rdi,[TEMP_BUFFER]
mov rcx,rdi
mov rax,[rbx + rsi]
call HexPrint64
mov ax,'h.'
stosw
mov al,0
stosb
jmp ConsoleWriteReport


;==============================================================================;
;                                                                              ;
;              Subroutines portable to JNI (Java Native Interface)             ;
;                         library of CPUID utility.                            ;
;          Accept RSI = optional input data array (or no input data).          ;
;                       IPB = Input Parameters Block.                          ;
;                 RDI = optional output data array (or no output data).        ;
;                       OPB = Output Parameters Block.                         ;
;                 RAX = optional output result or status info.                 ;
;                 CF flag = status.                                            ;
;                                                                              ;
;==============================================================================;


;--- Get CPU context management state for context save-restore subsystem. -----;
;                                                                              ;
; INPUT:   RSI = Pointer to IPB (Input Parameters Block). Not used here.       ;
;          RDI = Pointer to OPB (Output Parameters Block).                     ;
;                                                                              ;
; OUTPUT:  QWORD OPB[00] = [RDI + 00] = CPU-supported context features bitmap. ;
;          QWORD OPB[08] = [RDI + 08] = OS-enabled context features bitmap.    ;
;------------------------------------------------------------------------------;
GetContextBitmaps:
push rax rbx rcx rdx
xor eax,eax
mov [rdi + 00],rax     ; Pre-clear output data.
mov [rdi + 08],rax
call CheckCpuid
jc @f                  ; Skip if CPUID not supported or locked by VMM.
cmp eax,0Dh
jb @f                  ; Skip if CPUID context declaration not supported.
mov eax,1
cpuid
bt ecx,27
jnc @f                 ; Skip if CPU context management not supported.
mov eax,0Dh
xor ecx,ecx
cpuid
mov [rdi + 00],eax     ; QWORD OPB[00] = CPU validation mask.
mov [rdi + 04],edx
xor ecx,ecx
xgetbv
mov [rdi + 08],eax     ; QWORD OPB[08] = OS validation mask. 
mov [rdi + 12],edx
@@:
pop rdx rcx rbx rax
ret
