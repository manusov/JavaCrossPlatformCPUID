/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Parent class used for complex target devices. This class inherited by drivers.
At CPUIDv3 yet used for CPUID instruction information and platform topology
information only. This functionality has complex binary get/set interface and
multiple information screens.
Simple tasks (TSC clocks, CPU context, JVM info, OS info) not uses this class.

*/

package cpuidv3.sal;

import cpuidv3.pal.PAL.OS_TYPE;

public class BinaryToTextDecoder 
{
    private final static String[][] SIMPLEST_UP = { { "Parameter" , "Value" } };

    public String[]     getShortNames() { return null; }
    public String[]     getLongNames()  { return null; }
    public String[][]   getListsUps()   { return SIMPLEST_UP; }
    public String[][][] getLists()      { return null; }
    public String[][]   getDumpsUps()   { return null; }
    public String[][][] getDumps()      { return null; }

    public void setBinary( long[] x )                          {               }
    public long[] getBinary()                                  { return null;  }
    public boolean initBinary()                                { return false; }
    public boolean initBinary( OS_TYPE osType, int osOptions ) { return false; }
    public boolean parseBinary()                               { return false; }
}


