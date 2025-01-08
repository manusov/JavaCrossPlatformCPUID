;---------- Dump 8 MMX registers. ---------------------------------------------;
;                                                                              ;
; INPUT:   MMX registers values for dump.                                      ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpMMX:   ; Entry point for code under debug.
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
bt edx,23        ; EDX.23=MMX.
jnc .featureAbsent
; ...
; Place here code under debug.
; ...
call DumpMMX
pop ebx
ret
.featureAbsent:
lea ecx,[MsgAbsentMMX]
call HelperError
pop ebx
ret

DumpMMX:         ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push eax ebx ecx edx esi edi ebp
;---------- Store 8 registers. ------------------------------------------------;
sub esp,64
movq [esp + 8*0],mm0
movq [esp + 8*1],mm1
movq [esp + 8*2],mm2
movq [esp + 8*3],mm3
movq [esp + 8*4],mm4
movq [esp + 8*5],mm5
movq [esp + 8*6],mm6
movq [esp + 8*7],mm7
;---------- Initializing dump cycle. ------------------------------------------;
cld
mov ebp,8
lea esi,[NamesMMX]
;---------- Dump cycle with pop 8 registers. ----------------------------------;
.cycle:
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
mov ecx,esi
call ConsoleWriteReport 
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea edi,[TEMP_BUFFER]
mov ecx,edi
mov al,' '
stosb
pop eax edx       ; POP #[8-1]. 
call HexPrint64
mov al,0
stosb
call ConsoleWriteReport
lea ecx,[CrLf]
call ConsoleWriteReport
add esi,4         ; Select next text string for register name.
dec ebp           ; Cycle counter for 8 MMX registers.
jnz .cycle
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn
