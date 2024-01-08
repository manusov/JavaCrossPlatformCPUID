/*
CPUID Utility. (C)2022 IC Book Labs
------------------------------------
Class for support CPUID Standard Function
0000001Dh = Intel AMX Tile and Palette information.
*/

package cpuidv2.cpuidfunctions;

import java.util.ArrayList;

class Cpuid0000001D extends ParameterFunctionCpuid
{
Cpuid0000001D()
    { setFunction( 0x0000001D );  }

@Override String getLongName()
    { return "Intel AMX Tile and Palette information"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX_SUBFUNCTION_0 =
    { { "Maximum palette, highest numbered palette sub-leaf" , 31 , 0  } };
private final static Object[][] DECODER_EAX_SUBFUNCTION_1 =
    { { "Palette %d, bytes per tile"   , 31 , 16 } ,
      { "Palette %d, total tile bytes" , 15 ,  0 } };
private final static Object[][] DECODER_EBX_SUBFUNCTION_1 =
    { { "Palette %d, maximum names, number of tile registers" , 31 , 16 } ,
      { "Palette %d, bytes per row"                           , 15 , 0  } };
private final static Object[][] DECODER_ECX_SUBFUNCTION_1 =
    { { "Palette %d, maximum rows"                            , 15 , 0  } };

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    ArrayList<String[]> a = new ArrayList<>();
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX, subfunction 0
        dr = decodeBitfields
            ( "EAX", DECODER_EAX_SUBFUNCTION_0, entries[0].eax );
        a.addAll( dr.strings );
        // scan for subfunctions from 1
        int index = 1;
        while ( ( entries.length > index ) && 
                ( entries[index].subfunction == index ) )
            {
            a.add( interval );
            // EAX, subfunction [index]
            Object[][] obj = buildStrings( DECODER_EAX_SUBFUNCTION_1, index);
            dr = decodeBitfields( "EAX", obj, entries[index].eax );
            dr.strings.get(0)[4] = String.format("%d Bytes", dr.values[0]);
            dr.strings.get(1)[4] = String.format("%d Bytes", dr.values[1]);
            a.addAll( dr.strings );
            // EBX, subfunction [index]
            obj = buildStrings( DECODER_EBX_SUBFUNCTION_1, index);
            dr = decodeBitfields( "EBX", obj, entries[index].ebx );
            dr.strings.get(0)[4] = String.format("%d Registers", dr.values[0]);
            dr.strings.get(1)[4] = String.format("%d Bytes", dr.values[1]);
            a.addAll( dr.strings );
            // ECX, subfunction [index]
            obj = buildStrings( DECODER_ECX_SUBFUNCTION_1, index);
            dr = decodeBitfields( "ECX", obj, entries[index].ecx );
            dr.strings.get(0)[4] = String.format("%d Rows", dr.values[0]);
            a.addAll( dr.strings );
            index++;
            }
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }

// helper
private Object[][] buildStrings( Object[][] obj1, int index )
    {
    int n = obj1.length;
    Object[][] obj2 = new Object[n][3];
    for(int i=0; i<n; i++)
        {
        obj2[i][0] = String.format( (String)obj1[i][0], index);  // string
        obj2[i][1] = obj1[i][1];                                 // integer
        obj2[i][2] = obj1[i][2];                                 // integer
        }
    return obj2;
    }
}
