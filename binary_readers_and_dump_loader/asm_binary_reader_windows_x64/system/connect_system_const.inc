;------------------------------------------------------------------------------;
;                                                                              ;
;          This directory contains target functionality under debug.           ; 
;                          This file contains                                  ; 
;         globally used constants and variables with pre-defined state.        ; 
;                                                                              ;
;------------------------------------------------------------------------------;

; Constants located at exe file, part of code under debug.
; Located before variables for EXE file space minimization.

;---------- CPU registers names. ----------------------------------------------;
NamesGPR64:
DB  'RAX' , 0 
DB  'RBX' , 0
DB  'RCX' , 0
DB  'RDX' , 0
DB  'RSI' , 0
DB  'RDI' , 0
DB  'RBP' , 0
DB  'RSP' , 0
DB  'R8 ' , 0
DB  'R9 ' , 0
DB  'R10' , 0
DB  'R11' , 0
DB  'R12' , 0
DB  'R13' , 0
DB  'R14' , 0
DB  'R15' , 0
NamesSelectors:
DB  'CS' , 0
DB  'DS' , 0
DB  'ES' , 0
DB  'SS' , 0
DB  'FS' , 0
DB  'GS' , 0
NamesFPU:
DB  'ST0' , 0
DB  'ST1' , 0
DB  'ST2' , 0
DB  'ST3' , 0
DB  'ST4' , 0
DB  'ST5' , 0
DB  'ST6' , 0
DB  'ST7' , 0
NamesMMX:
DB  'MM0' , 0
DB  'MM1' , 0
DB  'MM2' , 0
DB  'MM3' , 0
DB  'MM4' , 0
DB  'MM5' , 0
DB  'MM6' , 0
DB  'MM7' , 0
NamesK:
DB  'K0' , 0
DB  'K1' , 0
DB  'K2' , 0
DB  'K3' , 0
DB  'K4' , 0
DB  'K5' , 0
DB  'K6' , 0
DB  'K7' , 0
NameSSE:
DB  'XMM' , 0
NameAVX256:
DB  'YMM' , 0
NameAVX512:
DB  'ZMM' , 0
IntervalAVX512:
DB  0Dh, 0Ah, '      ' , 0

MsgAbsentCpuid     DB  'CPUID instruction not supported or locked.'        , 0
MsgAbsentFPUTSC    DB  'Required x87 FPU (detect by CPUID) and TSC.'       , 0
MsgErrorWinAPI     DB  'WinAPI error.'                                     , 0
MsgAbsentMMX       DB  'MMX required.'                                     , 0
MsgAbsentSSE2      DB  'SSE2 required.'                                    , 0
MsgAbsentAVX256    DB  'AVX required.'                                     , 0
MsgAbsentAVX256os  DB  'OS context for AVX required.'                      , 0
MsgAbsentAVX512    DB  'AVX512 required.'                                  , 0
MsgAbsentAVX512os  DB  'OS context for AVX512 required.'                   , 0

MsgWarningWoW64    DB  'WoW64 not available for x64 application.' 
                   DB  0Dh, 0Ah, 0Dh, 0Ah, 0

NameCPUID          DB  'CPUID '                                            , 0
MsgErrorGetCpuid   DB  'Get CPUID information failed.'                     , 0
MsgErrorZeroCpuid  DB  'CPUID functions count is zero.'                    , 0  
MsgErrorOverCpuid  DB  'CPUID functions count is too big.'                 , 0

MsgErrorAlloc      DB  'Memory allocation error.'                          , 0
MsgErrorRelease    DB  'Memory release error.'                             , 0
MsgErrorAffin      DB  'Affinization WinAPI not found.'                    , 0
MsgErrorAffFailed  DB  'Affinization failed.'                              , 0
MsgErrorCpuNone    DB  'Logical processors detection failed.'              , 0
MsgErrorCpuLimit   DB  'Too many logical processors detected.'             , 0  

MsgContextCpu      DB  'CPU-supported context bitmap : '                   , 0
MsgContextOs       DB  'OS-enabled context bitmap    : '                   , 0

MsgMeasureTSC      DB  'Measure TSC : '                                    , 0
MsgErrorTSC        DB  'TSC clock measurement error.'                      , 0
NameMHz            DB  ' MHz.', 0Dh, 0Ah, 0Dh, 0Ah                         , 0

MsgCpuCount        DB  'Logical processors count : '                       , 0
MsgAffinityMask    DB  'System affinity mask     : '                       , 0

NameDll                             DB  'KERNEL32.DLL'                     , 0
NamesFunctions:
FnSetThreadAffinityMask             DB  'SetThreadAffinityMask'            , 0
FnGetLogicalProcessorInformation    DB  'GetLogicalProcessorInformation'   , 0
FnGetLogicalProcessorInformationEx  DB  'GetLogicalProcessorInformationEx' , 0
DB 0 ; This 0 means end of list.

; LoadNames:
; DQ  FnSetThreadAffinityMask
; DQ  FnGetLogicalProcessorInformation
; DQ  FnGetLogicalProcessorInformationEx
; DQ  0  ; 0 = List terminator.


  

