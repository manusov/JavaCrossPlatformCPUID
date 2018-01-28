//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// Kernel part: Registry of drivers.
// This component can be replaced at REMOTE/FILE alternate target systems.

// For REMOTE and FILE modes as alternative of LOCAL mode:
// this module still used, but PAL access changed to replacer PAL (Line 50),
// drivers also changed, probably with internal divergentions to:
// binary getter and texter.

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

public PAL getPAL()
    {
    return pal; 
    }

public void setPAL(PAL x)
    {
    pal = x; 
    }

public Device[] getDevices()
    {
    return devs; 
    }

public void setDevices(Device[] x)
    {
    devs = x; 
    }

public void createDriverList()
    {
    int n = CPR.values().length;
    devs = new Device[n];
    }

public abstract int loadPAL();
public abstract Device loadDriver(CPR x);
public abstract int binaryGate( long[] a, long[] b, long c, long d );
public abstract Properties getJvmProperties();
public abstract Map < String, String > getJvmEnvironment();

}
