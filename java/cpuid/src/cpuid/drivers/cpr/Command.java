/*---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
Interface for CPR model "Command" submodel.
Declares CPR.Command interface,
all methods can accept device state binary array.
*/

package cpuid.drivers.cpr;

public interface Command 
{
String getCommandShortName( long[] array );   // short brief name of command
String getCommandLongName( long[] array );    // long full name of command
String[] getCommandUp1( long[] array );       // up string for decoded content
String[][] getCommandText1( long[] array );   // decoded content
String[] getCommandUp2( long[] array );       // up string for hexadecimal dump
String[][] getCommandText2( long[] array );   // hexadecimal dump
boolean getCommandSupported( long[] array );  // return command support flag
}
