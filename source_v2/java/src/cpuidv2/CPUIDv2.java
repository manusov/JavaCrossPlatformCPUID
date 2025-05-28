/* 
Java cross-platform CPUID Utility.
This source (Java CPUID v2.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source_v2
Previous source (Java CPUID v1.xx.xx) repository: 
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master/source
All repositories: 
https://github.com/manusov?tab=repositories
(C) Manusov I.V. Refactoring at 2025.
-------------------------------------------------------------------------------
Java CPUID application main class. Contains application vendor, version,
name text strings and web links. Interconnects functions secnarios classes,
service functions classes and application root menu class.
After start, detects native platform (Win32/Win64/Linux32/Linux64), initializes
service classes, and runs application root menu.
*/

package cpuidv2;

import javax.swing.JOptionPane;
import cpuidv2.gui.RootMenu;
import cpuidv2.platforms.Detector;
import cpuidv2.services.*;
import cpuidv2.applications.ApplicationsManager;

public class CPUIDv2 
{
private final static String VERSION_NAME = "v2.00.14";
private final static String VENDOR_NAME  = "(C)2025 Manusov I.V.";
private final static String SHORT_NAME   = "CPUID " + VERSION_NAME;
private final static String LONG_NAME    = "Java " + SHORT_NAME;
private final static String PROJECT_WEB =
        "https://github.com/manusov/JavaCrossPlatformCPUID";
private final static String ALL_WEB = 
        "https://github.com/manusov?tab=repositories";
private final static String RESOURCE_PACKAGE = "/cpuidv2/resources/";
public static String getResourcePackage() { return RESOURCE_PACKAGE; }
public static String getVersionName() { return VERSION_NAME; }
public static String getVendorName()  { return VENDOR_NAME;  }
public static String getShortName()   { return SHORT_NAME;   }
public static String getLongName()    { return LONG_NAME;    }
public static String getProjectWeb()  { return PROJECT_WEB;  }
public static String getAllWeb()      { return ALL_WEB;      }
public static String getAppLogo()     { return RESOURCE_PACKAGE + "books.png"; }

private static Detector detector;
public static Detector getDetector()  { return detector; }

private static ApplicationsManager applicationsManager;
public static ApplicationsManager getApplicationsManager() 
    { return applicationsManager; }

private static ServiceCpuid serviceCpuid;
private static ServiceClocks serviceClocks;
private static ServiceContext serviceContext;
private static ServiceOsInfo serviceOsInfo;
private static ServiceJvmInfo serviceJvmInfo;
public static ServiceCpuid getServiceCpuid()      { return serviceCpuid;   }
public static ServiceClocks getServiceClocks()    { return serviceClocks;  }
public static ServiceContext getServiceContext()  { return serviceContext; }
public static ServiceOsInfo getServiceOsInfo()    { return serviceOsInfo;  }
public static ServiceJvmInfo getServiceJvmInfo()  { return serviceJvmInfo; }

public static void main(String[] args) 
    {
    detector = new Detector( getResourcePackage() );
    if( detector.platformDetect() )
        {
        if( detector.platformLoad() )
            { 
            applicationsManager = new ApplicationsManager();
            serviceCpuid        = new ServiceCpuid();
            serviceClocks       = new ServiceClocks();
            serviceContext      = new ServiceContext();
            serviceOsInfo       = new ServiceOsInfo();
            serviceJvmInfo      = new ServiceJvmInfo();
            // Build GUI: RootMenu extends JFrame
            RootMenu rootMenu = new RootMenu();
            rootMenu.showGUI();
            }
        else
            {
            JOptionPane.showMessageDialog
                ( null, "Loading native library failed.", 
                  getShortName(), JOptionPane.ERROR_MESSAGE );
            System.exit( 0 );
            }
        }
    else
        {
        JOptionPane.showMessageDialog
            ( null, "Unknown operating system.", 
              getShortName(), JOptionPane.ERROR_MESSAGE );
        System.exit( 0 );
        }
    }
}
