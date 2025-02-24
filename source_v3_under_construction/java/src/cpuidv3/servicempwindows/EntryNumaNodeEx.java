/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for representation Microsoft-defined structure, returned by WinAPI
function GetLogicalProcessorInformationEx().

*/

package cpuidv3.servicempwindows;

class EntryNumaNodeEx extends TopologyRelationship
{
    // Reserved.
    
    EntryNumaNodeEx( int offs )
    {
        super( offs );
    }
}
