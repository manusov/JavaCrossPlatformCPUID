/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for representation Microsoft-defined _PROCESSOR_GROUP_INFO structure.
Used for interpreting results of WinAPI function 
GetLogicalProcessorInformationEx().
https://learn.microsoft.com/ru-ru/windows/win32/api/sysinfoapi/nf-sysinfoapi-getlogicalprocessorinformationex

*/

package cpuidv3.serviceosmpwindows;

class GroupInfo 
{
    final byte maximumProcessorCount;
    final byte activeProcessorCount;
    long activeProcessorMask;
    
    GroupInfo( byte max, byte act, long mask )
    {
        maximumProcessorCount = max;
        activeProcessorCount = act;
        activeProcessorMask = mask;
    }
}
