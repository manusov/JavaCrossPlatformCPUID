/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000001Ch = AMD Lightweight profiling capabilities.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid8000001C extends ParameterFunctionCpuid
{
Cpuid8000001C()
    { setFunction( 0x8000001C ); }

@Override String getLongName()
    { return "AMD Lightweight profiling capabilities"; }

// Control tables for results decoding
private final static Object[][] DECODER_EBX =
    { { "LWP control block size" ,  7 ,  0 } ,
      { "LWP event size"         , 15 ,  8 } ,
      { "LWP maximum event ID"   , 23 , 16 } ,
      { "LWP event offset"       , 31 , 24 } };
private final static Object[][] DECODER_ECX =
    { { "LWP cache latency bit counter size" ,  4 , 0 } ,
      { "LWP data cache miss address valid"  ,  5 , 5 } ,
      { "Amount cache latency is rounded"    ,  8 , 6 } ,
      { "LWP version"                        , 15 , 9 } ,
      { "Minimum size of the LWP event ring buffer, units 32 records" , 23 , 16 } ,
      { "LWP branch prediction filtering supported" , 28 , 28 } ,
      { "LWP IP filtering supported" , 29 , 29 } ,
      { "LWP cache level filtering supported" , 30 , 30 } ,
      { "LWP cache latency filtering supported" , 31 , 31 } };
private final static String[][] DECODER_EDX =
    { { "LWP"      , "Lightweight profiling" } ,
      { "LWPVAL"   , "LWPVAL instruction available" } , 
      { "LWP IRE"  , "LWP instructions retired event available" } , 
      { "LWP BRE"  , "LWP branch retired event available" } , 
      { "LWP DME"  , "LWP DC miss event available" } , 
      { "LWP CNH"  , "LWP core clocks not halted event available" } , 
      { "LWP RNH"  , "LWP core reference clocks not halted event available" } , 
      { "x"        , "Reserved" } ,  // bit 7 reserved
      { "x"        , "Reserved" } ,  // ...
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } , 
      { "x"        , "Reserved" } ,  // ...
      { "x"        , "Reserved" } ,  // bit 28 reserved
      { "LWP Cont" , "Sampling in continuous mode" } , 
      { "LWP PTSC" , "LWP performance TSC in event record" } , 
      { "LWP INT"  , "LWP interrupt on threshold overflow available" } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        a.addAll( dr.strings );
        a.add( interval );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        a.addAll( dr.strings );
        a.add( interval );
        // EDX
        strings = decodeBitmap( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
