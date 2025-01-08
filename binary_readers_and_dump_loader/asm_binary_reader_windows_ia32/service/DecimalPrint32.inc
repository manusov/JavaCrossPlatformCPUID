;---------- Print 32-bit Decimal Number. -----------------------;
;                                                               ;
; INPUT:   EAX = Number value.                                  ;
;          BL  = Template size, chars. 0=No template.           ;
;          EDI = Destination Pointer (flat).                    ;
;                                                               ;
; OUTPUT:  EDI = New Destination Pointer (flat).                ;
;                modified because string write.                 ;
;---------------------------------------------------------------;
DecimalPrint32:
cld
push eax ebx ecx edx
mov bh,80h-10        ; Bit BH.7 = print zeroes flag.
add bh,bl
mov ecx,1000000000   ; ECX = service divisor.
.mainCycle:
xor edx,edx
div ecx              ; Produce current digit, EDX:EAX / ECX.
and al,0Fh
test bh,bh
js .firstZero
cmp ecx,1
je .firstZero
cmp al,0             ; Not actual left zero ?
jz .skipZero
.firstZero:
mov bh,80h           ; Flag = 1.
or al,30h
stosb                ; Store char.
.skipZero:
push edx             ; Push remainder.
xor edx,edx
mov eax,ecx
mov ecx,10
div ecx
mov ecx,eax          ; ECX = Quotient, used as divisor and cycle condition. 
pop eax              ; EAX = remainder.
inc bh
test ecx,ecx
jnz .mainCycle       ; Cycle if (unsigned) quotient still > 0. 
pop edx ecx ebx eax
ret
