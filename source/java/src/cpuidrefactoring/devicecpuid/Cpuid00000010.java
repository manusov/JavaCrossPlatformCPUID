/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
00000010h = Platform quality of service enforcement.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid00000010 extends ParameterFunctionCpuid
{
Cpuid00000010()
    { setFunction( 0x00000010 ); }

@Override String getLongName()
    { return "Platform quality of service enforcement"; }

// Control tables for results decoding, subfunction 0
private final static String[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "x"      , "Reserved" } , 
      { "L3 QoS" , "L3 cache allocation technology, QoS enforcement" } ,
      { "L2 QoS" , "L2 cache allocation technology, QoS enforcement" } ,
      { "M QoS"  , "Memory bandwidth allocation, QoS enforcement" } };
// subfunction 1
private final static Object[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "[ L3 Cache QoS ]"
        + "  Length of the capacity bit mask for ResID" , 4 , 0 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_1 =
    { { "Bit-granular map of isolation/contention" , 31 , 0 } };
private final static String[][] DECODER_ECX_SUBFUNCTION_1 =
    { { "x"      , "Reserved" } , 
      { "UCOS"   , "Update of COS should be infrequent" } ,
      { "CDP"    , "Code and Data prioritization technology" } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_1 =
    { { "Highest COS number supported for this ResID" , 15 , 0 } };
// subfunction 2, note ECX reserved for this subfunction
private final static Object[][] DECODER_EAX_SUBFUNCTION_2 =
    { { "[ L2 Cache QoS ]"
        + "  Length of the capacity bit mask for ResID" , 4 , 0 } };
private final static Object[][]
        DECODER_EBX_SUBFUNCTION_2 = DECODER_EBX_SUBFUNCTION_1;
private final static Object[][]
        DECODER_EDX_SUBFUNCTION_2 = DECODER_EDX_SUBFUNCTION_1;
// subfunction 3, note EBX reserved for this subfunction
private final static Object[][] DECODER_EAX_SUBFUNCTION_3 =
    { { "[ DRAM QoS ]"
        + "  Maximum MBA throttling value for this ResID" , 11 , 0 } };
private final static String[][] DECODER_ECX_SUBFUNCTION_3 =
    { { "PT MBA" , "Per thread Memory Bandwidth Allocation" } , 
      { "x"      , "Reserved" } , 
      { "UCOS"   , "Update of COS should be infrequent" } };
private final static Object[][] 
        DECODER_EDX_SUBFUNCTION_3 = DECODER_EDX_SUBFUNCTION_1;

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EBX, subfunction 0
        strings = decodeBitmap
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( strings );
        if ( entries.length > 1 )
            {
            for( int i=1; i<entries.length; i++ )
                {
                a.add( interval );
                switch( entries[i].subfunction )
                    {
                    case 1:    // subfunction 1
                        dr = decodeBitfields( "EAX", 
                            DECODER_EAX_SUBFUNCTION_1, entries[i].eax );
                        a.addAll( dr.strings );
                        dr = decodeBitfields( "EBX", 
                            DECODER_EBX_SUBFUNCTION_1, entries[i].ebx );
                        a.addAll( dr.strings );
                        strings = decodeBitmap( "ECX", 
                            DECODER_ECX_SUBFUNCTION_1, entries[i].ecx );
                        a.addAll( strings );
                        dr = decodeBitfields ( "EDX",
                            DECODER_EDX_SUBFUNCTION_1, entries[i].edx );
                        a.addAll( dr.strings );
                        break;
                    case 2:    // subfunction 2
                        dr = decodeBitfields( "EAX", 
                            DECODER_EAX_SUBFUNCTION_2, entries[i].eax );
                        a.addAll( dr.strings );
                        dr = decodeBitfields( "EBX", 
                            DECODER_EBX_SUBFUNCTION_2, entries[i].ebx );
                        a.addAll( dr.strings );
                        dr = decodeBitfields ( "EDX",
                            DECODER_EDX_SUBFUNCTION_2, entries[i].edx );
                        a.addAll( dr.strings );
                        break;
                    case 3:    // subfunction 3
                        dr = decodeBitfields( "EAX", 
                            DECODER_EAX_SUBFUNCTION_3, entries[i].eax );
                        a.addAll( dr.strings );
                        strings = decodeBitmap( "ECX", 
                            DECODER_ECX_SUBFUNCTION_3, entries[i].ecx );
                        a.addAll( strings );
                        dr = decodeBitfields ( "EDX",
                            DECODER_EDX_SUBFUNCTION_3, entries[i].edx );
                        a.addAll( dr.strings );
                        break;
                    default:
                        a.add( new String[] { "", "", "", "", String.format
                               ( "%08Xh", entries[i].subfunction ) } );
                        break;
                    }
                }
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
