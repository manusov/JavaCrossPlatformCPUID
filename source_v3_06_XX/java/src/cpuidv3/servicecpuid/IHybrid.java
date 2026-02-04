/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Interface for extract hybrid processor core type as text string.
Interface used for make function selection flexible, but typically it is
function 0000001Ah for INTEL and
function 80000026h for AMD.

*/

package cpuidv3.servicecpuid;

interface IHybrid 
{
    HybridReturn getHybrid();
}
