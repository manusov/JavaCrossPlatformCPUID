/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
0000001Ah = Reserved function.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid0000001A extends ParameterFunctionCpuid
{
Cpuid0000001A()
    { setFunction( 0x0000001A ); }

@Override String getLongName()
    { return "Hybrid processor information"; }

// Control tables for results decoding, subfunction 0, only 0 subf. supported
private final static Object[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "Enumerates the native model ID"  , 23 , 0  } ,
      { "Enumerates the native core type" , 31 , 24 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        switch( dr.values[1] )
            {
            case 0:
                dr.strings.get(1)[4] = "n/a";
                break;
            case 0x10:
            case 0x30:
                dr.strings.get(1)[4] = "Reserved";
                break;
            case 0x20:
                dr.strings.get(1)[4] = "Intel Atom";
                break;
            case 0x40:
                dr.strings.get(1)[4] = "Intel Core";
                break;
            default:
                dr.strings.get(1)[4] = "Unknown";
                break;
            }
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
