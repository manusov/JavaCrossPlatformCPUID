//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID java application main module.

package arch1;

import arch1.applications.rootmenus.*;
import arch1.kernel.LocalRegistry;
import arch1.kernel.Registry;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Arch1 
{
private static RootMenu rm;          // abstract class for some GUI modes
private static Registry registry;    // abstract class for: Local, Remote, File

// Method create registry entries for platform components
// and initialize Platform Abstraction Layer.
// Return value:
// -1  = Error, native library not found
// 32  = Loaded 32-bit native library under Windows or Linux
// 33  = Loaded 32-bit native library under Win64 (WoW64) because 32-bit JRE
// 64  = Loaded 64-bit native library under Windows or Linux
public static int createRegistry()
    {
    registry = new LocalRegistry();
    registry.createDriverList();
    int loadPalStatus = registry.loadPAL();  // Platform Abstraction Layer
    return loadPalStatus;
    }

// Method returns Registry object
public static Registry getRegistry()
    {
    return registry;
    }

// Application entry point
public static void main(String[] args) 
    {
    JFrame.setDefaultLookAndFeelDecorated(true);   // Style for frame
    JDialog.setDefaultLookAndFeelDecorated(true);  // Style for errors/warnings

    int status = createRegistry();
    
    if ( status <= 0 )
        {
        JOptionPane.showMessageDialog       // accepts parent (null) and string
            ( null, String.format
            ( "PAL initialization failed\n Run status = %d", status ),
            About.getShortName(), JOptionPane.ERROR_MESSAGE );
        System.exit(0);                     // exit for error handling branch
        }
    
    // rm = new RootMenu1();   // single CPUID screen
    // rm = new RootMenu2();   // system tree + CPUID
    rm = new RootMenu3();      // root menu + system tree + CPUID
    rm.showGUI();
    }
    
}
