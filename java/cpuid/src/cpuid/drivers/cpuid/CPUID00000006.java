/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 00000006h declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000006 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Thermal and Power Management features";
    
// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { 
        { "DTS"   , "Digital temperature sensor" } ,
        { "TB"    , "Intel Turbo Boost technology" } ,
        { "ARAT"  , "APIC timer always running" } ,
        { "x"     , "Reserved" } ,  // bit 3 reserved
        { "PLN"   , "Power limit notification controls" } ,
        { "ECMD"  , "Clock modulation duty cycle extension" } ,
        { "PTM"   , "Package thermal management" } ,
        { "HWP-B" , "Hardware ctrl. perf. states base registers" } ,
        { "HWP-N" , "HWP notification" } ,
        { "HWP-W" , "HWP activity window" } ,
        { "HWP-E" , "HWP energy performance preference" } ,
        { "HWP-P" , "HWP package level request" } ,
        { "x"     , "Reserved" } ,  // bit 12 reserved
        { "HDC"   , "Hardware duty cycling base register" } ,
        { "TB30"  , "Intel Turbo Boost Max Technology 3.0 available" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } ,
        { "x"     , "Reserved" } 
    };
private final static Object[][] DECODER_EBX =
    {
        { "Number of interrupt thresholds in digital thermal sensor" , 3 , 0 }
    }; 
private final static Object[][] DECODER_ECX =
    {
        { "Hardware coordination feedback capability, MPERF, APERF" , 0 , 0 } ,
        { "ACNT2"                                                   , 1 , 1 } ,
        { "Performance-energy bias preference, ENERGY_PERF_BIAS MSR"   , 3 , 3 }
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_ECX.length + 0;
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
    int x = CPUID.findFunction( array, 0x00000006 );
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
    int p=0;  // pointer for sequentally store strings in the table
    int y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    CPUID.decodeBitmap ( "EAX" , DECODER_EAX , y , result , p );
    // Parameters from CPUID dump, EBX register
    p = NY1;
    y = (int) ( array[x+2] >>> 32 );                                 // y = EBX
    int[] z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p][4] = String.format( "%d", z[0] );
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );          // y = ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    for ( int i=0; i<z.length; i++ ) 
        { result[p+i][4] = String.format("%d", z[i]); }
    // Result is ready, all strings filled
    return result;
    }
}