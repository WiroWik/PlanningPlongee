import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;


public class PlanningGUI implements ActionListener{

    public final String PORT = "3306";
    public final String USER = "root";
    public final String PASSWORD = "";

    JFrame frame;
    JPanel saveCard, editCard, cards, savePanel, editPanel;
    JLabel nomCompetLabel,dateCompetLabel,imageCompetLabel,paysCompetLabel,villeCompetLabel,adresseCompetLabel;
    JTextField nomCompetTextField,imageCompetTextField,paysCompetTextField,villeCompetTextField,adresseCompetTextField,feedbackField;
    UtilDateModel model = new UtilDateModel();
    JDatePanelImpl datePanel;
    JDatePickerImpl datePicker;
    JButton saveButton, button;
    JMenu gestionBBDMenu;
    JMenuItem saveMenuItem, editMenuItem;
    CardLayout cl;


    public PlanningGUI() {
        frame = new JFrame("Planning");

        saveCard = new JPanel();
        editCard = new JPanel();
        savePanel = new JPanel(new BorderLayout());
        editPanel = new JPanel(new BorderLayout());
        cards = new JPanel(new CardLayout());
        
        
        /*saveCard */
        JLabel titreLabel = new JLabel("Enregistrer une compétition :", JLabel.CENTER);
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JMenuBar menuBar = new JMenuBar();
        
        nomCompetLabel = new JLabel("Nom de la compétition");
        dateCompetLabel = new JLabel("Date");
        imageCompetLabel = new JLabel("Image d'illustration (URL)");
        paysCompetLabel = new JLabel("Pays");
        villeCompetLabel = new JLabel("Ville");
        adresseCompetLabel = new JLabel("Adresse");
        JLabel[] formLabels = {nomCompetLabel,dateCompetLabel,imageCompetLabel,paysCompetLabel,villeCompetLabel,adresseCompetLabel};

        nomCompetTextField = new JTextField();
        Properties dateProperties = new Properties();
        dateProperties.put("text.today", "Aujourd'hui");
        dateProperties.put("text.month", "Mois");
        dateProperties.put("text.year", "Année");
        datePanel = new JDatePanelImpl(model, dateProperties);
        datePicker = new JDatePickerImpl(datePanel, new AbstractFormatter() {
            private String datePattern = "yyyy-MM-dd";
            private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

            @Override
            public Object stringToValue(String text) throws ParseException{
                return dateFormatter.parseObject(text);
            }

            @Override
            public String valueToString(Object value) {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }

            return "";
            }
        });
        imageCompetTextField = new JTextField();
        paysCompetTextField = new JTextField();
        villeCompetTextField = new JTextField();
        adresseCompetTextField = new JTextField();
        feedbackField = new JTextField();
        feedbackField.setEditable(false);
        JTextField[] formTextFields = {nomCompetTextField,imageCompetTextField,paysCompetTextField,villeCompetTextField,adresseCompetTextField,feedbackField};

        saveButton = new JButton("Enregistrer la compétition");
        saveButton.addActionListener(this);

        gestionBBDMenu = new JMenu("Gestion Planning");
        saveMenuItem = new JMenuItem("Enregistrer", 0);
        saveMenuItem.addActionListener(this);
        editMenuItem = new JMenuItem("Edition", 0);
        editMenuItem.addActionListener(this);
        gestionBBDMenu.add(saveMenuItem);
        gestionBBDMenu.add(editMenuItem);
        menuBar.add(gestionBBDMenu);

        

        /*Colonne 0*/
        /*Inserer les labels du formulaire */
        int y = 0;
        c.gridx = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10,10,0,0);
        for (JLabel currentLabel : formLabels) {
            
            c.gridy = y;
            formPanel.add(currentLabel,c);
            y++;
        }
        c.gridy = y;
        formPanel.add(saveButton, c);

        /*Colonne 1*/
        /*Inserer les champs du formulaire */
        y = 0;
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        int i = 0;
        for (JTextField currentTextField : formTextFields) {
            c.gridy = y;
            currentTextField.setColumns(10);
            formPanel.add(currentTextField,c);
            y++;

            if (i == 0) {
                c.gridy = y;
                y++;
                formPanel.add(datePicker,c);
            }
            i++;
            System.out.println(y);
        }

        /*editCard */
        JPanel planningTablePanel = generatePlanningTable();
        editCard.add(planningTablePanel);

        


        savePanel.add(titreLabel, BorderLayout.NORTH);
        savePanel.add(formPanel,BorderLayout.CENTER);
        saveCard.add(savePanel);
        cards.add("Enregistrer", saveCard);
        cards.add("Edition", editCard);
        cl = (CardLayout) cards.getLayout();

        frame.add(cards);
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720, 720);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new PlanningGUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == saveButton) {
            JDialog errorDialog = new JDialog(frame, "Error Dialog");
            JLabel errorMessageLabel = new JLabel();
            errorDialog.setSize(500, 100);
            errorDialog.add(errorMessageLabel);



            /*Connexion SQL */
            String url = "jdbc:mysql://localhost:" + PORT + "/sddv_plongee";
            Connection con;
            Statement stmt;
            String dateSQL = datePicker.getModel().getYear() + "-" + (datePicker.getModel().getMonth() + 1) + "-" + datePicker.getModel().getDay();
            String query = "INSERT INTO `competitions` (`id_compet`, `nom_compet`, `date_compet`, `image_compet`, `pays`, `ville`, `adresse`) VALUES (NULL,'" + nomCompetTextField.getText() + "', '" + dateSQL + "', '" + imageCompetTextField.getText() + "', '" + paysCompetTextField.getText() + "', '" + villeCompetTextField.getText() + "', '" + adresseCompetTextField.getText() + "');";

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");

            } catch(java.lang.ClassNotFoundException error) {
                System.err.print("ClassNotFoundException (try): ");
                System.err.println(error.getMessage());
            }
            
            try {
                con = DriverManager.getConnection(url, USER, PASSWORD);
                stmt = con.createStatement();
                stmt.executeUpdate(query);
                stmt.close();
                con.close();
                System.out.println("La bd a bien été mise à jour");
                feedbackField.setText("La compétition " + nomCompetTextField.getText() + " a bien été enregistré" );
            } catch(SQLException ex) {
                System.err.println("SQLException " + ex.getMessage());
                errorMessageLabel.setText("SQLException " + ex.getMessage());
                errorDialog.setVisible(true);
            }
            editCard.removeAll();
            JPanel planningTablePanel = generatePlanningTable();
            editCard.add(planningTablePanel);
        }

        if (e.getSource() == saveMenuItem) {
            System.out.println("save");
            cl.show(cards, "Enregistrer");
        }

        if (e.getSource() == editMenuItem) {
            System.out.println("edit");
            cl.show(cards, "Edition");
            
        }
    }

    public JPanel generatePlanningTable() {
        JPanel competTablePanel = new JPanel(new BorderLayout());
        JTable competTable, headerTable;
        String[] columnName = {"id_compet", "nom_compet", "date_compet", "image_compet", "pays", "ville", "adresse", "modifier", "supprimer"};
        

        /*Connexion SQL */
        String url = "jdbc:mysql://localhost:" + PORT + "/sddv_plongee";
        Connection con;
        Statement stmt, stmt2;
        String query = "SELECT * FROM competitions";
        String query2 = "SELECT COUNT(*) AS rowCount FROM competitions";
        

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch(java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException (try): ");
            System.err.println(e.getMessage());
        }
        
        try {
            con = DriverManager.getConnection(url, USER, PASSWORD);
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            stmt2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs2 = stmt2.executeQuery(query2);


            System.out.println("Compétitions");
            int i = 0;
            
            rs2.next();
            System.out.println(rs2.getInt("rowCount"));
            int tableSize = rs2.getInt("rowCount");
            Object[][] data = new String[tableSize][9];
            
            

            while (rs.next()) {
                Object[] rowValue = new String[9];
                
                String idCompet = rs.getString("id_compet");
                rowValue[0] = idCompet;

                String nomCompet = rs.getString("nom_compet");
                rowValue[1] = nomCompet;

                String dateCompet = rs.getString("date_compet");
                rowValue[2] = dateCompet;

                String imageCompet = rs.getString("image_compet");
                rowValue[3] = imageCompet;

                String paysCompet = rs.getString("pays");
                rowValue[4] = paysCompet;

                String villeCompet = rs.getString("ville");
                rowValue[5] = villeCompet;

                String adresseCompet = rs.getString("adresse");
                rowValue[6] = adresseCompet;
                
                rowValue[7] = "Modifier";

                rowValue[8] = "Supprimer";

                data[i] = rowValue;
                i++;
            }
            String[][] headercolumnName = {columnName};
            DefaultTableModel model = new DefaultTableModel(data, columnName) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return col == 7 || col == 8;
                }
            };
            competTable = new JTable(model);
            headerTable = new JTable(headercolumnName, columnName);

            ButtonColumn buttonColumn = new ButtonColumn(competTable, 7);
            ButtonColumn buttonColumn2 = new ButtonColumn(competTable, 8);

            headerTable.setEnabled(false);
            competTable.setCellSelectionEnabled(false);

            competTablePanel.add(headerTable, BorderLayout.NORTH);
            competTablePanel.add(competTable, BorderLayout.CENTER);

            stmt.close();
            stmt2.close();
            con.close();
        } catch(SQLException ex) {
            System.err.println("SQLException " + ex.getMessage());
        }
        
        return competTablePanel;
    }

    class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener{
        JTable table;
        JButton renderButton;
        JButton editButton;
        String text;
        JLabel nomCompetLabelEdit,dateCompetLabelEdit,imageCompetLabelEdit,paysCompetLabelEdit,aeroArivLabelEdit,villeCompetLabelEdit,adresseCompetLabelEdit;
        JTextField nomCompetTextFieldEdit,imageCompetTextFieldEdit,paysCompetTextFieldEdit,villeCompetTextFieldEdit,adresseCompetTextFieldEdit,feedbackFieldEdit;
        JButton modifButton;
        JDialog editDialog;
        UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanelEdit;
        JDatePickerImpl datePickerEdit;

        public ButtonColumn(JTable table, int column)
        {
            super();
            this.table = table;
            renderButton = new JButton();

            editButton = new JButton();
            editButton.setFocusPainted( false );
            editButton.addActionListener(this);

            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(column).setCellRenderer( this );
            columnModel.getColumn(column).setCellEditor( this );

            
            JLabel titreLabelEdit = new JLabel("Enregistrer une compétition :", JLabel.CENTER);

            
            JPanel formPanelEdit = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            nomCompetLabelEdit = new JLabel("Nom de la compétition");
            dateCompetLabelEdit = new JLabel("Date");
            imageCompetLabelEdit = new JLabel("Image de la compétition (URL)");
            paysCompetLabelEdit = new JLabel("Pays");
            villeCompetLabelEdit = new JLabel("Ville");
            adresseCompetLabelEdit = new JLabel("Adresse");

            JLabel[] formLabelsEdit = {nomCompetLabelEdit,dateCompetLabelEdit,imageCompetLabelEdit,paysCompetLabelEdit,villeCompetLabelEdit,adresseCompetLabelEdit};

            if (nomCompetTextFieldEdit == null)
                nomCompetTextFieldEdit = new JTextField();
            
            if (datePickerEdit == null) {
                Properties datePropertiesEdit = new Properties();
                datePropertiesEdit.put("text.today", "Aujourd'hui");
                datePropertiesEdit.put("text.month", "Mois");
                datePropertiesEdit.put("text.year", "Année");
                datePanelEdit = new JDatePanelImpl(model, datePropertiesEdit);
                datePickerEdit = new JDatePickerImpl(datePanel, new AbstractFormatter() {
                private String datePattern = "yyyy-MM-dd";
                private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);
    
                @Override
                public Object stringToValue(String text) throws ParseException{
                    return dateFormatter.parseObject(text);
                }
    
                @Override
                public String valueToString(Object value) {
                if (value != null) {
                    Calendar cal = (Calendar) value;
                    return dateFormatter.format(cal.getTime());
                }
    
                return "";
                }
                });
                
            }

            if (imageCompetTextFieldEdit == null)
                imageCompetTextFieldEdit = new JTextField();

            if (paysCompetTextFieldEdit == null)
                paysCompetTextFieldEdit = new JTextField();

            if (villeCompetTextFieldEdit == null)
                villeCompetTextFieldEdit = new JTextField();

            if (adresseCompetTextFieldEdit == null)
                adresseCompetTextFieldEdit = new JTextField();


            

            feedbackFieldEdit = new JTextField();
            feedbackFieldEdit.setEditable(false);
            JTextField[] formTextFieldsEdit = {nomCompetTextFieldEdit,imageCompetTextFieldEdit,paysCompetTextFieldEdit,villeCompetTextFieldEdit,adresseCompetTextFieldEdit};

            modifButton = new JButton("Modifier la compétition");
            modifButton.addActionListener(this);

            /*Colonne 0*/
            /*Inserer les labels du formulaire */
            int y = 0;
            c.gridx = 0;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(10,10,0,0);
            for (JLabel currentLabel : formLabelsEdit) {
                c.gridy = y;
                formPanelEdit.add(currentLabel,c);
                y++;
            }
            c.gridy = y;
            formPanelEdit.add(modifButton, c);

            /*Colonne 1*/
            /*Inserer les champs du formulaire */
            y = 0;
            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            int i = 0;
            for (JTextField currentTextField : formTextFieldsEdit) {
            c.gridy = y;
            currentTextField.setColumns(10);
            formPanelEdit.add(currentTextField,c);
            y++;

            if (i == 0) {
                c.gridy = y;
                y++;
                formPanelEdit.add(datePickerEdit,c);
            }
            i++;
            System.out.println(y);
        }

            editDialog = new JDialog(frame, "Edit Dialog");
            System.out.println(nomCompetTextFieldEdit.getText());

            editDialog.add(titreLabelEdit);
            editDialog.add(formPanelEdit);
            editDialog.setSize(500,500);
        }

        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            if (hasFocus)
            {
                renderButton.setForeground(table.getForeground());
                renderButton.setBackground(UIManager.getColor("Button.background"));
            }
            else if (isSelected)
            {
                renderButton.setForeground(table.getSelectionForeground());
                 renderButton.setBackground(table.getSelectionBackground());
            }
            else
            {
                renderButton.setForeground(table.getForeground());
                renderButton.setBackground(UIManager.getColor("Button.background"));
            }

            renderButton.setText( (value == null) ? "" : value.toString() );
            return renderButton;
        }

        public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column)
        {
            text = (value == null) ? "" : value.toString();
            editButton.setText( text );
            return editButton;
        }

        public Object getCellEditorValue()
        {
            return text;
        }

        public void actionPerformed(ActionEvent e){
            String nomCompetCurrent, dateCompetCurrent, imageCompetCurrent, paysCompetCurrent, villeCompetCurrent, adresseCompetCurrent = "";
            

            if (e.getActionCommand() == "Modifier") {
                nomCompetCurrent = null;
                dateCompetCurrent = null; 
                imageCompetCurrent = null;
                paysCompetCurrent = null;
                villeCompetCurrent = null;
                adresseCompetCurrent = null;
                editDialog.setVisible(true);
            }

            
            
            if (e.getActionCommand() == "Modifier la compétition") {
                System.out.println(table.getValueAt(table.getSelectedRow(), 0));

                JDialog errorDialog = new JDialog(frame, "Error Dialog");
                JLabel errorMessageLabel = new JLabel();

                errorDialog.setSize(500, 100);
                errorDialog.add(errorMessageLabel);

                
                String dateSQLEdit = datePickerEdit.getModel().getYear() + "-" + (datePickerEdit.getModel().getMonth() + 1) + "-" + datePickerEdit.getModel().getDay();
                
                if (nomCompetTextFieldEdit.getText().equals(""))
                    nomCompetCurrent = (String) table.getValueAt(table.getSelectedRow(), 1);
                else
                    nomCompetCurrent = nomCompetTextFieldEdit.getText();
                    
                if (dateSQLEdit.equals(null))
                    dateCompetCurrent = (String) table.getValueAt(table.getSelectedRow(), 2);
                else
                    dateCompetCurrent = dateSQLEdit;
                
                if (imageCompetTextFieldEdit.getText().equals(""))
                    imageCompetCurrent = (String) table.getValueAt(table.getSelectedRow(), 3);
                else
                    imageCompetCurrent = imageCompetTextFieldEdit.getText();

                if (paysCompetTextFieldEdit.getText().equals(""))
                    paysCompetCurrent = (String) table.getValueAt(table.getSelectedRow(), 4);
                else
                    paysCompetCurrent = paysCompetTextFieldEdit.getText();

                if (villeCompetTextFieldEdit.getText().equals(""))
                    villeCompetCurrent = (String) table.getValueAt(table.getSelectedRow(), 5);
                else
                    villeCompetCurrent = villeCompetTextFieldEdit.getText();

                if (adresseCompetTextFieldEdit.getText().equals(""))
                    adresseCompetCurrent = (String) table.getValueAt(table.getSelectedRow(), 6);
                else
                    adresseCompetCurrent = adresseCompetTextFieldEdit.getText();
                    

                /*Connexion SQL */
                String url = "jdbc:mysql://localhost:" + PORT + "/sddv_plongee";
                Connection con;
                Statement stmt;
                String query = "UPDATE `competitions` SET nom_compet = '" + nomCompetCurrent + "', date_compet = '" + dateCompetCurrent + "', image_compet = '" + imageCompetCurrent + "', pays = '" + paysCompetCurrent + "', ville = '" + villeCompetCurrent + "', adresse = '" + adresseCompetCurrent + "' WHERE id_compet = " + (String) table.getValueAt(table.getSelectedRow(), 0) + ";";

                
                
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");

                } catch(java.lang.ClassNotFoundException error) {
                    System.err.print("ClassNotFoundException (try): ");
                    System.err.println(error.getMessage());
                }
                
                try {
                    con = DriverManager.getConnection(url, USER, PASSWORD);
                    stmt = con.createStatement();
                    stmt.executeUpdate(query);
                    stmt.close();
                    con.close();
                    System.out.println("La bd a bien été mise à jour");
                    feedbackFieldEdit.setText("La competition a bien été enregistré" );
                    System.out.println(query);
                    editCard.removeAll();
                    JPanel competTablePanel = generatePlanningTable();
                    editCard.add(competTablePanel);
                    editCard.revalidate();
                    editDialog.dispose();
                } catch(SQLException ex) {
                    System.err.println("SQLException " + ex.getMessage());
                    errorMessageLabel.setText("SQLException " + ex.getMessage());
                    errorDialog.setVisible(true);
                    System.out.println(query);
                }

                
            }

            if (e.getActionCommand() == "Supprimer") {
                    /*Connexion SQL */
                    String urlDel = "jdbc:mysql://localhost:" + PORT + "/sddv_plongee";
                    Connection conDel;
                    Statement stmtDel;
                    String queryDel = "DELETE FROM competitions WHERE competitions.id_compet = '" + (String) table.getValueAt(table.getSelectedRow(), 0) + "'";
                    
                    
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");

                    } catch(java.lang.ClassNotFoundException error) {
                        System.err.print("ClassNotFoundException (try): ");
                        System.err.println(error.getMessage());
                    }
                    
                    try {
                        conDel = DriverManager.getConnection(urlDel, USER, PASSWORD);
                        stmtDel = conDel.createStatement();
                        stmtDel.executeUpdate(queryDel);
                        stmtDel.close();
                        conDel.close();
                        System.out.println("La bd a bien été mise à jour");
                        System.out.println(queryDel);
                        editCard.removeAll();
                        JPanel competTablePanel = generatePlanningTable();
                        editCard.add(competTablePanel);
                        editCard.revalidate();
                        editCard.repaint();
                    } catch(SQLException ex) {
                        System.err.println("SQLException " + ex.getMessage());
                        System.out.println(queryDel);
                    }
                }
            System.out.println( e.getActionCommand() + " : " + table.getSelectedRow() + " " + table.getValueAt(table.getSelectedRow(), 0));
        }
        
        public static String removeSecondes(String s) {
            return (s == null || s.length() == 0)
              ? null 
              : (s.substring(0, s.length() - 3));
        }
    }
}




