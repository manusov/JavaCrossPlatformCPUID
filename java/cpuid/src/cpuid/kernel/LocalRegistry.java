/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Kernel part: Registry of drivers.
This component can be replaced at REMOTE/FILE alternate target systems.
Note 1.
For REMOTE and FILE modes as alternative of LOCAL mode:
this module still used, but PAL access changed to replacer PAL (Line 50),
drivers also changed, probably with internal divergentions to:
binary getter and texter.
Note 2.
This class constructor called from main class.
*/

package cpuid.kernel;

import cpuid.drivers.cpr.Device;
import cpuid.drivers.cpuclk.DeviceCPUCLK;
import cpuid.drivers.cpucontext.DeviceCPUcontext;
import cpuid.drivers.cpuid.DeviceCPUID;
import cpuid.drivers.jvmcontrol.DeviceJVMcontrol;
import cpuid.drivers.jvmenvironment.DeviceJVMenvironment;
import java.util.Map;
import java.util.Properties;

public class LocalRegistry extends Registry 
{

// Initializing Platform Abstraction Layer,
// load low-level native library for device-relevant functionality
@Override public int loadPAL()
    {
    pal = new PAL();
    loadStatus = pal.loadUserModeLibrary();
    runStatus = -1;
    if (loadStatus == 0)
        {
        try { runStatus = pal.checkBinary(); }
        catch (Exception e1)            { runStatus = -2; }
        catch (UnsatisfiedLinkError e2) { runStatus = -3; }
        }
    return runStatus;
    }

// Load high-level java driver for application-relevant functionality
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

// Local registry provide API to current platform-compliant native library
@Override public int binaryGate( long[] a, long[] b, long c, long d )
    {
    return pal.entryBinary( a, b, c, d );
    }

// Local registry get JVM properties from current platform
@Override public Properties getJvmProperties()
    {
    return System.getProperties();
    }

// Local registry get system environment from current platform
@Override public Map < String, String > getJvmEnvironment()
    {
    return System.getenv();
    }

}
