/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for representation Microsoft-defined structure, returned by WinAPI
function GetLogicalProcessorInformationEx().

*/

package cpuidv3.servicempwindows;

class EntryGroup extends TopologyRelationship
{
    final short maximumGroupCount;
    final short activeGroupCount;
    final GroupInfo[] ginfo;
    
    EntryGroup( int offs, short max, short act, GroupInfo[] gi )
    {
        super( offs );
        maximumGroupCount = max;
        activeGroupCount = act;
        ginfo = gi;
    }
}
