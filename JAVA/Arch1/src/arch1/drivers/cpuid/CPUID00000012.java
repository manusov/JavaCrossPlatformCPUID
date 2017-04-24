//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000012h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID00000012 extends CommandAdapter
{
private static final String 
        F_NAME = "Intel Security Guard Extensions information";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

private final static String[][] DECODER_EAX0 =
    {
        { "SGX1" , "SGX1 instruction set" } , 
        { "SGX2" , "SGX2 instruction set" }
    };
private final static Object[][] DECODER_EBX0 =
    {
        { "MISCSELECT extended features bit vector" , 31 , 0 } , 
    };
private final static Object[][] DECODER_EDX0 =
    {
        { "Enclave non-64 bit mode size length bits" ,  7 , 0 } , 
        { "Enclave 64-bit mode size length bits"     , 15 , 0 } 
    };

private final static Object[][] DECODER_EAX1 =
    {
        { "Validity bitmap for SECS.ATTRIBUTES[31-0]" ,  31 , 0 } 
    };
private final static Object[][] DECODER_EBX1 =
    {
        { "Validity bitmap for SECS.ATTRIBUTES[63-32]" ,  31 , 0 } 
    };
private final static Object[][] DECODER_ECX1 =
    {
        { "Validity bitmap for SECS.ATTRIBUTES[95-64]" ,  31 , 0 } 
    };
private final static Object[][] DECODER_EDX1 =
    {
        { "Validity bitmap for SECS.ATTRIBUTES[127-96]" ,  31 , 0 } 
    };

private final static Object[][] DECODER_EAX2 =
    {
        { "Sub-leaf tag"          ,  3 ,  0 } ,
        { "Physical address bits" , 31 , 12 }
    };
private final static Object[][] DECODER_EBX2 =
    {
        { "Physical address bits" ,  19 , 0 } 
    };
private final static Object[][] DECODER_ECX2 =
    {
        { "EPC section tag"  ,  3 ,  0 } ,
        { "Size bits"        , 31 , 12 }
    };
private final static Object[][] DECODER_EDX2 =
    {
        { "Size bits" ,  19 , 0 } 
    };

private final static int NX   = COMMAND_UP_1.length;
private final static int NY1  = DECODER_EAX0.length + 0;    // old: +1
private final static int NY2  = DECODER_EBX0.length + 0;
private final static int NY3  = DECODER_EDX0.length + 1;
private final static int NY4  = DECODER_EAX1.length + 0;
private final static int NY5  = DECODER_EBX1.length + 0;
private final static int NY6  = DECODER_ECX1.length + 0;
private final static int NY7  = DECODER_EDX1.length + 1;
private final static int NY8  = DECODER_EAX2.length + 0;
private final static int NY9  = DECODER_EBX2.length + 0;
private final static int NY10 = DECODER_ECX2.length + 0;
private final static int NY11 = DECODER_EDX2.length + 0;

private final static int NY  = NY1+NY2+NY3+NY4+NY5+NY6+NY7+NY8+NY9+NY10+NY11;

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

    int x1 = CPUID.findFunction( array, 0x00000012 );
    if (x1<0) { return result; }
    int x2 = (int) ( array[0] & (((long)((long)(-1)>>>32))) );
    x2 = (x2+1)*4;
    
    int y=0;
    int p=0;
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitmap ( "EAX" , DECODER_EAX0 , y , result , p );

    p = NY1;
    y = (int) ( array[x1+2] >>> 32 );                                    // EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX0 , y , result , p );

    p = NY1+NY2;
    y = (int) ( array[x1+3] >>> 32 );                                    // EDX
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX0 , y , result , p );
    
    //---------- Subfunction = 1 ----------    
    
    p = NY1+NY2+NY3;
    x1 = x1 + 4;
    if (x1>=x2) { return result; }
        
    y = (int) ( array[x1] >>> 32 );
    if (y != 0x00000012 ) { return result; }
            
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX1 , y , result , p );

    p = NY1+NY2+NY3+NY4;
    y = (int) ( array[x1+2] >>> 32 );                                    // EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX1 , y , result , p );

    p = NY1+NY2+NY3+NY4+NY5;
    y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );             // ECX
    CPUID.decodeBitfields ( "ECX" , DECODER_ECX1 , y , result , p );

    p = NY1+NY2+NY3+NY4+NY5+NY6;
    y = (int) ( array[x1+3] >>> 32 );                                    // EDX
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX1 , y , result , p );

    //---------- Subfunctions > 1 ----------    
    
    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7;
    x1 = x1 + 4;
    if (x1>=x2) { return result; }
    
    y = (int) ( array[x1] >>> 32 );
    if (y != 0x00000012 ) { return result; }
            
    y = (int) (array[x1+2] & (((long)((long)(-1)>>>32))) );              // EAX
    CPUID.decodeBitfields ( "EAX" , DECODER_EAX2 , y , result , p );

    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7+NY8;
    y = (int) ( array[x1+2] >>> 32 );                                    // EBX
    CPUID.decodeBitfields ( "EBX" , DECODER_EBX2 , y , result , p );

    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7+NY8+NY9;
    y = (int) ( array[x1+3] & (((long)((long)(-1)>>>32))) );             // ECX
    CPUID.decodeBitfields ( "ECX" , DECODER_ECX2 , y , result , p );

    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7+NY8+NY9+NY10;
    y = (int) ( array[x1+3] >>> 32 );                                    // EDX
    CPUID.decodeBitfields ( "EDX" , DECODER_EDX2 , y , result , p );
    
    return result;
    
    }

}