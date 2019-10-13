/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000006h = L2/L3 cache and L2 TLB information.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid80000006 extends ParameterFunctionCpuid
{
Cpuid80000006()
    { setFunction( 0x80000006 ); }

@Override String getLongName()
    { return "L2/L3 cache and L2 TLB information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "L2 data TLB for 2/4MB pages associativity"            , 31 , 28 } ,
      { "L2 data TLB for 2/4MB pages number of entries"        , 27 , 16 } ,
      { "L2 instruction TLB for 2/4MB pages associativity"     , 15 , 12 } , 
      { "L2 instruction TLB for 2/4MB pages number of entries" , 11 ,  0 } };
private final static Object[][] DECODER_EBX =
    { { "L2 data TLB for 4KB pages associativity"              , 31 , 28 } ,
      { "L2 data TLB for 4KB pages number of entries"          , 27 , 16 } ,
      { "L2 instruction TLB for 4KB pages associativity"       , 15 , 12 } ,
      { "L2 instruction TLB for 4KB pages number of entries"   , 11 ,  0 } };
private final static Object[][] DECODER_ECX =
    { { "L2 unified cache size"                                , 31 , 16 } ,
      { "L1 unified cache associativity"                       , 15 , 12 } ,
      { "L2 unified cache lines per tag"                       , 11 ,  8 } ,
      { "L2 unified cache line size"                           ,  7 ,  0 } };
private final static Object[][] DECODER_EDX =
    { { "L3 unified cache size"                                , 31 , 18 } ,
      { "L3 unified cache associativity"                       , 15 , 12 } ,
      { "L3 unified cache lines per tag"                       , 11 ,  8 } ,
      { "L3 unified cache line size"                           ,  7 ,  0 } };

private final static int L3_SCALE = 512;

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        dr.strings.get(0)[4] = writeAssociativityV2( dr.values[0] );
        dr.strings.get(1)[4] = "" + dr.values[1];
        dr.strings.get(2)[4] = writeAssociativityV2( dr.values[2] );
        dr.strings.get(3)[4] = "" + dr.values[3];
        a.addAll( dr.strings );
        a.add( interval );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = writeAssociativityV2( dr.values[0] );
        dr.strings.get(1)[4] = "" + dr.values[1];
        dr.strings.get(2)[4] = writeAssociativityV2( dr.values[2] );
        dr.strings.get(3)[4] = "" + dr.values[3];
        a.addAll( dr.strings );
        a.add( interval );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        dr.strings.get(0)[4] = dr.values[0] + " KB";
        dr.strings.get(1)[4] = writeAssociativityV2( dr.values[1] );
        dr.strings.get(2)[4] = "" + dr.values[2];
        dr.strings.get(3)[4] = dr.values[3] + " Bytes";
        a.addAll( dr.strings );
        a.add( interval );
        // EDX
        dr = decodeBitfields( "EDX", DECODER_EDX, entries[0].edx );
        dr.strings.get(0)[4] = dr.values[0] * L3_SCALE + " KB";
        dr.strings.get(1)[4] = writeAssociativityV2( dr.values[1] );
        dr.strings.get(2)[4] = "" + dr.values[2];
        dr.strings.get(3)[4] = dr.values[3] + " Bytes";
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
