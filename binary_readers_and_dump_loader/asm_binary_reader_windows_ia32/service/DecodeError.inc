;---------- Translation error code to error name string. ----------------------;
;                                                                              ;
; INPUT:   EAX = Error code for translation.                                   ;
;          EDI = Destination address for build text string.                    ;
;                                                                              ;
; OUTPUT:  EDI = Modified by string write.                                     ;
;          Memory at [Input EDI] = output string, not 0-terminated.            ;
;------------------------------------------------------------------------------;
DecodeError:
push esi
;---------- Get text string from OS. ------------------------------------------;
xor ecx,ecx
push ecx            ; Pointer to dynamically allocated buffer.
mov edx,esp
push ecx            ; Parm #7 = Arguments, parameter ignored.
push ecx            ; Parm #6 = Size, parameter ignored.
push edx            ; Parm #5 = Pointer to pointer to allocated buffer.
push LANG_NEUTRAL   ; Parm #4 = Language ID.
push eax            ; Parm #3 = Message ID, code for translation.
push ecx            ; Parm #2 = Message source, ignored.
push FORMAT_MESSAGE_ALLOCATE_BUFFER + FORMAT_MESSAGE_FROM_SYSTEM  ; Parm #1 = Flags.
call [FormatMessage]
pop esi              ; ESI = Updated pointer to allocated buffer.
;---------- End of get text string from OS, copy string. ----------------------;
mov ecx,esi
jecxz .unknown      ; Skip string copy if buffer pointer = null. 
test eax,eax
jz .unknown         ; Skip string copy if output size = 0. 
call StringWrite
jmp .release
.unknown:
mov al,'?'
stosb               ; Write "?" if cannot get string.
;---------- Release buffer. ---------------------------------------------------;
.release:
jecxz .exit         ; Skip memory release if buffer pointer = null.
push ecx            ; Parm#1 = Pointer to memory block for release. 
call [LocalFree]
.exit:
pop esi
ret
