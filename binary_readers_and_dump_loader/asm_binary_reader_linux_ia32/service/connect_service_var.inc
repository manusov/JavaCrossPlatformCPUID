;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains helpers library for debug support.          ;
;    This file contains globally used variables without pre-defined values.    ;
;        Console support library: variables without pre-defined values.        ;
; Console input, output, report file, scenario file control variables          ;
;                                IMPORTANT.                                    ; 
;   If change this values layout, update aliases at connect_service_equ.inc.   ;
;                                                                              ;
;------------------------------------------------------------------------------;

;---------- Address alias label. ----------------------------------------------;
Alias_Base:             ; This label used as base point at access aliases. 
;---------- Variables start ---------------------------------------------------;
StdIn           DD  ?   ; Handle for Input Device ( example = keyboard ).
StdOut          DD  ?   ; Handle for Output Device ( example = display ).
ReportName      DD  ?   ; Pointer to report file name ( example = output.txt ).
ReportHandle    DD  ?   ; Report file dynamically re-created handle, 0=None.
ScenarioHandle  DD  ?   ; Scenario file handle. 
ScenarioBase    DD  ?   ; Scenario file loading base address, 0 = None.
ScenarioSize    DD  ?   ; Scenario file loading size, 0 = None (load error). 
CommandLine     DD  ?   ; Pointer to command line string.
;--- This 3 variables must be continuous for return status from subroutines. --; 
ErrorPointer1   DD  ?   ; Pointer to first error description string, 0=none.
ErrorPointer2   DD  ?   ; Pointer to second error description string, 0=none.
ErrorCode       DD  ?   ; OS API error code, 0=none.    
;---------- Console color. ----------------------------------------------------;
OriginalColor   DD  ?   ; Original console color,
                        ; bits[3-0] = foreground, [7-4] = background,
                        ; [30-8] = reserved = 0, [31] = reset default flag.
;---------- Buffer for color control escape sequence. -------------------------;
SequenceBuffer  DB  SEQUENCE_BUFFER_SIZE DUP (?)
;---------- Buffer for terminal control by IOCTL. -----------------------------;
TermiosBuffer   DB  TERMIOS_SIZE DUP (?)
;---------- Multifunctional buffer. -------------------------------------------;
align  TEMP_BUFFER_ALIGNMENT 
TEMP_BUFFER  DB  TEMP_BUFFER_SIZE DUP (?)

