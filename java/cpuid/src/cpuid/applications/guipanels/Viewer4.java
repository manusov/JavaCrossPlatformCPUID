//---------- CPUID Utility. (C)2018 IC Book Labs -------------------------------
// Viewer model 4, two tables.

package cpuid.applications.guipanels;

import javax.swing.*;
import javax.swing.table.*;

public class Viewer4 extends ViewPanel 
{
private final JPanel p;                        // complex panel
private final JSplitPane pp;                   // split panel
private final JScrollPane sp1, sp2;            // scroll panels
private final JTable table1, table2;           // tables
private final AbstractTableModel atm1, atm2;   // tables models
private final BoxLayout bl;                    // layout manager
private static DefaultTableCellRenderer 
        mRenderer1, mRenderer2;                // this for centering

public Viewer4
    ( int x, int y, AbstractTableModel z1, AbstractTableModel z2 )
    {
    atm1=z1; atm2=z2;
    // Built panel components
    table1 = new JTable(atm1);
    table2 = new JTable(atm2);
    SetupTableSize.optimizeColumnsWidths( table1, x-35 );
    SetupTableSize.optimizeColumnsWidths( table2, x-35 );
    sp1 = new JScrollPane(table1);
    sp2 = new JScrollPane(table2);
    // Centering table(s)
    mRenderer1 = new DefaultTableCellRenderer();
    mRenderer2 = new DefaultTableCellRenderer();
    mRenderer1.setHorizontalAlignment(SwingConstants.CENTER);
    mRenderer2.setHorizontalAlignment(SwingConstants.CENTER);
    for (int i=0; i<table1.getColumnCount(); i++)
        { table1.getColumnModel().getColumn(i).setCellRenderer(mRenderer1); }
    for (int i=0; i<table2.getColumnCount(); i++)
        { table2.getColumnModel().getColumn(i).setCellRenderer(mRenderer2); }
    // Built split panel
    pp = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true );
    pp.setOneTouchExpandable(true);
    pp.setDividerSize(8);
    pp.setDividerLocation(y/2);
    pp.setTopComponent(sp1);
    pp.setBottomComponent(sp2);
    // Built panel and set layout
    p = new JPanel();
    bl = new BoxLayout(p, BoxLayout.X_AXIS);
    p.setLayout(bl);
    p.add(pp);
    }

// Return panel
@Override public JPanel getP()
    { return p; }

// Return components array
@Override public JComponent[] getComponents()
    {
    JComponent[] x = new JComponent[2];
    x[0] = table1;
    x[1] = table2;
    return x;    
    }

}
