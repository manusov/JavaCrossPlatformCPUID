//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Kernel part: Registry of drivers.
// This component can be replaced at REMOTE/FILE alternate target systems.

// For REMOTE and FILE modes as alternative of LOCAL mode:
// this module still used, but PAL access changed to replacer PAL (Line 50),
// drivers also changed, probably with internal divergentions to:
// binary getter and texter.

package arch1.kernel;

import arch1.drivers.cpr.Device;
import java.util.Map;
import java.util.Properties;

public abstract class Registry 
{
public static enum CPR 
    {
    driverCPUID   ,
    driverCPUCLK  ,
    driverJVMINFO ,
    driverJVMENVR ,
    driverCPUCTX
    };

protected int loadStatus, runStatus;
protected PAL pal;
protected Device[] devs;

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
public abstract int binaryChannel( long[] a, long[] b, long c, long d );
public abstract Properties getJvmProperties();
public abstract Map < String, String > getJvmEnvironment();

}
