/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
Kernel part: 
Platform Abstraction Layer for communications with native objects.
TODO: REMOVE CYCLE AND MAKE DELETE DLL AFTER APPLICATION EXIT ?
TODO: IMPROVE ERROR REPORTING, EXCEPTION E.
TODO: DELETE UNPACKED LIBRARY WHEN APPLICATION EXIT, "UNLOAD" REQUIRED.
*/

package cpuid.kernel;

import cpuid.CpuId;
import java.io.*;
import java.net.URL;

public class PAL
{
private final static String[] LIBRARY_NAMES = 
    { "WIN32JNI" , "WIN64JNI" , "libLINUX32JNI" , "libLINUX64JNI" };
private final static String[] LIBRARY_EXTENSIONS = 
    { ".dll"     , ".dll"     , ".so"           , ".so"           };
private final static int LIBRARY_COUNT = LIBRARY_NAMES.length;

// This is one block, not limit maximum library size
private final static int BINARY_SIZE = 16384;  
private static int binaryType = -1;
private static boolean binaryValid = false;
private static File library;

// Methods for get native platform detection results
// Binaries types: 0=Win32, 1=Win64, 2=Linux32, 3=Linux64, ... , -1=Unknown
protected int getBinaryType() { return binaryType; }
protected boolean getBinaryValid() { return binaryValid; }

// Target native methods
protected native int checkBinary();
protected native int entryBinary( long[] a, long[] b, long c, long d );

protected int loadUserModeLibrary()
    {
    // Initializing variables
    int status = 0;
    int count = 0;
    int i = 0;
    binaryType = OSDetector.detect();
    // Load library, cycle for find binary type match
    for (i=0; i<LIBRARY_COUNT; i++)
        {
        if ( i != binaryType ) { status=-1; continue; }
        try {        
            status = 0;
            URL resource = CpuId.class.getResource
                ( "/cpuid/resources/" + 
                LIBRARY_NAMES[i] + LIBRARY_EXTENSIONS[i] );
            InputStream input = resource.openStream();
            library = File.createTempFile
                ( LIBRARY_NAMES[i], LIBRARY_EXTENSIONS[i] );
            FileOutputStream output = new FileOutputStream(library);
            byte[] buffer = new byte[BINARY_SIZE];
            count = 0;
            for ( int j=input.read(buffer); j!=-1; j=input.read(buffer) )
                {
                output.write(buffer,0,j);
                count++;
                }
            output.close();
            input.close();
            if ( count>0   ) { System.load(library.getAbsolutePath()); }
            if ( status==0 ) { break; }
            }
        catch (Throwable e)
            {
            status = -1;
            }
        }
    binaryValid = status >= 0;
    return status;
    }
}
