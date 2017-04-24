//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 8000001Eh declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID8000001E extends CommandAdapter
{
private static final String 
        F_NAME = "AMD multiprocessing topology";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }

private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static Object[][] DECODER_EAX =
    {
        { "Extended APIC ID" ,  31 ,  0 } ,
    };
private final static Object[][] DECODER_EBX =
    {
        { "Compute unit ID"    ,  7 , 0 } ,
        { "Cores per unit - 1" ,  9 , 8 }
    };
private final static Object[][] DECODER_ECX =
    {
        { "Node ID"              ,   7 , 0 } ,
        { "Nodes per processor"  ,  10 , 8 }
    };
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_ECX.length + 1;
private final static int NY  = NY1+NY2+NY3;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x8000001E );
    if (x<0) { return result; }
    
    int y=0;
    int z[];
    int p=0;

    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );

    p=NY1;
    y = (int) ( array[x+2] >>> 32 );                                     // EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p+1][4] = ( z[1]+1 ) + " cores";

    p=NY1+NY2;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );              // ECX
    z = CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );
    result[p+1][4] = ( z[1]+1 ) + " nodes";
    
    return result;
    }



}
