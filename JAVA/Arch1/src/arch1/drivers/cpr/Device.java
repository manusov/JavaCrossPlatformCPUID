//---------- CPUID Utility. (C)2017 IC Book Labs -------------------------------
// Interface for CPR model, entire Device submodel.

package arch1.drivers.cpr;

public interface Device 
{

void setBinary(long[] x);
long[] getBinary();
boolean initBinary();
boolean parseBinary();

String getSummaryName();
String[] getSummaryUp();
String[][] getSummaryText();

String getDumpName();
String[] getDumpUp();
String[][] getDumpText();

int getCommandsCount();
String getCommandShortName(int x);
String getCommandLongName(int x);
boolean getCommandSupported(int x);
String[] getCommandUp1(int x);
String[][] getCommandText1(int x);
String[] getCommandUp2(int x);
String[][] getCommandText2(int x);

int getPinsCount();
String getPinName(int x);
boolean getPinSupported(int x);
String[] getPinUp1(int x);
String[][] getPinText1(int x);
String[] getPinUp2(int x);
String[][] getPinText2(int x);

int getRegistersCount();
String getRegisterName(int x);
boolean getRegisterSupported(int x);
String[] getRegisterUp1(int x);
String[][] getRegisterText1(int x);
String[] getRegisterUp2(int x);
String[][] getRegisterText2(int x);
    
}
