;---------- Print 64-bit hex number. --------------------------;
; INPUT:  RAX = Number.                                        ;
;         RDI = Destination pointer.                           ;
; OUTPUT: RDI = Modify.                                        ;
;--------------------------------------------------------------;
HexPrint64:
push rax
ror rax,32
call HexPrint32
pop rax
; no RET, continue at next subroutine.
;---------- Print 32-bit hex number. --------------------------;
; INPUT:  EAX = Number.                                        ;
;         RDI = Destination pointer.                           ;
; OUTPUT: RDI = Modify.                                        ;
;--------------------------------------------------------------;
HexPrint32:
push rax
ror eax,16
call HexPrint16
pop rax
; no RET, continue at next subroutine.
;---------- Print 16-bit hex number. --------------------------;
; INPUT:  AX  = Number.                                        ;
;         RDI = Destination pointer.                           ;
; OUTPUT: RDI = Modify.                                        ;
;--------------------------------------------------------------;
HexPrint16:
push rax
xchg al,ah
call HexPrint8
pop rax
; no RET, continue at next subroutine.
;---------- Print 8-bit hex number. ---------------------------;
; INPUT:  AL  = Number.                                        ;
;         RDI = Destination pointer.                           ;
; OUTPUT: RDI = Modify.                                        ;
;--------------------------------------------------------------;
HexPrint8:
push rax
ror al,4
call HexPrint4
pop rax
; no RET, continue at next subroutine.
;---------- Print 4-bit hex number. ---------------------------;
; INPUT:  AL  = Number (bits 0-3).                             ;
;         RDI = Destination pointer.                           ;
; OUTPUT: RDI = Modify.                                        ;
;--------------------------------------------------------------;
HexPrint4:
cld
push rax
and al,0Fh
cmp al,9
ja .modify
add al,'0'
jmp .store
.modify:
add al,'A'-10
.store:
stosb
pop rax
ret
