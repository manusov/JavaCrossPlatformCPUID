/* 
CPUID Utility. (C)2021 IC Book Labs
------------------------------------
Application main class.
*/

package cpuidrefactoring;

import cpuidrefactoring.rootmenu.RootMenu;
import cpuidrefactoring.system.Registry;
import cpuidrefactoring.system.RegistryLocalPlatform;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class CpuidRefactoring
{
private static RootMenu rootMenu;    // class for Root Menu GUI and handlers
private static Registry registry;    // abstract class for: Local, Remote, File

/*
Method create registry entries for platform components
and initialize Platform Abstraction Layer.
Return value:
 -1  = Error, native library not found
 32  = Loaded 32-bit native library under Windows or Linux
 33  = Loaded 32-bit native library under Win64 (WoW64) because 32-bit JRE
 64  = Loaded 64-bit native library under Windows or Linux
*/
public static int createRegistry()
    {
    registry = new RegistryLocalPlatform();
    registry.createDriverList();
    int loadPalStatus = registry.loadPAL();  // Platform Abstraction Layer
    return loadPalStatus;
    }
/*
Method returns Registry object for Local/Remote/File platform communication
*/
public static Registry getRegistry()
    {
    return registry;
    }
/*
Application entry point
*/
public static void main( String[] args ) 
    {
    JFrame.setDefaultLookAndFeelDecorated( true );   // Style for frame
    JDialog.setDefaultLookAndFeelDecorated( true );  // Style for errors/warns.
    int status = createRegistry();
    if ( status <= 0 )
        {                                // message when error detected
        JOptionPane.showMessageDialog    // accepts parent (null) and string
            ( null, String.format
            ( "PAL initialization failed\n Run status = %d", status ),
            About.getShortName(), JOptionPane.ERROR_MESSAGE );
        System.exit( 0 );                // exit for error handling branch
        }
    rootMenu = new RootMenu();   // Build GUI: RootMenu extends JFrame
    rootMenu.showGUI();
    }
}
