/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID extended function 80000007h declared as CPR.COMMAND.
TODO: add EAX monitors, EBX = RAS capabilities, 
ECX = Processor power monitoring.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID80000007 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Advanced Power Management information";
    
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
        { "Maximum wrap time, ms"               , 31 , 16 } ,
        { "Version"                             , 15 , 8  } ,
        { "Number of monitors MSR C001_008[01]" , 7 , 0 }
    };
private final static String[][] DECODER_EBX =
    {
        { "MCAOVR"   , "MCA overflow recovery" } ,
        { "SUCCOR"   , "Software uncorrectable error containment and recovery" } ,
        { "HWA"      , "Hardware assert MSR C001_10[DF...C0]h" } ,
        { "SCMCA"    , "Scalable MCA" } ,
        { "x"        , "Reserved" } 
/*
        fix v0.64 bug
        
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "Conn SB"  , "Connected standby" } ,  // 13
        { "RAPL"     , "Running Average Power Limit" } ,  // 14
        { "x"        , "Reserved" }   // bit 15
*/
    };
private final static Object[][] DECODER_ECX =
    {
        { "Ratio of power accumulator sample period to GTSC" , 31 , 0 }
    };
private final static String[][] DECODER_EDX =
    {
        { "TS"        , "Temperature sensor" } ,
        { "FID"       , "Frequency ID control" } ,
        { "VID"       , "Voltage ID control"   } ,
        { "TTP"       , "THERMTRIP"            } ,
        { "TM"        , "Hardware thermal control" } ,
        { "STC"       , "Software thermal control" } ,
        { "100 MHz"   , "100 MHz steps for multiplier control" } ,
        { "HwPstate"  , "Hardware P-state control" } ,
        { "INV TSC"   , "TSC invariant for P-States and C-States" } ,
        { "CPB"       , "Core performance boost" } ,
        { "EffFreqRO" , "Read only effective frequency interface" } ,
        { "PFI"       , "Processor feedback interface (deprecated)" } ,
        { "PPR"       , "Processor core power reporting interface" } ,
        { "CSB"       , "Connected standby" } ,
        { "RAPL"      , "Running average power limit" }
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
// private final static int NY1 = DECODER_EDX.length + 0;
// private final static int NY  = NY1;
private final static int NY1 = DECODER_EDX.length + 1;
private final static int NY2 = DECODER_EAX.length + 1;
private final static int NY3 = DECODER_EBX.length + 1;
private final static int NY4 = DECODER_ECX.length + 0;
private final static int NY  = NY1+NY2+NY3+NY4;
// add EAX, EBX, ECX

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
    int x = CPUID.findFunction( array, 0x80000007 );
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
    // Parameters from CPUID dump, EDX register
    int p = 0;  // pointer for sequentally store strings in the table
    int y = (int) ( array[x+3] >>> 32 );                        // y = EDX
    CPUID.decodeBitmap( "EDX", DECODER_EDX, y , result , p );
    // Parameters from CPUID dump, EAX register
    p=NY1;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    // Parameters from CPUID dump, EBX register
    p=NY1+NY2;
    y = (int) ( array[x+2] >>> 32 );                             // y = EBX
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX , y , result , p );
    // Parameters from CPUID dump, ECX register
    p=NY1+NY2+NY3;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );      // y = ECX
    CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    // Result is ready, all strings filled
    return result;
    }
    
}
