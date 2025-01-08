/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Interface for extract processor local x2APIC ID as text string.
Interface used for make function selection flexible, but
typically it is function 0000000Bh.

*/

package dumploader.cpuid;

public interface IX2ApicId 
{
    public String getX2ApicId();
}
