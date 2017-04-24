//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Kernel part: Registry of drivers.
// This component can be replaced at REMOTE/FILE alternate target systems.

// For REMOTE and FILE modes as alternative of LOCAL mode:
// this module still used, but PAL access changed to replacer PAL (Line 50),
// drivers also changed, probably with internal divergentions to:
// binary getter and texter.

package arch1.kernel;

import arch1.drivers.cpr.Device;
import arch1.drivers.cpuclk.DeviceCPUCLK;
import arch1.drivers.cpucontext.DeviceCPUcontext;
import arch1.drivers.cpuid.DeviceCPUID;
import arch1.drivers.jvmcontrol.DeviceJVMcontrol;
import arch1.drivers.jvmenvironment.DeviceJVMenvironment;
import java.util.Map;
import java.util.Properties;

public class LocalRegistry extends Registry 
{

@Override public int loadPAL()
    {
    pal = new PAL();
    loadStatus = pal.loadUserModeLibrary();
    runStatus = -1;
    if (loadStatus == 0)
        {
        try { runStatus = pal.checkPAL(); }
        catch (Exception e1)            { runStatus = -2; }
        catch (UnsatisfiedLinkError e2) { runStatus = -3; }
        }
    return runStatus;
    }

@Override public Device loadDriver(CPR x)
    {
    int i = x.ordinal();
    if ( devs[i] == null )
        {
        switch(x)
            {
            case driverCPUID:   { devs[i] = new DeviceCPUID();          break; }
            case driverCPUCLK:  { devs[i] = new DeviceCPUCLK();         break; }
            case driverJVMINFO: { devs[i] = new DeviceJVMcontrol();     break; }
            case driverJVMENVR: { devs[i] = new DeviceJVMenvironment(); break; }
            case driverCPUCTX:  { devs[i] = new DeviceCPUcontext();     break; }
            // this space reserved for extensions
            // ...
            }
        }
    return devs[i];
    }

@Override public int binaryChannel( long[] a, long[] b, long c, long d )
    {
    return pal.entryPAL( a, b, c, d );
    }

@Override public Properties getJvmProperties()
    {
    return System.getProperties();
    }

@Override public Map < String, String > getJvmEnvironment()
    {
    return System.getenv();
    }


}
