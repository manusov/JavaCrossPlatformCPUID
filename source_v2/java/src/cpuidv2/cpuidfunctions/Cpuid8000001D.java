/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
8000001Dh = AMD deterministic cache parameters.
*/

package cpuidv2.cpuidfunctions;

class Cpuid8000001D extends Cpuid00000004
{
Cpuid8000001D()
    { setFunction( 0x8000001D ); }

@Override String getLongName()
    { return "AMD deterministic cache parameters"; }
}
