/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for platform topology object representation: processor core.
Based on parent class for platform topology objects representation
( Entry.java ). Linux variant.

*/

package cpuidv3.servicemplinux;

class EntryProcessor extends Entry
{
    int core_id;
    int physical_package_id;
    long[] core_siblings;
    long[] thread_siblings;
    
    int numa_node_id;
    long[] numa_node_cpumap;
    
    EntryCache[] caches_visible;
}
