/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Class for support CPUID Extended Function 8000001Bh =
AMD Instruction based sampling.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid8000001B extends ParameterFunctionCpuid
{
Cpuid8000001B()
    { setFunction( 0x8000001B ); }

@Override String getLongName()
    { return "AMD Instruction based sampling"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "IBS FFV"     , "Instruction based sampling feature flag valid" } ,       // bit 0
      { "FETCH SAMP"  , "IBS fetch sampling" } , 
      { "OP SAM"      , "IBS execution sampling supported" } , 
      { "RW OP CNT"   , "Read write of OP counter" } , 
      { "OP CNT"      , "OP counting mode" } , 
      { "BRN TRG"     , "Branch target address reporting" } , 
      { "OP CNT EXT"  , "IbsOpCurCnt, IbsOpMaxCnt extend by 7 bits" } , 
      { "RIP INV"     , "Invalid RIP indication" } , 
      { "OP BRN FUSE" , "Fused branch micro-op indication" } ,
      { "IBS F MSR"   , "IBS fetch control extended MSR" } ,
      { "IBS OP D"    , "IBS op data 4 MSR" } ,                                // bit 10
      { "IBS L3MF"    , "L3 miss filtering for instruction based sampling" } , // bit 11
      { "IBS LATF"    , "Filtering of IBS samples based on load latency" },    // bit 12
      { "x"           , "Reserved" },    // bit 13
      { "x"           , "Reserved" },
      { "x"           , "Reserved" },
      { "x"           , "Reserved" },
      { "x"           , "Reserved" },
      { "x"           , "Reserved" },    // bit 18
      { "SIMP DTLB"   , "Simplified DTLB page size and miss reporting" } , // bit 19
      { "x"           , "Reserved" } ,   // bit 20
      { "x"           , "Reserved" } ,
      { "x"           , "Reserved" } ,
      { "x"           , "Reserved" } ,
      { "x"           , "Reserved" } ,
      { "x"           , "Reserved" } ,
      { "x"           , "Reserved" } ,
      { "x"           , "Reserved" } ,
      { "x"           , "Reserved" } ,
      { "x"           , "Reserved" } ,
      { "x"           , "Reserved" } ,
      { "x"           , "Reserved" } };  // bit 31

@Override String[][] getParametersList()
    {
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        strings = decodeBitmap( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
