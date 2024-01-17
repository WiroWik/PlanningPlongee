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

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
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


public class PlanningGUI implements ActionListener{

    public final String PORT = "3306";
    public final String USER = "root";
    public final String PASSWORD = "";

    JFrame frame;
    JPanel saveCard, editCard, cards, savePanel, editPanel;
    JLabel nomCompetLabel,dateCompetLabel,imageCompetLabel,paysCompetLabel,villeCompetLabel,adresseCompetLabel;
    JTextField nomCompetTextField,dateCompetTextField,imageCompetTextField,paysCompetTextField,villeCompetTextField,adresseCompetTextField,feedbackField;
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
        JLabel titreLabel = new JLabel("Enregistrer un nouveau vol :", JLabel.CENTER);
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
        dateCompetTextField = new JTextField();
        imageCompetTextField = new JTextField();
        paysCompetTextField = new JTextField();
        villeCompetTextField = new JTextField();
        adresseCompetTextField = new JTextField();
        feedbackField = new JTextField();
        feedbackField.setEditable(false);
        JTextField[] formTextFields = {nomCompetTextField,dateCompetTextField,imageCompetTextField,paysCompetTextField,villeCompetTextField,adresseCompetTextField,feedbackField};

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
        for (JTextField currentTextField : formTextFields) {
            c.gridy = y;
            currentTextField.setColumns(10);
            formPanel.add(currentTextField,c);
            y++;
        }

        /*editCard */
        JPanel volTablePanel = generateVolTable();
        editCard.add(volTablePanel);

        


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
            String query = "INSERT INTO `competitions` (`NumVol`, `Heure_depart`, `Heure_arrive`, `Ville_depart`, `Ville_arrivee`) VALUES ('" + numVolTextField.getText() + "', '" + heureDepTextField.getText() + "', '" + heureArivTextField.getText() + "', '" + aeroDepTextField.getText() + "', '" + aeroArivTextField.getText() + "');";

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
                feedbackField.setText("Le vol " + numVolTextField.getText() + " a bien été enregistré" );
            } catch(SQLException ex) {
                System.err.println("SQLException " + ex.getMessage());
                errorMessageLabel.setText("SQLException " + ex.getMessage());
                errorDialog.setVisible(true);
            }
            editCard.removeAll();
            JPanel volTablePanel = generateVolTable();
            editCard.add(volTablePanel);
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

    public JPanel generateVolTable() {
        JPanel volTablePanel = new JPanel(new BorderLayout());
        JTable volTable, headerTable;
        String[] columnName = {"NumVol", "Heure_depart", "Heure_arrive", "Ville_depart", "Ville_arrivee", "Modifier", "Supprimer"};
        

        /*Connexion SQL */
        String url = "jdbc:mysql://localhost:" + PORT + "/vols";
        Connection con;
        Statement stmt, stmt2;
        String query = "SELECT * FROM vol";
        String query2 = "SELECT COUNT(*) AS rowCount FROM vol";
        

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


            System.out.println("Les vols");
            int i = 0;
            
            rs2.next();
            System.out.println(rs2.getInt("rowCount"));
            int tableSize = rs2.getInt("rowCount");
            Object[][] data = new String[tableSize][7];
            
            

            while (rs.next()) {
                Object[] rowValue = new String[7];
                
                String numVol = rs.getString("Numvol");
                rowValue[0] = numVol;

                String heureDepart = rs.getString("Heure_depart");
                rowValue[1] = heureDepart;

                String heureArrive = rs.getString("Heure_arrive");
                rowValue[2] = heureArrive;

                String villeDepart = rs.getString("Ville_depart");
                rowValue[3] = villeDepart;

                String villeArrivee = rs.getString("Ville_arrivee");
                rowValue[4] = villeArrivee;
                
                rowValue[5] = "Modifier";

                rowValue[6] = "Supprimer";

                data[i] = rowValue;
                i++;
            }
            String[][] headercolumnName = {columnName};
            DefaultTableModel model = new DefaultTableModel(data, columnName) {
                @Override
                public boolean isCellEditable(int row, int col) {
                    return col == 5 || col == 6;
                }
            };
            volTable = new JTable(model);
            headerTable = new JTable(headercolumnName, columnName);

            ButtonColumn buttonColumn = new ButtonColumn(volTable, 5);
            ButtonColumn buttonColumn2 = new ButtonColumn(volTable, 6);

            headerTable.setEnabled(false);
            volTable.setCellSelectionEnabled(false);

            volTablePanel.add(headerTable, BorderLayout.NORTH);
            volTablePanel.add(volTable, BorderLayout.CENTER);

            stmt.close();
            stmt2.close();
            con.close();
        } catch(SQLException ex) {
            System.err.println("SQLException " + ex.getMessage());
        }
        
        return volTablePanel;
    }

    class ButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener{
        JTable table;
        JButton renderButton;
        JButton editButton;
        String text;
        JLabel numVolLabelEdit,heureDepLabelEdit,heureArivLabelEdit,aeroDepLabelEdit,aeroArivLabelEdit;
        JTextField numVolTextFieldEdit,heureDepTextFieldEdit,heureArivTextFieldEdit,aeroDepTextFieldEdit,aeroArivTextFieldEdit,feedbackFieldEdit;
        JButton modifButton;
        JDialog editDialog;

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

            
            JLabel titreLabelEdit = new JLabel("Enregistrer un nouveau vol :", JLabel.CENTER);

            
            JPanel formPanelEdit = new JPanel(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            numVolLabelEdit = new JLabel("Entrer numéro de vol");
            heureDepLabelEdit = new JLabel("Entrer Heure de départ HH:MM");
            heureArivLabelEdit = new JLabel("Entrer Heure d'arrivée HH:MM");
            aeroDepLabelEdit = new JLabel("Entrer Aéroport de depart");
            aeroArivLabelEdit = new JLabel("Entrer Aéroport d'arrivée");

            JLabel[] formLabelsEdit = {numVolLabelEdit,heureDepLabelEdit,heureArivLabelEdit,aeroDepLabelEdit,aeroArivLabelEdit};

            if (numVolTextFieldEdit == null)
                numVolTextFieldEdit = new JTextField();
            
            if (heureDepTextFieldEdit == null)
                heureDepTextFieldEdit = new JTextField();

            if (heureArivTextFieldEdit == null)
                heureArivTextFieldEdit = new JTextField();

            if (aeroDepTextFieldEdit == null)
                aeroDepTextFieldEdit = new JTextField();

            if (aeroArivTextFieldEdit == null)
                aeroArivTextFieldEdit = new JTextField();

            numVolTextFieldEdit.setEditable(false);

            

            feedbackFieldEdit = new JTextField();
            feedbackFieldEdit.setEditable(false);
            JTextField[] formTextFieldsEdit = {numVolTextFieldEdit,heureDepTextFieldEdit,heureArivTextFieldEdit,aeroDepTextFieldEdit,aeroArivTextFieldEdit,feedbackFieldEdit};

            modifButton = new JButton("Modifier le vol");
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
            for (JTextField currentTextField : formTextFieldsEdit) {
                c.gridy = y;
                currentTextField.setColumns(10);
                formPanelEdit.add(currentTextField,c);
                y++;
            }

            editDialog = new JDialog(frame, "Edit Dialog");
            System.out.println(numVolTextFieldEdit.getText());

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
            String numVolCurrent, heureDepCurrent, heureArivCurrent, aeroDepCurrent, aeroArivCurrent;
            numVolCurrent = "";
            heureDepCurrent = "";
            heureArivCurrent = "";
            aeroDepCurrent = "";
            aeroArivCurrent = "";

            if (e.getActionCommand() == "Modifier") {
                editDialog.setVisible(true);
            }

            
            
            if (e.getActionCommand() == "Modifier le vol") {
                System.out.println(table.getValueAt(table.getSelectedRow(), 0));

                JDialog errorDialog = new JDialog(frame, "Error Dialog");
                JLabel errorMessageLabel = new JLabel();

                errorDialog.setSize(500, 100);
                errorDialog.add(errorMessageLabel);

                numVolCurrent = (String) table.getValueAt(table.getSelectedRow(), 0);
                /*
                heureDepCurrent = (String) table.getValueAt(table.getSelectedRow(), 1);
                heureArivCurrent = (String) table.getValueAt(table.getSelectedRow(), 2);
                aeroDepCurrent = (String) table.getValueAt(table.getSelectedRow(), 3);
                aeroArivCurrent = (String) table.getValueAt(table.getSelectedRow(), 4);
                */

                if (heureDepTextFieldEdit.getText().equals((String) table.getValueAt(table.getSelectedRow(), 1) + ":00") || !heureDepTextFieldEdit.getText().equals(null))
                    heureDepCurrent = (String) table.getValueAt(table.getSelectedRow(), 1);
                else
                    heureDepCurrent = heureDepTextFieldEdit.getText();
                
                if (heureArivTextFieldEdit.getText().equals((String) table.getValueAt(table.getSelectedRow(), 2) + ":00") || !heureArivTextFieldEdit.getText().equals(null))
                    heureArivCurrent = (String) table.getValueAt(table.getSelectedRow(), 2);
                else
                    heureArivCurrent = heureArivTextFieldEdit.getText();

                if (aeroDepTextFieldEdit.getText().equals((String) table.getValueAt(table.getSelectedRow(), 3)) || aeroDepTextFieldEdit.getText().equals(""))
                    aeroDepCurrent = (String) table.getValueAt(table.getSelectedRow(), 3);
                else
                    aeroDepCurrent = aeroDepTextFieldEdit.getText();

                if (aeroArivTextFieldEdit.getText().equals((String) table.getValueAt(table.getSelectedRow(), 4)) || aeroArivTextFieldEdit.getText().equals(""))
                    aeroArivCurrent = (String) table.getValueAt(table.getSelectedRow(), 4);
                else
                    aeroArivCurrent = aeroArivTextFieldEdit.getText();
                    

                /*Connexion SQL */
                String url = "jdbc:mysql://localhost:" + PORT + "/vols";
                Connection con;
                Statement stmt;
                String query = "UPDATE vol SET Numvol = '" + numVolCurrent + "', Heure_depart = '" + heureDepCurrent + "', Heure_arrive = '" + heureArivCurrent + "', Ville_depart = '" + aeroDepCurrent + "', Ville_arrivee = '" + aeroArivCurrent + "' WHERE vol.Numvol = '" + (String) table.getValueAt(table.getSelectedRow(), 0) + "';";
                
                
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
                    feedbackFieldEdit.setText("Le vol " + numVolTextField.getText() + " a bien été enregistré" );
                    System.out.println(query);
                    editCard.removeAll();
                    JPanel volTablePanel = generateVolTable();
                    editCard.add(volTablePanel);
                    editCard.revalidate();
                } catch(SQLException ex) {
                    System.err.println("SQLException " + ex.getMessage());
                    errorMessageLabel.setText("SQLException " + ex.getMessage());
                    errorDialog.setVisible(true);
                    System.out.println(query);
                }

                
            }

            if (e.getActionCommand() == "Supprimer") {
                    /*Connexion SQL */
                    String urlDel = "jdbc:mysql://localhost:" + PORT + "/vols";
                    Connection conDel;
                    Statement stmtDel;
                    String queryDel = "DELETE FROM vol WHERE vol.Numvol = '" + (String) table.getValueAt(table.getSelectedRow(), 0) + "'";
                    
                    
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
                        JPanel volTablePanel = generateVolTable();
                        editCard.add(volTablePanel);
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




