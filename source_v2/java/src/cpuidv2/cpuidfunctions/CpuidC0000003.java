/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
Class for support CPUID Vendor Specific Function C0000003h =
VIA (Centaur) vendor-specific: purpose of this function yet unknown, 
some CPU can alias this function to function 80000003h.
*/

package cpuidv2.cpuidfunctions;

public class CpuidC0000003 extends ReservedFunctionCpuid
{
CpuidC0000003()
    { setFunction( 0xC0000003 ); }
    
}
