;---------- Dump 32 AVX512 registers as hex. ----------------------------------;
;                                                                              ;
; INPUT:   AVX512 registers values for dump.                                   ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpAVX512hex:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push rbx
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
pop rbx
ret
.cpuFeatureAbsent:
lea rcx,[MsgAbsentAVX512]
call HelperError
pop rbx
ret
.osFeatureAbsent:
lea rcx,[MsgAbsentAVX512os]
call HelperError
pop rbx
ret

DumpAVX512:            ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11
;---------- Store 32 registers. -----------------------------------------------;
sub rsp,2048
vmovupd [rsp + 64*00],zmm0
vmovupd [rsp + 64*01],zmm1
vmovupd [rsp + 64*02],zmm2
vmovupd [rsp + 64*03],zmm3
vmovupd [rsp + 64*04],zmm4
vmovupd [rsp + 64*05],zmm5
vmovupd [rsp + 64*06],zmm6
vmovupd [rsp + 64*07],zmm7
vmovupd [rsp + 64*08],zmm8
vmovupd [rsp + 64*09],zmm9
vmovupd [rsp + 64*10],zmm10
vmovupd [rsp + 64*11],zmm11
vmovupd [rsp + 64*12],zmm12
vmovupd [rsp + 64*13],zmm13
vmovupd [rsp + 64*14],zmm14
vmovupd [rsp + 64*15],zmm15
vmovupd [rsp + 64*16],zmm16
vmovupd [rsp + 64*17],zmm17
vmovupd [rsp + 64*18],zmm18
vmovupd [rsp + 64*19],zmm19
vmovupd [rsp + 64*20],zmm20
vmovupd [rsp + 64*21],zmm21
vmovupd [rsp + 64*22],zmm22
vmovupd [rsp + 64*23],zmm23
vmovupd [rsp + 64*24],zmm24
vmovupd [rsp + 64*25],zmm25
vmovupd [rsp + 64*26],zmm26
vmovupd [rsp + 64*27],zmm27
vmovupd [rsp + 64*28],zmm28
vmovupd [rsp + 64*29],zmm29
vmovupd [rsp + 64*30],zmm30
vmovupd [rsp + 64*31],zmm31
;---------- Initializing dump cycle. ------------------------------------------;
cld
xor ebp,ebp
;---------- Dump cycle with pop 32 registers. ---------------------------------;
.cycleVector:
;---------- Register name. ----------------------------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea rsi,[NameAVX512]
lea rdi,[TEMP_BUFFER]
call StringWrite
mov eax,ebp
mov bl,0
call DecimalPrint32   ; This number at register name, YMM0-YMM15.
cmp ebp,9
ja .formatText
mov al,' '
stosb
.formatText:
mov al,0
stosb
lea rcx,[TEMP_BUFFER]
call ConsoleWriteReport
;---------- Register value. ---------------------------------------------------;
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea rdi,[TEMP_BUFFER]
mov rcx,rdi
mov al,' '
stosb
;---------- ZMM[i] data frame start. ------------------------------------------; 
mov rax,[rsp + 56]
call HexPrint64
mov al,' '
stosb
mov rax,[rsp + 48] 
call HexPrint64
mov al,' '
stosb
mov rax,[rsp + 40] 
call HexPrint64
mov al,' '
stosb
mov rax,[rsp + 32] 
call HexPrint64
lea rsi,[IntervalAVX512]
call StringWrite
mov rax,[rsp + 24]
call HexPrint64
mov al,' '
stosb
mov rax,[rsp + 16] 
call HexPrint64
mov al,' '
stosb
mov rax,[rsp + 08] 
call HexPrint64
mov al,' '
stosb
mov rax,[rsp + 00] 
call HexPrint64
add rsp,64
;---------- ZMM[i] data frame end. --------------------------------------------;
mov al,0
stosb
call ConsoleWriteReport
;---------- Cycle. ------------------------------------------------------------;
lea rcx,[CrLf]
call ConsoleWriteReport
inc ebp
cmp ebp,32
jnz .cycleVector  ; Cycle counter for 16 SSE registers.
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn

;---------- Dump 32 AVX512 registers as double numbers. -----------------------;
;                                                                              ;
; INPUT:   AVX512 registers values for dump.                                   ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpAVX512dec:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push rbx
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
pop rbx
ret
.cpuFeatureAbsent:
lea rcx,[MsgAbsentAVX512]
call HelperError
pop rbx
ret
.osFeatureAbsent:
lea rcx,[MsgAbsentAVX512os]
call HelperError
pop rbx
ret

DumpAVX512asDouble:    ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11
;---------- Store 32 registers. -----------------------------------------------;
sub rsp,2048
vmovupd [rsp + 64*00],zmm0
vmovupd [rsp + 64*01],zmm1
vmovupd [rsp + 64*02],zmm2
vmovupd [rsp + 64*03],zmm3
vmovupd [rsp + 64*04],zmm4
vmovupd [rsp + 64*05],zmm5
vmovupd [rsp + 64*06],zmm6
vmovupd [rsp + 64*07],zmm7
vmovupd [rsp + 64*08],zmm8
vmovupd [rsp + 64*09],zmm9
vmovupd [rsp + 64*10],zmm10
vmovupd [rsp + 64*11],zmm11
vmovupd [rsp + 64*12],zmm12
vmovupd [rsp + 64*13],zmm13
vmovupd [rsp + 64*14],zmm14
vmovupd [rsp + 64*15],zmm15
vmovupd [rsp + 64*16],zmm16
vmovupd [rsp + 64*17],zmm17
vmovupd [rsp + 64*18],zmm18
vmovupd [rsp + 64*19],zmm19
vmovupd [rsp + 64*20],zmm20
vmovupd [rsp + 64*21],zmm21
vmovupd [rsp + 64*22],zmm22
vmovupd [rsp + 64*23],zmm23
vmovupd [rsp + 64*24],zmm24
vmovupd [rsp + 64*25],zmm25
vmovupd [rsp + 64*26],zmm26
vmovupd [rsp + 64*27],zmm27
vmovupd [rsp + 64*28],zmm28
vmovupd [rsp + 64*29],zmm29
vmovupd [rsp + 64*30],zmm30
vmovupd [rsp + 64*31],zmm31
;---------- Initializing dump cycle. ------------------------------------------;
cld
xor ebp,ebp
;---------- Dump cycle with pop 32 registers. ---------------------------------;
.cycleVector:
;---------- Register name. ----------------------------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea rsi,[NameAVX512]
lea rdi,[TEMP_BUFFER]
call StringWrite
mov eax,ebp
mov bl,0
call DecimalPrint32  ; This number at register name, YMM0-YMM15.
cmp ebp,9
ja .formatText
mov al,' '
stosb
.formatText:
mov al,0
stosb
lea rcx,[TEMP_BUFFER]
call ConsoleWriteReport
;---------- Register value. ---------------------------------------------------;
mov ecx,REGISTER_VALUE_COLOR
call SetFgColor
lea rdi,[TEMP_BUFFER]
mov al,' '
stosb
;---------- ZMM[i] data frame start. ------------------------------------------; 
mov rax,[rsp + 56]
call HelperDoubleDump
mov rax,[rsp + 48] 
call HelperDoubleDump
mov rax,[rsp + 40] 
call HelperDoubleDump
mov rax,[rsp + 32] 
call HelperDoubleDump
mov rax,[rsp + 24]
call HelperDoubleDump
mov rax,[rsp + 16] 
call HelperDoubleDump
mov rax,[rsp + 08] 
call HelperDoubleDump
mov rax,[rsp + 00] 
call HelperDoubleDump
add rsp,64
;---------- ZMM[i] data frame end. --------------------------------------------;
mov al,0
stosb
lea rcx,[TEMP_BUFFER]
call ConsoleWriteReport
;---------- Cycle. ------------------------------------------------------------;
lea rcx,[CrLf]
call ConsoleWriteReport
inc ebp
cmp ebp,32
jnz .cycleVector   ; Cycle counter for 16 SSE registers.
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn
