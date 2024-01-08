/* 
CPUID Utility. Refactoring 2024. (C)2024 Manusov I.V.
------------------------------------------------------

*/

package cpuidv2.applications;

import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import cpuidv2.CPUIDv2;

public class ApplicationsManager 
{
private final Application[] applications = 
    { 
    new InfoCpuid(),
    new InfoClk(),
    new InfoXcr(),
    new InfoOs(),
    new InfoJvm()
    };

private final int[][] appKeys =
    {
        { 0 , 0 },
        { 0 , 1 },
        { 0 , 2 },
        { 1 , 0 },
        { 2 , 0 },
        { 3 , 0 },
        { 4 , 0 }
    };

public String[] getTabNames()
    {
    ArrayList<String> names = new ArrayList<>();
    for( Application ap : applications )
        {
        String[] groupNames = ap.getPanelNames();
        if( groupNames != null )
            {
            for( String tabName : groupNames )
                {
                if( tabName != null )
                    {
                    names.add(tabName);
                    }
                }
            }
        }
    return names.isEmpty() ? null : names.toArray( new String[names.size()] );
    }

public Icon[] getTabIcons()
    {
    ArrayList<String> names = new ArrayList<>();
    for( Application ap : applications )
        {
        String[] groupNames = ap.getPanelIcons();
        if( groupNames != null )
            {
            for( String tabName : groupNames )
                {
                if( tabName != null )
                    {
                    names.add(tabName);
                    }
                }
            }
        }
    String path = CPUIDv2.getResourcePackage();
    ArrayList<Icon> icons = new ArrayList<>();
    for( String name : names )
        {
        try {
            Icon icon = new
                javax.swing.ImageIcon(getClass().getResource( path + name ));
            icons.add(icon);
            }
        catch (Exception e) { System.out.println(e); }
        }
    return icons.isEmpty() ? null : icons.toArray( new Icon[icons.size()] );
    }

public JPanel[] getTabPanels()
    {
    ArrayList<JPanel> panels = new ArrayList<>();
    for( Application ap : applications )
        {
        JPanel[] groupPanels = ap.getPanels();
        if( groupPanels != null )
            {
            for( JPanel panel : groupPanels )
                {
                if( panel != null )
                    {
                    panels.add(panel);
                    }
                }
            }
        }
    return panels.isEmpty() ? 
        null : panels.toArray( new JPanel[panels.size()] );
    }

public Boolean[] getTabActives()
    {
    ArrayList<Boolean> actives = new ArrayList<>();
    for( Application ap : applications )
        {
        boolean[] groupActives = ap.getPanelActives();
        if( groupActives != null )
            {
            for( boolean b : groupActives )
                {
                actives.add(b);
                }
            }
        }
    return actives.isEmpty() ? 
        null : actives.toArray( new Boolean[actives.size()] );
    }

public String[] getTabTips()
    {
    ArrayList<String> tips = new ArrayList<>();
    for( Application ap : applications )
        {
        String[] groupTips = ap.getPanelTips();
        if( groupTips != null )
            {
            for( String tabTip : groupTips )
                {
                if( tabTip != null )
                    {
                    tips.add(tabTip);
                    }
                }
            }
        }
    return tips.isEmpty() ? null : tips.toArray( new String[tips.size()] );
    }

public void buildTabPanels()
    {
    for( Application ap : applications )
        {
        ap.rebuildPanels();
        }
    }

public AbstractTableModel[] getTabModels() 
    { 
    ArrayList<AbstractTableModel> models = new ArrayList<>();
    for( Application ap : applications )
        {
        AbstractTableModel[] groupModels = ap.getPanelModels();
        if( groupModels != null )
            {
            for( AbstractTableModel tabModel : groupModels )
                {
                if( tabModel != null )
                    {
                    models.add( tabModel );
                    }
                }
            }
        }
    return models.isEmpty() ? null : 
            models.toArray( new AbstractTableModel[models.size()] );
    }

private final static int CPUID_APP_INDEX = 0;

public void rebuildAfterCpuidReload( boolean getPhysical )
    {
    Application cpuidApplication = applications[CPUID_APP_INDEX];
    JPanel[] panels = cpuidApplication.getPanels();
    for( JPanel panel : panels )
        {
        panel.removeAll();
        }
    
    if( getPhysical )
        {
        cpuidApplication.rebuildPanels();
        }
    else
        {
        cpuidApplication.refreshPanels();
        }
    
    for( JPanel panel : panels )
        {
        panel.revalidate();
        panel.repaint();
        }
    }

public AbstractTableModel[] getReportThisTableModels( int index )
    {
    AbstractTableModel[] thisReportModels = null;
    if ( index < appKeys.length )
        {
        int appIndex = appKeys[index][0];
        int subIndex = appKeys[index][1];
        Application application = applications[appIndex];
        thisReportModels = application.getReportThisModels( subIndex );
        }
    return thisReportModels;
    }

}
