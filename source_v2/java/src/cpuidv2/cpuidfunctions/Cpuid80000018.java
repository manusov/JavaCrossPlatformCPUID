/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000018h = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000018 extends ReservedFunctionCpuid
{
Cpuid80000018()
    { setFunction( 0x80000018 ); }
}
