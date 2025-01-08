/* 

This code is fragment of Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 0000000Dh =
Processor extended states context management enumeration.

*/

package dumploader.cpuid;

import java.util.ArrayList;

class Cpuid0000000D extends ReservedFunctionCpuid
{
Cpuid0000000D()
    { setFunction( 0x0000000D ); }

@Override String getLongName()
    { return "Processor extended states context management enumeration"; }

// Control tables for results decoding
private final static String[] CONTEXT_PARMS = 
    { "x87 ST0..7[79:0] state" ,                         // context component 0
      "SSE128 XMM0..15[127:0] state " ,
      "AVX256 YMM0..15[255:128] state" ,
      "MPX BNDREGS BND0..3[127:0] state" ,
      "MPX BNDCSR state" ,
      "AVX512 OPMASK K0..7[63:0] state" ,
      "AVX512 ZMM0..15[511:256] state" ,
      "AVX512 ZMM16..31[511:0] state" ,
      "PT state managed by IA32_XSS" ,                  // context component 8
      "PKRU control" ,
      "Reserved" ,                                      // context component 10
      "CET_U state managed by IA32_XSS" ,               // 11
      "CET_S state managed by IA32_XSS" ,               // 12
      "HDC state managed by IA32_XSS" ,                 // 13
      "UINTR state managed by IA32_XSS" ,               // 14
      "LBR state managed by IA32_XSS" ,                 // 15
      "HWP state managed by IA32_XSS" ,                 // 16
      "AMX XTILECFG state" ,                            // 17
      "AMX XTILEDATA state" ,                           // 18
      "APX EGPR state (R16-R31)" };                     // 19
private final static String ENABLED_BYTES =
    "Maximum size for XSAVE/XRSTOR area, enabled features";
private final static String SUPPORTED_BYTES =
    "Maximum size for XSAVE/XRSTOR area, supported features";
private final static String XCR_BITS = "XCR0 bits [63-32]";
private final static String[] XSAVE_SUPPORT =
    { "XSAVEOPT support" ,
      "XSAVEC and compact XRSTOR form support" ,
      "XGETBV function 1 support" , 
      "XSAVES/XRSTORS and IA32_XSS" };
private final static String XSAVE_AREA = "Size of XSAVE area for XCR0|IA32_XSS";
private final static String XSS_LOW    = "IA32_XSS MSR [31-00] bitmap";
private final static String XSS_HIGH   = "IA32_XSS MSR [63-32] bitmap";
private final static String BASE_SIZE  = "Base/Size";

@Override String[][] getParametersList()
    {
    String[] interval = new String[] { "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
/*
        // EAX, subfunction 0
        int mask = 1;
        for ( String item : CONTEXT_PARMS ) 
            {
            a.add( new String[] 
                { item, ( ( entries[0].eax & mask ) != 0 ) ? "1" : "0"} );
            mask <<= 1;
            }
*/        
        // EAX, subfunction 0 with conditionally use results of subfunction 1
        int index = 0;
        int mask = 1;
        int mapXcr0 = entries[0].eax;
        int mapXss = 0;
        if ( ( entries.length > 1 )&&( entries[1].subfunction == 1 ) )
            {
            mapXss = entries[1].ecx;
            }
        for ( String item : CONTEXT_PARMS ) 
            {
            String s = "n/a";
            if ( ( ( mapXcr0 & mask ) != 0 ) && ( ( mapXss & mask ) == 0 ) )
                {
                s = String.format( "XCR0.%d", index );
                }
            if ( ( ( mapXcr0 & mask ) == 0 ) && ( ( mapXss & mask ) != 0 ) )
                {
                s = String.format( "XSS.%d", index );
                }
            if ( ( ( mapXcr0 & mask ) != 0 ) && ( ( mapXss & mask ) != 0 ) )
                {
                s = String.format( "XCR0.%d | XSS.%d", index, index );
                }
            a.add( new String[] { item, s } );
            index++;
            mask <<= 1;
            }
        
        // XSAVE/XRSTOR bitmap done
        a.add( interval );
        a.add( new String[]  // EBX, subfunction 0
            { ENABLED_BYTES, String.format( "%d Bytes", entries[0].ebx ) } );
        a.add( new String[]  // ECX, subfunction 0
            { SUPPORTED_BYTES, String.format( "%d Bytes", entries[0].ecx ) } );
        a.add( new String[]  // EDX, subfunction 0
            { XCR_BITS, String.format( "%08Xh", entries[0].edx ) } );
        // Start second group of sub-leafs: optimized modes of save/restore
        if ( ( entries.length > 1 )&&( entries[1].subfunction == 1 ) )
            {
            a.add( interval );
            // EAX, subfunction 1
            mask = 1;
            for ( String item : XSAVE_SUPPORT ) 
                {
                a.add( new String[]
                    { item, ( ( entries[1].eax & mask ) != 0 ) ? "1" : "0"} );
                mask <<= 1;
                }
            a.add( new String[]  // EBX, subfunction 1
                { XSAVE_AREA, String.format( "%d Bytes", entries[1].ebx ) } );
            a.add( new String[]  // ECX, subfunction 1
                { XSS_LOW, String.format( "%08Xh", entries[1].ecx ) } );
            a.add( new String[]  // EDX, subfunction 1
                { XSS_HIGH, String.format( "%08Xh", entries[1].edx ) } );
            }
        // Start third group of sub-leafs: context store arrays base/size
        if ( entries.length > 2 )
            {
            a.add( interval );
            for( int i=2; i<entries.length; i++ )
                {
                a.add( new String[]  // Subfunctions 2+ , ebx and eax
                    { BASE_SIZE, String.format
                        ( "%d, %d", entries[i].ebx, entries[i].eax ) } );
                }
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
