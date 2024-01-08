/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000024h = Reserved (undocumented) function.
*/

package cpuidv2.cpuidfunctions;

class Cpuid80000024 extends ReservedFunctionCpuid
{
Cpuid80000024()
    { setFunction( 0x80000024 ); }
}
