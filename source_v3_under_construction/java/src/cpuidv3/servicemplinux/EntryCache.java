/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for platform topology object representation: cache memory unit.
Based on parent class for platform topology objects representation
( Entry.java ). Linux variant.

*/

package cpuidv3.servicemplinux;

public class EntryCache extends Entry
{
    public enum CACHE_TYPE_LINUX { DATA, INSTRUCTION, UNIFIED, TRACE, UNKNOWN };

    public CACHE_TYPE_LINUX type;
    public int level;
    public long size;
    public int coherency_line_size;
    public int number_of_sets;
    public int ways_of_associativity;
    public int physical_line_partition;
    public long[] shared_cpu_map;
}
