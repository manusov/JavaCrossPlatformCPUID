/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Interface adapter for CPR model "Command" submodel.
Implements stubs for CPR.Command interface,
all methods can accept device state binary array.
*/

package cpuid.drivers.cpr;

public abstract class CommandAdapter implements Command 
{

@Override public String getCommandShortName( long[] array )   { return null ; }
@Override public String getCommandLongName( long[] array )    { return null ; }
@Override public String[] getCommandUp1( long[] array )       { return null ; }
@Override public String[][] getCommandText1( long[] array )   { return null ; }
@Override public String[] getCommandUp2( long[] array )       { return null ; }
@Override public String[][] getCommandText2( long[] array )   { return null ; }
@Override public boolean getCommandSupported( long[] array )  { return false; }

}
