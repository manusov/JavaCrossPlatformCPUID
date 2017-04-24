//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 8000001Ch declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID8000001C extends CommandAdapter
{
private static final String 
        F_NAME = "AMD Lightweight profiling capabilities";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }

private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static Object[][] DECODER_EBX =
    {
        { "LWP control block size" ,  7 ,  0 } ,
        { "LWP event size"         , 15 ,  8 } ,
        { "LWP maximum event ID"   , 23 , 16 } ,
        { "LWP event offset"       , 31 , 24 }
     };
private final static Object[][] DECODER_ECX =
    {
        { "LWP cache latency bit counter size" ,  4 , 0 } ,
        { "LWP data cache miss address valid"  ,  5 , 0 } ,
        { "Amount cache latency is rounded"    ,  8 , 6 } ,
        { "LWP version"                        , 15 , 9 } ,
        { "Minimum size of the LWP event ring buffer, units 32 records" , 23 , 16 } ,
        { "LWP branch prediction filtering supported" , 28 , 28 } ,
        { "LWP IP filtering supported" , 29 , 29 } ,
        { "LWP cache level filtering supported" , 30 , 30 } ,
        { "LWP cache latency filtering supported" , 31 , 31 }
    };
private final static String[][] DECODER_EDX =
    {
        { "LWP"     , "Lightweight profiling" } ,
        { "LWPVAL"  , "LWPVAL instruction available" } , 
        { "LWP IRE" , "LWP instructions retired event available" } , 
        { "LWP BRE" , "LWP branch retired event available" } , 
        { "LWP DME" , "LWP DC miss event available" } , 
        { "LWP CNH" , "LWP core clocks not halted event available" } , 
        { "LWP RNH" , "LWP core reference clocks not halted event available" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "x"       , "Reserved" } , 
        { "LWP INT" , "LWP interrupt on threshold overflow available" } , 
    };

private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EBX.length + 1;
private final static int NY2 = DECODER_ECX.length + 1;
private final static int NY3 = DECODER_EDX.length + 1;
private final static int NY  = NY1+NY2+NY3;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x8000001C );
    if (x<0) { return result; }
    
    int y=0;
    int p=0;

    y = (int) ( array[x+2] >>> 32 );                                     // EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );

    p=NY1;
    y = (int) ( array[x+3] & (((long)((long)(-1)>>>32))) );              // ECX
    CPUID.decodeBitfields ( "ECX" , DECODER_ECX , y , result , p );

    p=NY1+NY2;
    y = (int) ( array[x+3] >>> 32 );                                     // EDX
    CPUID.decodeBitmap ( "EDX" , DECODER_EDX , y , result , p );
    
    return result;
    }

}
