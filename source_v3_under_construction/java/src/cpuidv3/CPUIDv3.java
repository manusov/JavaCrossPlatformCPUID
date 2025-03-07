/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Java CPUID application main class. Contains application description text
strings and web links. Interconnects functions secnarios classes, service
functions classes and application root menu class.
After start, detects native platform ( Win32/Win64/Linux32/Linux64 ),
initializes service classes, and runs application root menu.

*/

package cpuidv3;

import cpuidv3.gui.RootMenu;
import cpuidv3.pal.PAL.PAL_STATUS;
import cpuidv3.sal.SAL;
import cpuidv3.sal.SALDL;
import cpuidv3.sal.SALHW;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class CPUIDv3 
{
    private final static boolean BUILD_DECORATED = false;
    private final static boolean BUILD_LOADER = false;
    
    private final static String VERSION_NAME  = "v3.03.15";
    private final static String VENDOR_NAME_1 = "No copyright.";
    private final static String VENDOR_NAME_2 = 
        "Information belongs to Universe.";
    private final static String SHORT_NAME    = "CPUID " + VERSION_NAME;
    private final static String LONG_NAME     = "Java " + SHORT_NAME;
    private final static String PROJECT_WEB   =
        "https://github.com/manusov/JavaCrossPlatformCPUID";
    private final static String ALL_WEB = 
        "https://github.com/manusov?tab=repositories";
    private final static String RESOURCE_PACKAGE = "/cpuidv3/appresources/";
    private final static String APP_LOGO = RESOURCE_PACKAGE + "books.png";

    public static String getResourcePackage() { return RESOURCE_PACKAGE; }
    public static String getVersionName() { return VERSION_NAME;   }
    public static String getVendorName1() { return VENDOR_NAME_1;  }
    public static String getVendorName2() { return VENDOR_NAME_2;  }
    public static String getShortName()   { return SHORT_NAME;     }
    public static String getLongName()    { return LONG_NAME;      }
    public static String getProjectWeb()  { return PROJECT_WEB;    }
    public static String getAllWeb()      { return ALL_WEB;        }
    public static String getAppLogo()     { return APP_LOGO;       }

    private final static String  CONSOLE_KEY = "console";
    private static boolean consoleMode = false;

    private final static String MSG_SAL_OK = 
        "Service Abstraction Layer initialized OK.";
    private final static String MSG_INIT_FAILED = 
        "Service Abstraction Layer internal error.";
    private final static String MSG_LOAD_FAILED = 
        "Loading native library failed.";
    private final static String MSG_UNKNOWN_OS = 
        "Unknown operating system.";
    
    CPUIDv3( SAL sal )
    {
        if ( BUILD_DECORATED )
        {
            JFrame.setDefaultLookAndFeelDecorated( true );
            JDialog.setDefaultLookAndFeelDecorated( true );
        }
        if ( BUILD_LOADER )
        {
            JOptionPane.showMessageDialog( null, 
                getLongName() + ".\r\n" + 
                "This engineering sample builded as Dump Loader only.\r\n" +
                "Full version must be compiled with BUILD_LOADER = false." ,
                getShortName(), JOptionPane.WARNING_MESSAGE );
        }
        RootMenu rootMenu = new RootMenu( sal );
        rootMenu.showGui();
    }

    public static void main(String[] args) 
    {
        if ( ( args != null )&&( args.length > 0 )&&( args[0] != null )&&
             ( args[0].equals( CONSOLE_KEY ) ) )
        {       
            consoleMode = true;
        }
        
        SAL sal;
        if( BUILD_LOADER )
        {   // Use Service Abstraction Layer (SAL) for Dump Loader (DL) build.
            sal = SALDL.getInstance();
        }
        else
        {   // Use Service Abstraction Layer (SAL) for Hardware (HW) build.
            sal = SALHW.getInstance( getResourcePackage() );            
        }
        
        PAL_STATUS palStatus = sal.getPalStatus();
        String statusName = MSG_INIT_FAILED;
        if ( palStatus != null )
        {
            switch ( palStatus )
            {
                case SUCCESS:
                case NOT_REQUIRED:
                    statusName = MSG_SAL_OK;
                    break;
                case OS_DETECT_FAILED:
                    statusName = MSG_UNKNOWN_OS;
                    break;
                case LIBRARY_LOAD_FAILED:
                    statusName = MSG_LOAD_FAILED;
                    break;
                default:
             }
        }
        
        if( consoleMode && ( palStatus == PAL_STATUS.SUCCESS ) )
        {   // Console mode, successfully start.
            String runtimeName = sal.getRuntimeName();
            if( ( runtimeName != null )&&( !runtimeName.equals( "" ) ) )
            {
                System.out.println( "\r\n[ " + getLongName() + ". ][ "
                        + runtimeName + ". ]"   );
            }
            else
            {
                System.out.println( "\r\n[ " + getLongName() + ". ]" );
            }
            System.out.println( "[ " + statusName + " ]" );
            System.out.println( getProjectWeb() );
            System.out.println( getAllWeb() );
            System.out.println
                ( getVendorName1() + " " + getVendorName2() + "\r\n" );
            sal.consoleSummary();
        }
        else if ( consoleMode && ( palStatus != PAL_STATUS.SUCCESS ) )
        {   // Console mode, start failed.
            System.out.println( getShortName() + ": " + statusName );
        }
        else if ( !consoleMode && 
                ( ( palStatus == PAL_STATUS.SUCCESS )||
                (   palStatus == PAL_STATUS.NOT_REQUIRED ) ) )
        {   // GUI mode, successfully start.
            SwingUtilities.invokeLater( () -> { new CPUIDv3( sal ); } );
        }
        else
        {   // GUI mode, start failed.
            JOptionPane.showMessageDialog( null, statusName,
                getShortName(), JOptionPane.ERROR_MESSAGE );
        }
    }
}
