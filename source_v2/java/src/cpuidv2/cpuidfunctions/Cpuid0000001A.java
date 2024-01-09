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
Class for support CPUID Standard Function 0000001Ah = 
Hybrid processor topology information.
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
