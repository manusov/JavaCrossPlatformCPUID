;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains target functionality under debug.           ;
;            This file contains globally used source definitions.              ;
;                                                                              ;
;------------------------------------------------------------------------------;

PROCESSORS_LIMIT   EQU  64  ; TODO. Support Processor Groups for >64 CPUs.
                            ; Processor groups is NOT ACTUAL for Linux,
                            ; reserved for Windows.
                            ; Use affinity mask width selection at Linux.
