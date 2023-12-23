/* 
CPUID Utility. (C)2023 IC Book Labs
------------------------------------
Vendor, Product and Version description strings, 
pictogram resource reference, GUI geometry data.
*/

package cpuidrefactoring;

public class About 
{
private final static String VERSION_NAME = "v1.05.29";

private final static String VENDOR_NAME  = "(C)2023 IC Book Labs";
private final static String SHORT_NAME   = "CPUID " + VERSION_NAME;
private final static String LONG_NAME    = "Java " + SHORT_NAME;
private final static String WEB_SITE     = "https://github.com/manusov";

private final static String VENDOR_ICON  = 
        "/cpuidrefactoring/resources/icbook.jpg";

private final static int X1_SIZE = 914, Y1_SIZE = 658;  // external GUI box
private final static int X2_SIZE = 730, Y2_SIZE = 530;  // internal GUI box

public static String getVersionName() { return VERSION_NAME; }
public static String getVendorName()  { return VENDOR_NAME;  }
public static String getShortName()   { return SHORT_NAME;   }
public static String getLongName()    { return LONG_NAME;    }
public static String getWebSite()     { return WEB_SITE;     }
public static String getVendorIcon()  { return VENDOR_ICON;  }

public static int getX1size()         { return X1_SIZE;      }
public static int getY1size()         { return Y1_SIZE;      }
public static int getX2size()         { return X2_SIZE;      }
public static int getY2size()         { return Y2_SIZE;      }
}
