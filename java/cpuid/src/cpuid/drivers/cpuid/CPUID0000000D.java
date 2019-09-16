/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 0000000Dh declared as CPR.COMMAND.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID0000000D extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "Processor extended states context management enumeration";

// Return if function not found
private final static String[][] NO_RESULT =
    {
        { "n/a" , "" }
    };
    
// Control tables for results decoding
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

// Calculate control data total size for output formatting
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

// Return CPUID this function full name
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function full name
@Override public String getCommandLongName(long[] dummy ) 
    { return F_NAME; }

// Build and return CPUID this function detail information table
// INPUT:   Binary array = CPUID dump data
// OUTPUT:  Array of strings = CPUID this function detail information table
@Override public String[][] getCommandText1( long[] array )
    {
    // Scan binary dump, find entry for this function
    int pointer = CPUID.findFunction( array, 0x0000000D );
    // Return "n/a" if this function entry not found
    if ( pointer < 0 ) { return NO_RESULT; }
    // Build and pre-blank result text array
    String[][] result = new String[NY][NX];  // Text formatted by control data
    for (int i=0; i<NY; i++)  // Cycle for columns
        { 
        for(int j=0; j<NX; j++)  // Cycle for rows
            { 
            result[i][j]=""; 
            } 
        }
    // Get number of entries per CPUID dump, field from dump header
    int count = (int) ( ( array[0] ) & (long) ( (long)(-1) >>> 32) );
    int limit = ( count + 1 ) * 4;
    // Parameters from CPUID dump, EAX register
    String s;
    int p=0, mask=1;  // p = strings store pointer, mask = bitmask for test
    int x = (int) ( array[ pointer+2 ] & (((long)((long)(-1)>>>32))) );  // EAX
    for( int i=0; i<NY1; i++ )
        {
        if ( (x&mask)!=0 ) { s="1"; } else { s="0"; }
        result[p][0] = CONTEXT_PARMS[i];
        result[p][1] = s;
        p++;    
        mask <<= 1;
        }
    // Parameters from CPUID dump, EBX register
    p = NY1;
    x = (int) ( array[ pointer+2 ] >>> 32 );                         // x = EBX
    result[p][0] = ENABLED_BYTES[0];
    result[p][1] = String.format( "%d Bytes", x );
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2;
    x = (int) ( array[ pointer+3 ] & (((long)((long)(-1)>>>32))) );  // x = ECX
    result[p][0] = SUPPORTED_BYTES[0];
    result[p][1] = String.format( "%d Bytes", x );
    // Parameters from CPUID dump, EDX register
    p = NY1+NY2+NY3;
    x = (int) ( array[ pointer+3 ] >>> 32 );                         // x = EDX
    result[p][0] = XCR_BITS[0];
    result[p][1] = String.format( "%08Xh", x );
    //  Advance pointer, check end of CPUID function 0Dh sub-leafs sequence
    pointer += 4;  // Next entry, 4*8=32 bytes
    if ( pointer >= limit ) { return result; }
    x = (int) ( array [ pointer ] >> 32 );                 // x = function code
    // Return if end of sub-leafs of CPUID function 0000000Dh
    if ( x != 0x0000000D ) { return result; }
    
    // Start second group of sub-leafs: optimized modes of save/restore 
    // Parameters from CPUID dump, EAX register
    p = NY1+NY2+NY3+NY4;
    x = (int) ( array[ pointer+2 ] & (((long)((long)(-1)>>>32))) );  // x = EAX
    for( int i=0; i<NY5; i++ )
        {
        if ( (x&mask)!=0 ) { s="1"; } else { s="0"; }
        result[p][0] = XSAVE_SUPPORT[i];
        result[p][1] = s;
        p++;    
        mask <<= 1;
        }
    // Parameters from CPUID dump, EBX register
    p = NY1+NY2+NY3+NY4+NY5;
    x = (int) ( array[ pointer+2 ] >>> 32 );                         // x = EBX
    result[p][0] = XSAVE_AREA[0];
    result[p][1] = String.format( "%d Bytes", x );
    // Parameters from CPUID dump, ECX register
    p = NY1+NY2+NY3+NY4+NY5+NY6;
    x = (int) ( array[ pointer+3 ] & (((long)((long)(-1)>>>32))) );  // x = ECX
    result[p][0] = XSS_LOW[0];
    result[p][1] = String.format( "%08Xh", x );
    // Parameters from CPUID dump, EDX register
    p = NY1+NY2+NY3+NY4+NY5+NY6+NY7;
    x = (int) ( array[ pointer+3 ] >>> 32 );                         // x = EDX
    result[p][0] = XSS_HIGH[0];
    result[p][1] = String.format( "%08Xh", x );

    // Start third group of sub-leafs: context store arrays base/size
    // Parameters from CPUID dump, EBX,EAX registers
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
    // Result is ready, all strings filled
    return result;
    }

}