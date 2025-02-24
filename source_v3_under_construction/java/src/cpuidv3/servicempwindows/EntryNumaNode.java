/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for representation Microsoft-defined structure, returned by WinAPI
function GetLogicalProcessorInformationEx().

*/

package cpuidv3.servicempwindows;

class EntryNumaNode extends TopologyRelationship
{
    final int nodeNumber;
    final short groupsCount;          // TODO. This redundant. Can groups.length. But from binary structure. (?)
    final GroupAffinity[] groups;
    
    EntryNumaNode( int offs, int n, short g, GroupAffinity[] ga )
    {
        super( offs );
        nodeNumber = n;
        groupsCount = g;
        groups = ga;
    }
}
