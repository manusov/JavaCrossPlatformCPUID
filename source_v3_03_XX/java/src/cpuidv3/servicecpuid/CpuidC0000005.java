/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Vendor Specific Function C0000005h =
VIA (Centaur) vendor-specific: purpose of this function yet unknown, 
some CPU can alias this function to function 80000005h.

*/

package cpuidv3.servicecpuid;

class CpuidC0000005 extends ReservedFunctionCpuid
{
CpuidC0000005()
    { setFunction( 0xC0000005 ); }
    
}
