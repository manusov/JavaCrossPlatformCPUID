;---------- Dump 16 SSE registers as hex. -------------------------------------;
;                                                                              ;
; INPUT:   SSE registers values for dump.                                      ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpSSE:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push ebx
xor eax,eax
cpuid
cmp eax,1
jb .featureAbsent
mov eax,1
cpuid
mov eax,06000000h   ; EDX.26=SSE2, EDX.25=SSE.
and edx,eax
cmp edx,eax
jne .featureAbsent
; ...
; Place here code under debug.
; ...
call DumpSSE
pop ebx
ret
.featureAbsent:
lea ecx,[MsgAbsentSSE2]
call HelperError
pop ebx
ret

DumpSSE:         ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push eax ebx ecx edx esi edi ebp
;---------- Store 16 registers. -----------------------------------------------;
sub esp,128
movups [esp + 16*00],xmm0
movups [esp + 16*01],xmm1
movups [esp + 16*02],xmm2
movups [esp + 16*03],xmm3
movups [esp + 16*04],xmm4
movups [esp + 16*05],xmm5
movups [esp + 16*06],xmm6
movups [esp + 16*07],xmm7
;---------- Initializing dump cycle. ------------------------------------------;
cld
xor ebp,ebp
;---------- Dump cycle with pop 16 registers. ---------------------------------;
.cycleVector:
;---------- Register name. ----------------------------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea esi,[NameSSE]
lea edi,[TEMP_BUFFER]
call StringWrite
mov eax,ebp
mov bl,0
call DecimalPrint32   ; This number at register name, XMM0-XMM15 (XMM0-XMM7, ia32).
cmp ebp,9
ja .formatText
mov al,' '
stosb
.formatText:
mov al,0
stosb
lea ecx,[TEMP_BUFFER]
call ConsoleWriteReport
;---------- Register value. ---------------------------------------------------;
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea edi,[TEMP_BUFFER]
mov ecx,edi
mov al,' '
stosb
;---------- XMM[i] data frame start. ------------------------------------------; 
mov eax,[esp+00]
mov edx,[esp+04]
call HexPrint64
mov al,' '
stosb
mov eax,[esp+08]
mov edx,[esp+12] 
call HexPrint64
add esp,16
;---------- XMM[i] data frame start. ------------------------------------------;
mov al,0
stosb
call ConsoleWriteReport
;---------- Cycle. ------------------------------------------------------------;
lea ecx,[CrLf]
call ConsoleWriteReport
inc ebp
cmp ebp,8
jnz .cycleVector   ; Cycle counter for 16 SSE registers.
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn
