/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Kernel part: Registry of drivers.
This component can be replaced at REMOTE/FILE alternate target systems.
Note.
For REMOTE and FILE modes as alternative of LOCAL mode:
this module still used, but PAL access changed to replacer PAL (Line 50),
drivers also changed, probably with internal divergentions to:
binary getter and texter.
*/

package cpuid.kernel;

import cpuid.drivers.cpr.Device;
import java.util.Map;
import java.util.Properties;

public abstract class Registry 
{
public static enum CPR  // Application-relevant drivers list enumeration
    {
    driverCPUID   ,
    driverCPUCLK  ,
    driverJVMINFO ,
    driverJVMENVR ,
    driverCPUCTX
    };

protected int loadStatus, runStatus;   // load and execution status
protected PAL pal;                     // Platform Abstraction Layer
protected Device[] devs;               // Application-relevant drivers array

public PAL getPAL()  // get variable = pointer to platform abstraction layer
    {
    return pal; 
    }

public void setPAL(PAL x)  // set variable = pointer to platform abst. layer
    {
    pal = x; 
    }

public Device[] getDevices()  // get array of device drivers
    {
    return devs; 
    }

public void setDevices(Device[] x)  // set array of device drivers
    {
    devs = x; 
    }

public void createDriverList()  // create blank list of device drivers
    {
    int n = CPR.values().length;
    devs = new Device[n];
    }

// load platform abstraction layer, 
// this functions detects OS and loads native library for current detected OS
public abstract int loadPAL();

// load selected (by x) device driver
public abstract Device loadDriver(CPR x);

// gate for call native method, receive:
// a = Input Parameters Block, IPB
// b = Output Parameters Block, OPB
// c = Length of IPB, qwords, or sub-function number if IPB=null
// d = Length of OPB, qwords
public abstract int binaryGate( long[] a, long[] b, long c, long d );

// get JVM properties
public abstract Properties getJvmProperties();

// get JVM environment
public abstract Map < String, String > getJvmEnvironment();

}
