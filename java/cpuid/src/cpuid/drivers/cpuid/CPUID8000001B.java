//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 8000001Bh declared as CPR.COMMAND.

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID8000001B extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME =
    "AMD Instruction based sampling";
    
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
        { "IBS FFV"     , "Instruction based sampling feature flag valid" } ,
        { "FETCH SAMP"  , "IBS fetch sampling" } , 
        { "OP SAM"      , "IBS execution sampling supported" } , 
        { "RW OP CNT"   , "Read write of OP counter" } , 
        { "OP CNT"      , "OP counting mode" } , 
        { "BRN TRG"     , "Branch target address reporting" } , 
        { "OP CNT EXT"  , "IbsOpCurCnt, IbsOpMaxCnt extend by 7 bits" } , 
        { "RIP INV"     , "Invalid RIP indication" } , 
        { "OP BRN FUSE" , "Fused branch micro-op indication" } ,
        { "IBS F MSR"   , "IBS fetch control extended MSR" } ,
        { "IBS OP D"    , "IBS op data 4 MSR" }
    };

// Calculate control data total size for output formatting
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 0;
private final static int NY  = NY1;

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
    int x = CPUID.findFunction( array, 0x8000001B );
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
    // Result is ready, all strings filled
    return result;
    }

}
