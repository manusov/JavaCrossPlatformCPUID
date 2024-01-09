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
Class for support CPUID Extended Function 8000001Dh =
AMD deterministic cache parameters.
*/

package cpuidv2.cpuidfunctions;

class Cpuid8000001D extends Cpuid00000004
{
Cpuid8000001D()
    { setFunction( 0x8000001D ); }

@Override String getLongName()
    { return "AMD deterministic cache parameters"; }
}
