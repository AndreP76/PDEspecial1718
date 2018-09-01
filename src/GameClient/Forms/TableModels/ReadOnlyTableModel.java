package GameClient.Forms.TableModels;

import javax.swing.table.DefaultTableModel;

public class ReadOnlyTableModel extends DefaultTableModel {
    public ReadOnlyTableModel(Object[][] values, String[] titles) {
        super(values, titles);
    }

    @Override
    public boolean isCellEditable(int i, int i1) {
        return false;
    }
}
