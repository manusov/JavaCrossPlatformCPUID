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
This file contains Processors and Hypervisors data exported from
Todd Allen CPUID project. Some variables and functions names not compliant
with java naming conventions, this fields using original C/C++ naming.
-------------------------------------------------------------------------------
Decode AMD extended CPUID cache information and set flags, 
used for processor detection (differentiate between same TFMS signatures).
value = L2 cache size and associativity information, 
register ECX after CPUID function 80000006h.
val_80000006_ecx = register after CPUID function 80000006h.
stash = processor information collection, updated by detect() method.
*/

package cpuidv2.cpudatabase;

public class AmdStashCache 
{

void detect( DatabaseStash stash )
    {
    int size = stash.val_80000006_ecx >>> 16;            // units = kilobytes
    int ways = ( stash.val_80000006_ecx >>> 12 ) & 0xF;  // units = ways
    if ( ( size == 256 )&&( ways == 4 ) )
        stash.L2_4w_256K = true;
    if ( ( size == 512 )&&( ways == 4 ) )
        stash.L2_4w_512K = true;
    }
}
