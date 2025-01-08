;---------- Print 32-bit Decimal Number. --------------------------------------;
;                                                                              ;
; INPUT:   EAX = Number value.                                                 ;
;          BL  = Template size, chars. 0=No template.                          ;
;          RDI = Destination Pointer (flat).                                   ;
;                                                                              ;
; OUTPUT:  RDI = New Destination Pointer (flat)                                ;
;                modified because string write.                                ;
;------------------------------------------------------------------------------;
DecimalPrint32:
cld
push rax rbx rcx rdx
mov bh,80h-10         ; Bit BH.7 = print zeroes flag.
add bh,bl
mov ecx,1000000000    ; ECX = service divisor.
.MainCycle:
xor edx,edx
div ecx               ; Produce current digit, EDX:EAX / ECX.
and al,0Fh
test bh,bh
js .FirstZero
cmp ecx,1
je .FirstZero
cmp al,0              ; Not actual left zero ?
jz .SkipZero
.FirstZero:
mov bh,80h            ; Flag = 1.
or al,30h
stosb                 ; Store char.
.SkipZero:
push rdx              ; Push remainder.
xor edx,edx
mov eax,ecx
mov ecx,10
div ecx
mov ecx,eax          ; ECX = Quotient, used as divisor and cycle condition. 
pop rax              ; EAX = remainder.
inc bh
test ecx,ecx
jnz .MainCycle       ; Cycle if (unsigned) quotient still > 0. 
pop rdx rcx rbx rax
ret
