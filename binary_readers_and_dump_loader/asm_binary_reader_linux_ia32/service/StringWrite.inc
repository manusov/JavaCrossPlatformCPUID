;---------- Copy selected text string terminated by 00h. ------;
; Note last byte 00h not copied.                               ;
;                                                              ;
; INPUT:   ESI = Source address.                               ;
;          EDI = Destination address.                          ;
;          AL  = Selector.                                     ;
;          AH  = Limit  (if Selector>Limit, set Selector=0).   ; 
; OUTPUT:  ESI = Modified by copy.                             ;
;          EDI = Modified by copy.                             ;
;          Memory at [Input EDI] modified.                     ;
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
; No RET continue in the next subroutine
;---------- Copy text string terminated by 00h. ---------------;
; Note last byte 00h not copied.                               ;
;                                                              ;
; INPUT:   ESI = Source address.                               ;
;          EDI = Destination address.                          ;
; OUTPUT:  ESI = Modified by copy.                             ;
;          EDI = Modified by copy.                             ;
;          Memory at [Input EDI] modified.                     ;
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

