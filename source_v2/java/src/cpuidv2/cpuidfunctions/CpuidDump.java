/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
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
