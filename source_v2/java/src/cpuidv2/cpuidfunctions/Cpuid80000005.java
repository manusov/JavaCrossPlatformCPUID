/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000005h = L1 cache and L1 TLB information.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid80000005 extends ParameterFunctionCpuid
{
Cpuid80000005()
    { setFunction( 0x80000005 ); }

@Override String getLongName()
    { return "L1 cache and L1 TLB information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "L1 data TLB for 2/4MB pages associativity"            , 31 , 24 } ,
      { "L1 data TLB for 2/4MB pages number of entries"        , 23 , 16 } ,
      { "L1 instruction TLB for 2/4MB pages associativity"     , 15 ,  8 } , 
      { "L1 instruction TLB for 2/4MB pages number of entries" ,  7 ,  0 } };
private final static Object[][] DECODER_EBX =
    { { "L1 data TLB for 4KB pages associativity"              , 31 , 24 } ,
      { "L1 data TLB for 4KB pages number of entries"          , 23 , 16 } ,
      { "L1 instruction TLB for 4KB pages associativity"       , 15 ,  8 } ,
      { "L1 instruction TLB for 4KB pages number of entries"   ,  7 ,  0 } };
private final static Object[][] DECODER_ECX =
    { { "L1 data cache size"                                   , 31 , 24 } ,
      { "L1 data cache associativity"                          , 23 , 16 } ,
      { "L1 data cache lines per tag"                          , 15 ,  8 } ,
      { "L1 data cache line size"                              ,  7 ,  0 } };
private final static Object[][] DECODER_EDX =
    { { "L1 instruction cache size"                            , 31 , 24 } ,
      { "L1 instruction cache associativity"                   , 23 , 16 } ,
      { "L1 instruction cache lines per tag"                   , 15 ,  8 } ,
      { "L1 instruction cache line size"                       ,  7 ,  0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        dr.strings.get(0)[4] = writeAssociativityV1( dr.values[0] );
        dr.strings.get(1)[4] = "" + dr.values[1];
        dr.strings.get(2)[4] = writeAssociativityV1( dr.values[2] );
        dr.strings.get(3)[4] = "" + dr.values[3];
        a.addAll( dr.strings );
        a.add( interval );
        // EBX
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = writeAssociativityV1( dr.values[0] );
        dr.strings.get(1)[4] = "" + dr.values[1];
        dr.strings.get(2)[4] = writeAssociativityV1( dr.values[2] );
        dr.strings.get(3)[4] = "" + dr.values[3];
        a.addAll( dr.strings );
        a.add( interval );
        // ECX
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        dr.strings.get(0)[4] = dr.values[0] + " KB";
        dr.strings.get(1)[4] = writeAssociativityV1( dr.values[1] );
        dr.strings.get(2)[4] = "" + dr.values[2];
        dr.strings.get(3)[4] = dr.values[3] + " Bytes";
        a.addAll( dr.strings );
        a.add( interval );
        // EDX
        dr = decodeBitfields( "EDX", DECODER_EDX, entries[0].edx );
        dr.strings.get(0)[4] = dr.values[0] + " KB";
        dr.strings.get(1)[4] = writeAssociativityV1( dr.values[1] );
        dr.strings.get(2)[4] = "" + dr.values[2];
        dr.strings.get(3)[4] = dr.values[3] + " Bytes";
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
