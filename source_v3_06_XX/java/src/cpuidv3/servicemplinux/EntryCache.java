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

class EntryCache extends Entry
{
    enum CACHE_TYPE_LINUX { DATA, INSTRUCTION, UNIFIED, TRACE, UNKNOWN };

    CACHE_TYPE_LINUX type;
    int level;
    long size;
    int coherency_line_size;
    int number_of_sets;
    int ways_of_associativity;
    int physical_line_partition;
    long[] shared_cpu_map;
}
