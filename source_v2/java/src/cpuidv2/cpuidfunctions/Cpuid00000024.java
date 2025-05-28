/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2025.
-------------------------------------------------------------------------------
Class for support CPUID Standard Function 00000024h =
Intel AVX10 extension supported level information.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid00000024 extends ParameterFunctionCpuid
{
Cpuid00000024()
    { setFunction( 0x00000024 ); }

@Override String getLongName()
    { return "Intel AVX10 extension supported level leaf"; }

// Control tables for results decoding, subfunction 0
private final static Object[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "Intel AVX10 extension supported level" , 07 , 00  } ,
      { "128-bit vectors for Intel AVX10"       , 16 , 16  } ,
      { "256-bit vectors for Intel AVX10"       , 17 , 17  } ,
      { "512-bit vectors for Intel AVX10"       , 18 , 18  } };

// Subfunction 1.
private final static String[][] DECODER_ECX_SUBFUNCTION_1 =
    { { "VPMM"         , "Vector extension packed matrix multiplication" } ,   // bit 0
      { "x"            , "Reserved"    } ,
      { "A10-VNNI"     , "AVX10_VNNI_INT neural network instructions"    } };  // bit 2

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EBX, subfunction 0
        dr = decodeBitfields
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( dr.strings );
        
        // subfunction 1
        if ( ( entries.length > 1 )&&( entries[0].eax >= 1 )&&
             ( entries[1].subfunction == 1 ) )
            {
            a.add( interval );
            strings = decodeBitmap
                ( "ECX", DECODER_ECX_SUBFUNCTION_1, entries[1].ecx );
            a.addAll( strings );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}