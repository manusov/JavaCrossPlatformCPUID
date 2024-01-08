/* 
CPUID Utility. Refactoring 2024. (C)2024 Manusov I.V.
------------------------------------------------------
Helper for native platform and operating system detection.
Loader for OS-specific DLL load.
Functions for JNI (Java Native Interface) calls.
*/

package cpuidv2.platforms;

import cpuidv2.CPUIDv2;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Detector 
{
private final String libPath;
public Detector(String libPath)
{
    this.libPath = libPath;
}
    
public static enum OSTYPE   // Operating systems enumeration for detection
    { WIN32, WIN64, LINUX32, LINUX64, UNKNOWN };
private final static String[] osNames  = 
    { "Win32", "Win64", "Linux32", "Linux64" };
private final static String[] PATTERNS_PROP = { "os.name" , "os.arch" };
private final static String[] PATTERNS_OS   = { "win" , "linux" };
private final static String[] PATTERNS_CPU  = { "x86" , "amd64" , "i386" };
private final static int COUNT_PROP = PATTERNS_PROP.length;
private final static int COUNT_OS = PATTERNS_OS.length;
private final static int COUNT_CPU = PATTERNS_CPU.length;
private boolean platformDetected = false;
private OSTYPE platformType = OSTYPE.UNKNOWN;
/*
Detect platform: OS type and CPU type, result integer:
 0 = Windows ia32
 1 = Windows x64
 2 = Linux ia32
 3 = Linux x64
-1 = Unknown
*/
public boolean platformDetect()
    {
    Properties p = System.getProperties();
    String[] arguments = new String[COUNT_PROP];
    String s1, s2;
    int n1, n2;
    // Get system information, store it to array of strings
    for ( int i=0; i<COUNT_PROP; i++ )
        {
        s1 = p.getProperty( PATTERNS_PROP[i] );
        s1 = s1.trim();
        s1 = s1.toUpperCase();
        arguments[i] = s1;
        }
    // Detect Operating System type by recognize pattern.
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
    // Detect CPU type by recognize pattern
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
    // 0 = Windows ia32
    if ( ( osType == 0 )&&( cpuType == 0 ) ) { platformType = OSTYPE.WIN32; }
    // 1 = Windows x64
    if ( ( osType == 0 )&&( cpuType == 1 ) ) { platformType = OSTYPE.WIN64; }
    // 2 = Linux ia32
    if ( ( osType == 1 )&&( cpuType == 0 ) ) { platformType = OSTYPE.LINUX32; }
    // 2 = duplicated Linux ia32
    if ( ( osType == 1 )&&( cpuType == 2 ) ) { platformType = OSTYPE.LINUX32; }
    // 3 = Linux x64
    if ( ( osType == 1 )&&( cpuType == 1 ) ) { platformType = OSTYPE.LINUX64; }
    platformDetected = platformType != OSTYPE.UNKNOWN;
    return platformDetected;
    }

private final static String[] LIBRARY_NAMES = 
    { "WIN32JNI" , "WIN64JNI" , "libLINUX32JNI" , "libLINUX64JNI" };
private final static String[] LIBRARY_EXTENSIONS = 
    { ".dll"     , ".dll"     , ".so"           , ".so"           };
private final static int LIBRARY_COUNT = LIBRARY_NAMES.length;
// This is one block, not limit maximum library size
private final static int BLOCK_SIZE = 16384;  
private boolean platformLoaded = false;
private int nativeWidth = 0;
private File library;

public boolean platformLoad()
{
    platformLoaded = false;
    if( platformDetected )
        {
        int index = platformType.ordinal();
        if( index < LIBRARY_COUNT )
            {
            URL resource = CPUIDv2.class.getResource
                ( libPath + LIBRARY_NAMES[index] + LIBRARY_EXTENSIONS[index] );
            if (resource != null )
                {
                int count = 0;
                try ( InputStream input = resource.openStream() )
                    {
                    library = File.createTempFile
                        ( LIBRARY_NAMES[index], LIBRARY_EXTENSIONS[index] );
                    try ( FileOutputStream output = 
                          new FileOutputStream( library ) ) 
                        {
                        byte[] buffer = new byte[BLOCK_SIZE];
                        for ( int j = input.read( buffer ); j != -1; 
                              j = input.read( buffer ) )
                            {
                            output.write( buffer, 0, j );
                            count++;
                            }
                        }
                    if ( count > 0 )
                        { 
                        System.load( library.getAbsolutePath() );
                        nativeWidth = checkBinary();
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

// Methods for get native platform detection results.
// Binaries types: Win32, Win64, Linux32, Linux64, ..., Unknown.
public OSTYPE getPlatformType()      { return platformType;     }
public boolean getPlatformDetected() { return platformDetected; }
public boolean getPlatformLoaded()   { return platformLoaded;   }
public int getNativeWidth()          { return nativeWidth;      }
// Target native methods.
public native int checkBinary();
public native int entryBinary( long[] a, long[] b, long c, long d );
// Build platform description string.
public String getString()
    {
    String s1 = "", s2 = "";
    if(getPlatformDetected() && getPlatformLoaded())
        {
        int width = getNativeWidth();
        int index;
        if (width != 33)
            {
            index = getPlatformType().ordinal();
            }
        else
            {
            index = OSTYPE.WIN64.ordinal();
            }
        s1 = osNames[index];
        s2 = "";
        if ((width == 32)||(width == 33)) s2 = " JRE32";
        if (width == 64) s2 = " JRE64";
        }
    return s1 + s2;
    }
}

    
    

