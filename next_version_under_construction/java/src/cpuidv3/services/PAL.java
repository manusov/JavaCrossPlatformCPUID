/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

PAL = Platform Abstraction Layer.
Class supports native platform and operating system detection, 
load OS-specific JNI (Java Native Interface) *.DLL (Windows) or *.SO (Linux).
Also contains entry points for JNI calls.

IMPORTANT NOTE FOR JNI JAVA-ASM LINK.
Path strings at native libraries assembler sources files depends
on java source path to this file.

For example, string at DLL ASM source:
    detectBinary, 'Java_cpuidv3_services_PAL_detectBinary'
means path with packages names to class at java source:
    CPUIDv3 \ services \ PAL.java
method:
    detectBinary().

*/

package cpuidv3.services;

import cpuidv3.CPUIDv3;
import cpuidv3.services.SAL.OSTYPE;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

class PAL
{
    private final String libPath;

    private final static String[] 
        osNames  =  { "Win32", "Win64", "Linux32", "Linux64" };
    private final static String[] PATTERNS_PROP = { "os.name" , "os.arch" };
    private final static String[] PATTERNS_OS   = { "win" , "linux" };
    private final static String[] PATTERNS_CPU  = { "x86" , "amd64" , "i386" };
    private final static int COUNT_PROP = PATTERNS_PROP.length;
    private final static int COUNT_OS = PATTERNS_OS.length;
    private final static int COUNT_CPU = PATTERNS_CPU.length;

    private boolean platformDetected = false;
    private boolean platformLoaded = false;
    private boolean platformInitialized = false;
    private int detectionStatus = -1;
    private int initStatus = -1;

    private OSTYPE platformType = OSTYPE.UNKNOWN;
    private final static String[] LIBRARY_NAMES = 
        { "WIN32JNI" , "WIN64JNI" , "libLINUX32JNI" , "libLINUX64JNI" };
    private final static String[] LIBRARY_EXTENSIONS = 
        { ".dll"     , ".dll"     , ".so"           , ".so"           };
    private final static int LIBRARY_COUNT = LIBRARY_NAMES.length;
    private final static int BLOCK_SIZE = 16384;  

    private native int detectBinary();
    private native int initBinary();
    private native int deinitBinary();
    private native int requestBinary
        ( long[] ipb, long[] opb, long nIpb, long nOpb );
    
    OSTYPE getPlatformType()          { return platformType;          }
    boolean getPlatformDetected()     { return platformDetected;      }
    boolean getPlatformLoaded()       { return platformLoaded;        }
    boolean getPlatformInitialized()  { return platformInitialized;   }
    int getDetectionStatus()          { return detectionStatus;       }
    int getInitStatus()               { return initStatus;            }

    PAL( String libPath )
    {
        this.libPath = libPath;
    }
    
    boolean platformDetect() 
    {
        Properties p = System.getProperties();
        String[] arguments = new String[COUNT_PROP];
        String s1, s2;
        int n1, n2;
        // Get system information, store it to array of strings.
        for ( int i=0; i<COUNT_PROP; i++ ) 
        {
            s1 = p.getProperty( PATTERNS_PROP[i] );
            s1 = s1.trim();
            s1 = s1.toUpperCase();
            arguments[i] = s1;
        }
        // Detect Operating System type by recognize pattern.
        // Detect platform: OS type and CPU type, result integer:
        // 0 = Windows ia32
        // 1 = Windows x64
        // 2 = Linux ia32
        // 3 = Linux x64
        // -1 = Unknown
        int osType = -1;
        for ( int i=0; i<COUNT_OS; i++ ) 
        {
            s1 = arguments[0];
            n1 = s1.length();
            s2 = PATTERNS_OS[i];
            s2 = s2.trim();
            s2 = s2.toUpperCase();
            n2 = s2.length();
            if ( n1 >= n2 ) 
            {
                s1 = s1.substring( 0, n2 );
                if ( s1.equals(s2) ) { osType = i; }
            }
        }
        // Detect CPU type by recognize pattern.
        // 0 or 2 = x86 (32-bit)
        // 1 = x64 (64-bit)
        // -1 = Unknown
        int cpuType = -1;
        for ( int i=0; i<COUNT_CPU; i++ ) 
        {
            s1 = arguments[1];
            n1 = s1.length();
            s2 = PATTERNS_CPU[i];
            s2 = s2.trim();
            s2 = s2.toUpperCase();
            n2 = s2.length();
            if ( n1 >= n2 ) 
            {
                s1 = s1.substring( 0, n2 );
                if ( s1.equals(s2) ) { cpuType = i; }
            }
        }
        // Final data analysing, platform = F ( os, cpu ), 
        // returns OSTYPE.UNKNOWN if not recognized.
        platformType = OSTYPE.UNKNOWN; 
        if ((osType == 0)&&(cpuType == 0)) { platformType = OSTYPE.WIN32;   }
        if ((osType == 0)&&(cpuType == 1)) { platformType = OSTYPE.WIN64;   }
        if ((osType == 1)&&(cpuType == 0)) { platformType = OSTYPE.LINUX32; }
        if ((osType == 1)&&(cpuType == 2)) { platformType = OSTYPE.LINUX32; }
        if ((osType == 1)&&(cpuType == 1)) { platformType = OSTYPE.LINUX64; }
        platformDetected = (platformType != OSTYPE.UNKNOWN);
        return platformDetected;
    }

    boolean platformLoad() 
    {
        platformLoaded = false;
        if( platformDetected ) 
        {
            int index = platformType.ordinal();
            if( index < LIBRARY_COUNT ) 
            {
                URL resource = CPUIDv3.class.getResource( libPath + 
                        LIBRARY_NAMES[index] + LIBRARY_EXTENSIONS[index] );
                if (resource != null ) 
                {
                    File library;
                    int count = 0;
                    try (InputStream input = resource.openStream()) 
                    {
                        library = File.createTempFile
                            (LIBRARY_NAMES[index], LIBRARY_EXTENSIONS[index]);
                        try (FileOutputStream output = 
                            new FileOutputStream(library)) 
                        {
                            byte[] buffer = new byte[BLOCK_SIZE];
                            for (int j = input.read(buffer); j != -1; 
                                j = input.read(buffer) ) 
                            {
                                output.write(buffer, 0, j);
                                count++;
                            } 
                        }
                        if (count > 0) 
                        {
                            System.load(library.getAbsolutePath());
                            detectionStatus = detectBinary();
                            platformLoaded = true;
                        }
                    } 
                    catch ( IOException | UnsatisfiedLinkError e ) 
                    {
                        platformLoaded = false;
                    }
                }
            }
        }
        return platformLoaded;
    }

    final static int OS_API_OK = 0x10;
    final static int TSC_OK    = 0x8;
    final static int FPU_OK    = 0x4;
    final static int CPUID_OK  = 0x2;
    final static int WOW64     = 0x1;
    
    String platformInit() 
    {
        platformInitialized = false;
        initStatus = initBinary();
        boolean wow64 = false;
        if((initStatus >>> 16) == 0x55AA)
        {
            platformInitialized = true;
            if ((initStatus & WOW64) != 0)
            {
                wow64 = true;
            }
        }
        
        String s1 = "", s2 = "";
        if(getPlatformDetected() && getPlatformLoaded()) 
        {
            int width = getDetectionStatus() & 0xFF;
            int index = getPlatformType().ordinal();
            if ( (getPlatformType() == OSTYPE.WIN32) && wow64 )
            {
                index = OSTYPE.WIN64.ordinal();
            }
            s1 = osNames[index];
            s2 = "";
            if (width == 32) s2 = " JRE32";
            if (width == 64) s2 = " JRE64";
        }
        return s1 + s2;
    }
    
    int platformDeinit()
    {
        return deinitBinary();
    }
    
    final static int REQUEST_GET_CPUID                       = 0;
    final static int REQUEST_GET_CPUID_SUBFUNCTION           = 1;
    final static int REQUEST_GET_CPUID_AFFINIZED             = 2;
    final static int REQUEST_GET_CPUID_SUBFUNCTION_AFFINIZED = 3;
    final static int REQUEST_GET_OS_CONTEXT                  = 4;
    final static int REQUEST_MEASURE_TSC_FREQUENCY           = 5;
    final static int REQUEST_GET_PLATFORM_INFO               = 6;
    final static int REQUEST_GET_TOPOLOGY                    = 7;
    final static int REQUEST_GET_EXTENDED_TOPOLOGY           = 8;

    int platformRequest(long[] ipb, long[] opb, long nIpb, long nOpb)
    {
        return requestBinary(ipb, opb, nIpb, nOpb);
    }
}
