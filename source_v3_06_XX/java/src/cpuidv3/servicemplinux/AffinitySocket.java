/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Child class for platform topology summary object
representation: Processor Socket (Package).
Summarized by affinity masks. Linux variant.

*/

package cpuidv3.servicemplinux;

class AffinitySocket extends Affinity
{
    AffinitySocket(int id, long[] mask)
    {
        super(id, mask);
    }
}
