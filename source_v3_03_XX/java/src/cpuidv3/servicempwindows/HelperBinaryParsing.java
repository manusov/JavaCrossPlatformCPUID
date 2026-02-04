/*

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class provides helper methods for parsing platform topology binary data.
MS Windows variant, interpreting results buffers after WinAPI
GLPI , GetLogicalProcessorInformation()
GLPI_EX , GetLogicalProcessorInformationEx().

*/

package cpuidv3.servicempwindows;

class HelperBinaryParsing 
{
    static Long getVariableSize( long[] a, int index, int size )
    {
        int limit = a.length;
        int highIndex = index / 8;
        int lowIndex = index % 8;
        long data = 0;
        
        for(int i=0; i<size; i++)
        {
            if(highIndex >= limit)
            {  // Return Long object as null if array index overflow.
                return null;
            }
            
            long temp = (a[highIndex]  >> (lowIndex * 8)) & 0xFFL;
            data = data | (temp << (i * 8));
            lowIndex++;
            
            if(lowIndex > 7)
            {
                lowIndex = 0;
                highIndex++;
            }
        }
        return data;
    }
}
