/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Summary Information,
Processor name string, Vendor String, 
maximum standard and extended functions.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class CpuidSummary extends SummaryCpuid
{

@Override String getShortName() 
    { return "CPUID Summary"; }

@Override String getLongName() 
    { return "Show CPUID information as main parameters summary"; }

private final static int BASE_STANDARD_CPUID = 0x00000000;
private final static int BASE_EXTENDED_CPUID = 0x80000000;
private final static int NAME_STRING_CPUID   = 0x80000002;

@Override String[][] getParametersList()
    {
    ArrayList<String[]> a = new ArrayList<>();
    String[][] s;
    ReservedFunctionCpuid f = container.findFunction( NAME_STRING_CPUID );
    if ( f != null )
        {
        s = f.getParametersList();
        if ( ( s != null )&&( s.length >= 1 ) )
            {
            a.add( s[0] );
            }
        }
    f = container.findFunction( BASE_STANDARD_CPUID );
    if ( f != null )
        {
        s = f.getParametersList();
        if ( ( s != null )&&( s.length >= 2 ) )
            {
            a.add( s[1] );
            a.add( s[0] );
            }
        }
    f = container.findFunction( BASE_EXTENDED_CPUID );
    if ( f != null )
        {
        s = f.getParametersList();
        if ( ( s != null )&&( s.length >= 1 ) )
            {
            a.add( s[0] );
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
