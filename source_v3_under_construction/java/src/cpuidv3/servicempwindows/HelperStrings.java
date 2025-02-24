/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides helper methods for build text strings, used by platform
topology binary data to text report conversion.

*/

package cpuidv3.servicempwindows;

import static cpuidv3.servicemp.HelperBitmap.helperBitmapString;
import java.util.ArrayList;

class HelperStrings 
{
    static String bitmapString( GroupAffinity[] g )
    {
        int groupsCount = g.length;
        int bitIndex = 0;
        boolean[] bitArray = new boolean[groupsCount * 64];
        for(int i=0; i<groupsCount; i++)
        {
            for(int j=0; j<64; j++)
            {
                long patternMask = 1L << j;
                bitArray[bitIndex++] = (( g[i].mask & patternMask ) != 0);
            }
        }
        return helperBitmapString( bitArray );
    }

    static String bitmapString( GroupInfo[] g )
    {
        int groupsCount = g.length;
        int bitIndex = 0;
        boolean[] bitArray = new boolean[groupsCount * 64];
        for(int i=0; i<groupsCount; i++)
        {
            for(int j=0; j<64; j++)
            {
                long patternMask = 1L << j;
                bitArray[bitIndex++] = 
                    (( g[i].activeProcessorMask & patternMask ) != 0);
            }
        }
        return helperBitmapString( bitArray );
    }
    
    static int[][] affinityCount( GroupAffinity[] g )
    {
        ArrayList<int[]> bitCount = new ArrayList<>();
        int groupsCount = g.length;
        for( int i=0; i<groupsCount; i++ )
        {
            for( int j=0; j<64; j++ )
            {
                long patternMask = 1L << j;
                if (( g[i].mask & patternMask ) != 0 )
                {
                    bitCount.add( new int[]{ j, i } );  // maskBit, group.
                }
            }
        }
        return bitCount.toArray( new int[ bitCount.size() ][] );
    }
}
