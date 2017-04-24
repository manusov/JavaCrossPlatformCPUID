//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000014h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID00000014 extends CommandAdapter
{
private static final String 
        F_NAME = "Intel processor trace enumeration information";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

private final static Object[][] DECODER_EAX0 =
    {
        { "Maximum sub-leaf number" , 31 , 0 } , 
    };
private final static String[][] DECODER_EBX0 =
    {
        { "FILTER"    , "IA32_RTIT_CTL.CR3Filter can be set"            } ,
        { "CPSB"      , "Configurable PSB and Cycle-Accurate Mode"      } ,
        { "IP Filter" , "IP filtering, TraceStop filtering, warm pres." } ,
        { "MTC"       , "MTC timing packet and suppresion of COFI"      }
    };
private final static String[][] DECODER_ECX0 =
    {
        { "TR"        , "Tracing can be enabled with IA32_RTIT_CTL.ToPA" } ,
        { "ToPA"      , "ToPA tables can hold any number of output entries" } ,
        { "SROS"      , "Single-Range Output scheme" } ,
        { "OTTS"      , "Output to Trace transport subsystem" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "x"         , "Reserved" } ,
        { "LIP"       , "Generated packets include CS base component" } ,
    };
private final static Object[][] DECODER_EAX1 =
    {
        { "Number of configurable address ranges for filtering" ,  2 , 0  } ,
        { "Bitmap of supported MTC period encodings"            , 31 , 16 }
    };
private final static Object[][] DECODER_EBX1 =
    {
        { "Bitmap of supported Cycle threshold value encodings"      , 15 , 0  } ,
        { "Bitmap of supported Configurable PSB frequency encodings" , 31 , 16 }
    };

private final static int NX   = COMMAND_UP_1.length;
private final static int NY1  = DECODER_EAX0.length + 1;
private final static int NY2  = DECODER_EBX0.length + 1;
private final static int NY3  = DECODER_ECX0.length + 1;
private final static int NY4  = DECODER_EAX1.length + 1;
private final static int NY5  = DECODER_EBX1.length + 1;

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
    
    //---------- Subfunction = 0 ----------    

    int x1 = CPUID.findFunction( array, 0x00000014 );
    if (x1<0) { return result; }
    int x2 = (int) ( array[0] & (((long)((long)(-1)>>>32))) );
    x2 = (x2+1)*4;
    
    int y=0;
    int p=0;
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX0 , y , result , p );

    p = NY1;
    y = (int) ( array[x1+2] >>> 32 );                                    // EBX
    CPUID.decodeBitmap ( "EBX" , DECODER_EBX0 , y , result , p );

    p = NY1+NY2;
    y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );             // ECX
    CPUID.decodeBitmap ( "ECX" , DECODER_ECX0 , y , result , p );
    
    //---------- Subfunction = 1 ----------    
    
    p = NY1+NY2+NY3;
    x1 = x1 + 4;
    if (x1>=x2) { return result; }
        
    y = (int) ( array[x1] >>> 32 );
    if (y != 0x00000014 ) { return result; }
            
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX1 , y , result , p );

    p = NY1+NY2+NY3+NY4;
    y = (int) ( array[x1+2] >>> 32 );                                    // EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX1 , y , result , p );
    
    return result;
    
    }

}