/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Registry with platform communications methods: for local (current) platform.
*/

package cpuidrefactoring.system;

import cpuidrefactoring.deviceosinfo.DeviceOsInfo;
import cpuidrefactoring.devicejvminfo.DeviceJvmInfo;
import cpuidrefactoring.devicecpucontext.DeviceCpuContext;
import cpuidrefactoring.devicecpuclk.DeviceCpuClk;
import cpuidrefactoring.devicecpuid.DeviceCpuid;
import java.util.Map;
import java.util.Properties;

public class RegistryLocalPlatform extends Registry 
{
// Initializing Platform Abstraction Layer,
// load low-level native library for device-relevant functionality
@Override public int loadPAL()
    {
    pal = new PAL();
    loadStatus = pal.loadUserModeLibrary();
    runStatus = -1;
    if ( loadStatus == 0 )
        {
        try { runStatus = pal.checkBinary(); }
        catch ( Exception e1 )            { runStatus = -2; }
        catch ( UnsatisfiedLinkError e2 ) { runStatus = -3; }
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
            case DRIVER_CPUID:   
                devs[i] = new DeviceCpuid();
                break;
            case DRIVER_CPUCLK:
                devs[i] = new DeviceCpuClk();
                break;
            case DRIVER_CPUCTX:
                devs[i] = new DeviceCpuContext();
                break;
            case DRIVER_JVMINFO:
                devs[i] = new DeviceJvmInfo();
                break;
            case DRIVER_OSINFO:
                devs[i] = new DeviceOsInfo();
                break;
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
