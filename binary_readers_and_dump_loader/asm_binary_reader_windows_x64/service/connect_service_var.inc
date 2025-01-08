;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains helpers library for debug support.          ;
;    This file contains globally used variables without pre-defined values.    ;
;                                                                              ;
;------------------------------------------------------------------------------;

;---------- Variables not requires space in the exe file, part of template. ---;
; Located after constants for EXE file space minimization.

;--- Console input, output, report file, scenario file control variables. -----;
; IMPORTANT. If change this values layout,
; update aliases at connect_service_equ.inc.
Alias_Base:             ; This label used as base point at access aliases. 
StdIn           DQ  ?   ; Handle for Input Device ( example = keyboard ).
StdOut          DQ  ?   ; Handle for Output Device ( example = display ).
ReportName      DQ  ?   ; Pointer to report file name ( example = output.txt ).
ReportHandle    DQ  ?   ; Report file dynamically re-created handle, 0=None.
ScenarioHandle  DQ  ?   ; Scenario file handle. 
ScenarioBase    DQ  ?   ; Scenario file loading base address, 0 = None.
ScenarioSize    DQ  ?   ; Scenario file loading size, 0 = None (load error). 
CommandLine     DQ  ?   ; Pointer to command line string.
;--- This 3 variables must be continuous for return status from subroutines. --; 
ErrorPointer1   DQ  ?   ; Pointer to first error description string, 0=none.
ErrorPointer2   DQ  ?   ; Pointer to second error description string, 0=none.
ErrorCode       DQ  ?   ; WinAPI error code, 0=none.    
;---------- Console output support. -------------------------------------------;
ScreenInfo  CONSOLE_SCREEN_BUFFER_INFO  ; Console output control structure.

;---------- Multifunctional buffer. -------------------------------------------;
align 4096      ; Align by page. 
TEMP_BUFFER     DB  TEMP_BUFFER_SIZE DUP (?)

