;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains target functionality under debug.           ;
;            This file contains globally used source definitions.              ;
;                                                                              ;
;------------------------------------------------------------------------------;

CPUID_NAME_COLOR   EQU  FOREGROUND_RED + FOREGROUND_GREEN + FOREGROUND_BLUE + FOREGROUND_INTENSITY
CPUID_VALUE_COLOR  EQU  FOREGROUND_RED + FOREGROUND_GREEN + FOREGROUND_BLUE 

ERROR_STRING_1     EQU  dword [ebx + 00]
ERROR_STRING_2     EQU  dword [ebx + 04]
ERROR_CODE         EQU  dword [ebx + 08]
BUFFER_SIZE        EQU  16 * 1024

PROCESSORS_LIMIT   EQU  64  ; TODO. Support Processor Groups for >64 CPUs.
                            ; This for x64, not valid for ia32 version.