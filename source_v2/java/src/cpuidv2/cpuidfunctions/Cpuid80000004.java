/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000003h = Processor name string [3 of 3].
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000004 extends ReservedFunctionCpuid
{
Cpuid80000004()
    { setFunction( 0x80000004 ); }

@Override String getLongName()
    { return "Processor name string [3 of 3]"; }

@Override String[][] getParametersList()
    { return new String[][] { { getLongName(), "See function 80000002h" } }; }
}
