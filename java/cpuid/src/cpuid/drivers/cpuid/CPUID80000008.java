/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID extended function 80000008h declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID80000008 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Address size and physical core information";
    
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
        { "Physical address size"       ,  7 , 0 } ,
        { "Linear address size"         , 15 , 8 } ,
        { "Guest physical address size" , 23 , 16 }
    };

private final static String[][] DECODER_EBX =
    {
        { "CLZERO"    , "CLZERO instruction"          } ,
        { "IRC"       , "Instruction Retired Counter" } ,
        { "EPZR"      , "Error Pointer Zero-Restore"  } ,
        { "x"         , "Reserved"                    } ,              // bit 3
        { "RDPRU"     , "Read processor privileged registers at user mode" } ,
        { "x"         , "Reserved"                    } ,              // bit 5
        { "BE"        , "AMD Bandwidth Enforcement"   } ,
        { "x"         , "Reserved"                    } ,
        { "MCOMMIT"   , "MCOMMIT instruction"         } ,              // bit 8
        { "WBNOINVD"  , "Writeback with no invalidation" } ,
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "IBPB"      , "Indirect branch prediction barrier" } ,       // bit 12
        { "x"         , "Reserved"                    } ,
        { "IBRS"      , "Indirect branch restricted speculation"   } , // bit 14
        { "STIBP"     , "Single thread indirect branch predictor"  } , // bit 15
        { "IBRS AON"  , "Indirect branch restricted speculation always on"  } ,
        { "STIBP AON" , "Single thread indirect branch predictor always on" } ,
        { "IBRS PREF" , "Indirect branch restricted speculation is preferred" } ,
        { "x"         , "Reserved"                    } ,  // bit 19
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "SSBD"      , "Speculative Store Bypass Disable" } ,  // 24
        { "SSBD KL"   , "Speculative Store Bypass Disable, keep loads" } ,  // 25
        { "SSBD NO"   , "SSBD no longer need" } ,  // 26
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    }    // bit 31
    };

private final static Object[][] DECODER_ECX =
    {
//      { "Number of physical cores - 1"        ,  7 ,  0 } ,
        { "Number of threads per processor - 1" ,  7 ,  0 } ,
        { "APIC ID size"                        , 15 , 12 } ,
        { "Performance TSC size"                , 17 , 16 }
    };

private final static Object[][] DECODER_EDX =
    {
        { "RDPRU maximum register number" ,  31 ,  16 } ,
    };

// Additional decoders
private final static String[] DECODER_PERF_TSC =
    { "40 bits" , "48 bits" , "56 bits" , "64 bits" , "Unknown" };

private final static String[] DECODER_RDPRU =
    { "MPERF" , "APERF" , "Unknown" };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_ECX.length + 1;
private final static int NY4 = DECODER_EDX.length + 0;
private final static int NY  = NY1+NY2+NY3+NY4;

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
    int x = CPUID.findFunction( array, 0x80000008 );
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
    int[] z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    CPUID.writeSize( z, 0, result, p );
    CPUID.writeSize( z, 1, result, p+1 );
    CPUID.writeSize( z, 2, result, p+2 );
    // Parameters from CPUID dump, EBX register
    p=NY1;
    y = (int) ( array[x+2] >>> 32 );                                 // y = EBX
    boolean rdpruFlag = ( y & 0x10 ) > 0;
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX , y , result , p );
    // Parameters from CPUID dump, ECX register
    p=NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );          // y = ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
//  if ( z[0]>0 ) { result[p][4] = (z[0]+1) + " cores"; }
    if ( z[0]>0 ) { result[p][4] = (z[0]+1) + " threads"; }
    else          { result[p][4] = "n/a"; }
    if ( z[1]>0 ) { result[p+1][4] = z[1] + "-bit"; }
    else          { result[p+1][4] = "n/a"; }
    y = z[2];
    if (y>3) { y=4; }
    result[p+2][4] = DECODER_PERF_TSC[y];
    // Parameters from CPUID dump, EDX register
    p=NY1+NY2+NY3;
    y = (int) ( array[x+3] >>> 32 );                                 // y = EDX
    z = CPUID.decodeBitfields ( "EDX" , DECODER_EDX , y , result , p );
    y = z[0];
    if (y>1) { y=2; }
    if   (rdpruFlag) { result[p][4] = DECODER_RDPRU[y]; }
    else             { result[p][4] = "n/a"; }
    // Result is ready, all strings filled
    return result;
    }
}
