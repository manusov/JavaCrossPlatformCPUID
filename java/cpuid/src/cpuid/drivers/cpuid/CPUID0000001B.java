/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
CPUID driver component:
CPUID standard function 0000001Bh declared as CPR.COMMAND.
THIS MODULE IS EXPERIMENTAL, NOT VERIFIED, NOT CONNECTED YET.
REQUIRED JNI DRIVERS MODIFICATION FOR ALL SUBFUNCTIONS GET.
*/

package cpuid.drivers.cpuid;

import cpuid.drivers.cpr.CommandAdapter;

public class CPUID0000001B extends CommandAdapter
{
// CPUID function full name
private static final String F_NAME = 
    "PCONFIG instruction information";

// Parameters table up string
private final static String[] COMMAND_UP_1 =
    { "Parameter" , "Register" , "Bit(s)" , "Value, hex" , "Comments" };

// Return if function not found
private final static String[][] NO_DATA =
    {
        { "Unexpected no data" , "" , "" , "" , "" }
    };

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    {
        { "Sub-leaf type" , 11 , 0 }
    }; 
private final static Object[][] DECODER_EBX =
    {
        { "Identifier of target [3n+1]" , 31 , 0 }
    }; 
private final static Object[][] DECODER_ECX =
    {
        { "Identifier of target [3n+2]" , 31 , 0 }
    }; 
private final static Object[][] DECODER_EDX =
    {
        { "Identifier of target [3n+3]" , 31 , 0 }
    }; 

// Calculate control data total size for output formatting
private final static int NX = COMMAND_UP_1.length;
private final static int NYMAX = 50*4;

// Return CPUID this function full name
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function full name
@Override public String getCommandLongName(long[] dummy ) 
    { return F_NAME; }

// Return CPUID this function parameters table up string
// INPUT:   Reserved array
// OUTPUT:  String, CPUID function details table up string
@Override public String[] getCommandUp1( long[] dummy )
    { return COMMAND_UP_1; }

// Build and return CPUID this function detail information table
// INPUT:   Binary array = CPUID dump data
// OUTPUT:  Array of strings = CPUID this function detail information table
@Override public String[][] getCommandText1( long[] array )
    {
    // check function 00000007h for detect is function 0000001Bh supported
    boolean support = false;
    String[][] result = new String[1][2];
    int x = CPUID.findFunction( array, 0x00000007 );
    if ( x >= 0 )
        {
        int y = (int) ( array[x+3] >>> 32 );  // y = EDX
        if ( ( y & ( 1 << 18 ) ) != 0 )
            {
            support = true; 
            }
        }
    if ( support )
        {
        // get number of entries per CPUID dump, field from dump header
        // variable COUNT units = 32 byte entries
        int count = (int) ( ( array[0] ) & (long) ( (long)(-1) >>> 32) );
        // get pointer to this function entries sequence
        // variable X units = 8 byte LONG number
        x = CPUID.findFunction( array, 0x0000001B );
        if ( x > 0 )
            {
            String[][] temp = new String[NYMAX][NX];  // Text formatted by control data
            // blank text array
            for (int i=0; i<NYMAX; i++)               // Cycle for rows 
                { 
                for(int j=0; j<NX; j++)               // Cycle for columns
                    { 
                    temp[i][j]=""; 
                    } 
                }
            // build text array
            int pointer = 0;
            // note "<= count" , not "< count" because first entry = special
            while ( ( pointer < NYMAX ) && ( (x/4) <= count ) )
                {
                // Get CPUID function number field
                int function = (int) ( array[ x ] >>> 32 );
                // Break if end of sub-leafs of function 0000001Fh
                if ( function != 0x0000001B ) { break; }
                // load CPU registers images from dump
                int rEAX = (int)
                    ( ( array[ x+2 ] ) & (long) ( (long)(-1) >>> 32) );
                int rEBX = (int)
                    ( array[ x+2 ] >>> 32 );
                int rECX = (int)
                    ( ( array[ x+3 ] ) & (long) ( (long)(-1) >>> 32) );
                int rEDX = (int)
                    ( array[ x+3 ] >>> 32 );
                // build text strings
                int[] z = CPUID.decodeBitfields
                    ( "EAX" , DECODER_EAX , rEAX , temp , pointer );
                temp[pointer++][4] = String.format( "%d", z[0] );
                z = CPUID.decodeBitfields
                    ( "EBX" , DECODER_EBX , rEBX , temp , pointer );
                temp[pointer++][4] = String.format( "%d", z[0] );
                z = CPUID.decodeBitfields
                    ( "ECX" , DECODER_ECX , rECX , temp , pointer );
                temp[pointer++][4] = String.format( "%d", z[0] );
                z = CPUID.decodeBitfields
                    ( "EDX" , DECODER_EDX , rEDX , temp , pointer );
                temp[pointer++][4] = String.format( "%d", z[0] );
                // cycle, advance dump pointer, units = LONG , + 4*LONG
                x += 4;
                }
            // trim text array
            result = new String[pointer][NX];
            for( int i=0; i<pointer; i++ )
                {
                System.arraycopy( temp[i], 0, result[i], 0, NX );
                }
            }
        else
            {
            return NO_DATA;
            }
        }
    else
        {
        result[0][0] = "This function reserved: PCONFIG not supported";
        result[0][1] = "n/a";
        }
    return result;
    }
}