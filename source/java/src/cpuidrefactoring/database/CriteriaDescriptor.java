/*
CPUID Utility. (C)2020 IC Book Labs
------------------------------------
This file contains Processors and Hypervisors
data exported from Todd Allen CPUID project.
Some variables and functions names not compliant with java
naming conventions, this fields using original C/C++ naming.
-----------------------------------------------
Criteria descriptors for processors detection.
tfms = Standard Type, Family, Model, Stepping, 
       CPUID standard function 00000001h, register EAX
bi   = Brand Index, CPUID function 00000001h, register EBX,
       only bits[7-0] must be selected by AND mask inside 
       CriteriaDescriptor.detector() method called by detectorHelper.
*/

package cpuidrefactoring.database;

import static cpuidrefactoring.database.DefineArithmetic.*;

class CriteriaDescriptor 
{
    final String name;
    final WriteParms writer;
    boolean detector( int tfms, int bi )
    {
        return true; 
    }
    CriteriaDescriptor( String name )
    {
        this.name = name;
        this.writer = null;
    }
    CriteriaDescriptor( WriteParms writer )
    {
        this.name = null;
        this.writer = writer;
    }
}

class F extends CriteriaDescriptor
{
    final int extendedFamily;
    final int family;
    @Override boolean detector( int tfms, int bi ) 
    { 
        return __F( tfms ) == _XF( extendedFamily ) +_F( family );
    }
    F( int xf, int f, String name )
    {
        super( name );
        extendedFamily = xf;
        family = f;
    }
    F( int xf, int f, WriteParms writer )
    {
        super( writer );
        extendedFamily = xf;
        family = f;
    }
}

class FM extends F
{
    final int extendedModel;
    final int model;
    @Override boolean detector( int tfms, int bi ) 
    { 
        return __FM( tfms ) == _XF( extendedFamily ) + _XM( extendedModel ) +
                               _F( family ) + _M( model );
    }
    FM( int xf, int f, int xm, int m, String name )
    {
        super( xf, f, name );
        extendedModel = xm;
        model = m;
    }
    FM( int xf, int f, int xm, int m, WriteParms writer )
    {
        super( xf, f, writer );
        extendedModel = xm;
        model = m;
    }
}

class FMS extends FM
{
    final int stepping;
    @Override boolean detector( int tfms, int bi ) 
    { 
        return __FMS( tfms ) == _XF( extendedFamily ) + _XM( extendedModel ) + 
                                _F( family ) + _M( model ) + _S( stepping );
    }
    FMS( int xf, int f, int xm, int m, int s, String name )
    {
        super( xf, f, xm, m, name );
        stepping = s;
    }
    FMS( int xf, int f, int xm, int m, int s, WriteParms writer )
    {
        super( xf, f, xm, m, writer );
        stepping = s;
    }
}

class TFM extends FM
{
    final int type;
    @Override boolean detector( int tfms, int bi ) 
    { 
        return __TFM( tfms ) == _T( type ) + 
                                _XF( extendedFamily ) + _XM( extendedModel ) +
                                _F( family ) + _M( model ); 
    }
    TFM( int t, int xf, int f, int xm, int m, String name )
    {
        super( xf, f, xm, m, name );
        type = t;
    }
    TFM( int t, int xf, int f, int xm, int m, WriteParms writer )
    {
        super( xf, f, xm, m, writer );
        type = t;
    }
}

class FMB extends FM 
{
    final int brandId;
    @Override boolean detector( int tfms, int bi ) 
    { 
        return __B( bi ) == _B( brandId ) && super.detector( tfms, bi );
    }
    FMB( int xf, int f, int xm, int m, int bi, String name )
    {
        super( xf, f, xm, m, name );
        brandId = bi;
    }
}

class FMSB extends FMS
{
    final int brandId;
    @Override boolean detector( int tfms, int bi ) 
    { 
        return __B( bi ) == _B( brandId ) && super.detector( tfms, bi );
    }
    FMSB( int xf, int f, int xm, int m, int s, int bi, String name )
    {
        super( xf, f, xm, m, s, name );
        brandId = bi;
    }
}

class FMQ extends FM
{
    final boolean question;
    @Override boolean detector( int tfms, int bi )
    { 
        return question && super.detector( tfms, bi );
    }
    FMQ( int xf, int f, int xm, int m, boolean q, String name )
    {
        super( xf, f, xm, m, name );
        question = q;
    }
    FMQ( int xf, int f, int xm, int m, boolean q, WriteParms writer )
    {
        super( xf, f, xm, m, writer );
        question = q;
    }
}

class FMSQ extends FMS
{
    final boolean question;
    @Override boolean detector( int tfms, int bi )
    { 
        return question && super.detector( tfms, bi );
    }
    FMSQ( int xf, int f, int xm, int m, int s, boolean q, String name )
    {
        super( xf, f, xm, m, s, name );
        question = q;
    }
    FMSQ( int xf, int f, int xm, int m, int s, boolean q, WriteParms writer )
    {
        super( xf, f, xm, m, s, writer );
        question = q;
    }
}

class FQ extends F
{
    final boolean question;
    @Override boolean detector( int tfms, int bi ) 
    { 
        return question; 
    }
    FQ( int xf, int f, boolean q, String name )
    {
        super( xf, f, name );
        question = q;
    }
    FQ( int xf, int f, boolean q, WriteParms writer )
    {
        super( xf, f, writer );
        question = q;
    }
}
    
class B extends CriteriaDescriptor
{
    final int brandId;
    @Override boolean detector( int tfms, int bi ) 
    { 
        return __B( bi ) == _B( brandId );
    }
    B( int bi, String name )
    {
        super( name );
        brandId = bi;
    }

}

interface WriteParms
{
    void writeParms();
}
