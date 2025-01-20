/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides helper methods for build text strings, used by platform
topology binary data to text report conversion.
This class converts affinity bitmasks to lists of bits numbers.

*/

package cpuidv3.serviceosmp;

public class HelperBitmap
{
    public static String helperBitmapString( boolean[] bitArray )
    {
        boolean atOnes = false;
        boolean nonFirst = false;
        int startOnes = 0;
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<bitArray.length; i++)
        {
            if(( bitArray[i] )&&( !atOnes ))
            {
                atOnes = true;
                startOnes = i;
            }
            else if(( !bitArray[i] )&&( atOnes ))
            {
                if( nonFirst )
                {
                    sb.append(", ");
                }
                if( startOnes == (i - 1 ) )
                {
                    sb.append(String.format( "%d", startOnes ));
                }
                else if( i - startOnes == 2 )
                {
                    sb.append(String.format( "%d,%d", startOnes, i-1 ));
                }
                else
                {
                    sb.append(String.format( "%d-%d", startOnes, i-1 ));
                }
                atOnes = false;
                nonFirst = true;
            }
            else if( ( i == ( bitArray.length - 1 ) ) && atOnes)
            {
                if( nonFirst )
                {
                    sb.append(", ");
                }
                if( startOnes == i )
                {
                    sb.append(String.format( "%d", startOnes ));
                }
                else if( i - startOnes == 1 )
                {
                    sb.append(String.format( "%d,%d", startOnes, i ));
                }
                else
                {
                    sb.append(String.format( "%d-%d", startOnes, i ));
                }
            }
        }
        
    //  return sb.isEmpty() ? "n/a" : sb.toString();  // This solution is java version-dependent.
        return ( sb.length() == 0 ) ? "n/a" : sb.toString();
    }

    public static String helperBitmapString( long[] a )
    {
        if( a == null ) return "n/a";
        
        int bitIndex = 0;
        boolean[] bitArray = new boolean[a.length * 64];
        for( int i=0; i<a.length; i++ )
        {
            for( int j=0; j<64; j++ )
            {
                long patternMask = 1L << j;
                bitArray[bitIndex++] = (( a[i] & patternMask ) != 0);
            }
        }
        return helperBitmapString( bitArray );
    }
    
    public static int longsCount( long[] m )
    {
        int bitCount = 0;
        int maskCount = m.length;
        for( int i=0; i<maskCount; i++ )
        {
            for( int j=0; j<64; j++ )
            {
                long patternMask = 1L << j;
                if (( m[i] & patternMask ) != 0 )
                {
                    bitCount++;
                }
            }
        }
        return bitCount;
    }
}
