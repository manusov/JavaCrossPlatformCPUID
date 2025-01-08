/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Parent class for CPUID functions classes, with this constant lists up.
This class inherited by standard, extended and virtual
CPUID functions classes with this constant lists up.

*/

package dumploader.cpuid;

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
