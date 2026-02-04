/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for return bitfields decode information
from method decodeBitfields().
*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

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
