/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Vendor Specific Function
C0000005h = VIA (Centaur) vendor-specific: 
            purpose of this function yet unknown, 
            some CPU can alias this function to function 80000005h.
*/

package cpuidrefactoring.devicecpuid;

public class CpuidC0000005 extends ReservedFunctionCpuid
{
CpuidC0000005()
    { setFunction( 0xC0000005 ); }
    
}
