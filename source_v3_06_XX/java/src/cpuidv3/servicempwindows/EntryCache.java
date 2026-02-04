/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for representation Microsoft-defined structure, returned by WinAPI
function GetLogicalProcessorInformationEx().

*/

package cpuidv3.servicempwindows;

class EntryCache extends TopologyRelationship
{
    enum PROCESSOR_CACHE_TYPE_WINDOWS
    {
        CacheUnified,
        CacheInstruction,
        CacheData,
        CacheTrace,
        CacheUnknown
    }
    
    final byte level;
    final byte associativity;
    final short lineSize;
    final int cacheSize;
    final PROCESSOR_CACHE_TYPE_WINDOWS type;
    final short groupsCount;          // TODO. This redundant. Can groups.length. But from binary structure. (?)
    final GroupAffinity[] groups;
    
    EntryCache( int offs, byte l, byte a, short ls, int cs, 
            PROCESSOR_CACHE_TYPE_WINDOWS t, short g, GroupAffinity[] ga )
    {
        super( offs );
        level = l;
        associativity = a;
        lineSize = ls;
        cacheSize = cs;
        type = t;
        groupsCount = g;
        groups = ga;
    }
}
