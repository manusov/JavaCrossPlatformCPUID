/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Vendor Specific Function
80860004h = Transmeta vendor-specific: 
            Transmeta information string, part 2 of 4.
*/

package cpuidrefactoring.devicecpuid;

public class Cpuid80860004 extends ReservedFunctionCpuid
{
Cpuid80860004()
    { setFunction( 0x80860004 ); }

@Override String getLongName()
    { return "Transmeta information string [2 of 4]"; }

@Override String[][] getParametersList()
    { return new String[][] { { getLongName(), "See function 80860003h" } }; }
}
