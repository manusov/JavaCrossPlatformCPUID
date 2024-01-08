/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Extended Function
80000026h = AMD extended CPU topology.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid80000026 extends ParameterFunctionCpuid
{
Cpuid80000026()
    { setFunction( 0x80000026 ); }

@Override String getLongName()
    { return "AMD extended CPU topology"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Mask width, shift extended APIC ID to topology ID" ,  4 ,  0 } ,
      { "Efficiency ranking available"                      , 29 , 29 } ,
      { "Heterogeneous cores"                               , 30 , 30 } ,
      { "Asymmetric topology"                               , 31 , 31 } };
private final static Object[][] DECODER_EBX =
    { { "Number of logical processors at the hierarchy level" , 15 ,  0 } ,
      { "Power efficiency ranking"                            , 23 , 16 } ,
      { "Native model ID"                                     , 27 , 24 } ,
      { "Core type"                                           , 31 , 28 } };
private final static Object[][] DECODER_ECX =
    { { "Input ECX[7-0]"                                      ,  7 , 0 } ,
      { "Level type"                                          , 15 , 8 } };
private final static Object[][] DECODER_EDX =
    { { "Extended APIC ID"                                    , 31 , 0 } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        int index = 0;
        while ( index < entries.length )
            {
            // EAX
            dr = decodeBitfields( "EAX", DECODER_EAX, entries[index].eax );
            a.addAll( dr.strings );
            // EBX
            dr = decodeBitfields( "EBX", DECODER_EBX, entries[index].ebx );
            dr.strings.get(0)[4] = String.format( "%d", dr.values[0] );
            a.addAll( dr.strings );
            // ECX
            dr = decodeBitfields( "ECX", DECODER_ECX, entries[index].ecx );
            int levelType = dr.values[1];
            String levelName;
            switch(levelType)
            {
                case 1:
                    levelName = "core";
                    break;
                case 2:
                    levelName = "complex";
                    break;
                case 3:
                    levelName = "die";
                    break;
                case 4:
                    levelName = "socket";
                    break;
                default:
                    levelName = "reserved";
                    break;
            }
            dr.strings.get(1)[4] = String.format( "%s", levelName );
            a.addAll( dr.strings );
            // EDX
            dr = decodeBitfields( "EDX", DECODER_EDX, entries[index].edx );
            a.addAll( dr.strings );
            index++;
            if (index < entries.length )
                {
                a.add( interval );    
                }
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }
}
