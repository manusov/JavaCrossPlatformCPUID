//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 8000001Ch declared as CPR.COMMAND.

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID8000001C extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "AMD Lightweight profiling capabilities";
    
// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static Object[][] DECODER_EBX =
    {
        { "LWP control block size" ,  7 ,  0 } ,
        { "LWP event size"         , 15 ,  8 } ,
        { "LWP maximum event ID"   , 23 , 16 } ,
        { "LWP event offset"       , 31 , 24 }
     };
private final static Object[][] DECODER_ECX =
    {
        { "LWP cache latency bit counter size" ,  4 , 0 } ,
        { "LWP data cache miss address valid"  ,  5 , 5 } ,
        { "Amount cache latency is rounded"    ,  8 , 6 } ,
        { "LWP version"                        , 15 , 9 } ,
        { "Minimum size of the LWP event ring buffer, units 32 records" , 23 , 16 } ,
        { "LWP branch prediction filtering supported" , 28 , 28 } ,
        { "LWP IP filtering supported" , 29 , 29 } ,
        { "LWP cache level filtering supported" , 30 , 30 } ,
        { "LWP cache latency filtering supported" , 31 , 31 }
    };
private final static String[][] DECODER_EDX =
    {
        { "LWP"      , "Lightweight profiling" } ,
        { "LWPVAL"   , "LWPVAL instruction available" } , 
        { "LWP IRE"  , "LWP instructions retired event available" } , 
        { "LWP BRE"  , "LWP branch retired event available" } , 
        { "LWP DME"  , "LWP DC miss event available" } , 
        { "LWP CNH"  , "LWP core clocks not halted event available" } , 
        { "LWP RNH"  , "LWP core reference clocks not halted event available" } , 
        { "x"        , "Reserved" } ,  // bit 7 reserved
        { "x"        , "Reserved" } ,  // ...
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } , 
        { "x"        , "Reserved" } ,  // ...
        { "x"        , "Reserved" } ,  // bit 28 reserved
        { "LWP Cont" , "Sampling in continuous mode" } , 
        { "LWP PTSC" , "LWP performance TSC in event record" } , 
        { "LWP INT"  , "LWP interrupt on threshold overflow available" } , 
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EBX.length + 1;
private final static int NY2 = DECODER_ECX.length + 1;
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
    int x = CPUID.findFunction( array, 0x8000001C );
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
    // Parameters from CPUID dump, EBX register
    int p=0;  // pointer for sequentally store strings in the table
    int y = (int) ( array[x+2] >>> 32 );                             // y = EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    // Parameters from CPUID dump, ECX register
    p=NY1;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );          // y = ECX
    CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    // Parameters from CPUID dump, EDX register
    p=NY1+NY2;
    y = (int) ( array[x+3] >>> 32 );                                 // y = EDX
    CPUID.decodeBitmap ( "EDX" , DECODER_EDX , y , result , p );
    // Result is ready, all strings filled
    return result;
    }

}
