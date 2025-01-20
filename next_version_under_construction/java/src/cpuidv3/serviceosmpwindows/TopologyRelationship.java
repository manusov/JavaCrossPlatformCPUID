/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for representation Microsoft-defined structure, selected by 
LOGICAL_PROCESSOR_RELATIONSHIP enumeration.
Used for interpreting results of WinAPI function 
GetLogicalProcessorInformationEx().
https://learn.microsoft.com/ru-ru/windows/win32/api/sysinfoapi/nf-sysinfoapi-getlogicalprocessorinformationex
https://learn.microsoft.com/ru-ru/windows/win32/api/winnt/ne-winnt-logical_processor_relationship

*/

package cpuidv3.serviceosmpwindows;

class TopologyRelationship
{
/*
    public static enum LOGICAL_PROCESSOR_RELATIONSHIP
    {
        RelationProcessorCore,
        RelationNumaNode,
        RelationCache,
        RelationProcessorPackage,
        RelationGroup,
        RelationProcessorDie,
        RelationNumaNodeEx,
        RelationProcessorModule,
        RelationAll
    }
    
    public final LOGICAL_PROCESSOR_RELATIONSHIP relationship;
    public final int size;
    public TopologyRelationship(LOGICAL_PROCESSOR_RELATIONSHIP r, int s)
    {
        relationship = r;
        size = s;
    }
*/
    
    final int offset;
    
    TopologyRelationship( int offs )
    {
        offset = offs;
    }
}
