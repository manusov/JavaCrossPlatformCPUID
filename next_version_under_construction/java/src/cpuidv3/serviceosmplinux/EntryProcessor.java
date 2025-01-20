/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for platform topology object representation: processor core.
Based on parent class for platform topology objects representation
( Entry.java ). Linux variant.

*/

package cpuidv3.serviceosmplinux;

public class EntryProcessor extends Entry
{
    public int core_id;
    public int physical_package_id;
    public long[] core_siblings;
    public long[] thread_siblings;
    
    public int numa_node_id;
    public long[] numa_node_cpumap;
    
    public EntryCache[] caches_visible;
}
