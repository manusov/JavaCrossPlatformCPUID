/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Child class for platform topology summary object
representation: Processor Group.
Summarized by affinity masks. Linux variant.
NOTE. Processor Group object type is RESERVED, not used
at Linux variant. This is MS WINDOWS-style object actual
for platforms with >64 logical processors.

*/

package cpuidv3.serviceosmplinux;

public class AffinityGroup extends Affinity
{
    public AffinityGroup(int id, long[] mask)
    {
        super(id, mask);
    }

}
