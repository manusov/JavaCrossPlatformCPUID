//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 8000001Ah declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID8000001A extends CommandAdapter
{
private static final String 
        F_NAME = "AMD Performance optimization identifiers";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }

private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static Object[][] DECODER_EAX =
    {
        { "FP128, Hardware SSE 128-bit instead 2 x 64 emulation"   , 0 , 0 } ,
        { "MOVU,  Prefer unaligned 128 bit instead MOVL/MOVH"      , 1 , 1 } ,
        { "FP256, Hardware AVX 256-bit instead 2 x 128 emulation"  , 2 , 2 } 
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
    int x = CPUID.findFunction( array, 0x8000001A );
    if (x<0) { return result; }

    int y=0;
    int p=0;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    
    return result;
    }
}
