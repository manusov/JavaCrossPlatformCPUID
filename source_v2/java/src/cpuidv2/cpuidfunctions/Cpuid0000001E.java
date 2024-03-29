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
Class for support CPUID Standard Function 0000001Eh =
Tile Matrix Multiply Unit (TMUL) information.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid0000001E extends ParameterFunctionCpuid
{
Cpuid0000001E()
    { setFunction( 0x0000001E ); }

@Override String getLongName()
    { return "Tile Matrix Multiply Unit (TMUL) information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EBX =
    { { "tmul_maxn, column bytes"    , 23 , 8 } ,
      { "tmul_maxk, rows or columns" ,  7 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EBX
        dr = decodeBitfields
            ( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = String.format("%d Bytes", dr.values[0]);
        dr.strings.get(1)[4] = String.format("%d Elements", dr.values[1]);
        a.addAll( dr.strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
