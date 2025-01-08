;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains target functionality under debug.           ;
;    This file contains globally used variables without pre-defined values.    ;
;                                                                              ;
;------------------------------------------------------------------------------;

;---------- Variables not requires space in the exe file, part of template. ---;
; Located after constants for EXE file space minimization.

LoadPointers:
_SetThreadAffinityMask             DD  ?
_GetLogicalProcessorInformation    DD  ?
_GetLogicalProcessorInformationEx  DD  ?
_IsWow64Process                    DD  ?

SystemInfo SYSTEM_INFO  ?  ; Make this as TEMP_BUFFER.
                           ; Or fill at initialization.