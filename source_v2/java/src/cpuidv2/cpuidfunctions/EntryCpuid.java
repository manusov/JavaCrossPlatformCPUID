/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2024.
-------------------------------------------------------------------------------
32-byte entry definition for binary CPUID dump interpreting.
*/

package cpuidv2.cpuidfunctions;

class EntryCpuid 
{
int id, function, subfunction, pass, eax, ebx, ecx, edx;
}
