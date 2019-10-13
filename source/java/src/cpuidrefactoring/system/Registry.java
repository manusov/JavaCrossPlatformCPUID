/* 
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Parent class for registry with platform communications methods.
*/

package cpuidrefactoring.system;

import java.util.Map;
import java.util.Properties;

public abstract class Registry 
{
public static enum CPR  // Application-relevant drivers list enumeration
    {
    DRIVER_CPUID   ,
    DRIVER_CPUCLK  ,
    DRIVER_CPUCTX  ,
    DRIVER_JVMINFO ,
    DRIVER_OSINFO
    };

int loadStatus, runStatus;   // load and execution status
PAL pal;                     // Platform Abstraction Layer
Device[] devs;               // Application-relevant drivers array

public PAL getPAL()  // get variable = pointer to platform abstraction layer
    {
    return pal; 
    }

public void setPAL( PAL x )  // set variable = pointer to platform abst. layer
    {
    pal = x; 
    }

public Device[] getDevices()  // get array of device drivers
    {
    return devs; 
    }

public void setDevices( Device[] x )  // set array of device drivers
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
public abstract Device loadDriver( CPR x );
/*
gate for call native method, receive:
a = Input Parameters Block, IPB
b = Output Parameters Block, OPB
c = Length of IPB, qwords, or sub-function number if IPB=null
d = Length of OPB, qwords
*/
public abstract int binaryGate( long[] a, long[] b, long c, long d );

// get JVM properties
public abstract Properties getJvmProperties();

// get JVM environment
public abstract Map < String, String > getJvmEnvironment();

}
