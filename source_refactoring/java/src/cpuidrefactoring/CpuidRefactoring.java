/*
TODO. LOCAL ROADMAP.
[+] means executed.
[-] means not executed, rejected.

v 1.00.00
---------
1)  + CPUID functions and subfunctions, summary, dump and tree screens,
      modeling reactions.
2)  + After all applications screens build, unify classes:
      ApplicationController, ApplicationModel, ApplicationView.
      Select common fragments. Use inheritance: parent and child classes.
3)  + All comments. Headers, methods, difficult fragments. 
      Remove unused and locked by comments code.
4)  + Optimize (reduce) GUI window size and sub-windows sizes.
5)  + Redetect - button. Now yet exception or empty table after press.
      NOT: Transfer controller to view, 
      NOT: at controller must be method for reinitialization model.
      YES: same as Load Binary ? Method must be located at MODEL class ?
      method redetectPlatform();
6)  + ActionReport, method getShortString optimize.
      Wrong cycle usage, and too many memory used because string modification.
7)  + Tools: text report, binary load and save. Include report from root menu.
8)  + Update native layer, at this step paths only. Win32/64, Linux 32/64.
9)  + Compact form of private final static arrays for CPUID functions classes
      constant data. Minimize used strings for {{.
10) + Make final all methods can be final.
11) + Regularize private, default, protected, public.
12) + Safe remove method simplestText from Device.java.
13) + ReservedFunctionCpuid.java, string int y2 = ~( -1 << x3 ); And next string.
      required or not ? Required.
14) + IDE warnings. Now required legacy compatibility, use JDK8 mode.
15) + Inspect all classes and native libraries. Use compact forms {}.
16) + Check all strings and reports for acceptable valid text chars.
16) + Reserved XCR0 bits and CPUID function 0000000Dh results. 

v1.01.00
--------
1)  Simplify and documenting (in comments) procedure for add new application.
    Example for add new application.
2)  All must be binary-loadable, not functions dump only. 
    Or warnings at loaded state.
3)  Total checks for null objects and other exceptions.

v1.02.00
--------
1)  Correct native layer: Linux 32 JNI bug.
2)  Update native layer: CPUID subfunctions data read by irregular 
    subfunction-specific methods. Intel and AMD.
3)  Optimize strings to string builder, total verify, JVM, OS properties.
4)  Features names descriptions strings: regularize uppercase or lowercase.
5)  Comments fields column at CPUID function, carefully and regular.
6)  Brand ID decoding, TFMS decoding. Intel and AMD.
7)  Add support Oracle VirtualBox virtual functions.
8)  More detail block chart.

For all releases
----------------
1)  Check "Debug info" and "Pack" options for all projects. 
    Disable debug info and enable packing for prodction builds.


*/


/* 
CPUID Utility. (C)2020 IC Book Labs
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
