//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Kernel part: Native library loader and native methods entry points,
// for Windows 32/64 use *.DLL (Dynamical Loaded Library),
// for Linux 32/64 use *.SO (Shared Object).
// Required bug fix: delete library temporary file when application exit.

// For REMOTE and FILE modes as alternative of LOCAL mode:
// this module not used (disconnected) during REMOTE/FILE modes.

package arch1.kernel;

import arch1.Arch1;
import java.io.*;
import java.net.URL;

public class PAL 
{
private final static int BLOCK_SIZE = 16384;  // This not limit max. lib. size
private static int nativeType = -1;
private boolean nativeValid = false;
private File library;

//---------- Methods for get native platform detection results -----------------
// NativeType: 0=Win32, 1=Win64, 2=Linux32, 3=Linux64, ... , -1=Unknown

protected int getNativeType() { return nativeType; }
protected boolean getNativeValid() { return nativeValid; }

//---------- Target native methods ---------------------------------------------

protected native int checkPAL();
protected native int entryPAL( long[] a, long[] b, long c, long d );
    
//---------- Method for load user mode library ---------------------------------

protected int loadUserModeLibrary()
    {
    String[] libNames      = 
        { "WIN32JNI" , "WIN64JNI" , "libLINUX32JNI" , "libLINUX64JNI" };
    String[] libExtensions = 
        { ".dll"     , ".dll"     , ".so"           , ".so"           };
    int status = 0;
    int count = 0;
    int i = 0;
    
    int n = libNames.length;
    int m = OSDetector.detectNative();
    nativeType = m;
            
    for (i=0; i<n; i++)
        {
        if ( i != m ) { status=-1; continue; }
        try {        
            status = 0;
            URL resource = Arch1.class.getResource
                ( "/arch1/resources/" + 
                        libNames[i] + libExtensions[i] );
            InputStream input = resource.openStream();
            library = File.createTempFile( libNames[i], libExtensions[i] );
            FileOutputStream output = new FileOutputStream(library);
            byte[] buffer = new byte[BLOCK_SIZE];
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
            count = 0;
            }
        }
    if ( status<0 ) { nativeValid = false;  }
    else { nativeValid = true; }
    return status;
    }

protected void unloadUserModeLibrary()
        {
        // Under construction, must unload and delete
        // System.
        // Runtime.getRuntime().
        }


}
