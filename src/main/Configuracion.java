
package main;

import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;


public class Configuracion extends javax.swing.JFrame {

    public static String fuenteSeleccionada = "Arial";
    public static int estiloSeleccionado = Font.PLAIN;
    public static int tamañoSeleccionado = 14;
    public static String idiomaSeleccionado = "Español";
    public static String rutaTrabajo = "";
    private COMPILA mainWindow;

    
    public Configuracion(COMPILA mainWindow) {
        this.mainWindow = mainWindow;
        initComponents();
        setSize(715, 576); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Listener para la lista de fuentes
    jListFuente.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) { // Evita eventos duplicados
            fuenteSeleccionada = jListFuente.getSelectedValue();
            actualizarEjemplo(); // Actualiza el ejemplo inmediatamente
        }
    });

    // Listener para la lista de estilos
    jListEstilo.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            String estilo = jListEstilo.getSelectedValue();
            switch (estilo) {
                case "PLAIN":
                    estiloSeleccionado = Font.PLAIN;
                    break;
                case "BOLD":
                    estiloSeleccionado = Font.BOLD;
                    break;
                case "ITALIC":
                    estiloSeleccionado = Font.ITALIC;
                    break;
            }
            actualizarEjemplo();
        }
    });

    // Listener para la lista de tamaños
    jListTamaño.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            tamañoSeleccionado = Integer.parseInt(jListTamaño.getSelectedValue());
            actualizarEjemplo();
        }
    });
    }
    
    
    
    // Método para actualizar el ejemplo en tiempo real
    private void actualizarEjemplo() {
    Font font = new Font(fuenteSeleccionada, estiloSeleccionado, tamañoSeleccionado);
    jLabelEjemplo.setFont(font);
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListFuente = new javax.swing.JList<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListEstilo = new javax.swing.JList<>();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListTamaño = new javax.swing.JList<>();
        jLabel4 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        jLabelEjemplo = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Configuracion");
        getContentPane().setLayout(null);

        jLabel1.setText("Fuente");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(20, 30, 70, 16);

        jLabel2.setText("Estilo");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(280, 30, 50, 16);

        jListFuente.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Arial", "Helvetica", "Times New Roman", "Courier New", "Verdana" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jListFuente);

        getContentPane().add(jScrollPane3);
        jScrollPane3.setBounds(20, 60, 230, 330);

        jListEstilo.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "PLAIN", "BOLD", "ITALIC" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jListEstilo);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(280, 60, 120, 150);

        jLabel3.setText("Tamaño");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(430, 30, 70, 16);

        jListTamaño.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
        jScrollPane2.setViewportView(jListTamaño);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(430, 60, 50, 146);

        jLabel4.setText("Idioma");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(560, 50, 60, 16);

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("Español");
        getContentPane().add(jRadioButton1);
        jRadioButton1.setBounds(550, 90, 80, 21);

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Ingles");
        getContentPane().add(jRadioButton2);
        jRadioButton2.setBounds(550, 120, 80, 21);

        jLabel5.setText("Ejemplo");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(310, 250, 90, 16);

        jLabelEjemplo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelEjemplo.setText("AaBbYyZz");
        jLabelEjemplo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 4));
        getContentPane().add(jLabelEjemplo);
        jLabelEjemplo.setBounds(300, 290, 200, 80);

        jLabel7.setText("Ruta de trabajo");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(30, 420, 170, 16);
        getContentPane().add(jTextField2);
        jTextField2.setBounds(30, 440, 500, 22);

        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(600, 440, 72, 23);

        jButton2.setText("Aceptar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(480, 490, 72, 23);

        jButton3.setText("Cancelar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton3);
        jButton3.setBounds(560, 490, 100, 23);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 86, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 66, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel2);
        jPanel2.setBounds(540, 80, 90, 70);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // Obtener la fuente seleccionada
    fuenteSeleccionada = jListFuente.getSelectedValue();

    // Obtener el estilo seleccionado
    String estilo = jListEstilo.getSelectedValue();
    switch (estilo) {
        case "PLAIN":
            estiloSeleccionado = Font.PLAIN;
            break;
        case "BOLD":
            estiloSeleccionado = Font.BOLD;
            break;
        case "ITALIC":
            estiloSeleccionado = Font.ITALIC;
            break;
    }

    // Obtener el tamaño seleccionado
    tamañoSeleccionado = Integer.parseInt(jListTamaño.getSelectedValue());

    // Obtener el idioma seleccionado
    if (jRadioButton1.isSelected()) {
        idiomaSeleccionado = "Español";
    } else if (jRadioButton2.isSelected()) {
        idiomaSeleccionado = "Ingles";
    }

    // Obtener la ruta de trabajo
    rutaTrabajo = jTextField2.getText();

    // Actualizar el ejemplo
    actualizarEjemplo();

    // Llamar al método en la ventana principal para aplicar la configuración
    mainWindow.aplicarConfiguracion();

    // Cerrar la ventana de configuración
    this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser chooser = new JFileChooser();
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = chooser.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        File selectedDir = chooser.getSelectedFile();
        jTextField2.setText(selectedDir.getAbsolutePath());
    }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed
    
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Configuracion.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                
                
            }
        });
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabelEjemplo;
    private javax.swing.JList<String> jListEstilo;
    private javax.swing.JList<String> jListFuente;
    private javax.swing.JList<String> jListTamaño;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
