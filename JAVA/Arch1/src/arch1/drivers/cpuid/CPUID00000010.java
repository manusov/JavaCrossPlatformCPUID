//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000010h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID00000010 extends CommandAdapter
{
private static final String 
        F_NAME = "Platform Quality of Service enforcement";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static String[][] DECODER_EBX0 =
    {
        { "x"      , "Reserved" } , 
        { "L3 QoS" , "L3 cache QoS enforcement" }
    };
private final static Object[][] DECODER_EAX1 =
    {
        { "Length of the capacity bit mask for ResID" , 4 , 0 } , 
    };
private final static Object[][] DECODER_EBX1 =
    {
        { "Bit-granular map of isolation/contention" , 31 , 0 } , 
    };
private final static String[][] DECODER_ECX1 =
    {
        { "x"      , "Reserved" } , 
        { "UCOS"   , "Update of COS should be infrequent" } ,
        { "CDP"    , "Code and Data prioritization technology" }
    };
private final static Object[][] DECODER_EDX1 =
    {
        { "Highest COS number supported for this ResID" , 15 , 0 } 
    };
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EBX0.length + 1;
private final static int NY2 = DECODER_EAX1.length + 0;  // old: +1
private final static int NY3 = DECODER_EBX1.length + 0;
private final static int NY4 = DECODER_ECX1.length + 0;
private final static int NY5 = DECODER_EDX1.length + 0;

private final static int NY  = NY1+NY2+NY3+NY4+NY5;

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
    
    int x1 = CPUID.findFunction( array, 0x00000010 );
    if (x1<0) { return result; }                                // x1 = pointer
    int x2 = (int) ( array[0] & (((long)((long)(-1)>>>32))) );
    x2 = (x2+1)*4;                                              // x2 = limit
    
    int y=0;
    int p=0;
    y = (int) ( array[x1+2] >>> 32 );                                    // EBX
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX0 , y , result , p );

    // p=NY1;
    x1 = x1 + 4;
    if (x1>=x2) { return result; }
        
    y = (int) ( array[x1] >>> 32 );
    if (y != 0x00000010 ) { return result; }
            
    p = p + NY1;
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX1 , y , result , p );

    p = p + NY2;
    y = (int) ( array[x1+2] >>> 32 );                                    // EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX1 , y , result , p );

    p = p + NY3;
    y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );             // ECX
    CPUID.decodeBitmap ( "ECX" , DECODER_ECX1 , y , result , p );

    p = p + NY4;
    y = (int) ( array[x1+3] >>> 32 );                                    // EDX
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX1 , y , result , p );
    
    return result;
    
    }

}