;---------- Dump 8 AVX512 registers as hex. -----------------------------------;
;                                                                              ;
; INPUT:   AVX512 registers values for dump.                                   ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpAVX512hex:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push ebx
xor eax,eax
cpuid
cmp eax,7
jb .cpuFeatureAbsent
mov eax,1
cpuid
mov eax,18000000h   ; ECX.28 = AVX, EDX.27 = OSXSV.
and ecx,eax
cmp ecx,eax
jne .cpuFeatureAbsent
mov eax,7
xor ecx,ecx
cpuid
bt ebx,16           ; EBX.16 = AVX512F.
jnc .cpuFeatureAbsent
xor ecx,ecx
xgetbv
and al,11100110b    ; XCR0.7=ZMM[16-31], XCR0.6=ZMM[0-15], XCR0.5=K[0-7].
cmp al,11100110b    ; XCR0.2=YMM(AVX), XCR0.1=XMM(SSE).
jne .osFeatureAbsent 
; ...
; Place here code under debug.
; ...
call DumpAVX512
pop ebx
ret
.cpuFeatureAbsent:
lea ecx,[MsgAbsentAVX512]
call HelperError
pop ebx
ret
.osFeatureAbsent:
lea ecx,[MsgAbsentAVX512os]
call HelperError
pop ebx
ret

DumpAVX512:            ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push eax ebx ecx edx esi edi ebp
;---------- Store 8 registers. ------------------------------------------------;
sub esp,512
vmovupd [esp + 64*00],zmm0
vmovupd [esp + 64*01],zmm1
vmovupd [esp + 64*02],zmm2
vmovupd [esp + 64*03],zmm3
vmovupd [esp + 64*04],zmm4
vmovupd [esp + 64*05],zmm5
vmovupd [esp + 64*06],zmm6
vmovupd [esp + 64*07],zmm7
;---------- Initializing dump cycle. ------------------------------------------;
cld
xor ebp,ebp
;---------- Dump cycle with pop 8 registers. ----------------------------------;
.cycleVector:
;---------- Register name. ----------------------------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea esi,[NameAVX512]
lea edi,[TEMP_BUFFER]
call StringWrite
mov eax,ebp
mov bl,0
call DecimalPrint32   ; This number at register name, ZMM0-ZMM31 (ZMM0-ZMM7, ia32).
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
;---------- ZMM[i] data frame start. ------------------------------------------; 
mov eax,[esp + 56]
mov edx,[esp + 56 + 4]
call HexPrint64
mov al,' '
stosb
mov eax,[esp + 48]
mov edx,[esp + 48 + 4]
call HexPrint64
mov al,' '
stosb
mov eax,[esp + 40]
mov edx,[esp + 40 + 4]
call HexPrint64
mov al,' '
stosb
mov eax,[esp + 32]
mov edx,[esp + 32 + 4]
call HexPrint64
lea esi,[IntervalAVX512]
call StringWrite
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
add esp,64
;---------- ZMM[i] data frame end. --------------------------------------------;
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

;---------- Dump 8 AVX512 registers as double numbers. ------------------------;
;                                                                              ;
; INPUT:   AVX512 registers values for dump.                                   ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpAVX512dec:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push ebx
xor eax,eax
cpuid
cmp eax,7
jb .cpuFeatureAbsent
mov eax,1
cpuid
mov eax,18000000h   ; ECX.28=AVX, EDX.27=OSXSV.
and ecx,eax
cmp ecx,eax
jne .cpuFeatureAbsent
mov eax,7
xor ecx,ecx
cpuid
bt ebx,16           ; EBX.16 = AVX512F.
jnc .cpuFeatureAbsent
xor ecx,ecx
xgetbv
and al,11100110b    ; XCR0.7=ZMM[16-31], XCR0.6=ZMM[0-15], XCR0.5=K[0-7].
cmp al,11100110b    ; XCR0.2=YMM(AVX), XCR0.1=XMM(SSE).
jne .osFeatureAbsent 
; ...
; Place here code under debug.
; ...
call DumpAVX512asDouble
pop ebx
ret
.cpuFeatureAbsent:
lea ecx,[MsgAbsentAVX512]
call HelperError
pop ebx
ret
.osFeatureAbsent:
lea ecx,[MsgAbsentAVX512os]
call HelperError
pop ebx
ret

DumpAVX512asDouble:    ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push eax ebx ecx edx esi edi ebp
;---------- Store 8 registers. ------------------------------------------------;
sub esp,512
vmovupd [esp + 64*00],zmm0
vmovupd [esp + 64*01],zmm1
vmovupd [esp + 64*02],zmm2
vmovupd [esp + 64*03],zmm3
vmovupd [esp + 64*04],zmm4
vmovupd [esp + 64*05],zmm5
vmovupd [esp + 64*06],zmm6
vmovupd [esp + 64*07],zmm7
;---------- Initializing dump cycle. ------------------------------------------;
cld
xor ebp,ebp
;---------- Dump cycle with pop 8 registers. ----------------------------------;
.cycleVector:
;---------- Register name. ----------------------------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea esi,[NameAVX512]
lea edi,[TEMP_BUFFER]
call StringWrite
mov eax,ebp
mov bl,0
call DecimalPrint32   ; This number at register name, ZMM0-ZMM31 (ZMM0-ZMM7, ia32).
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
;---------- ZMM[i] data frame start. ------------------------------------------; 
mov eax,[esp + 56]
mov edx,[esp + 56 + 4]
call HelperDoubleDump
mov eax,[esp + 48]
mov edx,[esp + 48 + 4]
call HelperDoubleDump
mov eax,[esp + 40]
mov edx,[esp + 40 + 4]
call HelperDoubleDump
mov eax,[esp + 32]
mov edx,[esp + 32 + 4]
call HelperDoubleDump
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
add esp,64
;---------- ZMM[i] data frame end. --------------------------------------------;
mov al,0
stosb
lea ecx,[TEMP_BUFFER]
call ConsoleWriteReport
;---------- Cycle. ------------------------------------------------------------;
lea ecx,[CrLf]
call ConsoleWriteReport
inc ebp
cmp ebp,8
jnz .cycleVector      ; Cycle counter for 16 SSE registers.
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn
