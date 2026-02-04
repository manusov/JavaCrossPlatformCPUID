/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Parent class for CPUID functions classes.
This class inherited by standard, extended and virtual
CPUID functions classes.

*/

package cpuidv3.servicecpuid;

import cpuidv3.sal.EntryCpuidSubfunction;
import java.util.ArrayList;

class ReservedFunctionCpuid 
{
static ContainerCpuid container;
EntryCpuidSubfunction[] entries;
private int function;
private boolean isShowFlag = false;

final static void setContainer( ContainerCpuid c )
    { container = c; }

final int getFunction()
    { return function; }

final void setFunction( int f )
    { function = f; }

final void initData( EntryCpuidSubfunction[] ec )
    { entries = ec;
      isShowFlag = ( ec != null ) && ( ec.length != 0 ); }

String getShortName() 
    { return String.format( "%08X", function ); }

String getLongName()
    { return "Reserved"; }

String[] getParametersListUp()
    { return new String[] { "Parameter" , "Value" }; }

String[][] getParametersList()
    { return new String[][] { { "This function reserved" , "n/a" } }; }

String[] getRegistersDumpUp()
    { return new String[] 
        { "Function", "Subfunction", "Pass", "EAX", "EBX", "ECX", "EDX" }; }

private final static int DUMP_COLUMNS = 7;

final String[][] getRegistersDump()
    {
    if ( ( isShowFlag ) && ( entries != null ) )
        {
        int n = entries.length;
        int m = getRegistersDumpUp().length;
        if ( m == DUMP_COLUMNS )
            {
            String[][] s = new String[n][m];
            for( int i=0; i<n; i++ )
                {
                s[i][0] = String.format( "%08X", entries[i].function );
                s[i][1] = String.format( "%08X", entries[i].subfunction );
                s[i][2] = String.format( "%08X", entries[i].pass );
                s[i][3] = String.format( "%08X", entries[i].eax );
                s[i][4] = String.format( "%08X", entries[i].ebx );
                s[i][5] = String.format( "%08X", entries[i].ecx );
                s[i][6] = String.format( "%08X", entries[i].edx );
                }
            return s;
            }
        else
            {
            return null;
            }
        }
    else
        {
        return null;
        }
    }

final boolean isShow()
    { return isShowFlag; }

// Helpers used by child classes
// possible optimization, see also ApplicationCpuid.java
// same functionality duplication

final String extractVendorString( EntryCpuidSubfunction entry, boolean virtual )
    {
    boolean b = false;
    int data[] = new int[3];
    data[0] = entry.ebx;
    if ( virtual )  // select registers order for CPU Vendor Signature
        {  // for virtual function 40000000h order is EBX-ECX-EDX
        data[1] = entry.ecx;
        data[2] = entry.edx;
        }
    else
        {  // for functions 00000000h, 80000000h order is EBX-EDX-ECX
        data[1] = entry.edx;
        data[2] = entry.ecx;
        }
    // convert 3 integer numbers to 12-char string
    StringBuilder sb = new StringBuilder( "" );
    for( int i=0; i<3; i++ )
        {
        int d = data[i];
        for( int j=0; j<4; j++ )  // integer to 4 chars
            {
            char c = (char)( d & 0xFF );
            if ( c != 0 )
                {
                if ( ( c < ' ' )||( c > '}' ) ) c = '.';
                sb.append( c );
                b = true;
                }
            d = d >>> 8;
            }
        }
    return b ? sb.toString() : null;
    }

final class DecodeReturn
    {
    final ArrayList<String[]> strings;
    final int[] values;
    DecodeReturn( ArrayList<String[]> x1, int[] x2 )
        {
        strings = x1;
        values = x2;
        }
    DecodeReturn( ArrayList<String[]> x1 )
        {
        strings = x1;
        values = null;
        }
    }

/*
Build text block for one CPUID result register, 
for VARIABLE size bit fields. Optimal for parameters bitmaps.
INPUT:   regname = register name for use in the text block
         regmap = decoder constant pattern: [name][last bit][first bit]
         regvalue = register value from CPUID dump
         destination = text array for build
OUTPUT:  packed in the DecodeReturn class:
         text array
         array of extracted data values, for caller extra functionality
*/

final DecodeReturn decodeBitfields
    ( String regname, Object[][] regmap, int regvalue )
    {
    ArrayList<String[]> destination = new ArrayList<>();
    int n = regmap.length;  // length of fixed control pattern
    int[] extract = new int[n];
    // Iterations by input pattern elements
    for (int i=0; i<n; i++)
        {
        int x1 = (int)regmap[i][1];  // x1 = highest bit of bit field
        int x2 = (int)regmap[i][2];  // x2 = lowest bit of bit field
        int x3 = x1 - x2 + 1;        // x3 = width of bit field
        String stringBits;
        if ( x1 == x2 )
            {  // one bit 
            stringBits = String.format( "%d", x1 );
            }
        else
            {  // bitfield, some bits
            stringBits = String.format( "%d-%d", x1, x2 ); 
            }
        int y1 = regvalue >> x2;     // y1 = shift parameter, required masking
        int y2 = ~( -1 << x3 );      // y2 = mask
        if ( y2 == 0 )
            {
            y2 = -1;
            }
        y1 = y1 & y2;                // y1 = shifted and masked bit field value
        extract[i] = y1;
        String stringValue = String.format( "%X", y1 );
        String[] strings = new String[]
            { (String)regmap[i][0], regname, stringBits, stringValue,
            ""  // this string reserved for extra comments, provided by caller
            };
        destination.add( strings );
        }
    return new DecodeReturn( destination, extract );
    }

/*
Build text block for one CPUID result register,
for FIXED size bitfields, one bit per field. Optimal for features bitmaps.
INPUT:   regname = register name for use in the text block
         regmap = decoder constant pattern: [short name][long name]
         regvalue = register value from CPUID dump
OUTPUT:  text array
*/

final ArrayList<String[]> decodeBitmap
    ( String regname, String[][] regmap, int regvalue )
    {
    ArrayList<String[]> destination = new ArrayList<>();
    int n = regmap.length;  // length of fixed control pattern
    int x1 = 1;
    // Iterations by input pattern elements
    for (int i=0; i<n; i++)
        {
        String stringShortName = String.format( "%d = ", i ) + regmap[i][0];
        String stringValue, stringSupport;
        if ( ( regvalue & x1 ) == 0)
            { 
            stringValue = "0"; 
            stringSupport = "not supported"; 
            }
        else 
            { 
            stringValue = "1"; 
            stringSupport = "supported"; 
            }
        x1 <<= 1;
        String[] strings = new String[]
            {
            regmap[i][1],      // bit long name
            regname,           // register name
            stringShortName,   // bit number with short name
            stringValue,       // bit value: 0/1
            stringSupport      // bit comment: not sup./sup.
            };
        destination.add( strings );
        }
    return  destination;
    }

/*
Helper method,write address width and space size, string=F(number)
INPUT:   valueBits = address widths value, bits
OUTPUT:  text string
*/
    
final String writeSize( int valueBits )
    {
    String s = "";  // s = scratch string
    int maskMega = 1 << ( valueBits - 20 );  // z2 = space size in megabytes
    int maskGiga = 1 << ( valueBits - 30 );  // z3 = space size in gigabytes
    int maskTera = 1 << ( valueBits - 40 );  // z4 = space size in terabytes
    if ( valueBits == 0 ) s = "n/a";         // width = 0 means not supported
    if ( ( valueBits > 0 )&&( valueBits < 20 ) )
        {   // width < 20 means invalid
        s = "Invalid"; 
        }
    if ( ( valueBits >= 20 )&&( valueBits < 30 ) )
        {
        s = valueBits + "-bit, " + maskMega + " MB space"; 
        }
    if ( ( valueBits >= 30 )&&( valueBits < 40 ) )
        {
        s = valueBits + "-bit, " + maskGiga + " GB space"; 
        }
    if ( valueBits >= 40 )
        { 
        s = valueBits + "-bit, " + maskTera + " TB space"; 
        }
    return s;
    }

/*
Return string cache associativity = f( numeric value )
variant 1.
*/
final String writeAssociativityV1 ( int a )
    {
    String s = "?";
    if ( a == 0 )                   { s = "Reserved";             }
    if ( a == 1 )                   { s = "1 way, direct mapped"; }
    if ( (a >= 2 )&&( a <= 0xFE ) ) { s = "" + a;                 }
    if ( a == 0xFF )                { s = "Fully associative";    }
    if ( a > 0xFF )                 { s = "Invalid";              }
    return s;
    }

/*
Return string cache associativity = f( numeric value )
variant 2.
*/
final String writeAssociativityV2 ( int a )
    {
    String s;
    switch( a )
        {
        case 0:  { s = "Disabled";             break; }
        case 1:  { s = "1 way, direct mapped"; break; }
        case 2:  { s = "2 ways";               break; }
        case 3:  { s = "3 ways";               break; }
        case 4:  { s = "4 to 5 ways";          break; }
        case 5:  { s = "6 to 7 ways";          break; }
        case 6:  { s = "8 to 15 ways";         break; }
        case 7:  { s = "Reserved";             break; }
        case 8:  { s = "16 to 31 ways";        break; }
        case 9:  { s = "Use f=8000001Dh";      break; }
        case 10: { s = "32 to 47 ways";        break; }
        case 11: { s = "48 to 63 ways";        break; }
        case 12: { s = "64 to 95 ways";        break; }
        case 13: { s = "96 to 127 ways";       break; }
        case 14: { s = "128 or above ways";    break; }
        case 15: { s = "Fully associative";    break; }
        default: { s = "Invalid";              break; }
        }
    return s;
    }
}
