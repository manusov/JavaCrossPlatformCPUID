/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID java application main module.
This class contains application entry point.
1) Creates registry for communications with platform resources.
2) Creates and show GUI application root menu.
3) Provides public static methods for create and get registry, note
   re-create registry required for platform re-detect, include remote platform
   access by file or network socket.
*/

package cpuid;

import cpuid.applications.rootmenus.RootMenu3;
import cpuid.applications.rootmenus.RootMenu;
import cpuid.kernel.LocalRegistry;
import cpuid.kernel.Registry;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CpuId 
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
        {                                   // message when error detected
        JOptionPane.showMessageDialog       // accepts parent (null) and string
            ( null, String.format
            ( "PAL initialization failed\n Run status = %d", status ),
            About.getShortName(), JOptionPane.ERROR_MESSAGE );
        System.exit(0);                     // exit for error handling branch
        }
    
    // Build GUI: RootMenu extends JFrame
    // rm = new RootMenu1();   // single CPUID screen
    // rm = new RootMenu2();   // system tree + CPUID
    rm = new RootMenu3();      // root menu + system tree + CPUID
    rm.showGUI();
    }
}
