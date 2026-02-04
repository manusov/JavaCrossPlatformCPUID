/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Interface for extract processor local x2APIC ID as text string.
Interface used for make function selection flexible, but
typically it is function 0000000Bh.

*/

package cpuidv3.servicecpuid;

interface IX2ApicId 
{
    String getX2ApicId();
}
