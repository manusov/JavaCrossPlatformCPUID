//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID extended function 8000000Ah declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID8000000A extends CommandAdapter
{
private static final String 
        F_NAME = "AMD Secure Virtual Machine revision and features";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };
private final static Object[][] DECODER_EAX =
    {
        { "SVM revision" , 7 , 0 }
    };
private final static Object[][] DECODER_EBX =
    {
        { "NASID: number of address space identifiers (ASID)" , 31 , 0 }
    };
private final static String[][] DECODER_EDX =
    {
        { "NP"       , "Nested paging" } ,
        { "LBR Virt" , "Last branch record virtualization" } ,
        { "SVML"     , "SVM lock" } ,
        { "NRIPS"    , "Next RIP save" } ,
        { "TSC Rate" , "TSC rate control MSR" } ,
        { "VMCB CL"  , "VMCB clean bits" } ,
        { "FLASID"   , "TLB flush selectable by ASID" } ,
        { "DASSIST"  , "Decode assists" } ,
        { "x"        , "Reserved" } ,
        { "x"        , "Reserved" } ,
        { "PAUSE FL" , "Pause intercept filter" } ,
        { "x"        , "Reserved" } ,
        { "PAUSE FT" , "Pause intercept filter threshold" } ,
    };

private final static int NX  = COMMAND_UP_1.length;
private final static int NY1 = DECODER_EAX.length + 1;
private final static int NY2 = DECODER_EBX.length + 1;
private final static int NY3 = DECODER_EDX.length + 1;
private final static int NY  = NY1+NY2+NY3;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x8000000A );
    if (x<0) { return result; }
    
    int y=0;
    int[] z;
    int p=0;
    y = (int) ( array[x+2] & (((long)((long)(-1)>>>32))) );              // EAX
    z = CPUID.decodeBitfields ( "EAX" , DECODER_EAX , y , result , p );
    result[p][4] = "" + z[0];

    p=NY1;
    y = (int) ( array[x+2] >>> 32 );                                     // EBX
    z = CPUID.decodeBitfields ( "EBX" , DECODER_EBX , y , result , p );
    result[p][4] = "" + z[0];
    
    p=NY1+NY2;
    y = (int) ( array[x+3] >>> 32 );                                     // EDX
    CPUID.decodeBitmap ( "EDX" , DECODER_EDX , y , result , p );

    return result;
    }
}
