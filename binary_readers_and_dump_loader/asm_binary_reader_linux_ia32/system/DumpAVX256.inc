;---------- Dump 8 AVX256 registers as hex. -----------------------------------;
;                                                                              ;
; INPUT:   AVX256 registers values for dump.                                   ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpAVX256hex:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push ebx
xor eax,eax
cpuid
cmp eax,1
jb .cpuFeatureAbsent
mov eax,1
cpuid
mov eax,18000000h   ; ECX.28 = AVX, EDX.27 = OSXSV.
and ecx,eax
cmp ecx,eax
jne .cpuFeatureAbsent
xor ecx,ecx
xgetbv
and al,00000110b    ; XCR0.2 = YMM(AVX), XCR0.1 = XMM(SSE).
cmp al,00000110b
jne .osFeatureAbsent 
; ...
; Place here code under debug.
; ...
call DumpAVX256
pop ebx
ret
.cpuFeatureAbsent:
lea ecx,[MsgAbsentAVX256]
call HelperError
pop ebx
ret
.osFeatureAbsent:
lea ecx,[MsgAbsentAVX256os]
call HelperError
pop ebx
ret

DumpAVX256:            ; Entry point for dump.
;---------- Push registers include volatile for API.
push eax ebx ecx edx esi edi ebp
;---------- Store 8 registers. ------------------------------------------------;
sub esp,256
vmovupd [esp + 32*00],ymm0
vmovupd [esp + 32*01],ymm1
vmovupd [esp + 32*02],ymm2
vmovupd [esp + 32*03],ymm3
vmovupd [esp + 32*04],ymm4
vmovupd [esp + 32*05],ymm5
vmovupd [esp + 32*06],ymm6
vmovupd [esp + 32*07],ymm7
;---------- Initializing dump cycle. ------------------------------------------;
cld
xor ebp,ebp
;---------- Dump cycle with pop 8 registers. ----------------------------------;
.cycleVector:
;---------- Register name. ----------------------------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea esi,[NameAVX256]
lea edi,[TEMP_BUFFER]
call StringWrite
mov eax,ebp
mov bl,0
call DecimalPrint32   ; This number at register name, YMM0-YMM15 (YMM0-YMM7, ia32).
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
;---------- YMM[i] data frame start. ------------------------------------------; 
mov eax,[esp + 24]
mov edx,[esp + 24 + 4]
call HexPrint64
mov al,' '
stosb
mov eax,[esp + 16]
mov edx,[esp + 16 + 4] 
call HexPrint64
mov al,' '
stosb
mov eax,[esp + 08]
mov edx,[esp + 08 + 4] 
call HexPrint64
mov al,' '
stosb
mov eax,[esp + 00]
mov edx,[esp + 00 + 4] 
call HexPrint64
add esp,32
;---------- YMM[i] data frame end. --------------------------------------------;
mov al,0
stosb
call ConsoleWriteReport
;---------- Cycle. ------------------------------------------------------------;
lea ecx,[CrLf]
call ConsoleWriteReport
inc ebp
cmp ebp,8
jnz .cycleVector    ; Cycle counter for 16 SSE registers.
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn

;---------- Dump 8 AVX256 registers as double numbers. ------------------------;
;                                                                              ;
; INPUT:   AVX256 registers values for dump.                                   ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpAVX256dec:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push ebx
xor eax,eax
cpuid
cmp eax,1
jb .cpuFeatureAbsent
mov eax,1
cpuid
mov eax,18000000h   ; ECX.28 = AVX, EDX.27 = OSXSV.
and ecx,eax
cmp ecx,eax
jne .cpuFeatureAbsent
xor ecx,ecx
xgetbv
and al,00000110b    ; XCR0.2 = YMM(AVX), XCR0.1 = XMM(SSE).
cmp al,00000110b
jne .osFeatureAbsent 
; ...
; Place here code under debug.
; ...
call DumpAVX256asDouble
pop ebx
ret
.cpuFeatureAbsent:
lea ecx,[MsgAbsentAVX256]
call HelperError
pop ebx
ret
.osFeatureAbsent:
lea ecx,[MsgAbsentAVX256os]
call HelperError
pop ebx
ret

DumpAVX256asDouble:    ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push eax ebx ecx edx esi edi ebp
; Store 16 registers. ---------------------------------------------------------;
sub esp,256
vmovupd [esp + 32*00],ymm0
vmovupd [esp + 32*01],ymm1
vmovupd [esp + 32*02],ymm2
vmovupd [esp + 32*03],ymm3
vmovupd [esp + 32*04],ymm4
vmovupd [esp + 32*05],ymm5
vmovupd [esp + 32*06],ymm6
vmovupd [esp + 32*07],ymm7
;---------- Initializing dump cycle. ------------------------------------------;
cld
xor ebp,ebp
;---------- Dump cycle with pop 8 registers. ----------------------------------;
.cycleVector:
;---------- Register name. ----------------------------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea esi,[NameAVX256]
lea edi,[TEMP_BUFFER]
call StringWrite
mov eax,ebp
mov bl,0
call DecimalPrint32   ; This number at register name, YMM0-YMM15 (YMM0-YMM7, ia32).
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
mov al,' '
stosb
;---------- YMM[i] data frame start. ------------------------------------------; 
mov eax,[esp + 24]
mov edx,[esp + 24 + 4]
call HelperDoubleDump
mov eax,[esp + 16]
mov edx,[esp + 16 + 4] 
call HelperDoubleDump
mov eax,[esp + 08]
mov edx,[esp + 08 + 4] 
call HelperDoubleDump
mov eax,[esp + 00]
mov edx,[esp + 00 + 4] 
call HelperDoubleDump
add esp,32
;---------- YMM[i] data frame end. --------------------------------------------;
mov al,0
stosb
lea ecx,[TEMP_BUFFER]
call ConsoleWriteReport
;---------- Cycle. ------------------------------------------------------------;
lea ecx,[CrLf]
call ConsoleWriteReport
inc ebp
cmp ebp,8
jnz .cycleVector   ; Cycle counter for 16 SSE registers.
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn

;--- Helper subroutine for dump. --------------------;
; INPUT:   EDI = Pointer to destination buffer.      ; 
;          EDX:EAX = 64-bit doube number .           ;
; OUTPUT:  EDI updated by text data write.           ; 
;----------------------------------------------------;
HelperDoubleDump:
push edi eax
mov ecx,8
mov al,' '
rep stosb
pop eax edi
push edi
add edi,2
mov bx,0200h
push eax
test edx,edx
js .sign
mov al,'+'
stosb
.sign:
pop eax
call DoublePrint
pop edi
add edi,7
mov al,' '
cmp byte [edi],' '
je .exit
mov al,'\'
.exit:
stosb
ret

