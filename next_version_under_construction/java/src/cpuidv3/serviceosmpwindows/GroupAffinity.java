/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for representation Microsoft-defined GROUP_AFFINITY structure.
Used for interpreting results of WinAPI function 
GetLogicalProcessorInformationEx().
https://learn.microsoft.com/ru-ru/windows/win32/api/sysinfoapi/nf-sysinfoapi-getlogicalprocessorinformationex

*/

package cpuidv3.serviceosmpwindows;

class GroupAffinity 
{
    final long mask;
    final short group;
    GroupAffinity(long m, short g)
    {
        mask = m;
        group = g;
    }
}
