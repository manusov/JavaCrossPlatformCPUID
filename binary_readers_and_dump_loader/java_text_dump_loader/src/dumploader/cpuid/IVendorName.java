/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Interface for extract processor vendor string as result of one of CPUID
functions. Interface used for make function selection flexible, but
typically it is function 00000000h.

*/

package dumploader.cpuid;

public interface IVendorName 
{
    public String getVendorName();
}
