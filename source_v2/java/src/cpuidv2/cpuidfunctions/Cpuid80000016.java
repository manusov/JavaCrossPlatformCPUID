/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000016h = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000016 extends ReservedFunctionCpuid
{
Cpuid80000016()
    { setFunction( 0x80000016 ); }
}
