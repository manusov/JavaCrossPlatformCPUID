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
Class for support CPUID Binary dump.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class CpuidDump extends SummaryCpuid
{

@Override String getShortName() 
    { return "CPUID Dump"; }

@Override String getLongName() 
    { return "Show CPUID instruction results as binary dump"; }

@Override String[] getParametersListUp()
    { return new String[] 
        { "Function", "Subfunction", "Pass", "EAX", "EBX", "ECX", "EDX" }; }

@Override String[][] getParametersList()
    {
    ArrayList<String[]> a = new ArrayList<>();
    EntryCpuid[] ec = container.buildEntries();
    if ( ec != null )
        {
        for( EntryCpuid e : ec )
            {
            String[] s = new String[]
                { String.format( "%08X", e.function    ) ,
                  String.format( "%08X", e.subfunction ) ,
                  String.format( "%08X", e.pass        ) ,
                  String.format( "%08X", e.eax         ) ,
                  String.format( "%08X", e.ebx         ) ,
                  String.format( "%08X", e.ecx         ) ,
                  String.format( "%08X", e.edx         ) };
            a.add( s );
            }
        }
    return a.isEmpty() ?
        new String[][] { { "?", "?", "?", "?", "?", "?", "?" } } :
        a.toArray( new String[a.size()][] );
    }
}
