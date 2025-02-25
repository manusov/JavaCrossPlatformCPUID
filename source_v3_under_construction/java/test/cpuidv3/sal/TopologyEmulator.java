/*

Special class for emulating WinAPI topology data.
GetLogicalProcessorInformation(),
GetLogicalProcessorInformationEx().

Fragment of report:

Java CPUID v3.03.07. Win32 JRE32.
https://github.com/manusov/JavaCrossPlatformCPUID
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.
Current screen report.

[ SMP ]

[ package[0] = Processor package ]

  Parameter        Value              
---------------------------------------
  Offset           00000000h          
  Raw index        0                  
  Object type      Processor package  
  Affinity mask    0                  
---------------------------------------

  Offset      x0    x1    x2    x3    x4    x5    x6    x7    x8    x9    xA    xB    xC    xD    xE    xF  
-------------------------------------------------------------------------------------------------------------
  00000000    01    00    00    00    03    00    00    00    00    00    00    00    00    00    00    00  
  00000010    00    00    00    00    00    00    00    00    01    00    00    00    00    00    00    00  
  00000020    00    00    00    00    00    00    00    00    00    00    00    00    00    00    00    00  
  00000030    01    00    00    00    02    00    00    00    01    0C    40    00    00    C0    00    00  
  00000040    02    00    00    00    00    00    00    00    01    00    00    00    02    00    00    00  
  00000050    01    08    40    00    00    80    00    00    01    00    00    00    00    00    00    00  
  00000060    01    00    00    00    02    00    00    00    02    08    40    00    00    00    08    00  
  00000070    00    00    00    00    00    00    00    00    01    00    00    00    02    00    00    00  
  00000080    03    10    40    00    00    00    00    01    00    00    00    00    00    00    00    00  
  00000090    01    00    00    00    01    00    00    00    00    00    00    00    00    00    00    00  
  000000A0    00    00    00    00    00    00    00    00                                                  
-------------------------------------------------------------------------------------------------------------

*/

package cpuidv3.sal;

public class TopologyEmulator 
{
    private final static int[] EMULATE_DATA =
    {
        0x01, 0x00, 0x00, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  
        0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x01, 0x0C, 0x40, 0x00, 0x00, 0xC0, 0x00, 0x00,  
        0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00,  
        0x01, 0x08, 0x40, 0x00, 0x00, 0x80, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  
        0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x02, 0x08, 0x40, 0x00, 0x00, 0x00, 0x08, 0x00,  
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00,  
        0x03, 0x10, 0x40, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  
        0x01, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  
        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00                                                  
    };

    
    public static long[] getEmulateData()
    {
        long[] result = new long[ EMULATE_DATA.length / 8 + 2];
        result[0] = EMULATE_DATA.length;
        result[1] = 0;
        
        int inIndex = 0;
        for( int outIndex = 2; outIndex<result.length; outIndex++ )
        {
            long data = 0;
            for( int i=0; i<8; i++ )
            {
                long x = (long)( EMULATE_DATA[inIndex++] & 0xFFL );
                x <<= ( i * 8);
                data |= x;
            }
            result[ outIndex ] = data;
        }
        
        return result;
    }
}

