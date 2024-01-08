/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000025h = Reserved (undocumented) function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000025 extends ReservedFunctionCpuid
{
Cpuid80000025()
    { setFunction( 0x80000025 ); }
}
