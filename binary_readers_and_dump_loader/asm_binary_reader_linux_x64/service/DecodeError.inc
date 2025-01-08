;---------- Translation error code to error name string. ----------------------;
;                                                                              ;
; INPUT:   RAX = Error code for translation.                                   ;
;          RDI = Destination address for build text string.                    ;
;                                                                              ;
; OUTPUT:  RDI = Modified by string write.                                     ;
;          Memory at [Input RDI] = output string not 0-terminated.             ;
;------------------------------------------------------------------------------;
DecodeError:
; ...
; UNDER CONSTRUCTION.
; ...
ret
