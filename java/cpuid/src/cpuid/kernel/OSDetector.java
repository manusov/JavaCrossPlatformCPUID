//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// Kernel part: Detector for native operating system,
// supported Windows ia32, Windows x64, Linux ia32, Linux x64.

// For REMOTE and FILE modes as alternative of LOCAL mode:
// this module not used (disconnected) during REMOTE/FILE modes.

package cpuid.kernel;

import java.util.Properties;

public class OSDetector
{
private final static String[] PATTERNS_PROP = { "os.name" , "os.arch" };
private final static String[] PATTERNS_OS   = { "win" , "linux" };
private final static String[] PATTERNS_CPU  = { "x86" , "amd64" , "i386" };
private final static int COUNT_PROP = PATTERNS_PROP.length;
private final static int COUNT_OS = PATTERNS_OS.length;
private final static int COUNT_CPU = PATTERNS_CPU.length;

// Detect platform: OS type and CPU type, result integer:
//  0 = Windows ia32
//  1 = Windows x64
//  2 = Linux ia32
//  3 = Linux x64
// -1 = Unknown

protected static int detect()
    {
    // Initializing temporary variables
    int osType = -1;
    int cpuType = -1;
    int platformType = -1; 
    Properties p = System.getProperties();
    String[] arguments = new String[COUNT_PROP];
    String s1, s2;
    int n1, n2;
    // Get system information, store it to array of strings
    for ( int i=0; i<COUNT_PROP; i++ )
        {
        s1 = p.getProperty(PATTERNS_PROP[i]);
        s1 = s1.trim();
        s1 = s1.toUpperCase();
        arguments[i] = s1;
        }
    // Detect Operating System type by recognize pattern
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
    // Final data analysing, platform = F ( os, cpu )
    // 0 = Windows ia32
    if ( ( osType==0 ) && ( cpuType==0 ) ) { platformType = 0; }
    // 1 = Windows x64
    if ( ( osType==0 ) && ( cpuType==1 ) ) { platformType = 1; }
    // 2 = Linux ia32
    if ( ( osType==1 ) && ( cpuType==0 ) ) { platformType = 2; }
    // 2 = duplicated Linux ia32
    if ( ( osType==1 ) && ( cpuType==2 ) ) { platformType = 2; }
    // 3 = Linux x64
    if ( ( osType==1 ) && ( cpuType==1 ) ) { platformType = 3; }
    return platformType;
    }
}
