/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000013h = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000013 extends ReservedFunctionCpuid
{
Cpuid80000013()
    { setFunction( 0x80000013 ); }
}
