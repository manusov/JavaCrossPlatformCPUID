/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
Class for support CPUID Vendor Specific Function
C0000000h = VIA (Centaur) vendor-specific: 
            VIA C7 actual performance data.
*/

/*
TODO.
Temperature decoding (EAX) required update.
Detect wrong values. Multipliers can be zero or too big.
Typo corrected or not (EDX) ?
Multiplier helper, not private as voltage helper, used also for C0000004h.
*/

package cpuidrefactoring.devicecpuid;

import cpuidrefactoring.database.VendorDetectPhysical.VENDOR_T;
import static cpuidrefactoring.database.VendorDetectPhysical.VENDOR_T.VENDOR_VIA;
import java.util.ArrayList;

public class CpuidC0000002 extends ParameterFunctionCpuid
{
CpuidC0000002()
    { setFunction( 0xC0000002 ); }

@Override String getLongName()
    { return "VIA C7 actual performance data"; }

// Control tables for results decoding
private final static Object[][] DECODER_EAX =
    { { "Processor temperature encoded value"  , 31 , 0 } };
private final static Object[][] DECODER_EBX =
    { { "Lowest clock ratio"                   , 31 , 24 } ,   // parm #0
      { "Performance control MSR transition"   , 21 , 20 } ,
      { "Thermal monitor 2 transition"         , 19 , 19 } ,
      { "Thermal monitor 2 transition"         , 18 , 18 } ,
      { "Voltage transition in progress"       , 17 , 17 } ,
      { "Clock ratio transition in progress"   , 16 , 16 } ,
      { "Actual clock multipler"               , 15 ,  8 } ,   // parm #6
      { "Input voltage"                        ,  7 ,  0 } };  // parm #7
final static Object[][] DECODER_ECX =
    { { "Lowest clock multiplier"              , 31, 24 } ,    // parm #0
      { "Lowest voltage"                       , 23, 16 } ,
      { "Highest clock multiplier"             , 15,  8 } ,
      { "Highest voltage"                      ,  7,  0 } };   // parm #3
private final static Object[][] DECODER_EDX =
    { { "Actual clock multiplier"              , 26, 22 } ,    // parm #0
      { "APIC agent ID"                        , 21, 20 } ,
      { "Input front side bus clock"           , 19, 18 } ,    // Typo corrected or not ?
      { "APIC cluster ID"                      , 17, 16 } ,
      { "MB reset vector"                      , 14, 14 } };   // parm #4

@Override String[][] getParametersList()
    {
    DecodeReturn dr;
    String[] interval = new String[] { "", "", "", "", "" };
    String s;
    ArrayList<String[]> a = new ArrayList<>();
    
    VENDOR_T t = container.getCpuVendor();
    boolean via = ( t == VENDOR_VIA );
    
    if ( ( entries != null )&&( entries.length > 0 ) )
        {
        // EAX: VIA C7 processor actual temperature encoded value
        dr = decodeBitfields( "EAX", DECODER_EAX, entries[0].eax );
        s = "?";
        if ( via )
            {
            int tm  = dr.values[0];
            int tm1 = tm & 0xFF;
            int tm2 = ( tm >> 8 ) & 0xFFFFFF;
            double temp;
            if    ( tm1 != 0 ) temp = ( (double)tm ) / 256.0;
            else               temp = ( (double)tm2 );
            if ( ( temp > 0 ) && ( temp < 200 ) ) 
                s = String.format( "%.1f C", temp );
            }
        dr.strings.get(0)[4] = s;
        a.addAll( dr.strings );
        a.add( interval );
        // EBX: VIA C7 processor actual operational paramerers
        dr = decodeBitfields( "EBX", DECODER_EBX, entries[0].ebx );
        dr.strings.get(0)[4] = ratioHelper( dr.values[0] );
        dr.strings.get(6)[4] = ratioHelper( dr.values[6] );
        dr.strings.get(7)[4] = voltageHelper ( dr.values[7] );
        a.addAll( dr.strings );
        a.add( interval );
        // ECX: VIA C7 processor minimum/maximum operational paramerers
        dr = decodeBitfields( "ECX", DECODER_ECX, entries[0].ecx );
        dr.strings.get(0)[4] = ratioHelper( dr.values[0] );
        dr.strings.get(1)[4] = voltageHelper ( dr.values[1] );
        dr.strings.get(2)[4] = ratioHelper( dr.values[2] );
        dr.strings.get(3)[4] = voltageHelper ( dr.values[3] );
        a.addAll( dr.strings );
        a.add( interval );
        // EDX: VIA C7 processor platform configuration paramerers
        dr = decodeBitfields( "EDX", DECODER_EDX, entries[0].edx );
        dr.strings.get(0)[4] = ratioHelper( dr.values[0] );
        int fsbclk = dr.values[2];
        if ( fsbclk < BUS_CLOCK.length ) s = BUS_CLOCK[fsbclk];
        else s = "?";
        dr.strings.get(2)[4] = s;
        int reset = dr.values[4];
        if ( reset < RESET_VECTOR.length ) s = RESET_VECTOR[reset];
        else s = "?";
        dr.strings.get(4)[4] = s;
        a.addAll( dr.strings );
        a.add( interval );
        }
    return a.isEmpty() ? 
        super.getParametersList() : a.toArray( new String[a.size()][] );
    }

private final static String[] 
        BUS_CLOCK = { "100 MHz", "133 MHz", "200 MHz", "166 MHz" };
private final static String[] 
        RESET_VECTOR =  { "FFFFFFF0h", "000FFFF0h" };

// helpers used by this class and some other classes ( CpuidC0000004.java 

String voltageHelper( int x )
    {
    String s = "n/a";
    if ( x != 0 )
        {
        int voltage = ( x << 4 ) + 700;
        s = String.format( "%d mV", voltage );
        }
    return s;
    }

String ratioHelper( int x )
    {
    String s = "n/a";
    if ( x != 0 )
        {
        s = String.format( "%d x", x );
        }
    return s;
    }

}
