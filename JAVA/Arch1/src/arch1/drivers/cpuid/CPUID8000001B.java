//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 8000001Bh declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID8000001B extends CommandAdapter
{
private static final String 
        F_NAME = "AMD Instruction based sampling";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }

private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static String[][] DECODER_EAX =
    {
        { "IBS FFV"    , "Instruction based sampling feature flag valid" } ,
        { "FETCH SAMP" , "IBS fetch sampling" } , 
        { "OP SAM"     , "IBS execution sampling supported" } , 
        { "RW OP CNT"  , "Read write of OP counter" } , 
        { "OP CNT"     , "OP counting mode" } , 
        { "BRN TRG"    , "Branch target address reporting" } , 
        { "OP CNT EXT" , "IbsOpCurCnt, IbsOpMaxCnt extend by 7 bits" } , 
        { "RIP INV"    , "Invalid RIP indication" } , 
    };

private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY  = NY1;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x8000001B );
    if (x<0) { return result; }

    int y=0;
    int p=0;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitmap ( "EAX" , DECODER_EAX , y , result , p );
    
    return result;
    }

}
