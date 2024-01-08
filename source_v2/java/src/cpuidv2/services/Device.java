/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Parent class for target device, this class inherited by drivers
for support operations: cpuid, cpu context, cpu clock, jvm info, os info.
At CPUIDv2 yet used for CPUID dump only, 
not used for cpu context, cpu clock, jvm info, os info.
*/

package cpuidv2.services;

public class Device 
{
private final static String[][] SIMPLEST_UP = { { "Parameter" , "Value" } };

public String[]     getScreensShortNames() { return null; }
public String[]     getScreensLongNames()  { return null; }
public String[][]   getScreensListsUp()    { return SIMPLEST_UP; }
public String[][][] getScreensLists()      { return null; }
public String[][]   getScreensDumpsUp()    { return null; }
public String[][][] getScreensDumps()      { return null; }

public void setBinary( long[] x )          {               }
public long[] getBinary()                  { return null;  }
public boolean initBinary()                { return false; }
public boolean parseBinary()               { return false; }
}
