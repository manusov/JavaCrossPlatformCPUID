/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID extended function 8000001Fh declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID8000001F extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "AMD Secure Encrypted Virtualization";

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
        { "SME"    , "Secure Memory Encryption" } ,
        { "SEV"    , "Secure Encrypted Virtualization" } ,
        { "PGFMSR" , "Page Flush MSR" } ,
        { "SEV-ES" , "Encrypted State" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" } ,
        { "x"      , "Reserved" }
    };
private final static Object[][] DECODER_EBX =
    {
        { "Page table bit used to enable protection"  ,   5 ,  0 } ,
        { "Reduction of physical address space"       ,  11 ,  6 }
    };
private final static Object[][] DECODER_ECX =
    {
        { "Number of encrypted guests supported simultaneously"  ,  31 ,  0 } 
    };
private final static Object[][] DECODER_EDX =
    {
        { "Minimum SEV enabled, SEV-ES disabled ASID"  ,  31 ,  0 } 
    };

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
    int x = CPUID.findFunction( array, 0x8000001F );
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
    p=NY1;
    y = (int) ( array[x+2] >>> 32 );                                 // y = EBX
    int[] z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p][4] = String.format( "bit %d", z[0] );
    if ( z[1]==0 )
        { result[p+1][4] = "no reduction"; }
    else
        { result[p+1][4] = String.format( "minus %d bits", z[1] ); }
    // Parameters from CPUID dump, ECX register
    p=NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );          // y = ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    result[p][4] = String.format( "%d guests", z[0] );
    // Parameters from CPUID dump, EDX register
    p=NY1+NY2+NY3;
    y = (int) ( array[x+3] >>> 32 );                                 // y = EDX
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX , y , result , p );
    // Result is ready, all strings filled
    return result;
    }
    
}
