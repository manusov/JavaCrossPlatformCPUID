;---------- Print double precision value. -------------------------------------;
; x87 FPU used, required x87 presence validation by CPUID before call this.    ;
;                                                                              ;
; INPUT:   EDX:EAX = Double precision number, EDX=High32, EAX=Low32.           ;
;          BL  = Number of digits in the INTEGER part,                         ;
;                used for add left non-signed zeroes.                          ; 
;                BL=0 means not print left unsigned zeroes.                    ;
;          BH  = Number of digits in the FLOAT part,                           ;
;                used as precision control.                                    ;
;          EDI = Destination text buffer pointer.                              ;
;                                                                              ;
; OUTPUT:  EDI = Modified by text string write.                                ;  
;------------------------------------------------------------------------------;
DoublePrint:
pushad
cld
;---------- Detect special cases for DOUBLE format, yet unsigned indication. --;
test eax,eax
jnz @f                 ; Go if low 32 bits not zero, not a special case.
mov ecx,07FFFFFFFh
and ecx,edx            ; This mask clear sign bit ECX.31 = All number.63.
jz .fp64_Zero          ; Go if special cases = 0.0  or  -0.0.
cmp ecx,07FF80000h
je .fp64_QNAN          ; Go if special case = QNAN (Quiet Not a Number).
cmp ecx,07FF00000h
je .fp64_INF           ; Go if special case = INF (Infinity).
ja .fp64_NAN           ; Go if special case = NAN (Not a Number).
@@:
;---------- Initializing FPU x87. ---------------------------------------------;
finit
; Change rounding mode from default (nearest) to truncate.  
push edx eax   ; Save input value.
push eax       ; Reserve space.
fstcw [esp]
pop eax
or ax,0C00h    ; Correct Rounding Control, RC = FPU CW bits [11-10].
push eax
fldcw [esp]
pop eax
;---------- Load input value, note rounding mode already changed. -------------;
fld qword [esp]
pop eax edx
;---------- Separate integer and float parts. ---------------------------------; 
fld st0         ; st0 = value   , st1 = value copy.
frndint         ; st0 = integer , st1 = value copy.
fxch            ; st0 = value copy , st1 = integer.
fsub st0,st1    ; st0 = float , st1 = integer.
;---------- Build divisor = f(precision selected). ----------------------------; 
mov eax,1
movzx ecx,bh    ; BH = count digits after ".".
jecxz .divisorDone
@@:
imul eax,eax,10
loop @b
.divisorDone:
;---------- Build float part as integer number. -------------------------------; 
push eax
fimul dword [esp]
pop eax
;--- Extract signed Binary Coded Decimal (BCD) to [esp+00] float part .X. -----; 
sub esp,32        ; Make frame for stack variable, used for x87 write data.
fbstp [esp + 00]  ; Store BCD integer and pop, destination is 80 bit = 10 bytes.
;--- Extract signed Binary Coded Decimal (BCD) to [esp+16], integer part X. ---;
fbstp [esp + 16]  ; Store BCD integer and pop, destination is 80 bit = 10 bytes.
;--- Check sign of integer and float part. ------------------------------------; 
test byte [esp + 16 + 09],80h  ; Test bit 79 of 80-bit x87 operand (integer part).
setnz dl                       ; DL = Sign of integer part.
test byte [esp + 00 + 09],80h  ; Test bit 79 of 80-bit x87 operand (floating part).
setnz dh                       ; DH = Sign of floating part.
;---------- Go error if sign of integer and float part mismatch. --------------;
;---------------------------------
; This comparision and error branching rejected 
; because bug with -1.0 "-" AND "+", CHECK IF SIGN SAVED ?
; cmp dx,0100h
; je .Error
; cmp dx,0001h
; je .Error
; Write "-" if one of signs "-".
;---------------------------------
test dx,dx
jz @f       ; Go skip write "-" if both integer/floating signs "+".
;---------- Write "-" if negative value. --------------------------------------;
mov al,'-'
stosb
@@:
;---------- Write INTEGER part, note chars # 18,19 not printed. ---------------; 
                         ; CH = 0  = flag "minimum one digit always printed".
mov cx,20                ; CL = 20 = maximum number of digits in the integer part. 
mov edx,[esp + 16 + 06]  ; EDX = Integer part BCD , bytes [06-09] = chars [12-19].  
mov esi,[esp + 16 + 02]  ; ESI = bytes [02-05] = chars [04-11]. 
mov ebp,[esp + 16 + 00]  ; EBP = bytes [00-01] = chars [00-03]. 
shl ebp,16
and edx,07FFFFFFFh   ; Clear sign bit.
.cycleInteger:  ; Cycle for digits in the INTEGER part.
mov eax,edx
shr eax,28      ; AL = current digit, can be 00h-07h for positive only context.
cmp cl,1
je .store       ; Go print if last pass, otherwise .X instead 0.X.
cmp cl,bl
jbe .store      ; Go print if required by formatting option, BL=count.
test ch,ch
jnz .store      ; Go print, if digits sequence already beginned.
test al,al
jz .position    ; Otherwise, can go skip print if digit = 0. 
.store:
mov ch,1
or al,30h
stosb           ; Write current ASCII digit.
.position:
shld edx,esi,4  ; Positioning digits sequence at EBP:ESI:EDX group.
shld esi,ebp,4
shl ebp,4
dec cl
jnz .cycleInteger  ; Cycle for digits in the INTEGER part.
;---------- Write decimal point. ----------------------------------------------;
test bh,bh
jz .exit           ; Skip if not print float part.
mov al,'.'
stosb
;---------- Write FLOATING part, note chars # 18-23 not printed. --------------;
std                      ; Write from right to left. 
movzx ecx,bh             ; ECX = digits count.     
lea edi,[edi + ecx]      ; EDI = After last digit (char) position.
mov edx,[esp + 00 + 00]  ; EDX = Floating part BCD , bytes [00-03] = chars [00-07].  
mov esi,[esp + 00 + 04]  ; ESI = bytes [04-07] = chars [08-15]. 
mov ebp,[esp + 00 + 00]  ; EBP = bytes [08-11] = chars [16-23]. 
push edi
dec edi
.cycleFloat:         ; Cycle for digits in the FLOATING part.
mov al,dl
and al,0Fh
or al,30h
stosb
shrd edx,esi,4       ; Positioning digits sequence at EBP:ESI:EDX group.
shrd esi,ebp,4
shr ebp,4
loop .cycleFloat     ; Cycle for digits in the FLOATING part.
pop edi
cld                  ; Restore strings increment mode.
;---------- Go exit subroutine. -----------------------------------------------;
add esp,32
jmp .exit
;---------- Write strings for different errors types. -------------------------;
.fp64_Zero:					; Zero.
mov eax,'0.0 '
jmp .fp64special
.fp64_INF:          ; "INF" = Infinity, yet unsigned infinity indicated.
mov eax,'INF '
jmp .fp64special
.fp64_NAN:
mov eax,'NAN '      ; "NAN" = (Signaled) Not a number.
jmp .fp64special
.fp64_QNAN:
mov eax,'QNAN'      ; "QNAN" = Quiet not a number.
.fp64special:
stosd
jmp .exit
.Error:
mov al,'?'
stosb
.exit:
;---------- Exit with re-initialize x87 FPU. ----------------------------------; 
finit
mov [esp],edi
popad
ret

