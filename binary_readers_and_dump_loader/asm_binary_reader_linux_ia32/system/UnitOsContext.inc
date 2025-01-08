;---------- Unit test handler: dump OS context management bitmaps. ------------;
; Dump CPU-supported and OS-enabled context save-restore features.             ;
;                                                                              ;
; INPUT:  None.                                                                ;
; OUTPUT: None.                                                                ;
;------------------------------------------------------------------------------;
SHELL_UnitOsContext:
push ebx esi edi ebp
lea edi,[TEMP_BUFFER + 8192]
;---------- Get context features bitmaps to OPB at RDI. -----------------------; 
mov ebx,edi
call GetContextBitmaps
;---------- Write string: CPU-supported context features bitmap. --------------;
lea ebp,[MsgContextCpu]
xor esi,esi
call HelperContextBitmap
lea ecx,[CrLf]
call ConsoleWriteReport 
;---------- Write string: OS-enabled context features bitmap. -----------------;
lea ebp,[MsgContextOs]
mov esi,8
call HelperContextBitmap
lea ecx,[CrLf2]
call ConsoleWriteReport 
;---------- Done. -------------------------------------------------------------;
pop ebp edi esi ebx
ret

;--- Helper for console output context features bitmaps as hex. ---------------;
;                                                                              ;
; INPUT:   EBP = Pointer to parameter name text string.                        ;
;          ESI = Offset of parameter in the OPB.                               ;
;          EBX = Pointer to OPB. OPB data is input data for this subroutine.   ;       
;                                                                              ;
; OUTPUT:  None.                                                               ; 
;------------------------------------------------------------------------------;
HelperContextBitmap:
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
mov ecx,ebp
call ConsoleWriteReport 
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea edi,[TEMP_BUFFER]
mov ecx,edi
mov eax,[ebx + esi + 0]
mov edx,[ebx + esi + 4]
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
;          Accept ESI = optional input data array (or no input data).          ;
;                       IPB = Input Parameters Block.                          ;
;                 EDI = optional output data array (or no output data).        ;
;                       OPB = Output Parameters Block.                         ;
;                 EAX = optional output result or status info.                 ;
;                 CF flag = status.                                            ;
;                                                                              ;
;==============================================================================;


;--- Get CPU context management state for context save-restore subsystem. -----;
;                                                                              ;
; INPUT:   ESI = Pointer to IPB (Input Parameters Block). Not used here.       ;
;          EDI = Pointer to OPB (Output Parameters Block).                     ;
;                                                                              ;
; OUTPUT:  QWORD OPB[00] = [EDI + 00] = CPU-supported context features bitmap. ;
;          QWORD OPB[08] = [EDI + 08] = OS-enabled context features bitmap.    ;
;------------------------------------------------------------------------------;
GetContextBitmaps:
push eax ebx ecx edx
xor eax,eax
mov [edi + 00],eax     ; Pre-clear output data.
mov [edi + 04],eax
mov [edi + 08],eax
mov [edi + 12],eax
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
mov [edi + 00],eax     ; QWORD OPB[00] = CPU validation mask.
mov [edi + 04],edx
xor ecx,ecx
xgetbv
mov [edi + 08],eax     ; QWORD OPB[08] = OS validation mask. 
mov [edi + 12],edx
@@:
pop edx ecx ebx eax
ret
