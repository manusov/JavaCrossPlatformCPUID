/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Extended Function 80000003h = 
Processor name string [2 of 3].

*/

package cpuidv3.servicecpuid;

class Cpuid80000003 extends ReservedFunctionCpuid
{
Cpuid80000003() { setFunction( 0x80000003 ); }

@Override String getLongName()
    { return "Processor name string [2 of 3]"; }

@Override String[][] getParametersList()
    { return new String[][] { { getLongName(), "See function 80000002h" } }; }
}
