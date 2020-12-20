/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Vendor Specific Function
C0000003h = VIA (Centaur) vendor-specific: 
            VIA (Centaur) actual operational parameters.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

public class CpuidC0000004 extends CpuidC0000002
{
CpuidC0000004()
    { setFunction( 0xC0000004 ); }

@Override String getLongName()
    { return "VIA C7 actual operational parameters"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Thermal monitor temperature"  ,  7 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "XE operation (R/O)"                        , 31 , 31 } ,  // parm #0
      { "Lowest clock ratio"                        , 30 , 24 } ,
      { "IA32_PERF_CTL transition in progress"      , 21 , 21 } ,
      { "IA32_PERF_CTL transition in progress"      , 20 , 20 } ,
      { "Thermal monitor 2 transition in progress"  , 19 , 19 } ,
      { "Thermal monitor 2 transition in progress"  , 18 , 18 } ,
      { "Voltage transition in progress"            , 17 , 17 } ,
      { "Clock ratio transition in progress"        , 16 , 16 } ,
      { "Actual clock ratio"                        , 15 ,  8 } ,
      { "Actual voltage"                            ,  7 , 0 } };  // parm #9


@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX: VIA C7 actual voltage
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        dr.strings.get(0)[4] = String.format ( "%d C", dr.values[0] );
        a.addAll( dr.strings );
        a.add( interval );
        // EBX: VIA C7 actual operational parameters
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(1)[4] = ratioHelper( dr.values[1] );
        dr.strings.get(8)[4] = ratioHelper( dr.values[8] );
        dr.strings.get(9)[4] = voltageHelper ( dr.values[9] );
        a.addAll( dr.strings );
        a.add( interval );
        // ECX: VIA C7 processor minimum/maximum operational paramerers
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        dr.strings.get(0)[4] = ratioHelper( dr.values[0] );
        dr.strings.get(1)[4] = voltageHelper ( dr.values[1] );
        dr.strings.get(2)[4] = ratioHelper( dr.values[2] );
        dr.strings.get(3)[4] = voltageHelper ( dr.values[3] );
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }


}
