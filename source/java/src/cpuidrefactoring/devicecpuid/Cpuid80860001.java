/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Vendor Specific Function
80860001h = Transmeta vendor-specific: 
            Transmeta get processor information.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

public class Cpuid80860001 extends ParameterFunctionCpuid
{
Cpuid80860001()
    { setFunction( 0x80860001 ); }

@Override String getLongName()
    { return "Transmeta processor information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Type"              , 13 , 12 } ,
      { "Family"            , 11 ,  8 } ,
      { "Model"             ,  7 ,  4 } ,
      { "Stepping"          ,  3 ,  0 } };
private final static Object[][] DECODER_EBX =
    { { "Major version"     , 31 , 24 } ,
      { "Minor version"     , 23 , 16 } ,
      { "Major mask"        , 15 ,  8 } ,
      { "Minor mask"        ,  7 ,  0 } };
private final static Object[][] DECODER_ECX =
    { { "Frequency in MHz"  , 31 ,  0 } };
private final static String[][] DECODER_EDX =
    { { "RCMS" , "Recovery CMS active (after a bad flash)" } ,
      { "LR"   , "LongRun technology"                      } ,
      { "x"    , "Reserved"                                } ,  // bit 2 = reserved
      { "LRTI" , "LongRun Table Interface LRTI (CMS 4.2)"  } ,
      { "x"    , "Reserved"                                } ,
      { "x"    , "Reserved"                                } ,
      { "x"    , "Reserved"                                } ,
      { "PTT1" , "Persistent translation technology 1.x"   } ,  // bit 7
      { "PTT2" , "Persistent translation technology 2.0"   } ,  // bit 8
      { "x"    , "Reserved"                                } ,
      { "x"    , "Reserved"                                } ,
      { "x"    , "Reserved"                                } ,
      { "PBE"  , "Processor break events"                  } };  // bit 12

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    String s1, s2;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
            
        // EAX: Transmeta processor type, family, model, stepping
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        int stepping = dr.values[3];
        switch ( stepping )
            {
            case 2:
                s1 = "TM3x00";
                break;
            case 3: 
                s1 = "TM5x00";
                break;
            case 4: 
                s1 = "TM8xx0";    // added
                break;
            default:
                s1 = "?";
            }
        dr.strings.get(0)[4] = s1;  // line 0 column 4 (0-based)
        a.addAll( dr.strings );
        a.add( interval );
        
        // Detect L2 cache size by other function, for models differentiate
        int cache = 0;  // 0 means not detected
        EntryCpuid[] cacheentries = container.buildEntries( 0x80000006 );
        if ( ( cacheentries != null )&&( cacheentries.length == 1 ) )
            {
            cache = cacheentries[0].ecx >>> 16;  // units = kilobytes
            }

        // EBX: Transmeta processor revision ID
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        int majorVersion = 0, minorVersion = 0, majorMask = 0, minorMask = 0;
        int alternative = 0;
        if ( entries[0].ebx != 0x02000000 )
            {  // this branch for detect processor revision by this function
            majorVersion = dr.values[0];
            minorVersion = dr.values[1];
            majorMask    = dr.values[2];
            minorMask    = dr.values[3];
            }
        else
            {  // this branch for detect processor revision by other function
            EntryCpuid[] altentries = container.buildEntries( 0x80860002 );
            if ( ( altentries != null )&&( altentries.length == 1 ) )
                {
                alternative = altentries[0].eax;
                }
            }

        s2 = "unknown";
        if      ( ( majorVersion == 1 )&&( minorVersion == 1 ) )
            s2 = "TM3200";
        else if ( ( majorVersion == 1 )&&( minorVersion == 2 ) )
            s2 = "TM5400";
        else if ( ( majorVersion == 1 )&&( minorVersion == 3 ) )
            {
            if      ( cache == 256 ) s2 = "TM5400";
            else if ( cache == 512 ) s2 = "TM5600";
            }
        else if ( /* ( ( majorVersion == 1 )&&( minorVersion == 3 ) && 
                       ( majorMask == 0 )                           ) ||  */
                  ( ( majorVersion == 1 )&&( minorVersion == 4 ) ) ||
                  ( ( majorVersion == 1 )&&( minorVersion == 5 ) ) ) 
            {
            if      ( cache == 256 ) s2 = "TM5500";
            else if ( cache == 512 ) s2 = "TM5800";
            }
        else if ( alternative == 0x24C01101 )
            s2 = "TM8000";
        
        if ( alternative == 0 )
            {
            s1 = String.format( "%d.%d-%d.%d-%d, %s", 
                 majorVersion, minorVersion, majorMask, minorMask, 
                 entries[0].ecx, s2 );
            }
        else
            {
            s1 = String.format( "By F=80860002h EAX=%08Xh, %s", 
                 alternative, s2 );
            }
        dr.strings.get(0)[4] = s1;
        a.addAll( dr.strings );
        a.add( interval );
        
        // ECX: Transmeta processor frequency in MHz
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        int frequency = dr.values[0];
        s1 = String.format( "%d MHz", frequency );
        dr.strings.get(0)[4] = s1;
        a.addAll( dr.strings );
        a.add( interval );
        
        // EDX: Transmeta processor features bitmap
        strings = decodeBitmap( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( strings );
        
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
