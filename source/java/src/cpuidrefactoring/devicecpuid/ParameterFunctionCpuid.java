/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Parent class for CPUID functions classes, with this constant lists up.
This class inherited by standard, extended and virtual
CPUID functions classes with this constant lists up.
*/

package cpuidrefactoring.devicecpuid;

class ParameterFunctionCpuid extends ReservedFunctionCpuid
{
@Override final String[] getParametersListUp()
    {
    return new String[]
        { "Parameter", "Register", "Bit(s)", "Value, hex" , "Comments" };
    }

@Override String[][] getParametersList()
    {
    return new String[][] { { "?", "?", "?", "?", "?"  } };
    }
}
