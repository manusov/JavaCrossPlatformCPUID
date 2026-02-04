/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Interface for extract processor local APIC ID as text string.
Interface used for make function selection flexible, but
typically it is function 00000001h.

*/

package cpuidv3.servicecpuid;

interface IApicId 
{
    String getApicId();
}
