;---------- Dump 16 AVX256 registers as hex. ----------------------------------;
;                                                                              ;
; INPUT:   AVX256 registers values for dump.                                   ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpAVX256hex:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push rbx
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
pop rbx
ret
.cpuFeatureAbsent:
lea rcx,[MsgAbsentAVX256]
call HelperError
pop rbx
ret
.osFeatureAbsent:
lea rcx,[MsgAbsentAVX256os]
call HelperError
pop rbx
ret

DumpAVX256:            ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11
;---------- Store 16 registers. -----------------------------------------------;
sub rsp,512
vmovupd [rsp + 32*00],ymm0
vmovupd [rsp + 32*01],ymm1
vmovupd [rsp + 32*02],ymm2
vmovupd [rsp + 32*03],ymm3
vmovupd [rsp + 32*04],ymm4
vmovupd [rsp + 32*05],ymm5
vmovupd [rsp + 32*06],ymm6
vmovupd [rsp + 32*07],ymm7
vmovupd [rsp + 32*08],ymm8
vmovupd [rsp + 32*09],ymm9
vmovupd [rsp + 32*10],ymm10
vmovupd [rsp + 32*11],ymm11
vmovupd [rsp + 32*12],ymm12
vmovupd [rsp + 32*13],ymm13
vmovupd [rsp + 32*14],ymm14
vmovupd [rsp + 32*15],ymm15
;---------- Initializing dump cycle. ------------------------------------------;
cld
xor ebp,ebp
;---------- Dump cycle with pop 16 registers. ---------------------------------;
.cycleVector:
;---------- Register name. ----------------------------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea rsi,[NameAVX256]
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
;---------- YMM[i] data frame start. ------------------------------------------; 
mov rax,[rsp + 24]
call HexPrint64  ; First 64-bit scalar as hex.
mov al,' '
stosb
mov rax,[rsp + 16] 
call HexPrint64  ; Second 64-bit scalar as hex.
mov al,' '
stosb
mov rax,[rsp + 08] 
call HexPrint64  ; Third 64-bit scalar as hex.
mov al,' '
stosb
mov rax,[rsp + 00] 
call HexPrint64  ; Forth 64-bit scalar as hex.
add rsp,32
;---------- YMM[i] data frame end. --------------------------------------------;
mov al,0
stosb
call ConsoleWriteReport
;---------- Cycle. ------------------------------------------------------------;
lea rcx,[CrLf]
call ConsoleWriteReport
inc ebp
cmp ebp,16
jnz .cycleVector   ; Cycle counter for 16 SSE registers.
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn

;---------- Dump 16 AVX256 registers as double numbers. -----------------------;
;                                                                              ;
; INPUT:   AVX256 registers values for dump.                                   ;
; OUTPUT:  None.                                                               ;
;------------------------------------------------------------------------------;
SHELL_DumpAVX256dec:   ; Entry point for code under debug.
; ...
; Place here code under debug, include save-restore registers and feature check.
; ...
push rbx
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
pop rbx
ret
.cpuFeatureAbsent:
lea rcx,[MsgAbsentAVX256]
call HelperError
pop rbx
ret
.osFeatureAbsent:
lea rcx,[MsgAbsentAVX256os]
call HelperError
pop rbx
ret

DumpAVX256asDouble:    ; Entry point for dump.
;---------- Push registers include volatile for API. --------------------------;
push rax rbx rcx rdx rsi rdi rbp r8 r9 r10 r11
;---------- Store 16 registers. -----------------------------------------------;
sub rsp,512
vmovupd [rsp + 32*00],ymm0
vmovupd [rsp + 32*01],ymm1
vmovupd [rsp + 32*02],ymm2
vmovupd [rsp + 32*03],ymm3
vmovupd [rsp + 32*04],ymm4
vmovupd [rsp + 32*05],ymm5
vmovupd [rsp + 32*06],ymm6
vmovupd [rsp + 32*07],ymm7
vmovupd [rsp + 32*08],ymm8
vmovupd [rsp + 32*09],ymm9
vmovupd [rsp + 32*10],ymm10
vmovupd [rsp + 32*11],ymm11
vmovupd [rsp + 32*12],ymm12
vmovupd [rsp + 32*13],ymm13
vmovupd [rsp + 32*14],ymm14
vmovupd [rsp + 32*15],ymm15
;---------- Initializing dump cycle. ------------------------------------------;
cld
xor ebp,ebp
;---------- Dump cycle with pop 16 registers. ---------------------------------;
.cycleVector:
;---------- Register name. ----------------------------------------------------;
mov ecx,REGISTER_NAME_COLOR
call SetFgColor
lea rsi,[NameAVX256]
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
mov al,' '
stosb
;---------- YMM[i] data frame start. ------------------------------------------; 
mov rax,[rsp + 24]
call HelperDoubleDump
mov rax,[rsp + 16] 
call HelperDoubleDump
mov rax,[rsp + 08] 
call HelperDoubleDump
mov rax,[rsp + 00] 
call HelperDoubleDump
add rsp,32
;---------- YMM[i] data frame end. --------------------------------------------;
mov al,0
stosb
lea rcx,[TEMP_BUFFER]
call ConsoleWriteReport
; Cycle
lea rcx,[CrLf]
call ConsoleWriteReport
inc ebp
cmp ebp,16
jnz .cycleVector   ; Cycle counter for 16 SSE registers.
;--- Go to restore original color, insert empty string, restore registers. ----;
jmp DumpReturn

;--- Helper subroutine for dump. ----------------;
; INPUT:   RDI = Pointer to destination buffer.  ; 
;          RAX = 64-bit doube number.            ;
; OUTPUT:  RDI updated by text data write.       ; 
;------------------------------------------------;
HelperDoubleDump:
push rdi rax
mov ecx,8
mov al,' '
rep stosb
pop rax rdi
push rdi
add rdi,2
mov bx,0200h
push rax
test rax,rax
js .sign
mov al,'+'
stosb
.sign:
pop rax
call DoublePrint
pop rdi
add rdi,7
mov al,' '
cmp byte [rdi],' '
je .exit
mov al,'\'
.exit:
stosb
ret
