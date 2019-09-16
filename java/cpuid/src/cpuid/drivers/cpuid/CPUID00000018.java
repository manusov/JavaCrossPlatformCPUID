/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 00000016h declared as CPR.COMMAND.
THIS MODULE IS EXPERIMENTAL, NOT VERIFIED, NOT CONNECTED YET.
BETTER MAKE TABLE AS FUNCTION 4 ?
make visual maximum sub-leaf functions, see other functions with
same subleaf declare mechanism.
See function 0000000Fh for correct trim.
TODO: add subfunctions.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000018 extends CommandAdapter 
{
// CPUID function full name
private static final String F_NAME =
        "Deterministic address translation parameters";

// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Return if invalid data detected
private final static String[][] BAD_RESULT =
    {
        { "Invalid data" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    {
        { "Maximum sub-leaf index" , 31 , 0 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "4KB page size entries supported by this structure" ,  0 , 0  } ,
        { "2MB page size entries supported by this structure" ,  1 , 1  } ,
        { "4MB page size entries supported by this structure" ,  2 , 2  } ,
        { "1GB page size entries supported by this structure" ,  3 , 3  } ,
        { "Reserved"                                          ,  7 , 4  } ,
        { "Partitioning"                                      , 10 , 8  } ,
        { "Reserved"                                          , 15 , 11 } ,
        { "Ways of associativity"                             , 31 , 16 }
    };
private final static Object[][] DECODER_ECX =
    {
        { "Number of sets" , 31 , 0 }
    };
private final static Object[][] DECODER_EDX =
    {
        { "Translation cache type"                             ,  4 ,  0 } ,
        { "Translation cache level"                            ,  7 ,  5 } ,
        { "Fully associative structure"                        ,  8 ,  8 } ,
        { "Reserved"                                           , 13 ,  9 } ,
        { "Maximum logical CPU sharing this translation cache" , 25 , 14 } ,
        { "Reserved"                                           , 31 , 26 }
    };

// Additional data for parameters decode
private final static String[] DECODER_TYPE =
    { "Unknown" , "Data TLB" , "Instruction TLB" , "Unified TLB"  };

// Calculate control data total size for output formatting
private final static int NX   = COMMAND_UP_1.length;
private final static int NY1  = 0; // DECODER_EAX.length + 0;
private final static int NY2  = DECODER_EDX.length + 0;
private final static int NY3  = DECODER_ECX.length + 0;
private final static int NY4  = DECODER_EBX.length + 1;
private final static int NY  = NY1 + NY2 + NY3 + NY4 + 0;

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
    int x1 = CPUID.findFunction( array, 0x00000018 );
    // Return "n/a" if this function entry not found
    if (x1<0) { return NO_RESULT; }
    // Get maximum subfunction number, EAX after subfunction 0
    int subMaximum = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );  
    // Return "Invalid data" if maximum subfunction number is wrong
    if ((subMaximum<=0)|(subMaximum>20)) { return BAD_RESULT; }
    // Build and pre-blank result text array
    int dynamicNy = NY * (subMaximum+1);
      // Text formatted by control data
    String[][] result = new String[dynamicNy][NX];
    // Cycle for pre-blank text table
    for (int i=0; i<dynamicNy; i++)  // Cycle for rows 
        { 
        for(int j=0; j<NX; j++)  // Cycle for columns
            { 
            result[i][j]=""; 
            } 
        }
    
    // Cycle for sub-functions
    int p=0;  // p = current result position at entire result array
    for(int i=0; i<=subMaximum; i++)
        {
        // Get function number
        int y = (int) ( array[x1+0] & (((long)((long)(-1)>>>32))) );
        // Break if end of this function entries
        if ( y != 0x00000018 ) { break; }
        // Parameters from CPUID dump, ECX register
        y = (int) ( array[x1+3] >>> 32 );                            // y = EDX
        int[] z = CPUID.decodeBitfields 
            ( "EDX" , DECODER_EDX , y , result , p );
        y = z[0];                         // y = TLB cache type
        if ( y==0 ) { x1+=4; continue; }  // Skip not valid entry
        if ( y>3 )  { y=0; }              // Value for unknown type
        result[p][4] = DECODER_TYPE[y];
        y = z[1];                         // y = TLB cache level
        result[p+1][4] = "L" + y;
        p = p + NY2;
        // Parameters from CPUID dump, ECX register
        y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );     // y = ECX
        CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
        p = p + NY3;
        // Parameters from CPUID dump, EBX register
        y = (int) ( array[x1+2] >>> 32 );                            // y = EBX
        CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
        p = p + NY4;
        // Modify dump pointer for cycle
        x1 += 4;  // Next subfunction entry
        }

/*    
    
// THIS NOT REQUIRED BECAUSE UNUSED SUBFUNCTIONS REJECTED BY BINARY LEVEL
// BUT see function 0000000Fh for correct trim.
    
    // Trim text table, reject extra strings
    String[][] result1 = new String[p+1][NX];
    // Cycle for pre-blank text table
    int i, j;
    for (i=0; i<p; i++)  // Cycle for rows, copy valid part of table 
        {   // Cycle for columns
        System.arraycopy( result[i] , 0 , result1[i] , 0 , NX );
        }
    for (j=0; j<NX; j++)  // Make last empty string
        {
        result1[i][j] = "";
        }
*/

    // Result is ready, all strings filled    
    return result;  // result1
    }

}
