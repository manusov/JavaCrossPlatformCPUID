/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 00000014h declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID00000014 extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "Intel processor trace enumeration information";
    
// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static Object[][] DECODER_EAX0 =
    {
        { "Maximum sub-leaf number" , 31 , 0 } , 
    };
private final static String[][] DECODER_EBX0 =
    {
        { "FILTER"    , "IA32_RTIT_CTL.CR3Filter can be set"            } ,
        { "CPSB"      , "Configurable PSB and Cycle-Accurate Mode"      } ,
        { "IP Filter" , "IP filtering, TraceStop filtering, warm pres." } ,
        { "MTC"       , "MTC timing packet and suppresion of COFI"      } ,
        { "PTWRITE"   , "PTWRITE can generate packets"                  } ,
        { "PET"       , "Power Event Trace"                             }
    };
private final static String[][] DECODER_ECX0 =
    {
        { "TR"        , "Tracing can be enabled with IA32_RTIT_CTL.ToPA" } ,
        { "ToPA"      , "ToPA tables can hold any number of output entries" } ,
        { "SROS"      , "Single-Range Output scheme" } ,
        { "OTTS"      , "Output to Trace transport subsystem" } ,
        { "x"         , "Reserved" } ,  // bit 4 reserved
        { "x"         , "Reserved" } ,  // ...
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,  // ...
        { "x"         , "Reserved" } ,  // bit 30 reserved
        { "LIP"       , "Generated packets include CS base component" } ,
    };
private final static Object[][] DECODER_EAX1 =
    {
        { "Number of configurable address ranges for filtering" ,  2 , 0  } ,
        { "Bitmap of supported MTC period encodings"            , 31 , 16 }
    };
private final static Object[][] DECODER_EBX1 =
    {
        { "Bitmap of supported Cycle threshold value encodings"      , 15 , 0  } ,
        { "Bitmap of supported Configurable PSB frequency encodings" , 31 , 16 }
    };

// Calculate control data total size for output formatting
private final static int NX   = COMMAND_UP_1.length;
private final static int NY1  = DECODER_EAX0.length + 1;
private final static int NY2  = DECODER_EBX0.length + 1;
private final static int NY3  = DECODER_ECX0.length + 1;
private final static int NY4  = DECODER_EAX1.length + 1;
private final static int NY5  = DECODER_EBX1.length + 0;
private final static int NY  = NY1+NY2+NY3+NY4+NY5;

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
    int x1 = CPUID.findFunction( array, 0x00000014 );
    // Return "n/a" if this function entry not found
    if (x1<0) { return NO_RESULT; }
    // Build and pre-blank result text array
    String[][] result = new String[NY][NX];  // Text formatted by control data
    for (int i=0; i<NY; i++)  // Cycle for rows
        { 
        for(int j=0; j<NX; j++)  // Cycle for columns
            { 
            result[i][j]=""; 
            } 
        }
    
    // Results of subfunction = 0
    // Get number of entries per CPUID dump, field from dump header
    int x2 = (int) ( array[0] & (((long)((long)(-1)>>>32))) );
    x2 = (x2+1)*4;  // Calculate x2 = limit
    // Parameters from CPUID dump, EAX register
    int p=0;  // pointer for sequentally store strings in the table
    int y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );      // y = EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX0 , y , result , p );
    // Parameters from CPUID dump, EBX register
    p = NY1;
    y = (int) ( array[x1+2] >>> 32 );                                // y = EBX
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX0 , y , result , p );
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2;
    y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );         // y = ECX
    CPUID.decodeBitmap ( "ECX" , DECODER_ECX0 , y , result , p );
    
    // Result of subfunction = 1
    // Parameters from CPUID dump, EAX register
    p = NY1+NY2+NY3;
    x1 = x1 + 4;
    if (x1>=x2) { return result; }    // Return if dump size limit
    y = (int) ( array[x1] >>> 32 );   // y = function code for current entry
    if (y != 0x00000014 ) { return result; }  // End of function entries
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );          // y = EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX1 , y , result , p );
    // Parameters from CPUID dump, EBX register
    p = NY1+NY2+NY3+NY4;
    y = (int) ( array[x1+2] >>> 32 );                                // y = EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX1 , y , result , p );
    
    // Result is ready, all strings filled
    return result;
    }

}