//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000015h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID00000015 extends CommandAdapter
{
private static final String 
        F_NAME = "TSC/Core crystal clock ratio";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }

private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

private final static Object[][] DECODER_EAX =
    {
        { "Denominator of the TSC/core crystal clock ratio" , 31 , 0 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "Numerator of the TSC/core crystal clock ratio" , 31 , 0 }
    };

private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 0;    // old: +1
private final static int NY2 = DECODER_EBX.length + 0;
private final static int NY  = NY1 + NY2;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    
    int x1 = CPUID.findFunction( array, 0x00000015 );
    if (x1<0) { return result; }
    
    int y=0;
    int p=0;
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    result[p][NX-1] = String.format("%d",y);

    p = NY1;
    y = (int) ( array[x1+2] >>> 32 );                                    // EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p][NX-1] = String.format("%d",y);

    return result;
    }
}