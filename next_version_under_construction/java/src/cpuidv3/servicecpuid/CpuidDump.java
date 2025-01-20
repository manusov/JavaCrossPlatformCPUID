/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Binary dump.

*/

package cpuidv3.servicecpuid;

import cpuidv3.services.EntryCpuidSubfunction;
import java.util.ArrayList;

class CpuidDump extends ReservedFunctionCpuid
{

/*
@Override String getShortName() 
    { return "CPUID Dump"; }

@Override String getLongName() 
    { return "Show CPUID instruction results as binary dump"; }
*/

@Override String[] getParametersListUp()
    { return new String[] 
        { "Function", "Subfunction", "Pass", "EAX", "EBX", "ECX", "EDX" }; }

@Override String[][] getParametersList()
    {
    ArrayList<String[]> a = new ArrayList<>();
    EntryCpuidSubfunction[] ec = container.buildEntries();
    if ( ec != null )
        {
            for( EntryCpuidSubfunction e : ec )
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
