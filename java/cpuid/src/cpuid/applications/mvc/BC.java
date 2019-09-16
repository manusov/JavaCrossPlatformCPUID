/*---------- CPUID Utility. (C)2019 IC Book Labs -------------------------------
Interface for BuiltController module.
Declares public methods:
get GUI object(s) data model(s) and get GUI object(s) view components.
*/

package cpuid.applications.mvc;

public interface BC 
{
public BM getModel();
public BV getView();
}
