//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 0000000Fh declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID0000000F extends CommandAdapter
{
private static final String 
        F_NAME = "Platform Quality of Service enumeration";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static Object[][] DECODER_EBX0 =
    {
        { "Maximum range of RMID within this physical CPU" , 31 , 0 }
    };
private final static String[][] DECODER_EDX0 =
    {
        { "x"      , "Reserved" } ,
        { "L3 QoS" , "L3 cache quality of service monitoring" }
    };
private final static Object[][] DECODER_EBX1 =
    {
        { "Conversion factor from IA32_QM_CTR to metric" , 31 , 0 }
    };
private final static Object[][] DECODER_ECX1 =
    {
        { "Maximum range of RMID of this resource type" , 31 , 0 }
    };
private final static String[][] DECODER_EDX1 =
    {
        { "L3 OM" , "L3 occupancy monitoring" }
    };

private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EBX0.length + 0;  // old: +1
private final static int NY2 = DECODER_EDX0.length + 0;
private final static int NY3 = DECODER_EBX1.length + 0;
private final static int NY4 = DECODER_ECX1.length + 0;
private final static int NY5 = DECODER_EDX1.length + 0;

private final static int NSF = 2; // 10;  // max. number of subfunctions
private final static int NY  = NY1+NY2 + (NY3+NY4+NY5) * NSF;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }

    // LOCKED FOR SKYLAKE DEBUG.
    // return result;
    // LOCKED FOR SKYLAKE DEBUG.
    
    //--- v0.21 changes ---
    
    
    int x1 = CPUID.findFunction( array, 0x0000000F );
    if (x1<0) { return result; }
    int x2 = (int) ( array[0] & (((long)((long)(-1)>>>32))) );
    x2 = (x2+1)*4;
    
    int y=0;
    int p=0;
    y = (int) ( array[x1+2] >>> 32 );                                    // EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX0 , y , result , p );
    p=NY1;
    y = (int) ( array[x1+3] >>> 32 );                                    // EDX
    CPUID.decodeBitmap ( "EDX" , DECODER_EDX0 , y , result , p );

    p=NY1+NY2;

    for ( int i=0; i<NSF; i++ )
        {
        x1 = x1 + 4;
        if (x1>=x2) { return result; }
        
        y = (int) ( array[x1] >>> 32 );
        if (y != 0x0000000F ) { return result; }

        p = p + NY3;
        y = (int) ( array[x1+2] >>> 32 );                                // EBX
        CPUID.decodeBitfields ( "EBX" , DECODER_EBX1 , y , result , p );

        p = p + NY4;
        y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );         // ECX
        CPUID.decodeBitfields ( "ECX" , DECODER_ECX1 , y , result , p );

        p = p + NY5;
        y = (int) ( array[x1+3] >>> 32 );                                // EDX
        CPUID.decodeBitmap ( "EDX" , DECODER_EDX1 , y , result , p );
        }
    
    return result;
    
    }

}