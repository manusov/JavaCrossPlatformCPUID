/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
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
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
        { "x"         , "Reserved"                    } ,
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
        { "Number of physical cores - 1" ,  7 ,  0 } ,
        { "APIC ID size"                 , 15 , 12 } ,
        { "Performance TSC size"         , 17 , 16 }
    };

// Additional decoders
private final static String[] DECODER_PERF_TSC =
    { "40 bits" , "48 bits" , "56 bits" , "64 bits" , "Unknown" };

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
    writeSize( z, 0, result, p );
    writeSize( z, 1, result, p+1 );
    writeSize( z, 2, result, p+2 );
    // Parameters from CPUID dump, EBX register
    p=NY1;
    y = (int) ( array[x+2] >>> 32 );                                 // y = EBX
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX , y , result , p );
    // Parameters from CPUID dump, ECX register
    p=NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );          // y = ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    if ( z[0]>0 ) { result[p][4] = (z[0]+1) + " cores"; }
    else          { result[p][4] = "n/a"; }
    if ( z[1]>0 ) { result[p+1][4] = z[1] + "-bit"; }
    else          { result[p+1][4] = "n/a"; }
    y = z[2];
    if (y>3) { y=4; }
    result[p+2][4] = DECODER_PERF_TSC[y];
    // Result is ready, all strings filled
    return result;
    }

// Helper method,write address width and space size, string=F(number)
// INPUT:   z = source array of widths
//          i = index in the source array of widths
//          result = destination strings array
//          p = index in the destination array of strings
// OUTPUT:  none (void)
private static void writeSize( int[] z , int i, String[][] result, int p )
    {
    String s="";            // s = scratch string
    int z1 = z[i];          // z1 = address width in bits
    int z2 = 1 << (z1-30);  // z2 = space size in gigabytes
    int z3 = 1 << (z1-40);  // z3 = space size in terabytes
    if (z1==0) s = "n/a";                      // width=0 means not supported
    if ((z1>0)&&(z1<30)) { s = "Invalid"; }    // width<30 means invalid
    if ((z1>=30)&&(z1<40)) { s = z1 + "-bit, " + z2 + " GB space"; }
    if (z1>=40) { s = z1 + "-bit, " + z3 + " TB space"; }
    result[p][4] = s;  // write generated string into destination array
    }

}
