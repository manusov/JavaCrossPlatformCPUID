/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Entry for logical CPU representation.

*/

package dumploader.cpuenum;

import dumploader.cpuid.HybridReturn;

public class EntryLogicalCpu
{
    public final EntryCpuidSubfunction[] sunfunctionsList;
    public HybridReturn hybridDesc = null;

    public EntryLogicalCpu( EntryCpuidSubfunction[] s )
    {
        sunfunctionsList = s;
    }
}