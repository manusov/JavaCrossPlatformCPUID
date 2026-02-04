/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

PAL = Platform Abstraction Layer.
Class supports native platform and operating system detection, 
load OS-specific JNI (Java Native Interface) *.DLL (Windows) or *.SO (Linux).
Also contains entry points for JNI calls.

Special thanks to:
https://refactoring.guru/design-patterns/singleton/java/example#example-2
https://refactoring.guru/java-dcl-issue
about Singleton pattern.

IMPORTANT NOTE FOR JNI JAVA-ASM LINK.
Path strings at native libraries assembler sources files depends
on java source path to this file.

For example, string at DLL ASM source:
    detectBinary, 'Java_cpuidv3_pal_PAL_detectBinary'
means path with packages names to class at java source:
    CPUIDv3 \ pal \ PAL.java
method:
    detectBinary().

*/

package cpuidv3.pal;

public final class PAL
{
    public static enum PAL_STATUS 
        { SUCCESS, NOT_REQUIRED, OS_DETECT_FAILED, LIBRARY_LOAD_FAILED }

    public static enum  OS_TYPE 
        { WIN32, WIN64, LINUX32, LINUX64, UNKNOWN, NOT_USED };
    
    final static int REQUEST_GET_CPUID                       = 0;
    final static int REQUEST_GET_CPUID_SUBFUNCTION           = 1;
    final static int REQUEST_GET_CPUID_AFFINIZED             = 2;
    final static int REQUEST_GET_CPUID_SUBFUNCTION_AFFINIZED = 3;
    final static int REQUEST_GET_OS_CONTEXT                  = 4;
    final static int REQUEST_MEASURE_TSC_FREQUENCY           = 5;
    final static int REQUEST_GET_PLATFORM_INFO               = 6;
    final static int REQUEST_GET_TOPOLOGY                    = 7;
    final static int REQUEST_GET_EXTENDED_TOPOLOGY           = 8;

    native int detectBinary();
    native int initBinary();
    native int deinitBinary();
    native int requestBinary( long[] ipb, long[] opb, long nIpb, long nOpb );
        
    private final PAL_STATUS palStatus;
    public PAL_STATUS getPalStatus() { return palStatus; }
   
    private final OS_TYPE osType;
    public OS_TYPE getOsType() { return osType; }
    
    private String runtimeName;
    public String getRuntimeName() { return runtimeName; }
    
    private final HelperDetector detector;
    private final HelperCpuidReader cpuidReader;
    private final HelperOsContextReader contextReader;
    private final HelperTscMeasurer tscMeasurer;
    private final HelperPlatformReader platformReader;
    private final HelperTopologyReader topologyReader;
    
    // Thread-safe singleton pattern for PAL class.
    private static volatile PAL instance;
    public static PAL getInstance( String libPath )
    {
        PAL result = instance;
        if ( result != null )
        {
            return result;
        }
        // Some redundant operations required for thread-safe, see links above.
        synchronized( PAL.class ) 
        {
            if ( instance == null ) 
            {
                instance = new PAL( libPath );
            }
            return instance;
        }
    }

    // For singleton constructor must be private.
    private PAL( String libPath )
    {
        detector = new HelperDetector( libPath, this );
        cpuidReader = new HelperCpuidReader( this );
        contextReader = new HelperOsContextReader( this );
        tscMeasurer = new HelperTscMeasurer( this );
        platformReader = new HelperPlatformReader( this );
        topologyReader = new HelperTopologyReader( this );
        
        if( detector.platformDetect() )
        {
            if( detector.platformLoad() )
            { 
                runtimeName = detector.platformInit();
                palStatus = PAL_STATUS.SUCCESS;
                osType = detector.getPlatformType();
            }
            else
            {
                palStatus = PAL_STATUS.LIBRARY_LOAD_FAILED;
                osType = OS_TYPE.UNKNOWN;
            }
        }
        else
        {
            palStatus = PAL_STATUS.OS_DETECT_FAILED;
            osType = OS_TYPE.UNKNOWN;
        }
    }
    
    // Native interface assisted by internal helpers.
    
    public long[] getCpuid()
    {
        return cpuidReader.getCpuid(); 
    }
    
    public long[] getCpuidSubfunction( int fnc, int sfnc )
    {
        return cpuidReader.getCpuidSubfunction( fnc, sfnc );
    }

    public long[] getCpuidAffinized( int ncpu )
    {
        return cpuidReader.getCpuidAffinized( ncpu ); 
    }
    
    public long[] getCpuidSubfunctionAffinized
        ( int fnc, int sfnc, int ncpu )
    {
        return cpuidReader.getCpuidSubfunctionAffinized( fnc, sfnc, ncpu );
    }
    
    public long[] getOsContext()
    {
        return contextReader.getOsContext();
    }
    
    public long[] measureTscFrequency()
    {
        return tscMeasurer.measureTscFrequency();
    }
    
    public long[] getPlatformInfo()
    {
        return platformReader.getPlatformInfo();
    }
    
    public long[] getTopology()
    {
        return topologyReader.getTopology();
    }

    public long[] getExtendedTopology()
    {
        return topologyReader.getExtendedTopology();
    }
    
    // Method for re-initialization native platform support.
    public int resetPAL()
    {
        int result = deinitBinary();
        instance = null;
        return result;
    }
}

