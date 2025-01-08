;---------- Print memory block size as Integer.Float. -------------------------;
; Float part is 1 char, use P1-version of Floating Print.                      ;
; If rounding precision impossible, print as hex.                              ;
; Only x.5 floating values supported, otherwise as hex.                        ;
;                                                                              ;
; INPUT:   EDX:EAX = Number value, units = Bytes, EDX=High32, EAX=Low32.       ;
;          BL  = Force units (override as smallest only),                      ;
;                FF = No force units, auto select,                             ;
;                0 = Bytes, 1 = KB, 2 = MB, 3 = GB, 4 = TB.                    ;
;          EDI = Destination Pointer (flat).                                   ;
;                                                                              ;
; OUTPUT:  EDI = New Destination Pointer (flat),                               ;
;                modified because string write.                                ;
;------------------------------------------------------------------------------;
; If BL=FFh, auto-select units ( Bytes, KB, MB, GB, TB ) by size and mod=0
; criteria. Otherwise print with selected units with 1 digit floating part.
SizePrint64:
pushad
cld
;---------- Cycle for units selection. ----------------------------------------;
xor ecx,ecx          ; ECX = Units selector.
test eax,eax
jnz .unitsAutoCycle
test edx,edx
jz .decimalMode      ; Go if value  = 0.
xor ebp,ebp
xor esi,esi
.unitsAutoCycle:
mov ebp,eax          ; EBP = Save previous value.
shrd eax,edx,10
shr edx,10
jnz .above32bit      ; Go execute next division if value > 32-bit. 
cmp cl,bl
je .modNonZero       ; Go print if override units match.
xor esi,esi
shrd esi,ebp,10
shr esi,22           ; ESI = mod.
cmp bl,0FFh
jne .above32bit      ; Go skip mod logic if override units mode. 
test esi,esi
jnz .modNonZero      ; Go print if mod non-zero.
.above32bit:                
inc ecx              ; Units selector + 1.
jmp .unitsAutoCycle  ; Make cycle for select optimal units.
;---------- Check overflow. ---------------------------------------------------;
.modNonZero:
cmp ecx,4
ja .hexMode          ; Go print hex if units too big.
;---------- Print value integer part. -----------------------------------------;
mov eax,ebp
.decimalMode:
push ebx
mov bl,0
call DecimalPrint32  ; Print value, integer part.
pop ebx
;---------- Pring floating part if override units mode. -----------------------;
jecxz .afterNumber   ; Go skip float part if units = bytes.
cmp bl,0FFh
je .afterNumber      ; Go skip float part if units = auto.
mov al,'.'
stosb
xchg eax,esi
xor edx,edx
mov ebx,102
div ebx
cmp eax,9
jbe .limitDecimal
mov eax,9
.limitDecimal:
mov bl,0
call DecimalPrint32         ; Print value, floating part.
;---------- Print units. ------------------------------------------------------;
.afterNumber:
mov al,' '
stosb
lea esi,[U_B]
mov al,cl
mov ah,4
call StringWriteSelected    ; Print units.
jmp .exit
;---------- Entry point for print as HEX if value too big. --------------------;
.hexMode:
call HexPrint64             ; Print 64-bit hex integer: number of Bytes.
mov al,'h'
stosb 
.exit:
mov [esp],edi
popad
ret
