/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Entry for logical CPU representation.

*/

package cpuidv3.sal;

import cpuidv3.servicecpudata.EntryCpuidSubfunction;
import cpuidv3.servicecpuid.HybridReturn;

class EntryLogicalCpu
{
    final EntryCpuidSubfunction[] sunfunctionsList;
    HybridReturn hybridDesc = null;

    EntryLogicalCpu( EntryCpuidSubfunction[] s )
    {
        sunfunctionsList = s;
    }
}
