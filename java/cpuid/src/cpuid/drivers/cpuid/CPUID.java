/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component: helper library.
Binary dump scan and bitmaps analysing.
*/

package cpuid.drivers.cpuid;

public class CPUID 
{

// Scan CPUID results dump, find required function data by function number.
// INPUT:   x = CPUID dump array , y = required CPUID function number
// OUTPUT:  return required function first element index (64-bit long qwords)
//          return -1 if function entry not found
public static int findFunction(long[] x, int y)
    {
    int base = 4;  // This index skips header, header contains length dword
    int length = (int)(x[0] & ((long)((long)(-1)>>>32)));  // Get from header
    for ( int i=0; i<length; i++ )
        {
        if ( (x[base] >> 32 ) == y ) { return base; } 
        base += 4;  // Next entry, 4 long means 4*8=32 bytes
        }
    return -1;
    }

// Build text block for one CPUID result register, 
// for VARIABLE size bit fields. Optimal for parameters bitmaps.
// INPUT:   regname = register name for use in the text block
//          regmap = decoder constant pattern: [name][last bit][first bit]
//          regvalue = register value from CPUID dump
//          destination = text array for build
//          offset = offset in the destination text array, for calls sequences
// OUTPUT:  array of extracted data values, for caller extra functionality
public static int[] decodeBitfields
    ( String regname, Object[][] regmap, int regvalue, 
      String[][] destination, int offset )
    {
    int n = regmap.length;  // length of fixed control pattern
    String s1, s2;
    int x1,x2,x3,y1,y2;
    int[] extract = new int[n];
    // Iterations by input pattern elements
    for (int i=0; i<n; i++)
        {
        x1 = (int)regmap[i][1];  // x1 = highest bit of bit field
        x2 = (int)regmap[i][2];  // x2 = lowest bit of bit field
        x3 = x1 - x2 + 1;        // x3 = width of bit field
        if (x1==x2) { s1 = String.format( "%d", x1 ); }  // one bit number
        else        { s1 = String.format( "%d-%d", x1, x2 ); }  // bit field
        y1 = regvalue >> x2;     // y1 = parameter required masking
        y2 = ~( -1 << x3 );      // y2 = mask
        if (y2==0) { y2=-1; }
        y1 = y1 & y2;            // y1 = shifted and masked bit field value
        extract[i] = y1;
        s2 = String.format("%X", y1);
        destination[i+offset][0] = (String)regmap[i][0];  // parameter name
        destination[i+offset][1] = regname;               // register name
        destination[i+offset][2] = s1;                    // bit(s) number(s)
        destination[i+offset][3] = s2;                    // hex value
        }                                    // note comments entry yet empty
    return extract;
    }

// Build text block for one CPUID result register,
// for FIXED size bitfields, one bit per field. Optimal for features bitmaps.
// INPUT:   regname = register name for use in the text block
//          regmap = decoder constant pattern: [short name][long name]
//          regvalue = register value from CPUID dump
//          destination = text array for build
//          offset = offset in the destination text array, for calls sequences
// OUTPUT:  none, void
public static void decodeBitmap
    ( String regname, String[][] regmap, int regvalue, 
      String[][] destination, int offset )
    {
    int n = regmap.length;  // length of fixed control pattern
    String s1, s2, s3;
    int x1 = 1;
    // Iterations by input pattern elements
    for (int i=0; i<n; i++)
        {
        s1 = String.format("%d = ",i) + regmap[i][0];
        if ((regvalue & x1) == 0) { s2 = "0"; s3 = "not supported"; }
        else { s2 = "1"; s3 = "supported"; }
        x1 <<= 1;
        destination[i+offset][0] = regmap[i][1];  // bit long name
        destination[i+offset][1] = regname;       // register name
        destination[i+offset][2] = s1;            // bit number with short name
        destination[i+offset][3] = s2;            // bit value: 0/1
        destination[i+offset][4] = s3;            // bit comment: not sup./sup.
        }
    }
    
}
