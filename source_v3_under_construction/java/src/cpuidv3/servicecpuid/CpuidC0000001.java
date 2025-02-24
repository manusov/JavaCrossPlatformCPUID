/* 

Java cross-platform CPUID Utility.
https://github.com/manusov/JavaCrossPlatformCPUID/tree/master
https://github.com/manusov?tab=repositories
No copyright. Information belongs to Universe.

Class for support CPUID Vendor Specific Function C0000001h =
VIA (Centaur) vendor-specific: VIA (Centaur) vendor extended features flags.

*/

package cpuidv3.servicecpuid;

import java.util.ArrayList;

class CpuidC0000001 extends ParameterFunctionCpuid
{
CpuidC0000001()
    { setFunction( 0xC0000001 ); }

@Override String getLongName()
    { return "VIA (Centaur) vendor extended features flags"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Vendor-specific parameter"                 , 31 , 0 } };
private final static String[][] DECODER_EDX =
    { { "AIS"     , "Alternate instruction set"                 } ,  // bit 0
      { "AIS EN"  , "Alternate instruction set enabled"         } ,
      { "RNG"     , "Random number generator"                   } ,
      { "RNG EN"  , "Random number generator enabled"           } ,
      { "LH MSR"  , "LongHaul MSR 0000_110Ah"                   } ,
      { "FEMMS"   , "FEMMS instruction"                         } ,
      { "ACE"     , "Advanced cryptography engine"              } ,
      { "ACE EN"  , "Advanced cryptography engine enabled"      } ,
      { "ACE2"    , "Montgomery multiplier/hash (ACE2)"         } ,  // bit 8
      { "ACE2 EN" , "Montgomery multiplier/hash (ACE2) enabled" } ,
      { "PHE"     , "Padlock hash engine"                       } ,
      { "PHE EN"  , "Padlock hash engine enabled"               } ,
      { "PMM"     , "Padlock montgomery multiplication"         } ,
      { "PMM EN"  , "Padlock montgomery multiplication enabled" } ,  // bit 13
      { "x"       , "Reserved"                                  } ,  // bit 14
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } ,
      { "x"       , "Reserved"                                  } }; // bit 31

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX: VIA (Centaur) vendor-specific parameter
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( dr.strings );
        a.add( interval );
        // EDX: VIA (Centaur) processor features bitmap
        strings = decodeBitmap( "EDX", DECODER_EDX, entries[0].edx );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
