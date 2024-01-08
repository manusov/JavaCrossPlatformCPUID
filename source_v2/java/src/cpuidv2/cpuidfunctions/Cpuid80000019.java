/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000019h = 1GB paging TLB parameters.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid80000019 extends ParameterFunctionCpuid
{
Cpuid80000019()
    { setFunction( 0x80000019 ); }

@Override String getLongName()
    { return "1GB paging TLB parameters"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "L1 data TLB for 1GB pages associativity"            , 31 , 28 } ,
      { "L1 data TLB for 1GB pages number of entries"        , 27 , 16 } ,
      { "L1 instruction TLB for 1GB pages associativity"     , 15 , 12 } , 
      { "L1 instruction TLB for 1GB pages number of entries" , 11 ,  0 } };
private final static Object[][] DECODER_EBX =
    { { "L2 data TLB for 1GB pages associativity"              , 31 , 28 } ,
      { "L2 data TLB for 1GB pages number of entries"          , 27 , 16 } ,
      { "L2 instruction TLB for 1GB pages associativity"       , 15 , 12 } ,
      { "L2 instruction TLB for 1GB pages number of entries"   , 11 ,  0 } };

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
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
