/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class - data structure for MP ( multiprocessing ) platform capabilities
and current state, include processors count and processor, selected by
affinization.

*/

package cpuidv3.gui;

public class MpState 
{
    public final int processorsCount;
    public final int enumerationLimit;
    public final boolean affinizationSupported;
    
    public int processorSelected = 0;
    public int enumerationEnabled = 0;
    public boolean affinizationEnabled = false;
    public String[] cpuNames = null;
    
    public MpState( int count, int limit, int curLimit,
                    boolean affinization, String[] names )
    {
        processorsCount = count;
        enumerationLimit = limit;
        affinizationSupported = affinization;
        enumerationEnabled = curLimit;
        cpuNames = names;
    }
}
