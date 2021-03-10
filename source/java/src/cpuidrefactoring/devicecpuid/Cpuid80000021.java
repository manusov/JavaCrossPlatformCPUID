/*
CPUID Utility. (C)2021 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000021h = AMD architectural differences.
*/

package cpuidrefactoring.devicecpuid;

import java.util.ArrayList;

class Cpuid80000021 extends ParameterFunctionCpuid
{
Cpuid80000021()
    { setFunction( 0x80000021 ); }

@Override String getLongName()
    { return "AMD architectural differences"; }

// Control tables for results decoding
private final static String[][] DECODER_EAX =
    { { "x"         , "Reserved"          } ,                         // bit 0
      { "x"         , "Reserved"          } ,
      { "LAS"       , "LFENCE always serializing"    } ,              // bit 2
      { "SPCL"      , "SMM page configuration lock"  } ,              // bit 3
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "NSCB"      , "Null selector clear base"      } ,             // bit 6
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,                         // bit 8
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,                         // bit 16
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,                         // bit 24
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } ,
      { "x"         , "Reserved"          } };                        // bit 31

@Override String[][] getParametersList()
    {
    ArrayList<String[]> strings;
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX
        strings = decodeBitmap( "EAX", DECODER_EAX, entries[0].eax );
        a.addAll( strings );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
