/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000017h = Reserved function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000017 extends ReservedFunctionCpuid
{
Cpuid80000017()
    { setFunction( 0x80000017 ); }
}
