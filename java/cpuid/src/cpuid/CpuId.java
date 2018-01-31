/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
CPUID java application main module.
This class contains application entry point.
1) Creates registry for communications with platform resources.
2) Creates and show GUI application root menu.
3) Provides public static methods for create and get registry, note
   re-create registry required for platform re-detect, include remote platform
   access by file or network socket.
*/

/*
TODO #1

First, 
+ version v0.54 with base functionality verify and Intel, AMD verify,
+ make screen shots, binaries, reports.

version 0.56
+ class = DeviceAdapter.java
+ method = simplestText()
+ bug with limit = 100 strings per function, otherwise exception.

TODO #2

Priority function 80000007h , EBX.
Priority for WoW64 (or all native OS data) ?

1)+ Rename "Arch1" to "cpuid", with paths corrections 
   "about" picture, library loader, native Windows/Linux 32/64 libraries,
   and SystemTreeBuilder. Find "arch1" string after all changes make,
   for carefully find unchanged code parts.

2) Inspect all files at source, change (C)2017 to (C)2018, 
   add detail comments. 
   Inspect also because packages renaming.
   Inspect also because IDE/compiler warnings.

3) Update database from sandpile.org, verify data and detection code,
   make this by text reports.
   Verify all functions for some Intel and AMD binaries.
   
4) No-GUI mode, option "-c" send report to console.
5) No-GUI mode, option "-f" send report to file.
6) No-GUI mode, option "-i" input binary data.
7) No-GUI mode, option "-o" output binary data.
8) Verify at some Intel/AMD old and new machines and binaries,
   see "C:\Projects\project_cpuid\reports_and_binaries\...",
   don't reject strings length.
9) New CPUID functions and subfunctions. Include Virtual CPUID.
   Function 7 EDX. CET Indirect Branch Tracking, connect array.
   Function 0000000Dh.
   Function 00000010h subfunctions.
   Function 00000015h ECX, and frequencies calculations.
   Function 00000017h subfunctions, include native drivers modify.
   New Function 0000001Bh.
   Function 80000007h, add registers.
   Function 8000001Ch, add registers. EAX.
   Device context bit for CET.
10) Correct deterministic functions, cache under virtual machines.
11) TFMS decoding by database in the application.
12) Conditionally add WoW64 info, see PowerInfo works.
13) Convergention with benchmarks, calculator, drawings.
14) Use binary readers for UEFI/DOS.
15) Android version.
16) Support alternative third vendors, for example Transmeta.

17) Get info about new features.
ACNT2
GFNI
VAES
VPCL
TME
CR4.VA57, 5-level paging
PCONFIG for MK TME
IBRS_IBPB
STIBP
ARCH_CAPABILITIES MSR
STC software thermal control
CSB connected standby
RAPL running average power limit
WBNOINVD
VLS
VGIF
IBS op data 4 MSR

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
