/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Helper for native platform detection. Detect OS type: 
Windows ia32, Windows x64, Linux ia32, Linux x64.

*/

package cpuidv3.pal;

import cpuidv3.CPUIDv3;
import cpuidv3.pal.PAL.OS_TYPE;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

class HelperDetector 
{
    private final PAL pal;
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

    private OS_TYPE platformType = OS_TYPE.UNKNOWN;
    private final static String[] LIBRARY_NAMES = 
        { "WIN32JNI" , "WIN64JNI" , "libLINUX32JNI" , "libLINUX64JNI" };
    private final static String[] LIBRARY_EXTENSIONS = 
        { ".dll"     , ".dll"     , ".so"           , ".so"           };
    private final static int LIBRARY_COUNT = LIBRARY_NAMES.length;
    private final static int BLOCK_SIZE = 16384;
    
    OS_TYPE getPlatformType()         { return platformType;          }
    boolean getPlatformDetected()     { return platformDetected;      }
    boolean getPlatformLoaded()       { return platformLoaded;        }
    boolean getPlatformInitialized()  { return platformInitialized;   }
    int getDetectionStatus()          { return detectionStatus;       }
    int getInitStatus()               { return initStatus;            }

    HelperDetector( String s, PAL p )
    {
        pal = p;
        libPath = s;
    }
    
    // Detect operating system type.
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
        platformType = OS_TYPE.UNKNOWN; 
        if ((osType == 0)&&(cpuType == 0)) { platformType = OS_TYPE.WIN32;   }
        if ((osType == 0)&&(cpuType == 1)) { platformType = OS_TYPE.WIN64;   }
        if ((osType == 1)&&(cpuType == 0)) { platformType = OS_TYPE.LINUX32; }
        if ((osType == 1)&&(cpuType == 2)) { platformType = OS_TYPE.LINUX32; }
        if ((osType == 1)&&(cpuType == 1)) { platformType = OS_TYPE.LINUX64; }
        platformDetected = (platformType != OS_TYPE.UNKNOWN);
        return platformDetected;
    }

    // Load native library for previously detected platform.
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
                            detectionStatus = pal.detectBinary();
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

    private final static int OS_API_OK = 0x10;
    private final static int TSC_OK    = 0x8;
    private final static int FPU_OK    = 0x4;
    private final static int CPUID_OK  = 0x2;
    private final static int WOW64     = 0x1;
    
    // Call initialization handler from previously loaded native library.
    // returns native OS type string.
    String platformInit() 
    {
        platformInitialized = false;
        initStatus = pal.initBinary();
        boolean wow64 = false;
        if( ( initStatus >>> 16 ) == 0x55AA )
        {
            platformInitialized = true;
            if ( ( initStatus & WOW64 ) != 0 )
            {
                wow64 = true;
            }
        }
        
        String s1 = "", s2 = "";
        if( getPlatformDetected() && getPlatformLoaded() ) 
        {
            int width = getDetectionStatus() & 0xFF;
            int index = getPlatformType().ordinal();
            if ( ( getPlatformType() == OS_TYPE.WIN32 ) && wow64 )
            {
                index = OS_TYPE.WIN64.ordinal();
            }
            s1 = osNames[index];
            s2 = "";
            if ( width == 32 ) s2 = " JRE32";
            if ( width == 64 ) s2 = " JRE64";
        }
        return s1 + s2;
    }
}
