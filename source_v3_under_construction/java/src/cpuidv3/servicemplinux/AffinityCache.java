/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Child class for platform topology summary object
representation: Cache Memory.
Summarized by affinity masks. Linux variant.

*/

package cpuidv3.servicemplinux;

public class AffinityCache extends Affinity
{
    public final EntryCache entryCache;
    
    public AffinityCache(int id, long[] mask, EntryCache e)
    {
        super(id, mask);
        entryCache = e;
    }
}
