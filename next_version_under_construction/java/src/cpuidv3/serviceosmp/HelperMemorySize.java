/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides helper methods for build text strings, used by platform
topology binary data to text report conversion.
This class converts memory block size 64-bit values to text strings
with numeric values and units. Used for readable visualization.

*/

package cpuidv3.serviceosmp;

public class HelperMemorySize 
{
    public static enum UNITS { BYTES, KB, MB, GB, TB, AUTO };
    private final static long K = 1024L;
    private final static long M = 1024*1024L;
    private final static long G = 1024*1024*1024L;
    private final static long T = 1024*1024*1024*1024L;
    
    // This method selects units by mods, prevents non-precision shows.
    public static String writeMemorySize( long value, UNITS control)
    {
        String result = "?";
        if ( value >= 0 )
        {
            long unitsCount;
            String unitsName;
            if( ( value == 0 )||( value % K != 0 )||( control == UNITS.BYTES ) )
            {
                unitsCount = value;
                unitsName = "Bytes";
            }
            else if( ( value % M != 0 )||( control == UNITS.KB ) )
            {
                unitsCount = value / K;
                unitsName = "KB";
            }
            else if( ( value % G != 0 )||( control == UNITS.MB ) )
            {
                unitsCount = value / M;
                unitsName = "MB";
            }
            else if( ( value % T != 0 )||( control == UNITS.GB ) )
            {
                unitsCount = value / G;
                unitsName = "GB";
            }
            else
            {
                unitsCount = value / T;
                unitsName = "TB";
            }
            result = String.format( "%d %s", unitsCount, unitsName);
        }
        return result;
    }
}
