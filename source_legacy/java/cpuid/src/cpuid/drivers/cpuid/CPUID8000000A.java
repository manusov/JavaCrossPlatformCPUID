/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID extended function 8000000Ah declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID8000000A extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "AMD Secure Virtual Machine revision and features";
    
// Parameters table up string    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    {
        { "SVM revision" , 7 , 0 } ,
        { "Hypervisor present and intercepting this bit" , 8 , 8 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "NASID: number of address space identifiers (ASID)" , 31 , 0 }
    };
private final static String[][] DECODER_EDX =
    {
        { "NP"       , "Nested paging" } ,
        { "LBR Virt" , "Last branch record virtualization" } ,
        { "SVML"     , "SVM lock" } ,
        { "NRIPS"    , "Next RIP save" } ,
        { "TSC Rate" , "TSC rate control MSR" } ,
        { "VMCB CL"  , "VMCB clean bits" } ,
        { "FLASID"   , "TLB flush selectable by ASID" } ,
        { "DASSIST"  , "Decode assists" } ,
        { "x"        , "Reserved" } ,  // bit 8 reserved
        { "SSE35D"   , "SSSE3 and SSE5A disable" } ,
        { "PAUSE FL" , "Pause intercept filter" } ,
        { "x"        , "Reserved" } ,  // bit 11 reserved
        { "PAUSE FT" , "Pause intercept filter threshold" } ,
        { "AVIC"     , "AMD advanced virtual interrupt controller" } ,
        { "x"        , "Reserved" } ,  // bit 14 reserved
        { "VVMLS"    , "Virtualized VMLOAD and VMSAVE" } ,
        { "VGIF"     , "Virtualized global interrupt flag" } ,
        { "GMET"     , "Guest mode execute trap extension" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,  // bit 24
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" }    // bit 31
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_EDX.length + 0;
private final static int NY  = NY1+NY2+NY3;

// Return CPUID this function full name
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function full name
@Override public String getCommandLongName(long[] dummy ) 
    { return F_NAME; }

// Return CPUID this function parameters table up string
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function details table up string
@Override public String[] getCommandUp1( long[] dummy )
    { return COMMAND_UP_1; }

// Build and return CPUID this function detail information table
// INPUT:   Binary array = CPUID dump data
// OUTPUT:  Array of strings = CPUID this function detail information table
@Override public String[][] getCommandText1( long[] array )
    {
    // Scan binary dump, find entry for this function
    int x = CPUID.findFunction( array, 0x8000000A );
    // Return "n/a" if this function entry not found
    if (x<0) { return NO_RESULT; }
    // Build and pre-blank result text array
    String[][] result = new String[NY][NX];  // Text formatted by control data
    for (int i=0; i<NY; i++)  // Cycle for rows
        {
        for(int j=0; j<NX; j++)  // Cycle for columns
            { 
            result[i][j]=""; 
            } 
        }
    // Parameters from CPUID dump, EAX register
    int p=0;
    int y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    int[] z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    result[p][4] = "" + z[0];
    // Parameters from CPUID dump, EBX register
    p=NY1;
    y = (int) ( array[x+2] >>> 32 );                                 // y = EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p][4] = "" + z[0];
    // Parameters from CPUID dump, EDX register
    p=NY1+NY2;
    y = (int) ( array[x+3] >>> 32 );                                 // y = EDX
    CPUID.decodeBitmap ( "EDX" , DECODER_EDX , y , result , p );
    // Result is ready, all strings filled
    return result;
    }
}
