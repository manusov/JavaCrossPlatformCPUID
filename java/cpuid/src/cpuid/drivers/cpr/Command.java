//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// Interface for CPR model "Command" submodel.

package cpuid.drivers.cpr;

public interface Command 
{
String getCommandShortName( long[] array );
String getCommandLongName( long[] array );
String[] getCommandUp1( long[] array );
String[][] getCommandText1( long[] array );
String[] getCommandUp2( long[] array );
String[][] getCommandText2( long[] array );
boolean getCommandSupported( long[] array );
}
