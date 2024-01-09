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
Class for support CPUID Function, specific for Intel Xeon Phi:
20000001h = Xeon Phi architecture and graphics function features.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

public class Cpuid20000001 extends ParameterFunctionCpuid
{
Cpuid20000001()
    { setFunction( 0x20000001 ); }

@Override String getLongName()
    { return "Xeon Phi architecture and graphics function features"; }

// Control tables for results decoding
private final static String[][] DECODER_EDX =
    { { "x"    , "Reserved" } , 
      { "x"    , "Reserved" } , 
      { "x"    , "Reserved" } , 
      { "x"    , "Reserved" } , 
      { "K1OM" , "K1OM architecture components" } };

@Override String[][] getParametersList()
    {
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EDX
        strings = decodeBitmap( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
