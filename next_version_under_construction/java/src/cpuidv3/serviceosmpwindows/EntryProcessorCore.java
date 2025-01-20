/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for representation Microsoft-defined structure, returned by WinAPI
function GetLogicalProcessorInformationEx().

*/

package cpuidv3.serviceosmpwindows;

class EntryProcessorCore extends TopologyRelationship
{
    final byte flags;
    final byte efficiencyClass;
    final short groupsCount;          // TODO. This redundant. Can groups.length. But from binary structure. (?)
    final GroupAffinity[] groups;
    
    EntryProcessorCore( int offs, byte f, byte e, short g, GroupAffinity[] ga )
    {
        super( offs );
        flags = f;
        efficiencyClass = e;
        groupsCount = g;
        groups = ga;
    }
}
