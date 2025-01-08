;---------- Translation error code to error name string. ----------------------;
;                                                                              ;
; INPUT:   EAX = Error code for translation.                                   ;
;          EDI = Destination address for build text string.                    ;
;                                                                              ;
; OUTPUT:  RDI = Modified by string write.                                     ;
;          Memory at [Input EDI] = output string not 0-terminated.             ;
;------------------------------------------------------------------------------;
DecodeError:
; Reserved.
ret
