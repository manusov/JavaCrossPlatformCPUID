/*

Debug sample for A&S (Agents and Stations) application model.
Required external binary reader or InstLatX64 text dump file.

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Application main class, contains application information,
root enumerator reference, root menu GUI runner.

*/

package dumploader;

import dumploader.cpuenum.CpuRootEnumerator;
import dumploader.gui.RootMenu;
import javax.swing.SwingUtilities;

public class DumpLoader 
{
    private final static String VERSION_NAME  = "v0.00.07";
    private final static String VENDOR_NAME_1 = "No copyright.";
    private final static String VENDOR_NAME_2 = 
            "Information belongs to Universe.";
    private final static String SHORT_NAME    = 
            "CPUID dump loader " + VERSION_NAME;
    private final static String LONG_NAME     = "Java " + SHORT_NAME;
    private final static String PROJECT_WEB   =
            "https://github.com/manusov/JavaCrossPlatformCPUID";
    private final static String ALL_WEB = 
            "https://github.com/manusov?tab=repositories";
    private final static String RESOURCE_PACKAGE = "/dumploader/resources/";
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
    
    private static CpuRootEnumerator cpuRootEnumerator = null;
    public static CpuRootEnumerator getCpuRootEnumerator()
    { 
        return cpuRootEnumerator;
    }
    public static void setCpuRootEnumerator( CpuRootEnumerator cre )
    {
        cpuRootEnumerator = cre;
    }
    
    DumpLoader()
    {
        RootMenu rootMenu = new RootMenu();
        rootMenu.showGui();
    }
    
    public static void main( String[] args ) 
    {
        SwingUtilities.invokeLater( () -> { new DumpLoader(); } );
    }
}
