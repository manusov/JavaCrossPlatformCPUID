/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for return hybrid processor topology information.

*/

package dumploader.cpuid;

import dumploader.cpuid.IHybrid.HYBRID_CPU;

public class HybridReturn 
{
    public final HYBRID_CPU hybridCpu;
    public final String hybridName;
    public final int hybridSmt;
    public HybridReturn( HYBRID_CPU hc, String hn, int hs )
    {
        hybridCpu = hc;
        hybridName = hn;
        hybridSmt = hs;
    }
}