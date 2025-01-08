;---------- Copy selected text string terminated by 00h. ------;
; Note last byte 00h not copied.                               ;
;                                                              ;
; INPUT:   RSI = Source address.                               ;
;          RDI = Destination address.                          ;
;          AL  = Selector.                                     ;
;          AH  = Limit  (if Selector>Limit, set Selector=0).   ; 
; OUTPUT:  RSI = Modified by copy.                             ;
;          RDI = Modified by copy.                             ;
;          Memory at [Input RDI] modified.                     ; 
;--------------------------------------------------------------;
StringWriteSelected:
test al,al
jz StringWrite    ; Direct write for first string entry.
cmp al,ah
ja StringWrite    ; Set limit if selector value > limit.  
mov ah,al
;---------- Skip AH strings. ----------------------------------;
cld
@@:
lodsb
cmp al,0
jne @b
dec ah
jnz @b
; No RET continue in the next subroutine.
;---------- Copy text string terminated by 00h. ---------------;
; Note last byte 00h not copied.                               ;
;                                                              ;
; INPUT:   RSI = Source address.                               ;
;          RDI = Destination address.                          ;
; OUTPUT:  RSI = Modified by copy.                             ;
;          RDI = Modified by copy.                             ;
;          Memory at [Input RDI] modified.                     ; 
;--------------------------------------------------------------;
StringWrite:
cld
@@:
lodsb
cmp al,0
je @f
stosb
jmp @b
@@:
ret
