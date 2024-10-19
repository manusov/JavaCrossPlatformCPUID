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
private final static Object[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "tmul_maxn, column bytes"    , 23 , 8 } ,
      { "tmul_maxk, rows or columns" ,  7 , 0 } };
private final static String[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "AMX-INT8" , "AMX computational operations for 8-bit integers" } ,  // bit 0
      { "AMX-BF16" , "AMX computational operations for bfloat 16-bit"  } ,
      { "AMX-COMP" , "AMX complex instructions"                        } ,
      { "AMX-FP16" , "AMX computational operations for float 16-bit"   } ,
      { "AMX-FP8"  , "AMX computational operations for float 8-bit"    } ,
      { "AMX-TRAN" , "AMX transpose instructions"                      } ,
      { "AMX-TF32" , "AMX matrix multiplication for single precision"  } ,
      { "AMX-A512" , "AMX-AVX512 TMM to ZMM moves instructions"        } ,
      { "AMX-MVRS" , "AMX move read-shared instructions"               } };  // bit 8

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    // subfunction 0
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EBX, subfunction 0
        dr = decodeBitfields
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        dr.strings.get(0)[4] = String.format("%d Bytes", dr.values[0]);
        dr.strings.get(1)[4] = String.format("%d Elements", dr.values[1]);
        a.addAll( dr.strings );
        // subfunction 1
        if ( ( entries.length > 1 )&& ( entries[1].subfunction == 1 ) )
            {
            // EAX, subfunction 1
            strings = decodeBitmap
                ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            a.add( interval );
            a.addAll( strings );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
