;---------- Parse scenario file and update options variables. --------------;
; Input / Output parameters and Volatile / Non volatile registers           ;
; compatible with Microsoft x64 calling convention.                         ;
;                                                                           ;
; INPUT:   RCX = Pointer to buffer with loaded scenario file.               ;  
;          RDX = Limit for this buffer, address of first not-valid byte.    ;          
;          R8  = Pointer to options descriptors list.                       ;
;          R9  = Pointer to error status variables, for error reporting:    ;
;                3 QWORDS, 2 pointers to strings + 1 OS API error code.     ;         
;                                                                           ;
; OUTPUT:  RAX = Status, 0 = error, error status variables valid,           ;
;                        1 = no errors, error status variables not used.    ;
;          Update options values variables, addressed by descriptors at R8  ;
;          Update status variables, addressed by R9, if error.              ;
;                                                                           ;         
;---------------------------------------------------------------------------;
ParseScenario:
cld
push rbx rsi rdi rbp r12
mov rbp,rsp
and rsp,0FFFFFFFFFFFFFFF0h  ; Align stack for call WinAPI by convention.
;---------- This cycle for input scenario file strings. -----------------------;
mov r12,r9        ; R12 = Pointer to error status variables, 3 qwords. 
mov r11,r8        ; R11 = Pointer to options descriptors list. 
mov r8,rcx        ; R8  = Pointer to buffer with scenario file.
mov r9,rdx        ; R9  = Buffer limit, addr. of first not valid byte.
;---------- Pre-clear status. -------------------------------------------------;
xor eax,eax
mov [r12 + 00],rax
mov [r12 + 08],rax
mov [r12 + 16],rax
;---------- This cycle for strings in the scenario. ---------------------------;
StringsCycle:
mov [r12 + 08],r8  ; R8 = Pointer to parsed error cause string.
mov r10,r11        ; R10 = Reload pointer to options descriptors list. 
;---------- This cycle for options descriptors. -------------------------------;
.optionsCycle:
mov al,[r10 + X0]  ; AL = Option type from option descriptor.
cmp al,XEND
je .parseError1    ; Go error if option not found at list, unknown option.
cmp al,XLAST
ja .parseError1    ; Go error if option not found at list, unknown option.
mov rsi,r8         ; RSI = Pointer to text file buffer. 
mov edi,[r10 + X3] ; RDI = Pointer to option keyword.
add rdi,OpDesc
;---------- This cycle for option name word compare. --------------------------;
.detectName:
cmp rsi,r9         ; RSI = Pointer to scenario data, R9 = Pointer limit.
jae .parseExitOK   ; Go if scenario done.  
mov ah,[rdi]       ; AH = Current char from option descriptor, keyword. 
inc rdi
test ah,ah              ; AH = 0 means keyword done, means keyword match. 
jz .detectedThisOption  ; Go if keyword match detected. 
lodsb                   ; AL = Current char from scenario file.
cmp al,0Ah
je .detectNextString ; Go if LF(Line Feed), keyword done, try next.
cmp al,0Dh
je .detectNextString ; Go if CR(Carriage Return), keyword done, try next.
cmp al,';'
je .detectTailString ; Go if comments, keyword done, try next.
cmp al,' '
je .detectNextOption ; Go if SPACE, keyword done, try next.
cmp al,09h
je .detectNextOption ; Go if TAB, keyword done, try next. 
cmp al,'='
je .detectNextOption ; Go if EQUAL, keyword done, try next. 
cmp al,'0'
jb .parseError2      ; Go error if unexpected char.
cmp al,'z'
ja .parseError2      ; Go error if unexpected char.
cmp al,'A'
jb @f                ; Skip if not a text char.
cmp al,'z'
ja @f                ; Skip if not a text char. 
and ax,0DFDFh        ; If text char, convert both compare chars to UPPER CASE. 
@@:
cmp al,ah            ; Compare chars from keyword pattern and scenario.
je .detectName       ; Continue compare if this chars match. 
;--- Option not detected, select next element of options descriptors list. ----;
.detectNextOption:
mov al,[r10 + X0]       ; AL = Option type from option descriptor.
add r10,XSMALL
cmp al,XKEY
jne .optionsCycle
add r10,XDELTA
jmp .optionsCycle
;---------- Option detected, select and run option handler. -------------------;
.detectedThisOption:
mov al,[r10 + X0]       ; AL = Option type from option descriptor. 
cmp al,XKEY
je .handlerOptionKeys
cmp al,XDEC
je .handlerOptionDecimal32
cmp al,XHEX
je .handlerOptionHex64
cmp al,XSIZE
je .handlerOptionSize64
;---------- Handler for option type 5 = string. -------------------------------;
call OptionString 
jmp .parseDone
;---------- Handler for option type 1 = one of keywords. ----------------------;
.handlerOptionKeys:
call OptionKeys
jmp .parseDone
;---------- Handler for option type 2 = 32-bit value as decimal. --------------;
.handlerOptionDecimal32:
call OptionDecimal32
jmp .parseDone
;---------- Handler for option type 3 = 64-bit value as hex. ------------------;
.handlerOptionHex64: 
call OptionHex64
jmp .parseDone
;---------- Handler for option type 4 = 64-bit value as size. -----------------;
.handlerOptionSize64:
call OptionSize64 
.parseDone:
jc .parseError2       ; Go if error detected.
;---------- Detect tail, non-informative part of string. ----------------------;
.detectTailString:
cmp rsi,r9
jae .parseExitOK      ; Go if scenario done.
lodsb                 ; Read current char, scan for end of this string.
cmp al,0Ah
je .detectNextString
cmp al,0Dh
jne .detectTailString   
;---------- Step to next string of scenario file. -----------------------------; 
.detectNextString:
cmp rsi,r9
jae .parseExitOK      ; Go if scenario done.
lodsb                 ; Read current char, scan for start next string.
cmp al,0Ah
je .detectNextString 
cmp al,0Dh
je .detectNextString 
lea r8,[rsi-1]        ; R8 = Address of first char of next string.
jmp StringsCycle
;---------- Error branches. ---------------------------------------------------;
.parseError1:         ; This handler for unknown option keyword.
lea rax,[MsgUnknownOption]
jmp .entryError
.parseError2:         ; This handler for errors in option string.
lea rax,[MsgOption]
.entryError:
mov [r12 + 00],rax    ; RAX = Pointer to error comments string.
mov r8,[r12 + 08]     ; R8 = Pointer to parsed error cause string.
;--- Terminate error caused string for prevent show all scenario file. --------;
mov rsi,r8            ; Start scanning end of error cause string.
lea rdi,[r9 - 1]
.scancrlf:
cmp rsi,rdi           ; RDI = Loaded scenario file limit in the buffer.
jae .limitcrlf        ; Go if scenario file done. 
lodsb
cmp al,0Ah
je .foundlf
cmp al,0Dh
.foundlf:
jne .scancrlf
.limitcrlf:
mov byte [rsi],0      ; Mark end of string for output error cause string.
;---------- Exit points. ------------------------------------------------------;
xor eax,eax   ; Status = error.
jmp .parseExit 
.parseExitOK:
mov eax,1     ; Status = no errors.
.parseExit:
mov rsp,rbp   ; This for restore after alignment and also instead ADD RSP,32.
pop r12 rbp rdi rsi rbx
ret
;---------- Local subroutine: OPTION_KEYS handler. ----------------------------;
;                                                                              ;
; INPUT:    RSI = Pointer to scenario file current parse fragment.             ;
;           R9  = Limit for RSI, address of first not valid byte.              ;
;           R10 = Pointer to this detected option descriptor.                  ;
;                                                                              ;
; OUTPUT:   R8  = Updated pointer to current scenario.                         ;        
;           CF flag = status, CF=0(NC)=skipped OK, CF=1(C)=spec. case, see ZF. ;
;           ZF flag = special case type, valid if CF = 1.                      ;
;           ZF=1(Z)=parse error, ZF=0(NZ)=end of scenario file reached.        ;
;------------------------------------------------------------------------------;
OptionKeys:
call SkipEqual      ; Skip " = " fragment.
jc ParseSpecialCase ; Go if scenario done or parsing error.
mov edi,[r10 + X4]  ; RDI = Patterns pointer , RSI = Scenario pointer.
add rdi,OpDesc
xor ecx,ecx         ; ECX = Possible keywords pointer.
mov r8,rsi          ; R8 = Pointer to keyword in the file.
.nextKeywordCycle:  ; This cycle for select next possible keyword. 
mov rsi,r8          ; RSI = Restore pointer to keyword in the file.
.keywordCycle:      ; This cycle for compare option current keyword.
cmp rsi,r9          ; R9 = Loaded scenario file limit in the buffer.
jae EndOfScenario   ; Go exit if scenario file done. 
lodsb               ; AL = current char from scenario file, Pointer + 1.
mov ah,[rdi]        ; AH = current char from comparision pattern. 
inc rdi             ; Pattern pointer + 1.
test ah,ah
jz .keywordMatch    ; Go if possible keyword done, keyword match.
cmp al,'0'
jb .error           ; Go if wrong char in the keyword, next line.
cmp al,'z'
ja .error           ; Go if wrong char in the keyword, next line.
cmp al,'A'
jb .skipConvert     ; Go if convert to upper case not required.
cmp al,'z'
ja .skipConvert     ; Go if convert to upper case not required.
and ax,0DFDFh       ; Convert both compared chars to upper case.
.skipConvert:
cmp al,ah
jne .scanZero       ; Go to next possible keyword comparision if mismatch.
test ah,ah
jz .keywordMatch    ; Go if keyword match, zero reached at pattern.
jmp .keywordCycle
.error:             ; Go this if wrong char detected after "=".
jmp ParseError
.scanZero:          ; Go this if next possible keyword compare.
mov al,[rdi]
inc rdi
cmp al,0
jne .scanZero
cmp byte [rdi],0
je .error             ; Go error if list done but keyword not detected.
inc ecx               ; ECX = counter for option value.
jmp .nextKeywordCycle ; Otherwise, go compare with next possible keyword.
.keywordMatch:        ; Go this if keyword match.
mov edx,[r10 + X2]    ; RDX = Pointer to option value.
add rdx,OpDesc
mov [rdx],cl         ; Write option value, one byte selector.
; Global exit points with global-visible labels.
ParseOK:             ; Next, return and skip remaining string part.
clc
ParseSpecialCase:  ; Return with CF, ZF valid.
ret                ; Return with CF=0 means normal status. 
EndOfScenario:
or al,1
stc
ret          ; Return with CF=1, ZF=0 means end of scenario file. 
ParseError:
xor al,al
stc
ret          ; Return with CF=1, ZF=1 means parse error: unexpected char.
;---------- Local subroutine: OPTION_DECIMAL_32 handler. ----------------------;
;                                                                              ;
; INPUT:    RSI = Pointer to scenario file current parse fragment.             ;
;           R9  = Limit for RSI, address of first not valid byte.              ;
;           R10 = Pointer to this detected option descriptor.                  ;
;                                                                              ;
; OUTPUT:   R8  = Updated pointer to current scenario.                         ;        
;           CF flag = status, CF=0(NC)=skipped OK, CF=1(C)=spec. case, see ZF, ;
;           ZF flag = special case type, valid if CF = 1.                      ;
;           ZF=1(Z)=parse error, ZF=0(NZ)=end of scenario file reached.        ;
;------------------------------------------------------------------------------;
OptionDecimal32:
call SkipEqual      ; Skip " = " fragment.
jc ParseSpecialCase ; Go if scenario done or parsing error.
xor ecx,ecx         ; ECX = Numeric value for extract.
.cycleDecimal:      ; Cycle for interpreting decimal numeric string.
cmp rsi,r9          ; R9 = Loaded scenario file limit in the buffer.
jae EndOfScenario   ; Go exit if scenario file done. 
lodsb
cmp al,'0'
jb .stopDecimal     ; Go if not a decimal digit char '0'...'9'.
cmp al,'9'
ja .stopDecimal     ; Go if not a decimal digit char '0'...'9'.
and eax,0Fh         ; Mask for convert '0'...'9' to 0...9.
imul rcx,rcx,10     ; Update value, use 64-bit RCX because unsigned required.
add ecx,eax         ; Add current value.
jmp .cycleDecimal   ; Continue cycle for interpreting decimal numeric string.
.stopDecimal:       ; This point for first non-decimal char detected.
call CheckLineChar  ; Detect 0Ah(LF), 0Dh(CR), 3Bh(';'), 20h(' '), 09h(TAB).
jne ParseError      ; Go error if wrong char after digit.
.normalTerm:        ; Otherwise normal termination. 
mov edx,[r10 + X2]  ; RDX = Pointer to option value.
add rdx,OpDesc
mov [rdx],ecx       ; Write option value, dword, extracted as decimal. 
clc                 ; Next, return and skip remaining string part.
ret
;---------- Local subroutine: OPTION_HEX_64 handler. --------------------------;
;                                                                              ;
; INPUT:    RSI = Pointer to scenario file current parse fragment.             ;
;           R9  = Limit for RSI, address of first not valid byte.              ;
;           R10 = Pointer to this detected option descriptor.                  ;
;                                                                              ;
; OUTPUT:   R8  = Updated pointer to current scenario.                         ;        
;           CF flag = status, CF=0(NC)=skipped OK, CF=1(C)=spec. case, see ZF, ;
;           ZF flag = special case type, valid if CF = 1.                      ;
;           ZF=1(Z)=parse error, ZF=0(NZ)=end of scenario file reached.        ;
;------------------------------------------------------------------------------;
OptionHex64:
call SkipEqual      ; Skip " = " fragment.
jc ParseSpecialCase ; Go if scenario done or parsing error.
xor ecx,ecx         ; RCX = Numeric value for extract.
.cycleHex:          ; Cycle for interpreting hex numeric string.
cmp rsi,r9          ; R9 = Loaded scenario file limit in the buffer.
jae EndOfScenario   ; Go exit if scenario file done. 
lodsb               ; Read char from scenario.
cmp al,'0'
jb .nodecimal       ; Go if not a decimal digit char '0'...'9'.
cmp al,'9'
jna .decimal        ; Go if decimal digit char '0'...'9'.
.nodecimal:
mov ah,al
and ah,0DFh         ; Make uppercase for convert 'a'...'f' to 'A'...'F'.
cmp ah,'A'
jb .stopHex         ; Go if not a hex digit char 'A'...'F'. 
cmp ah,'F'
ja .stopHex         ; Go if not a hex digit char 'A'...'F'.
mov al,ah
sub al,'A'-10       ; Convert 'A'...'F' to 10...15.
.decimal:
and eax,0Fh         ; Convert to 00h...0Fh values.
shl rcx,4           ; Shift previous extracted value.
or ecx,eax          ; Add current char.
jmp .cycleHex       ; Continue cycle for interpreting hex numeric string.
.stopHex:           ; This point for first non-hexadecimal char detected.
call CheckLineChar  ; Detect 0Ah(LF), 0Dh(CR), 3Bh(';'), 20h(' '), 09h(TAB).
jne ParseError      ; Go error if wrong char after digit.
.normalTerm:        ; Otherwise normal termination, store extracted value. 
mov edx,[r10 + X2]  ; RDX = Pointer to option value.
add rdx,OpDesc
mov [rdx],rcx       ; Write option value, qword, extracted as decimal. 
clc                 ; Next, return and skip remaining string part.
ret
;---------- Local subroutine: OPTION_SIZE_64 handler. -------------------------;
;                                                                              ;
; INPUT:    RSI = Pointer to scenario file current parse fragment.             ;
;           R9  = Limit for RSI, address of first not valid byte.              ;
;           R10 = Pointer to this detected option descriptor.                  ;
;                                                                              ;
; OUTPUT:   R8  = Updated pointer to current scenario.                         ;        
;           CF flag = status, CF=0(NC)=skipped OK, CF=1(C)=spec. case, see ZF, ;
;           ZF flag = special case type, valid if CF = 1.                      ;
;           ZF=1(Z)=parse error, ZF=0(NZ)=end of scenario file reached.        ;
;------------------------------------------------------------------------------;
OptionSize64:
call SkipEqual      ; Skip " = " fragment.
jc ParseSpecialCase ; Go if scenario done or parsing error.
xor ecx,ecx         ; RCX = Numeric value for extract.
.cycleNumStr:       ; Cycle for interpreting numeric string.
cmp rsi,r9          ; R9 = Loaded scenario file limit in the buffer.
jae EndOfScenario   ; Go exit if scenario file done. 
lodsb               ; Read char from scenario.
cmp al,'0'
jb .notadigit     ; Go if not a digit.
cmp al,'9'
ja .notadigit     ; Go if not a digit.
and eax,0Fh       ; Mask digit, '0'...'9' converted to 0...9.
imul rcx,rcx,10   ; Update value, use 64-bit RCX because unsigned required.
add ecx,eax       ; Add current digit to extracted value.
jmp .cycleNumStr
.notadigit:       ; First non-numeric char detected, also cycle for this part.
cmp al,0Ah
je .normalTerm    ; Go string end if 0Ah (LF).
cmp al,0Dh
je .normalTerm    ; Go string end if 0Dh (CR).
cmp al,';'
je .normalTerm    ; Go string end if comments.
cmp al,09h
je .nextChar
cmp al,' '
je .nextChar
cmp al,'M'        ; Detect M = megabytes.
je .megabytes
cmp al,'m'
je .megabytes
cmp al,'G'        ; Detect G = gigabytes.
je .gigabytes
cmp al,'g'
je .gigabytes
cmp al,'K'        ; Detect K = kilobytes.
je .kilobytes
cmp al,'k'
jne ParseError
.kilobytes:
imul rcx,rcx,1024       ; Make kilobytes from accumulated value.
jmp .nextChar
.megabytes:
imul rcx,rcx,1024*1024  ; Make megabytes from accumulated value.
jmp .nextChar
.gigabytes:
imul rcx,rcx,1024*1024*1024  ; Make gigabytes from accumulated value. 
.nextChar:
lodsb              ; Get next char after numeric value.
cmp rsi,r9         ; R9 = Loaded scenario file limit in the buffer.
jae EndOfScenario  ; Go exit if scenario file done. 
jmp .notadigit
.normalTerm:       ; Otherwise normal termination, store extracted value. 
mov edx,[r10 + X2] ; RDX = Pointer to option value.
add rdx,OpDesc
mov [rdx],rcx      ; Write option value, qword, extracted as decimal. 
clc                ; Next, return and skip remaining string part.
ret
;---------- Local subroutine: OPTION_STRING handler. --------------------------;
;                                                                              ;
; INPUT:    RSI = Pointer to scenario file current parse fragment.             ;
;           R9  = Limit for RSI, address of first not valid byte.              ;
;           R10 = Pointer to this detected option descriptor.                  ;
;                                                                              ;
; OUTPUT:   R8  = Updated pointer to current scenario.                         ;        
;           CF flag = status, CF=0(NC)=skipped OK, CF=1(C)=spec. case, see ZF, ;
;           ZF flag = special case type, valid if CF = 1.                      ;
;           ZF=1(Z)=parse error, ZF=0(NZ)=end of scenario file reached.        ;
;------------------------------------------------------------------------------;
OptionString:
call SkipEqual      ; Skip " = " fragment.
jc ParseSpecialCase ; Go if scenario done or parsing error. 
mov edi,[r10 + X2]  ; RDI = Pointer to pointer to string.
add rdi,OpDesc
mov rdi,[rdi]       ; RDI = Pointer to string.
mov ecx,PATH_BUFFER_SIZE - 1  ; Limit for string buffer, exclude last 0.
.cycle:             ; Cycle for string copy from scenario to buffer.
cmp rsi,r9          ; R9 = Loaded scenario file limit in the buffer.
jae EndOfScenario   ; Go exit if scenario file done. 
lodsb               ; Read current char.
call CheckLineChar  ; Detect 0Ah(LF), 0Dh(CR), 3Bh(';'), 20h(' '), 09h(TAB).
je .stop            ; Go string end if one of this chars.
stosb               ; Store char in the destination buffer.
loop .cycle         ; Cycle for string copy, limited by buffer size (RCX). 
jmp ParseError      ; Go error if too long string.
.stop:              ; End of informative part of string.
mov al,0
stosb               ; Write 0-terminator.
clc                 ; Next, return and skip remaining string part.
ret
;---------- Check line continue by compare current char. ----------------------; 
;                                                                              ;
; INPUT:    AL = Char for comparision                                          ;
;                                                                              ;
; OUTPUT    ZF flag, 1(Z) if char detected, 0(NZ) if char not detected         ;
;           char detected if match one of:                                     ;
;           0Ah(LF), 0Dh(CR), 3Bh(';'), 20h(' '), 09h(TAB)                     ;
;------------------------------------------------------------------------------;
CheckLineChar:
cmp al,0Ah
je @f       ; Go with ZF=1 if LF (Line Feed).
cmp al,0Dh
je @f       ; Go with ZF=1 if CR (Carriage Return).
cmp al,' '
je @f       ; Go with ZF=1 if SPACE.
cmp al,09h     
je @f       ; Go with ZF=1 if TAB.
cmp al,';'
@@:
ret
;---------- Local subroutine for skip " = ". ----------------------------------;
;                                                                              ;
; INPUT:   RSI = Pointer to scenario file current parse fragment.              ;
;          R9  = Limit for RSI, address of first not valid byte.               ;
;                                                                              ;
; OUTPUT:  RSI = Updated by skip fragment " = ".                               ;
;          CF flag = status, CF=0(NC)=skipped OK, CF=1(C)=spec. case, see ZF,  ;
;          ZF flag = special case type, valid if CF = 1.                       ;
;          ZF=1(Z)=parse error, ZF=0(NZ)=end of scenario file reached.         ;
;------------------------------------------------------------------------------;
SkipEqual:
cmp rsi,r9    ; Check end of file.
jae .normal   ; Go exit if end of file.
lodsb         ; AL = current char.
cmp al,' '
je SkipEqual  ; Continue skip if SPACE.
cmp al,09h    
je SkipEqual  ; Continue skip if TAB.
cmp al,'='
jne .error 
.cycle:
cmp rsi,r9    ; Check end of file.
jae .normal   ; Go exit if end of file.
lodsb         ; AL = current char.
cmp al,' '
je .cycle     ; Continue skip if SPACE.
cmp al,09h
je .cycle     ; Continue skip if TAB.
dec rsi       ; RSI = Pointer to first char after " = " sequence.
clc
ret           ; Return with CF=0 means normal status. 
.normal:
or al,1
stc
ret           ; Return with CF=1, ZF=0 means end of scenario file. 
.error:
xor al,al
stc
ret           ; Return with CF=1, ZF=1 means parse error: unexpected char.
