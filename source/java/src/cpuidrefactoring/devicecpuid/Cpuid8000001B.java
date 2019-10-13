/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000001Bh = AMD Instruction based sampling.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid8000001B extends ParameterFunctionCpuid
{
Cpuid8000001B()
    { setFunction( 0x8000001B ); }

@Override String getLongName()
    { return "AMD Instruction based sampling"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "IBS FFV"     , "Instruction based sampling feature flag valid" } ,
      { "FETCH SAMP"  , "IBS fetch sampling" } , 
      { "OP SAM"      , "IBS execution sampling supported" } , 
      { "RW OP CNT"   , "Read write of OP counter" } , 
      { "OP CNT"      , "OP counting mode" } , 
      { "BRN TRG"     , "Branch target address reporting" } , 
      { "OP CNT EXT"  , "IbsOpCurCnt, IbsOpMaxCnt extend by 7 bits" } , 
      { "RIP INV"     , "Invalid RIP indication" } , 
      { "OP BRN FUSE" , "Fused branch micro-op indication" } ,
      { "IBS F MSR"   , "IBS fetch control extended MSR" } ,
      { "IBS OP D"    , "IBS op data 4 MSR" } };

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
