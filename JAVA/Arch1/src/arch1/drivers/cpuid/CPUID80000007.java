//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 80000007h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID80000007 extends CommandAdapter
{
private static final String 
        F_NAME = "Advanced Power Management information";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static String[][] DECODER_EDX =
    {
        { "TS"        , "Temperature sensor" } ,
        { "FID"       , "Frequency ID control" } ,
        { "VID"       , "Voltage ID control"   } ,
        { "TTP"       , "THERMTRIP"            } ,
        { "TM"        , "Hardware thermal control" } ,
        { "x"         , "Reserved" } ,
        { "100 MHz"   , "100 MHz steps for multiplier control" } ,
        { "HwPstate"  , "Hardware P-state control" } ,
        { "INV TSC"   , "TSC invariant for P-States and C-States" } ,
        { "CPB"       , "Core performance boost" } ,
        { "EffFreqRO" , "Read only effective frequency interface" }
    };
private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EDX.length + 1;
private final static int NY  = NY1;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x80000007 );
    if (x<0) { return result; }

    int y = 0;
    int p = 0;
    y = (int) ( array[x+3] >>> 32 );                                     // EDX
    CPUID.decodeBitmap( "EDX", DECODER_EDX, y , result , p );
    
    return result;
    }
    
}
