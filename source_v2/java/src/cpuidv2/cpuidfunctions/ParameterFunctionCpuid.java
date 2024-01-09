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
Parent class for CPUID functions classes, with this constant lists up.
This class inherited by standard, extended and virtual
CPUID functions classes with this constant lists up.
*/

package cpuidv2.cpuidfunctions;

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
