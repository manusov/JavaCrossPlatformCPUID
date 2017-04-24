//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 0000000Dh declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;

public class CPUID0000000D extends CommandAdapter
{
private static final String 
        F_NAME = "Processor extended states context management enumeration";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] CONTEXT_PARMS = 
    {
    "x87 ST0..7[79:0] state" ,
    "SSE128 XMM0..15[127:0] state " ,
    "AVX256 YMM0..15[255:128] state" ,
    "MPX BNDREGS BND0..3[127:0] state" ,
    "MPX BNDCSR state" ,
    "AVX512 OPMASK K0..7[63:0] state" ,
    "AVX512 ZMM0..15[511:256] state" ,
    "AVX512 ZMM16..31[511:0] state" ,
    "IA32_XSS control" ,
    "PKRU control" ,
    };
private final static String[] ENABLED_BYTES =
    { "Maximum size for XSAVE/XRSTOR area, enabled features" };
private final static String[] SUPPORTED_BYTES =
    { "Maximum size for XSAVE/XRSTOR area, supported features" };
private final static String[] XCR_BITS =
    { "XCR0 bits [63-32]" };
private final static String[] XSAVE_SUPPORT =
    {
    "XSAVEOPT support" ,
    "XSAVEC and compact XRSTOR form support" ,
    "XGETBV function 1 support" , 
    "XSAVES/XRSTORS and IA32_XSS"
    };
private final static String[] XSAVE_AREA =
    { "Size of XSAVE area for XCR0|IA32_XSS" };
private final static String[] XSS_LOW =
    { "IA32_XSS MSR [31-00] bitmap" };
private final static String[] XSS_HIGH =
    { "IA32_XSS MSR [63-32] bitmap" };
private final static String[] BASE_SIZE =
    { "Base/Size" };

private final static int NX  = 2;
private final static int NY1 = CONTEXT_PARMS.length;
private final static int NY2 = ENABLED_BYTES.length;
private final static int NY3 = SUPPORTED_BYTES.length;
private final static int NY4 = XCR_BITS.length + 1;
private final static int NY5 = XSAVE_SUPPORT.length;
private final static int NY6 = XSAVE_AREA.length;
private final static int NY7 = XSS_LOW.length;
private final static int NY8 = XSS_HIGH.length + 1;
private final static int NY9 = 3; // max. 64;
private final static int NY  = NY1+NY2+NY3+NY4+NY5+NY6+NY7+NY8+NY9;

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    
    int count = (int) ( ( array[0] ) & (long) ( (long)(-1) >>> 32) );
    int limit = ( count + 1 ) * 4;
    int pointer = CPUID.findFunction( array, 0x0000000D );
    if ( pointer < 0 ) { return result; }
    
    String s;
    int x=0;
    int p=0, mask=1;
    x = (int) ( array[ pointer+2 ] & (((long)((long)(-1)>>>32))) );      // EAX
    for( int i=0; i<NY1; i++ )
        {
        if ( (x&mask)!=0 ) { s="1"; } else { s="0"; }
        result[p][0] = CONTEXT_PARMS[i];
        result[p][1] = s;
        p++;    
        mask <<= 1;
        }
    
    p = NY1;
    x = (int) ( array[ pointer+2 ] >>> 32 );                             // EBX
    result[p][0] = ENABLED_BYTES[0];
    result[p][1] = String.format( "%d Bytes", x );
    
    p = NY1+NY2;
    x = (int) ( array[ pointer+3 ] & (((long)((long)(-1)>>>32))) );      // ECX
    result[p][0] = SUPPORTED_BYTES[0];
    result[p][1] = String.format( "%d Bytes", x );

    p = NY1+NY2+NY3;
    x = (int) ( array[ pointer+3 ] >>> 32 );                             // EDX
    result[p][0] = XCR_BITS[0];
    result[p][1] = String.format( "%08Xh", x );

    pointer += 4;  // Next entry, 4*8=32 bytes
    if ( pointer >= limit ) { return result; }
    x = (int) ( array [ pointer ] >> 32 );
    if ( x != 0x0000000D ) { return result; }

    p = NY1+NY2+NY3+NY4;
    x = (int) ( array[ pointer+2 ] & (((long)((long)(-1)>>>32))) );      // EAX
    for( int i=0; i<NY5; i++ )
        {
        if ( (x&mask)!=0 ) { s="1"; } else { s="0"; }
        result[p][0] = XSAVE_SUPPORT[i];
        result[p][1] = s;
        p++;    
        mask <<= 1;
        }

    p = NY1+NY2+NY3+NY4+NY5;
    x = (int) ( array[ pointer+2 ] >>> 32 );                             // EBX
    result[p][0] = XSAVE_AREA[0];
    result[p][1] = String.format( "%d Bytes", x );

    p = NY1+NY2+NY3+NY4+NY5+NY6;
    x = (int) ( array[ pointer+3 ] & (((long)((long)(-1)>>>32))) );      // ECX
    result[p][0] = XSS_LOW[0];
    result[p][1] = String.format( "%08Xh", x );

    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7;
    x = (int) ( array[ pointer+3 ] >>> 32 );                             // EDX
    result[p][0] = XSS_HIGH[0];
    result[p][1] = String.format( "%08Xh", x );

    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7+NY8;
    int x1, x2;
    for (int i=0; i<NY9; i++)
        {
        pointer += 4;  // Next entry, 4*8=32 bytes
        if ( pointer >= limit ) { return result; }
        x = (int) ( array [ pointer ] >> 32 );
        if ( x != 0x0000000D ) { return result; }
        x1 = (int) ( array[ pointer+2 ] >>> 32 );                        // EBX
        x2 = (int) ( array[ pointer+2 ] & (((long)((long)(-1)>>>32))) ); // EAX
        result[p][0] = BASE_SIZE[0];
        result[p][1] = String.format( "%d, %d", x1, x2 );
        p++;
        }

    
    return result;
    }


}