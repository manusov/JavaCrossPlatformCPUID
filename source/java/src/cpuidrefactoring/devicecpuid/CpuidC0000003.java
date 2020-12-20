/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Vendor Specific Function
C0000003h = VIA (Centaur) vendor-specific: 
            purpose of this function yet unknown, 
            some CPU can alias this function to function 80000003h.
*/

package cpuidrefactoring.devicecpuid;

public class CpuidC0000003 extends ReservedFunctionCpuid
{
CpuidC0000003()
    { setFunction( 0xC0000003 ); }
    
}
