//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// CPUID driver component:
// CPUID standard function 00000002h declared as CPR.COMMAND.

package arch1.drivers.cpuid;

import arch1.drivers.cpr.CommandAdapter;
import arch1.kernel.IOPB;

public class CPUID00000002 extends CommandAdapter
{
private static final String 
        F_NAME = "Cache/TLB/Prefetch descriptors";
    
@Override public String getCommandLongName(long[] dummy ) { return F_NAME; }
    
private final static String[] COMMAND_UP_1 =
    { "Cache descriptor, hex" , "Cache or TLB type" };
private final static int NX  = COMMAND_UP_1.length;
private final static int NY  = 16;

private final static Object[][] CACHE_DESCRIPTORS =
{
    { 0x00, "null descriptor (=unused descriptor)" } ,
    { 0x01, "code TLB, 4K pages, 4 ways, 32 entries" } ,
    { 0x02, "code TLB, 4M pages, fully, 2 entries" } ,
    { 0x03, "data TLB, 4K pages, 4 ways, 64 entries" } ,
    { 0x04, "data TLB, 4M pages, 4 ways, 8 entries" } ,
    { 0x05, "data TLB, 4M pages, 4 ways, 32 entries" } , 
    { 0x06, "code L1 cache, 8 KB, 4 ways, 32 byte lines" } , 
    { 0x08, "code L1 cache, 16 KB, 4 ways, 32 byte lines" } , 
    { 0x09, "code L1 cache, 32 KB, 4 ways, 64 byte lines" } , 
    { 0x0A, "data L1 cache, 8 KB, 2 ways, 32 byte lines" } , 
    { 0x0B, "code TLB, 4M pages, 4 ways, 4 entries" } , 
    { 0x0C, "data L1 cache, 16 KB, 4 ways, 32 byte lines" } ,
    { 0x0D, "data L1 cache, 16 KB, 4 ways, 64 byte lines (ECC)" } , 
    { 0x0E, "data L1 cache, 24 KB, 6 ways, 64 byte lines" } , 
    { 0x10, "data L1 cache, 16 KB, 4 ways, 32 byte lines (IA-64)" } , 
    { 0x15, "code L1 cache, 16 KB, 4 ways, 32 byte lines (IA-64)" } , 
    { 0x1A, "code and data L2 cache, 96 KB, 6 ways, 64 byte lines (IA-64)" } , 
    { 0x1D, "code and data L2 cache, 128 KB, 2 ways, 64 byte lines" } , 
    { 0x21, "code and data L2 cache, 256 KB, 8 ways, 64 byte lines" } , 
    { 0x22, "code and data L3 cache, 512 KB, 4 ways (!), 64 byte lines, dual-sectored" } , 
    { 0x23, "code and data L3 cache, 1024 KB, 8 ways, 64 byte lines, dual-sectored" } , 
    { 0x24, "code and data L2 cache, 1024 KB, 16 ways, 64 byte lines" } , 
    { 0x25, "code and data L3 cache, 2048 KB, 8 ways, 64 byte lines, dual-sectored" } , 
    { 0x29, "code and data L3 cache, 4096 KB, 8 ways, 64 byte lines, dual-sectored" } , 
    { 0x2C, "data L1 cache, 32 KB, 8 ways, 64 byte lines" } ,  
    { 0x30, "code L1 cache, 32 KB, 8 ways, 64 byte lines" } ,  
    { 0x39, "code and data L2 cache, 128 KB, 4 ways, 64 byte lines, sectored" } ,  
    { 0x3A, "code and data L2 cache, 192 KB, 6 ways, 64 byte lines, sectored" } ,  
    { 0x3B, "code and data L2 cache, 128 KB, 2 ways, 64 byte lines, sectored" } ,  
    { 0x3C, "code and data L2 cache, 256 KB, 4 ways, 64 byte lines, sectored" } ,  
    { 0x3D, "code and data L2 cache, 384 KB, 6 ways, 64 byte lines, sectored" } ,  
    { 0x3E, "code and data L2 cache, 512 KB, 4 ways, 64 byte lines, sectored" } ,  
    { 0x40, "no integrated L2 cache (P6 core) or L3 cache (P4 core)" } ,  
    { 0x41, "code and data L2 cache, 128 KB, 4 ways, 32 byte lines" } ,  
    { 0x42, "code and data L2 cache, 256 KB, 4 ways, 32 byte lines" } ,  
    { 0x43, "code and data L2 cache, 512 KB, 4 ways, 32 byte lines" } ,  
    { 0x44, "code and data L2 cache, 1024 KB, 4 ways, 32 byte lines" } ,  
    { 0x45, "code and data L2 cache, 2048 KB, 4 ways, 32 byte lines" } ,  
    { 0x46, "code and data L3 cache, 4096 KB, 4 ways, 64 byte lines" } ,  
    { 0x47, "code and data L3 cache, 8192 KB, 8 ways, 64 byte lines" } ,  
    { 0x48, "code and data L2 cache, 3072 KB, 12 ways, 64 byte lines" } ,  
    { 0x49, "code and data L3 cache, 4096 KB, 16 ways, 64 byte lines (P4) or"
           + " code and data L2 cache, 4096 KB, 16 ways, 64 byte lines (Core 2)" } ,   
    { 0x4A, "code and data L3 cache, 6144 KB, 12 ways, 64 byte lines" } ,  
    { 0x4B, "code and data L3 cache, 8192 KB, 16 ways, 64 byte lines" } ,  
    { 0x4C, "code and data L3 cache, 12288 KB, 12 ways, 64 byte lines" } ,  
    { 0x4D, "code and data L3 cache, 16384 KB, 16 ways, 64 byte lines" } ,  
    { 0x4E, "code and data L2 cache, 6144 KB, 24 ways, 64 byte lines" } ,  
    { 0x4F, "code TLB, 4K pages, ???, 32 entries" } ,  
    { 0x50, "code TLB, 4K/4M/2M pages, fully, 64 entries" } ,  
    { 0x51, "code TLB, 4K/4M/2M pages, fully, 128 entries" } ,  
    { 0x52, "code TLB, 4K/4M/2M pages, fully, 256 entries" } ,  
    { 0x55, "code TLB, 2M/4M, fully, 7 entries" } ,  
    { 0x56, "L0 data TLB, 4M pages, 4 ways, 16 entries" } ,  
    { 0x57, "L0 data TLB, 4K pages, 4 ways, 16 entries" } ,  
    { 0x59, "L0 data TLB, 4K pages, fully, 16 entries" } ,  
    { 0x5A, "L0 data TLB, 2M/4M, 4 ways, 32 entries" } ,  
    { 0x5B, "data TLB, 4K/4M pages, fully, 64 entries" } ,  
    { 0x5C, "data TLB, 4K/4M pages, fully, 128 entries" } ,  
    { 0x5D, "data TLB, 4K/4M pages, fully, 256 entries" } ,  
    { 0x60, "data L1 cache, 16 KB, 8 ways, 64 byte lines, sectored" } ,  
    { 0x61, "code TLB, 4K pages, fully, 48 entries" } ,  
    { 0x63, "data TLB, 2M/4M pages, 4-way, 32-entries, and"
           + " data TLB, 1G pages, 4-way, 4 entries" } ,   
    { 0x64, "data TLB, 4K pages, 4-way, 512 entries" } ,  
    { 0x66, "data L1 cache, 8 KB, 4 ways, 64 byte lines, sectored" } ,  
    { 0x67, "data L1 cache, 16 KB, 4 ways, 64 byte lines, sectored" } ,  
    { 0x68, "data L1 cache, 32 KB, 4 ways, 64 byte lines, sectored" } ,  
    { 0x6A, "L0 data TLB, 4K pages, 8-way, 64 entries" } ,  
    { 0x6B, "data TLB, 4K pages, 8-way, 256 entries" } ,  
    { 0x6C, "data TLB, 2M/4M pages, 8-way, 126 entries" } ,  
    { 0x6D, "data TLB, 1G pages, fully, 16 entries" } ,  
    { 0x70, "trace L1 cache, 12 KµOPs, 8 ways" } ,  
    { 0x71, "trace L1 cache, 16 KµOPs, 8 ways" } ,  
    { 0x72, "trace L1 cache, 32 KµOPs, 8 ways" } ,  
    { 0x73, "trace L1 cache, 64 KµOPs, 8 ways" } ,  
    { 0x76, "code TLB, 2M/4M pages, fully, 8 entries" } ,  
    { 0x77, "code L1 cache, 16 KB, 4 ways, 64 byte lines, sectored (IA-64)" } ,  
    { 0x78, "code and data L2 cache, 1024 KB, 4 ways, 64 byte lines" } ,  
    { 0x79, "code and data L2 cache, 128 KB, 8 ways, 64 byte lines, dual-sectored" } ,  
    { 0x7A, "code and data L2 cache, 256 KB, 8 ways, 64 byte lines, dual-sectored" } ,  
    { 0x7B, "code and data L2 cache, 512 KB, 8 ways, 64 byte lines, dual-sectored" } ,  
    { 0x7C, "code and data L2 cache, 1024 KB, 8 ways, 64 byte lines, dual-sectored" } ,  
    { 0x7D, "code and data L2 cache, 2048 KB, 8 ways, 64 byte lines" } ,  
    { 0x7E, "code and data L2 cache, 256 KB, 8 ways, 128 byte lines, sect. (IA-64)" } ,  
    { 0x7F, "code and data L2 cache, 512 KB, 2 ways, 64 byte lines" } ,  
    { 0x80, "code and data L2 cache, 512 KB, 8 ways, 64 byte lines" } ,  
    { 0x81, "code and data L2 cache, 128 KB, 8 ways, 32 byte lines" } ,  
    { 0x82, "code and data L2 cache, 256 KB, 8 ways, 32 byte lines" } ,  
    { 0x83, "code and data L2 cache, 512 KB, 8 ways, 32 byte lines" } ,  
    { 0x84, "code and data L2 cache, 1024 KB, 8 ways, 32 byte lines" } ,  
    { 0x85, "code and data L2 cache, 2048 KB, 8 ways, 32 byte lines" } ,  
    { 0x86, "code and data L2 cache, 512 KB, 4 ways, 64 byte lines" } ,  
    { 0x87, "code and data L2 cache, 1024 KB, 8 ways, 64 byte lines" } ,  
    { 0x88, "code and data L3 cache, 2048 KB, 4 ways, 64 byte lines (IA-64)" } ,  
    { 0x89, "code and data L3 cache, 4096 KB, 4 ways, 64 byte lines (IA-64)" } ,  
    { 0x8A, "code and data L3 cache, 8192 KB, 4 ways, 64 byte lines (IA-64)" } ,  
    { 0x8D, "code and data L3 cache, 3072 KB, 12 ways, 128 byte lines (IA-64)" } ,  
    { 0x90, "code TLB, 4K...256M pages, fully, 64 entries (IA-64)" } ,  
    { 0x96, "data L1 TLB, 4K...256M pages, fully, 32 entries (IA-64)" } ,  
    { 0x9B, "data L2 TLB, 4K...256M pages, fully, 96 entries (IA-64)" } ,  
    { 0xA0, "data TLB, 4K pages, fully, 32 entries" } ,  
    { 0xB0, "code TLB, 4K pages, 4 ways, 128 entries" } ,  
    { 0xB1, "code TLB, 4M pages, 4 ways, 4 entries and"
           + " code TLB, 2M pages, 4 ways, 8 entries " } ,  
    { 0xB2, "code TLB, 4K pages, 4 ways, 64 entries" } ,  
    { 0xB3, "data TLB, 4K pages, 4 ways, 128 entries" } ,  
    { 0xB4, "data TLB, 4K pages, 4 ways, 256 entries" } ,  
    { 0xB5, "code TLB, 4K pages, 8 ways, 64 entries" } ,  
    { 0xB6, "code TLB, 4K pages, 8 ways, 128 entries" } ,  
    { 0xBA, "data TLB, 4K pages, 4 ways, 64 entries" } ,  
    { 0xC0, "data TLB, 4K/4M pages, 4 ways, 8 entries" } ,  
    { 0xC1, "L2 code and data TLB, 4K/2M pages, 8 ways, 1024 entries" } ,  
    { 0xC2, "data TLB, 2M/4M pages, 4 ways, 16 entries" } ,  
    { 0xC3, "L2 code and data TLB, 4K/2M pages, 6 ways, 1536 entries and"
           + " L2 code and data TLB, 1G pages, 4 ways, 16 entries" } ,   
    { 0xC4, "data TLB, 2M/4M pages, 4-way, 32 entries" } ,  
    { 0xCA, "L2 code and data TLB, 4K pages, 4 ways, 512 entries" } ,  
    { 0xD0, "code and data L3 cache, 512-kb, 4 ways, 64 byte lines" } ,  
    { 0xD1, "code and data L3 cache, 1024-kb, 4 ways, 64 byte lines" } ,  
    { 0xD2, "code and data L3 cache, 2048-kb, 4 ways, 64 byte lines" } ,  
    { 0xD6, "code and data L3 cache, 1024-kb, 8 ways, 64 byte lines" } ,  
    { 0xD7, "code and data L3 cache, 2048-kb, 8 ways, 64 byte lines" } ,  
    { 0xD8, "code and data L3 cache, 4096-kb, 8 ways, 64 byte lines" } ,  
    { 0xDC, "code and data L3 cache, 1536-kb, 12 ways, 64 byte lines" } ,  
    { 0xDD, "code and data L3 cache, 3072-kb, 12 ways, 64 byte lines" } ,  
    { 0xDE, "code and data L3 cache, 6144-kb, 12 ways, 64 byte lines" } ,  
    { 0xE2, "code and data L3 cache, 2048-kb, 16 ways, 64 byte lines" } ,  
    { 0xE3, "code and data L3 cache, 4096-kb, 16 ways, 64 byte lines" } ,  
    { 0xE4, "code and data L3 cache, 8192-kb, 16 ways, 64 byte lines" } ,  
    { 0xEA, "code and data L3 cache, 12288-kb, 24 ways, 64 byte lines" } ,  
    { 0xEB, "code and data L3 cache, 18432-kb, 24 ways, 64 byte lines" } ,  
    { 0xEC, "code and data L3 cache, 24576-kb, 24 ways, 64 byte lines" } ,  
    { 0xF0, "64 byte prefetching" } ,  
    { 0xF1, "128 byte prefetching" } ,  
    { 0xFF, "query standard level 0000_0004h instead" } , 
    };

private static final int NC = CACHE_DESCRIPTORS.length;

@Override public String[] getCommandUp1( long[] array )
    { return COMMAND_UP_1; }

@Override public String[][] getCommandText1( long[] array )
    {
    String[][] result = new String[NY][NX];
    for (int i=0; i<NY; i++) { for(int j=0; j<NX; j++) { result[i][j]=""; } }
    int x = CPUID.findFunction( array, 0x00000002 );
    if (x<0) { return result; }
    
    byte[] y = IOPB.receiveBytes(array, x+2, 2);
    if ( y[0] != 1 ) { return result; }
    
    int k=0;
    for(int i=1; i<4; i++)         { k = storeDescriptors(y, i, result, k); }
    if ((y[7] & 0x80)==0)
        { for(int i=4; i<8; i++)   { k = storeDescriptors(y, i, result, k); } }
    if ((y[11] & 0x80)==0)
        { for(int i=8; i<12; i++)  { k = storeDescriptors(y, i, result, k); } }
    if ((y[15] & 0x80)==0)
        { for(int i=12; i<16; i++) { k = storeDescriptors(y, i, result, k); } }
    
    //--- Trim result -> result1 ---
    String[][] result1 = new String[k][NX];
    for (int i=0; i<k; i++) 
        { for (int j=0; j<NX; j++) { result1[i][j] = result[i][j]; } }

    return result1;
    }

//---------- Helper method -----------------------------------------------------
private int storeDescriptors( byte[] y, int i, String[][] result, int k )
    {
    int z = (int)(y[i] & 0xFF);
    if( z != 0 )
        {
        result[k][0] = String.format( "%02X", y[i] );
        boolean flag = false;
        for (int j=0; j<NC; j++)
            {
            if( z == (int)CACHE_DESCRIPTORS[j][0] )
                { result[k][1] = (String)CACHE_DESCRIPTORS[j][1]; flag = true; }
            }
        if ( flag==false ) { result[k][1] = "Unknown cache descriptor"; }
        k++;
        }
    return k;
    }
}
