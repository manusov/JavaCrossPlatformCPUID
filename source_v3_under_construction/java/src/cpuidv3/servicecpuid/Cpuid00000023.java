/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Standard Function 00000023h = 
Architectural performance monitoring extended leaf.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class Cpuid00000023 extends ParameterFunctionCpuid
{
Cpuid00000023()
    { setFunction( 0x00000023 ); }

@Override String getLongName()
    { return "Architectural performance monitoring extended leaf"; }

// Control tables for results decoding, subfunction 0
private final static Object[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "Intel architectural performance valid sub-leaves bitmap" , 31 , 0  } };
private final static String[][] DECODER_EBX_SUBFUNCTION_0 =
    { { "UNITMSK2"     , "UnitMask2 field in the IA32_PERFEVTSELx MSRs" } , // bit 0
      { "ZBIT"         , "Zero-bit in the IA32_PERFEVTSELx MSRs" } };       // bit 1
private final static Object[][] DECODER_ECX_SUBFUNCTION_0 =
    { { "Number of TMA slots per cycle" , 7 , 0  } };

// subfunction 1
private final static Object[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "General purpose counters bitmap"   , 31 , 0 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_1 =
    { { "Fixed function counters bitmap"    , 31 , 0 } };

// subfunction 2
private final static Object[][] DECODER_EAX_SUBFUNCTION_2 =
    { { "Auto counter reload (ACR) general counters bitmap"     , 31 , 0 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_2 =
    { { "Auto counter reload (ACR) fixed counters bitmap"       , 31 , 0 } };
private final static Object[][] DECODER_ECX_SUBFUNCTION_2 =
    { { "Bitmap of ACR general counters that can cause reloads" , 31 , 0 } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_2 =
    { { "Bitmap of ACR fixed counters that can cause reloads"   , 31 , 0 } };

// subfunction 3
private final static String[][] DECODER_EAX_SUBFUNCTION_3 =
    { { "CORE CYC"     , "Core cycles monitoring event" } ,  // bit 0
      { "INST RET"     , "Instructions retired monitoring event" } ,
      { "REF CYC"      , "Reference cycles monitoring event" } ,
      { "LLC REF"      , "Last level cache references monitoring event" } ,
      { "LLC MISS"     , "Last level cache misses monitoring event" } ,
      { "BRANCH"       , "Branch instructions retired monitoring event" } ,
      { "BRN MISS"     , "Branch mispredicts retired monitoring event" } ,
      { "TPDN SLOT"    , "Topdown slots monitoring event" } ,
      { "TPDN BACK"    , "Topdown backend bound monitoring event" } ,
      { "TPDN BADS"    , "Topdown bad speculation monitoring event" } ,
      { "TPDN FRNT"    , "Topdown frontend bound monitoring event" } ,
      { "TPDN RET"     , "Topdown retiring monitoring event" } ,
      { "LBR INS"      , "LBR inserts" } ,     // bit 12
      { "x"            , "Reserved"    } ,
      { "x"            , "Reserved"    } ,
      { "x"            , "Reserved"    } };    // bit 15

// subfunction 4
private final static Object[][] DECODER_EBX_SUBFUNCTION_4 =
    { { "Allow in record mode for performance counters"   , 3  , 3  } ,
      { "CNTR, Counters group sub-groups bitmap"          , 7  , 4  } ,
      { "LBR, Last branch records groups code"            , 9  , 8  } ,
      { "XER, XSAVE enabled registers group bitmap"       , 23 , 16 } ,
      { "GPR, GPR group in PEBS"                          , 29 , 29 } ,
      { "AUX, AUX group in PEBS"                          , 30 , 30 } };

// subfunction 5
private final static Object[][] DECODER_EAX_SUBFUNCTION_5 =
    { { "Bitmap for GP counters with architectural PEBS"           , 31 , 0 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_5 =
    { { "Bitmap for GP counters PEBS+PDIST (precise distribution)" , 31 , 0 } };
private final static Object[][] DECODER_ECX_SUBFUNCTION_5 =
    { { "Bitmap for fixed counters with architectural PEBS"        , 31 , 0 } };
private final static Object[][] DECODER_EDX_SUBFUNCTION_5 =
    { { "Bitmap for fixed counters PEBS+PDIST"                     , 31 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();

    // subfunction 0 and all other (1-5)
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        a.addAll( dr.strings );
        int bitmapSubFunction = dr.values[0];
        strings = decodeBitmap
            ( "EBX", DECODER_EBX_SUBFUNCTION_0, entries[0].ebx );
        a.addAll( strings );
        dr = decodeBitfields
            ( "ECX", DECODER_ECX_SUBFUNCTION_0, entries[0].ecx );
        a.addAll( dr.strings );
        
        // subfunction 1
        if ( ( entries.length > 1 )&&( ( bitmapSubFunction & 0x3 ) == 0x3 )&&
             ( entries[1].subfunction == 1 ) )
            {
            a.add( interval );
            // EAX, subfunction 1
            dr = decodeBitfields
                ( "EAX", DECODER_EAX_SUBFUNCTION_1, entries[1].eax );
            a.addAll( dr.strings );
            // EBX, subfunction 1
            dr = decodeBitfields
                ( "EBX", DECODER_EBX_SUBFUNCTION_1, entries[1].ebx );
            a.addAll( dr.strings );
            }
            
        // subfunction 2
        if ( ( entries.length > 2 )&&( ( bitmapSubFunction & 0x5 ) == 0x5 )&&
             ( entries[2].subfunction == 2 ) )
            {
            a.add( interval );
            // EAX, subfunction 2
            dr = decodeBitfields
                ( "EAX", DECODER_EAX_SUBFUNCTION_2, entries[2].eax );
            a.addAll( dr.strings );
            // EBX, subfunction 2
            dr = decodeBitfields
                ( "EBX", DECODER_EBX_SUBFUNCTION_2, entries[2].ebx );
            a.addAll( dr.strings );
            // ECX, subfunction 2
            dr = decodeBitfields
                ( "ECX", DECODER_ECX_SUBFUNCTION_2, entries[2].ecx );
            a.addAll( dr.strings );
            // EDX, subfunction 2
            dr = decodeBitfields
                ( "EDX", DECODER_EDX_SUBFUNCTION_2, entries[2].edx );
            a.addAll( dr.strings );
            }
            
        // subfunction 3
        if ( ( entries.length > 3 )&&( ( bitmapSubFunction & 0x9 ) == 0x9 )&&
             ( entries[3].subfunction == 3 ) )
            {
            a.add( interval );
            // EAX, subfunction 3
            strings = decodeBitmap
                ( "EAX", DECODER_EAX_SUBFUNCTION_3, entries[3].eax );
            a.addAll( strings );
            }
        
        // subfunction 4    
        if ( ( entries.length > 4 )&&( ( bitmapSubFunction & 0x11 ) == 0x11 )&&
             ( entries[4].subfunction == 4 ) )
            {
            a.add( interval );
            // EBX, subfunction 4
            dr = decodeBitfields
                ( "EBX", DECODER_EBX_SUBFUNCTION_4, entries[4].ebx );
            a.addAll( dr.strings );
            }
        
        // subfunction 5
        if ( ( entries.length > 5 )&&( ( bitmapSubFunction & 0x21 ) == 0x21 )&&
             ( entries[5].subfunction == 5 ) )
            {
            a.add( interval );
            // EAX, subfunction 2
            dr = decodeBitfields
                ( "EAX", DECODER_EAX_SUBFUNCTION_5, entries[5].eax );
            a.addAll( dr.strings );
            // EBX, subfunction 2
            dr = decodeBitfields
                ( "EBX", DECODER_EBX_SUBFUNCTION_5, entries[5].ebx );
            a.addAll( dr.strings );
            // ECX, subfunction 2
            dr = decodeBitfields
                ( "ECX", DECODER_ECX_SUBFUNCTION_5, entries[5].ecx );
            a.addAll( dr.strings );
            // EDX, subfunction 2
            dr = decodeBitfields
                ( "EDX", DECODER_EDX_SUBFUNCTION_5, entries[5].edx );
            a.addAll( dr.strings );
            }
        }
    
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
