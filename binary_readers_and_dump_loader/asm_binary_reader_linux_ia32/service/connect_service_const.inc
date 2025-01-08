;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains helpers library for debug support.          ; 
;                          This file contains                                  ; 
;         globally used constants and variables with pre-defined state.        ; 
;                                                                              ;
;------------------------------------------------------------------------------;

;---------- Strings for console output. ---------------------------------------;
StartMsg          DB  0Dh, 0Ah, 'Starting...', 0Dh, 0Ah, 0Dh, 0Ah, 0
DoneMsgWait       DB  'Done, press ENTER...', 0
DoneMsgNoWait     DB  'Done.', 0
TitleString       DB  'Hardware Shell v0.93 (ia32)', 0
CrLf2             DB  0Dh, 0Ah
CrLf              DB  0Dh, 0Ah, 0
;---------- Names for scenario file and report file. --------------------------;
InputName         DB  'input.txt',0
OutputName        DB  'output.txt',0
;---------- Error messages. ---------------------------------------------------;
MsgError          DB  'ERROR: ', 0
MsgErrorTab       DB  '       ', 0
MsgErrorOS        DB  'OS error ', 0
MsgUnknownOption  DB  'Unknown option.', 0 
MsgOption         DB  'Bad option string.', 0
MsgInputSize      DB  'Input scenario file size too big.', 0 

;---------- Memory size and speed units. --------------------------------------;
U_B     DB  'Bytes',0
U_KB    DB  'KB',0
U_MB    DB  'MB',0
U_GB    DB  'GB',0
U_TB    DB  'TB',0
U_MBPS  DB  'MBPS',0
U_NS    DB  'nanoseconds',0

;--- Options descriptors, for values, controlled by scenario file INPUT.TXT. --; 
OpDesc:
OPTION_KEYS   Name_Display        , Option_Display        , Word_Display        , KeyOnOff
OPTION_KEYS   Name_Report         , Option_Report         , Word_Report         , KeyOnOff
OPTION_KEYS   Name_Waitkey        , Option_Waitkey        , Word_Waitkey        , KeyOnOff
OPTION_KEYS   Name_DumpGPR32      , Option_DumpGPR32      , Word_DumpGPR32      , KeyOnOff
OPTION_KEYS   Name_DumpSelectors  , Option_DumpSelectors  , Word_DumpSelectors  , KeyOnOff
OPTION_KEYS   Name_DumpFPU        , Option_DumpFPU        , Word_DumpFPU        , KeyOnOff
OPTION_KEYS   Name_DumpMMX        , Option_DumpMMX        , Word_DumpMMX        , KeyOnOff
OPTION_KEYS   Name_DumpSSE        , Option_DumpSSE        , Word_DumpSSE        , KeyOnOff
OPTION_KEYS   Name_DumpAVX256hex  , Option_DumpAVX256hex  , Word_DumpAVX256hex  , KeyOnOff
OPTION_KEYS   Name_DumpAVX256dec  , Option_DumpAVX256dec  , Word_DumpAVX256dec  , KeyOnOff
OPTION_KEYS   Name_DumpAVX512hex  , Option_DumpAVX512hex  , Word_DumpAVX512hex  , KeyOnOff
OPTION_KEYS   Name_DumpAVX512dec  , Option_DumpAVX512dec  , Word_DumpAVX512dec  , KeyOnOff
OPTION_KEYS   Name_DumpPredicates , Option_DumpPredicates , Word_DumpPredicates , KeyOnOff
OPTION_KEYS   Name_DumpMemory     , Option_DumpMemory     , Word_DumpMemory     , KeyOnOff
OPTION_KEYS   Name_UnitWoW64      , Option_UnitWoW64      , Word_UnitWoW64      , KeyOnOff
OPTION_KEYS   Name_UnitCPUID      , Option_UnitCPUID      , Word_UnitCPUID      , KeyOnOff
OPTION_KEYS   Name_UnitSubFnc     , Option_UnitSubFnc     , Word_UnitSubFnc     , KeyOnOff
OPTION_KEYS   Name_UnitAffCPUID   , Option_UnitAffCPUID   , Word_UnitAffCPUID   , KeyOnOff
OPTION_KEYS   Name_UnitAffSubFnc  , Option_UnitAffSubFnc  , Word_UnitAffSubFnc  , KeyOnOff
OPTION_KEYS   Name_UnitOsContext  , Option_UnitOsContext  , Word_UnitOsContext  , KeyOnOff
OPTION_KEYS   Name_UnitTSC        , Option_UnitTSC        , Word_UnitTSC        , KeyOnOff
OPTION_KEYS   Name_UnitAffinity   , Option_UnitAffinity   , Word_UnitAffinity   , KeyOnOff
OPTION_KEYS   Name_UnitTopology   , Option_UnitTopology   , Word_UnitTopology   , KeyOnOff
OPTION_KEYS   Name_UnitExTopology , Option_UnitExTopology , Word_UnitExTopology , KeyOnOff
OPTION_END

;---------- Long names for options. -------------------------------------------;
; Used for display and save report with parameters list.

Name_Display         DB  'Display console messages'     , 0  
Name_Report          DB  'Generate report file'         , 0
Name_Waitkey         DB  'Wait key press from user'     , 0
  
Name_DumpGPR32       DB  'View ia32 registers'           , 0  
Name_DumpSelectors   DB  'View segment selectors'       , 0   
Name_DumpFPU         DB  'View x87 FPU registers'       , 0  
Name_DumpMMX         DB  'View MMX registers'           , 0   
Name_DumpSSE         DB  'View SSE registers'           , 0  
Name_DumpAVX256hex   DB  'View AVX256 as hex'           , 0   
Name_DumpAVX256dec   DB  'View AVX256 as doubles'       , 0  
Name_DumpAVX512hex   DB  'View AVX512 as hex'           , 0  
Name_DumpAVX512dec   DB  'View AVX512 as doubles'       , 0  
Name_DumpPredicates  DB  'View AVX512 predicates'       , 0  
Name_DumpMemory      DB  'View memory dump'             , 0    

Name_UnitWoW64       DB  'Get WoW64 status'             , 0  
Name_UnitCPUID       DB  'Get CPUID dump'               , 0  
Name_UnitSubFnc      DB  'Get CPUID (sub)function'      , 0  
Name_UnitAffCPUID    DB  'Get affinized CPUID dump'     , 0  
Name_UnitAffSubFnc   DB  'Get affinized function'       , 0  
Name_UnitOsContext   DB  'Get OS context'               , 0  
Name_UnitTSC         DB  'Measure TSC clock'            , 0  
Name_UnitAffinity    DB  'Get affinity mask'            , 0  
Name_UnitTopology    DB  'Get topology'                 , 0  
Name_UnitExTopology  DB  'Get extended topology'        , 0  

;---------- Short single word names for options, used for parsing. ------------;

Word_Display         DB  'display'       , 0
Word_Report          DB  'report'        , 0
Word_Waitkey         DB  'waitkey'       , 0

Word_DumpGPR32       DB  'reg_gpr'       , 0  
Word_DumpSelectors   DB  'reg_selectors' , 0   
Word_DumpFPU         DB  'reg_fpu'       , 0  
Word_DumpMMX         DB  'reg_mmx'       , 0   
Word_DumpSSE         DB  'reg_sse'       , 0  
Word_DumpAVX256hex   DB  'reg_avx256hex' , 0   
Word_DumpAVX256dec   DB  'reg_avx256dec' , 0  
Word_DumpAVX512hex   DB  'reg_avx512hex' , 0  
Word_DumpAVX512dec   DB  'reg_avx512dec' , 0  
Word_DumpPredicates  DB  'reg_predicate' , 0  
Word_DumpMemory      DB  'memory_dump'   , 0    

Word_UnitWoW64       DB  'wow64'         , 0  
Word_UnitCPUID       DB  'cpuid'         , 0  
Word_UnitSubFnc      DB  'subfnc'        , 0  
Word_UnitAffCPUID    DB  'affcpuid'      , 0  
Word_UnitAffSubFnc   DB  'affsubfnc'     , 0  
Word_UnitOsContext   DB  'oscontext'     , 0  
Word_UnitTSC         DB  'tsc'           , 0  
Word_UnitAffinity    DB  'affinity'      , 0  
Word_UnitTopology    DB  'topology'      , 0  
Word_UnitExTopology  DB  'extopology'    , 0  

;---------- Keywords for options. ---------------------------------------------;
KeyOnOff  DB  'off', 0, 'on', 0, 0

;---------- Options values, controlled by scenario file INPUT.TXT. ------------;
; Located at constants part, because this variables has pre-defined values.

Option_Display         DB  1   ; on = console output enabled, off = disabled.
Option_Report          DB  1   ; on = save report to file output.txt enabled, off = disabled.
Option_Waitkey         DB  1   ; on = wait "Press any key" after operation, off = skip this waiting.

Handlers_Map:

Option_DumpGPR32       DB  0   ; Dump general purpose registers.
Option_DumpSelectors   DB  0   ; Dump segment selectors. 
Option_DumpFPU         DB  0   ; Dump x87 FPU data registers.
Option_DumpMMX         DB  0   ; Dump MMX data registers. 
Option_DumpSSE         DB  0   ; Dump SSE data registers.
Option_DumpAVX256hex   DB  0   ; Dump AVX256 data registers as hex. 
Option_DumpAVX256dec   DB  0   ; Dump AVX256 data registers as decimal.
Option_DumpAVX512hex   DB  0   ; Dump AVX512 data registers as hex.
Option_DumpAVX512dec   DB  0   ; Dump AVX512 data registers as decimal.
Option_DumpPredicates  DB  0   ; Dump AVX512 predicates registers.
Option_DumpMemory      DB  0   ; Dump memory (base, size, data set by user).  

Option_UnitWoW64       DB  0   ; Unit test for check WoW64 mode.
Option_UnitCPUID       DB  0   ; Unit test for CPUID all functions dump for one CPU.
Option_UnitSubFnc      DB  0   ; Unit test for CPUID one subfunction dump for one CPU.
Option_UnitAffCPUID    DB  0   ; Unit test for affinized variant of same dump.
Option_UnitAffSubFnc   DB  0   ; Unit test for affinized variant of same dump.
Option_UnitOsContext   DB  0   ; Unit test for CPU supported and OS enabled context.
Option_UnitTSC         DB  0   ; Unit test for measurement TSC clock frequency.
Option_UnitAffinity    DB  0   ; Unit test for platform (process) affinity mask dump.
Option_UnitTopology    DB  0   ; Unit test for platform topology dump, by WinAPI.
Option_UnitExTopology  DB  0   ; Unit test for platform extended topology dump, by WinAPI.

;---------- Handlers for check flags and run scenarios at cycle. --------------;
Handlers_Pointers:
DD  SHELL_DumpGPR32
DD  SHELL_DumpSelectors
DD  SHELL_DumpFPU
DD  SHELL_DumpMMX
DD  SHELL_DumpSSE
DD  SHELL_DumpAVX256hex
DD  SHELL_DumpAVX256dec
DD  SHELL_DumpAVX512hex
DD  SHELL_DumpAVX512dec
DD  SHELL_DumpPredicates
DD  SHELL_DumpMemory
DD  SHELL_UnitWoW64
DD  SHELL_UnitCPUID
DD  SHELL_UnitSubFnc
DD  SHELL_UnitAffCPUID
DD  SHELL_UnitAffSubFnc
DD  SHELL_UnitOsContext
DD  SHELL_UnitTSC
DD  SHELL_UnitAffinity
DD  SHELL_UnitTopology
DD  SHELL_UnitExTopology
DD  0  ; 0 = Terminator.


