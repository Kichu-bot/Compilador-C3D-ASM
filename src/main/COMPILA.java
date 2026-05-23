package main;

import compilerTools.ErrorLSSL;
import compilerTools.Functions;
import compilerTools.Grammar;
import compilerTools.Production;
import compilerTools.Token;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.Color;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.swing.table.DefaultTableModel;
import java.io.PrintStream;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

public class COMPILA extends javax.swing.JFrame {

    // Variables para almacenar rutas y estado de archivos
    private String lastDirectory = System.getProperty("user.home") + "/Downloads";
    private boolean archivoModificado = false;
    private File archivoActual = null;
    private Map<JPanel, File> archivosPestañas = new HashMap<>();
    private List<JPopupMenu> contextMenus = new ArrayList<>();

    // Variables para el menú contextual
    private JPopupMenu contextMenu;
    private JMenuItem menuItemCopiar;
    private JMenuItem menuItemCortar;
    private JMenuItem menuItemPegar;

    // Variables para las etiquetas según el idioma
    private String etiquetaCopiar = "Copiar";
    private String etiquetaCortar = "Cortar";
    private String etiquetaPegar = "Pegar";

    // Variables para guardar el estado
    private List<String> listaTitulosPestanas = new ArrayList<>();
    private List<String> listaContenidoPestanas = new ArrayList<>();
    private List<String> listaRutasArchivos = new ArrayList<>();

    //Analisis sintatico
    private ArrayList<Token> listaTokens;
    private ArrayList<ErrorLSSL> gestionErrores;
    private HashMap<String, Simbolo> tablaSimbolos;
    private ArrayList<Production> idDeclaracion, idAsignacion, idSentencia, idProgramaCompleto, idCondiciones, idElegir;

    private Generador3D generador3D;
    private Production tokens;
    private int posicionTokenActual;
    private String nombreArchivo;
    // Al comienzo de la clase:
    private Deque<String> elseLabelStack = new ArrayDeque<>();
    private Deque<String> endIfLabelStack = new ArrayDeque<>();

    // En tu clase COMPILA, declara al inicio:
    private Deque<String> falseLabelStack = new ArrayDeque<>();
    private boolean skipSiCase = false;

    //Ensamblador
    private GeneradorASM generadorASM;
    private String varTemp;

    public COMPILA() {
        initComponents();

        //Tabla simbolos
        tablaSimbolos = new HashMap<>();
        listaTokens = new ArrayList<>();
        gestionErrores = new ArrayList<>();
        idDeclaracion = new ArrayList<>();
        idAsignacion = new ArrayList<>();
        idSentencia = new ArrayList<>();
        idCondiciones = new ArrayList<>();
        idElegir = new ArrayList<>();
        idProgramaCompleto = new ArrayList<>();
        generador3D = new Generador3D();
        generadorASM = new GeneradorASM();

        // Crear el menú contextual para el JTextArea principal
        crearMenuContextual();

        // Aplicar el idioma y actualizar las etiquetas
        aplicarIdioma();

        // Inicializar las listas antes de cargar el estado
        listaTitulosPestanas = new ArrayList<>();
        listaContenidoPestanas = new ArrayList<>();
        listaRutasArchivos = new ArrayList<>();

        // Cargar el estado de la aplicación
        cargarEstadoAplicacion();

        // Remover la pestaña "Programa" si existe
        int indexPrograma = jTabbedPane1.indexOfTab("tab1");
        if (indexPrograma != -1) {
            jTabbedPane1.removeTabAt(indexPrograma);
        }

        listaTokens = new ArrayList<Token>();
        gestionErrores = new ArrayList<>();

        // Agregar el CaretListener al jTextArea1
        agregarCaretListener(jTextArea1);

        // Actualizar el jLabel1 con la posición inicial del cursor
        actualizarPosicionLabel(jTextArea1);

        // Asignar el menú contextual al jTextArea1
        jTextArea1.setComponentPopupMenu(contextMenu);

        // Método para actualizar la posición del cursor al cambiar de pestaña
        actualizarPosicionCursor();

        // Listener para detectar cambios en el texto del JTextArea principal
        jTextArea1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                archivoModificado = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                archivoModificado = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                archivoModificado = true;
            }
        });

        // Hilo que actualiza la hora en el jLabel2 cada segundo
        Thread clockThread = new Thread(() -> {
            while (true) {
                try {
                    String time = "Hora local: " + new SimpleDateFormat("HH:mm:ss").format(new Date());
                    SwingUtilities.invokeLater(() -> jLabel2.setText(time));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        clockThread.start();
    }

    private void agregarCaretListener(JTextArea textArea) {
        if (textArea != null) {
            textArea.addCaretListener(new javax.swing.event.CaretListener() {
                @Override
                public void caretUpdate(javax.swing.event.CaretEvent e) {
                    actualizarPosicionLabel(textArea);
                }
            });
        }
    }

    private void actualizarPosicionLabel(JTextArea textArea) {
        try {
            // Obtener la posición actual del cursor en el JTextArea actual
            int caretPosition = textArea.getCaretPosition();

            // Obtener el número de línea en el JTextArea actual
            int linea = textArea.getLineOfOffset(caretPosition) + 1;

            // Obtener el offset al inicio de la línea actual
            int startOfLineOffset = textArea.getLineStartOffset(linea - 1);

            // Calcular la columna actual (posición en la línea)
            int columna = caretPosition - startOfLineOffset + 1;

            // Actualizar el jLabel1 con la línea y columna
            jLabel1.setText("Renglón: " + linea + " | Columna: " + columna);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

private void guardarEstadoAplicacion() {
    try {
        // Limpiar las listas antes de llenarlas
        listaTitulosPestanas.clear();
        listaContenidoPestanas.clear();
        listaRutasArchivos.clear();

        // Recorrer todas las pestañas abiertas
        for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
            // CORRECCIÓN: Obtener como Component genérico para evitar ClassCastException
            Component comp = jTabbedPane1.getComponentAt(i);
            JPanel panel = null;
            JTextArea textArea = null;

            // Identificar si el componente es un Scroll o un Panel
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                Component vista = scroll.getViewport().getView();
                if (vista instanceof JPanel) {
                    panel = (JPanel) vista;
                    textArea = obtenerTextAreaDePanel(panel);
                } else if (vista instanceof JTextArea) {
                    textArea = (JTextArea) vista;
                }
            } else if (comp instanceof JPanel) {
                panel = (JPanel) comp;
                textArea = obtenerTextAreaDePanel(panel);
            }

            // Solo proceder si logramos encontrar el área de texto
            if (textArea != null) {
                // Guardar el título de la pestaña
                String tituloPestana = jTabbedPane1.getTitleAt(i);
                listaTitulosPestanas.add(tituloPestana);

                // Guardar el contenido del JTextArea
                String contenido = textArea.getText();
                listaContenidoPestanas.add(contenido);

                // Obtener archivo asociado (usando el panel o el componente directamente como llave)
                File archivoAsociado = archivosPestañas.get(comp); 
                // Nota: Si usas el panel como llave en tu HashMap, usa archivosPestañas.get(panel)
                
                String rutaArchivo = (archivoAsociado != null) ? archivoAsociado.getAbsolutePath() : null;
                listaRutasArchivos.add(rutaArchivo);
            }
        }

        // Serialización
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("app_state.dat"))) {
            oos.writeObject(listaTitulosPestanas);
            oos.writeObject(listaContenidoPestanas);
            oos.writeObject(listaRutasArchivos);
            oos.flush();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}

private void cargarEstadoAplicacion() {
        File estadoArchivo = new File("app_state.dat");
        if (!estadoArchivo.exists()) {
            return; 
        }

        try {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(estadoArchivo))) {
                listaTitulosPestanas = (List<String>) ois.readObject();
                listaContenidoPestanas = (List<String>) ois.readObject();
                listaRutasArchivos = (List<String>) ois.readObject();
            }

            // --- CORRECCIÓN AQUÍ ---
            jTabbedPane1.removeAll(); // Elimina pestañas creadas por defecto al inicio
            archivosPestañas.clear(); // Limpia el mapa para evitar referencias a paneles viejos
            // -----------------------

            for (int i = 0; i < listaTitulosPestanas.size(); i++) {
                String tituloPestana = listaTitulosPestanas.get(i);
                String contenido = listaContenidoPestanas.get(i);
                String rutaArchivo = listaRutasArchivos.get(i);

                JTextArea nuevoTextArea = new JTextArea();
                nuevoTextArea.setText(contenido);
                configurarTextArea(nuevoTextArea);

                JPanel panel = crearPanelConTextArea(nuevoTextArea);

                File archivoAsociado = (rutaArchivo != null) ? new File(rutaArchivo) : null;
                archivosPestañas.put(panel, archivoAsociado);

                jTabbedPane1.addTab(tituloPestana, panel);
                jTabbedPane1.setTabComponentAt(jTabbedPane1.indexOfComponent(panel), new ButtonTabComponent(jTabbedPane1));
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private JTextArea obtenerTextAreaDePanel(JPanel panel) {
        JScrollPane scrollPane = (JScrollPane) panel.getComponent(1);
        return (JTextArea) scrollPane.getViewport().getView();
    }

    private void configurarTextArea(JTextArea textArea) {
        textArea.setFont(obtenerFuenteConfigurada());
        agregarCaretListener(textArea);
        agregarDocumentListener(textArea);
        textArea.setComponentPopupMenu(contextMenu);
    }

    private JPanel crearPanelConTextArea(JTextArea textArea) {
        JTextArea lineNumbers = new JTextArea("1\n");
        lineNumbers.setEditable(false);

        sincronizarLineas(textArea, lineNumbers);

        JScrollPane scrollTextArea = new JScrollPane(textArea);
        JScrollPane scrollLineNumbers = new JScrollPane(lineNumbers);

        scrollLineNumbers.setPreferredSize(new Dimension(33, 576));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollLineNumbers, BorderLayout.WEST);
        panel.add(scrollTextArea, BorderLayout.CENTER);

        return panel;
    }

    private void sincronizarLineas(JTextArea textArea, JTextArea lineNumbers) {
        textArea.getDocument().addDocumentListener(new DocumentListener() {
            public String getText() {
                int caretPosition = textArea.getDocument().getLength();
                Element root = textArea.getDocument().getDefaultRootElement();
                StringBuilder text = new StringBuilder("1\n");
                for (int i = 2; i <= root.getElementIndex(caretPosition) + 2; i++) {
                    text.append(i).append("\n");
                }
                return text.toString();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                lineNumbers.setText(getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                lineNumbers.setText(getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                lineNumbers.setText(getText());
            }
        });
    }

    private void crearMenuContextual() {
        contextMenu = new JPopupMenu();
        menuItemCortar = new JMenuItem(etiquetaCortar);
        menuItemCortar.setActionCommand("CUT");
        menuItemCopiar = new JMenuItem(etiquetaCopiar);
        menuItemCopiar.setActionCommand("COPY");
        menuItemPegar = new JMenuItem(etiquetaPegar);
        menuItemPegar.setActionCommand("PASTE");

        contextMenu.add(menuItemCortar);
        contextMenu.add(menuItemCopiar);
        contextMenu.add(menuItemPegar);

        menuItemCortar.addActionListener(e -> jTextArea1.cut());
        menuItemCopiar.addActionListener(e -> jTextArea1.copy());
        menuItemPegar.addActionListener(e -> jTextArea1.paste());
    }

    // Método para agregar el DocumentListener que detecta cambios y actualiza la bandera archivoModificado
    private void agregarDocumentListener(JTextArea textArea) {
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                archivoModificado = true; // Marcar como modificado al insertar texto
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                archivoModificado = true; // Marcar como modificado al eliminar texto
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                archivoModificado = true; // Marcar como modificado en cambios
            }
        });
    }

    // Método que actualiza el jLabel1 con la posición del cursor en jTextArea1 (área de trabajo)
    // Método que actualiza el jLabel1 con la posición del cursor en el JTextArea de la pestaña activa
    public void actualizarPosicionCursor() {
        jTabbedPane1.addChangeListener(e -> {
            Component componenteActual = jTabbedPane1.getSelectedComponent();

            if (componenteActual instanceof JPanel) {
                JScrollPane scrollPane = (JScrollPane) ((JPanel) componenteActual).getComponent(1);
                JTextArea textAreaActual = (JTextArea) scrollPane.getViewport().getView();

                textAreaActual.addCaretListener(new javax.swing.event.CaretListener() {
                    @Override
                    public void caretUpdate(javax.swing.event.CaretEvent e) {
                        try {
                            // Obtener la posición actual del cursor en el JTextArea actual
                            int caretPosition = textAreaActual.getCaretPosition();

                            // Obtener el número de línea en el JTextArea actual
                            int linea = textAreaActual.getLineOfOffset(caretPosition) + 1;

                            // Obtener el offset al inicio y al final de la línea actual
                            int startOfLineOffset = textAreaActual.getLineStartOffset(linea - 1);
                            int endOfLineOffset = textAreaActual.getLineEndOffset(linea - 1);

                            // Calcular la cantidad de caracteres en la línea actual
                            int columna = endOfLineOffset - startOfLineOffset;

                            // Actualizar el jLabel1 con la línea y la cantidad de caracteres
                            jLabel1.setText("Renglón: " + linea + " | Columna: " + columna);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private Font obtenerFuenteConfigurada() {
        return new Font(Configuracion.fuenteSeleccionada, Configuracion.estiloSeleccionado, Configuracion.tamañoSeleccionado);
    }

    public void agregarPestaña() {
        JTextArea nuevoTextArea = new JTextArea(); // Área de texto principal
        JTextArea nuevoTextArea5 = new JTextArea("1\n"); // Área de numeración de líneas
        nuevoTextArea5.setEditable(false); // El área de numeración es solo lectura

        // Aplicar la fuente configurada al nuevo JTextArea
        Font fuenteActual = obtenerFuenteConfigurada();
        nuevoTextArea.setFont(fuenteActual);

        // Sincronizar la numeración de líneas con el contenido del área de texto
        nuevoTextArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public String getTextNumeracion() {
                int caretPosition = nuevoTextArea.getDocument().getLength(); // Longitud del texto
                Element root = nuevoTextArea.getDocument().getDefaultRootElement();
                StringBuilder text = new StringBuilder("1\n");
                for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
                    text.append(i).append("\n"); // Añade la numeración de líneas
                }
                return text.toString();
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                nuevoTextArea5.setText(getTextNumeracion()); // Actualiza la numeración al insertar texto
                archivoModificado = true;
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                nuevoTextArea5.setText(getTextNumeracion()); // Actualiza la numeración al eliminar texto
                archivoModificado = true;
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                nuevoTextArea5.setText(getTextNumeracion()); // Actualiza la numeración al cambiar el formato
                archivoModificado = true;
            }
        });

        // Crear un menú contextual con opciones de copiar, cortar y pegar utilizando las etiquetas actuales
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem copiar = new JMenuItem(etiquetaCopiar);
        copiar.setActionCommand("COPY");
        JMenuItem cortar = new JMenuItem(etiquetaCortar);
        cortar.setActionCommand("CUT");
        JMenuItem pegar = new JMenuItem(etiquetaPegar);
        pegar.setActionCommand("PASTE");

        copiar.addActionListener(e -> nuevoTextArea.copy());
        cortar.addActionListener(e -> nuevoTextArea.cut());
        pegar.addActionListener(e -> nuevoTextArea.paste());

        contextMenu.add(copiar);
        contextMenu.add(cortar);
        contextMenu.add(pegar);

        nuevoTextArea.setComponentPopupMenu(contextMenu); // Asignar el menú contextual al área de texto

        // Añadir el menú contextual a la lista para poder actualizarlo al cambiar el idioma
        contextMenus.add(contextMenu);

        // Crear un JScrollPane para la numeración y otro para el área de texto
        JScrollPane scrollTextArea = new JScrollPane(nuevoTextArea);
        JScrollPane scrollTextArea5 = new JScrollPane(nuevoTextArea5);
        scrollTextArea5.setPreferredSize(new Dimension(33, 576)); // Ajustar el ancho y alto del área de numeración
        scrollTextArea.setPreferredSize(new Dimension(933, 582)); // Ajustar el ancho y alto del área de texto

        // Crear un panel con BorderLayout que contenga ambas áreas
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollTextArea5, BorderLayout.WEST); // Colocar la numeración a la izquierda
        panel.add(scrollTextArea, BorderLayout.CENTER); // Colocar el área de texto en el centro

        // Añadir una nueva pestaña al JTabbedPane
        jTabbedPane1.addTab("Nuevo Archivo", panel);
        jTabbedPane1.setSelectedComponent(panel); // Seleccionar la nueva pestaña

        archivosPestañas.put(panel, null); // Asociar un archivo con la pestaña
        
        // Añadir una nueva pestaña al JTabbedPane
        jTabbedPane1.addTab("Nuevo Archivo", panel);
        
        // --- AQUÍ INSERTAMOS LA MAGIA ---
        int index = jTabbedPane1.indexOfComponent(panel);
        jTabbedPane1.setTabComponentAt(index, new ButtonTabComponent(jTabbedPane1));
        // --------------------------------
        
        jTabbedPane1.setSelectedComponent(panel); // Seleccionar la nueva pestaña

        archivosPestañas.put(panel, null); // Asociar un archivo con la pestaña
    }

// Método para guardar el archivo en la pestaña seleccionada
    public void guardarArchivoPestaña() {
        JPanel panelSeleccionado = (JPanel) jTabbedPane1.getSelectedComponent(); // Obtener la pestaña seleccionada
        File archivoActual = archivosPestañas.get(panelSeleccionado); // Obtener el archivo actual de esa pestaña

        JTextArea textArea = (JTextArea) ((JScrollPane) ((BorderLayout) panelSeleccionado.getLayout()).getLayoutComponent(BorderLayout.CENTER)).getViewport().getView();

        if (archivoActual == null) {
            // Si no hay un archivo previamente guardado, abrir JFileChooser
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto (*.re)", "re");
            fileChooser.setFileFilter(filter);
            int opcion = fileChooser.showSaveDialog(null);
            if (opcion == JFileChooser.APPROVE_OPTION) {
                archivoActual = fileChooser.getSelectedFile();
                if (!archivoActual.getName().endsWith(".re")) {
                    archivoActual = new File(archivoActual.getAbsolutePath() + ".re");
                }
                archivosPestañas.put(panelSeleccionado, archivoActual); // Asignar el archivo a la pestaña
            }
        }

        if (archivoActual != null) {
            // Guardar el contenido en el archivo
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoActual))) {
                writer.write(textArea.getText());
                JOptionPane.showMessageDialog(null, "Archivo guardado correctamente.");

                // Actualizar el título de la pestaña con el nombre del archivo
                int index = jTabbedPane1.indexOfComponent(panelSeleccionado);
                jTabbedPane1.setTitleAt(index, archivoActual.getName());
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al guardar el archivo.");
            }
        }
    }

    public void abrirArchivo() {
        // Usar lastDirectory para inicializar el JFileChooser
        JFileChooser fileChooser = (lastDirectory != null) ? new JFileChooser(lastDirectory) : new JFileChooser();
        fileChooser.setDialogTitle("Abrir archivo");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto (*.re)", "re"));

        int seleccion = fileChooser.showOpenDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            // Actualizar lastDirectory con la ruta del archivo seleccionado
            lastDirectory = archivo.getParent();

            try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
                JTextArea nuevoTextArea = new JTextArea();
                // Aplicar la fuente configurada al nuevo JTextArea
                nuevoTextArea.setFont(obtenerFuenteConfigurada());

                JTextArea nuevoTextArea5 = new JTextArea("1\n");
                nuevoTextArea5.setEditable(false);

                // Leer el contenido del archivo y cargarlo en el JTextArea
                String linea;
                while ((linea = reader.readLine()) != null) {
                    nuevoTextArea.append(linea + "\n");
                }

                // Sincronizar numeración de líneas
                nuevoTextArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public String getTextNumeracion() {
                        int caretPosition = nuevoTextArea.getDocument().getLength();
                        Element root = nuevoTextArea.getDocument().getDefaultRootElement();
                        StringBuilder text = new StringBuilder("1\n");
                        for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
                            text.append(i).append("\n");
                        }
                        return text.toString();
                    }

                    @Override
                    public void insertUpdate(javax.swing.event.DocumentEvent e) {
                        nuevoTextArea5.setText(getTextNumeracion());
                        archivoModificado = true;
                    }

                    @Override
                    public void removeUpdate(javax.swing.event.DocumentEvent e) {
                        nuevoTextArea5.setText(getTextNumeracion());
                        archivoModificado = true;
                    }

                    @Override
                    public void changedUpdate(javax.swing.event.DocumentEvent e) {
                        nuevoTextArea5.setText(getTextNumeracion());
                        archivoModificado = true;
                    }
                });

                // Crear un menú contextual con opciones de copiar, cortar y pegar utilizando las etiquetas actuales
                JPopupMenu contextMenu = new JPopupMenu();
                JMenuItem copiar = new JMenuItem(etiquetaCopiar);
                copiar.setActionCommand("COPY");
                JMenuItem cortar = new JMenuItem(etiquetaCortar);
                cortar.setActionCommand("CUT");
                JMenuItem pegar = new JMenuItem(etiquetaPegar);
                pegar.setActionCommand("PASTE");

                copiar.addActionListener(e -> nuevoTextArea.copy());
                cortar.addActionListener(e -> nuevoTextArea.cut());
                pegar.addActionListener(e -> nuevoTextArea.paste());

                contextMenu.add(copiar);
                contextMenu.add(cortar);
                contextMenu.add(pegar);

                nuevoTextArea.setComponentPopupMenu(contextMenu); // Asignar el menú contextual al área de texto

                // Añadir el menú contextual a la lista para poder actualizarlo al cambiar el idioma
                contextMenus.add(contextMenu);

                // Crear los JScrollPane para la numeración y el área de texto
                JScrollPane scrollTextArea = new JScrollPane(nuevoTextArea);
                JScrollPane scrollTextArea5 = new JScrollPane(nuevoTextArea5);
                scrollTextArea5.setPreferredSize(new Dimension(33, 576)); // Ajustar el ancho y alto del área de numeración
                scrollTextArea.setPreferredSize(new Dimension(933, 582)); // Ajustar el ancho y alto del área de texto

                // Crear un panel con BorderLayout que contenga ambas áreas
                JPanel panel = new JPanel(new BorderLayout());
                panel.add(scrollTextArea5, BorderLayout.WEST);
                panel.add(scrollTextArea, BorderLayout.CENTER);

                // Añadir la pestaña al jTabbedPane1
                jTabbedPane1.addTab(archivo.getName(), panel);
                jTabbedPane1.setTabComponentAt(jTabbedPane1.indexOfComponent(panel), new ButtonTabComponent(jTabbedPane1));
                jTabbedPane1.setSelectedComponent(panel);

                archivosPestañas.put(panel, archivo); // Asignar archivo abierto a la pestaña

                JOptionPane.showMessageDialog(null, "Archivo abierto exitosamente.");

                // Actualizar el título de la pestaña con el nombre del archivo
                int index = jTabbedPane1.indexOfComponent(panel);
                jTabbedPane1.setTitleAt(index, archivo.getName());
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al abrir el archivo.");
            }
        }
    }

    // Método para guardar el archivo en la pestaña seleccionada
    public void saveFile() {
        JPanel panelActual = obtenerPanelActual(); // Obtener el panel de la pestaña actual
        File archivoActual = archivosPestañas.get(panelActual); // Obtener el archivo asociado a esta pestaña

        JFileChooser fileChooser; // Declarar fileChooser una sola vez

        if (archivoActual == null) {
            fileChooser = new JFileChooser(lastDirectory);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto (*.re)", "re");
            fileChooser.setFileFilter(filter);

            // Sugerir un nombre predeterminado si no se ha guardado antes
            fileChooser.setSelectedFile(new File("proyecto1.re"));

            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                archivoActual = fileChooser.getSelectedFile();
                lastDirectory = archivoActual.getParent();

                // Si el archivo no tiene extensión, agregarla
                if (!archivoActual.getName().endsWith(".re")) {
                    archivoActual = new File(archivoActual.getAbsolutePath() + ".re");
                }
                archivosPestañas.put(panelActual, archivoActual); // Asociar el archivo con la pestaña
            } else {
                return; // Si no se selecciona ningún archivo, salir del método
            }
        } else {
            fileChooser = null; // No se necesita en este caso, pero es buena práctica inicializarlo
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoActual))) {
            JTextArea textAreaActual = obtenerTextAreaActual(); // Obtener el JTextArea de la pestaña actual
            writer.write(textAreaActual.getText());

            // Actualizar el título de la pestaña con el nombre del archivo
            int indicePestaña = jTabbedPane1.indexOfComponent(panelActual);
            nombreArchivo = archivoActual.getName();
            jTabbedPane1.setTitleAt(indicePestaña, nombreArchivo); // Actualizar el nombre en la pestaña

            // Indicar que el archivo no ha sido modificado
            archivoModificado = false;

            JOptionPane.showMessageDialog(this, "Archivo guardado correctamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

// Método para obtener el JTextArea actual en la pestaña seleccionada
    private JTextArea obtenerTextAreaActual() {
        JPanel panelSeleccionado = obtenerPanelActual();

        if (panelSeleccionado != null) {
            JScrollPane scrollTextArea = (JScrollPane) panelSeleccionado.getComponent(1); // Obtener el JScrollPane del JPanel
            return (JTextArea) scrollTextArea.getViewport().getView(); // Retornar el JTextArea dentro del JScrollPane
        }

        return null; // Retornar null si no se encuentra el JTextArea
    }

// Método para obtener el JPanel actual de la pestaña seleccionada
    private JPanel obtenerPanelActual() {
        return (JPanel) jTabbedPane1.getSelectedComponent(); // Retorna el panel actualmente seleccionado
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane3 = new javax.swing.JScrollPane();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane8 = new javax.swing.JScrollPane();
        salidaTokens = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        resultados = new javax.swing.JTextArea();
        jToolBar1 = new javax.swing.JToolBar();
        nuevo = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        salidaSimbolos = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        Archivo = new javax.swing.JMenu();
        Nuevo = new javax.swing.JMenuItem();
        Abrir = new javax.swing.JMenuItem();
        Cerrar = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        Guardar = new javax.swing.JMenuItem();
        Guardar_como = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        Salir = new javax.swing.JMenuItem();
        Editar = new javax.swing.JMenu();
        Copiar = new javax.swing.JMenuItem();
        Cortar = new javax.swing.JMenuItem();
        Pegar = new javax.swing.JMenuItem();
        Compilar = new javax.swing.JMenu();
        compilar = new javax.swing.JMenuItem();
        Compilar_correr = new javax.swing.JMenuItem();
        Herramientas = new javax.swing.JMenu();
        menuItemConfiguracion = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("COMPILADORE");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        salidaTokens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Token", "Lexema", "[Renglon, Columna]"
            }
        ));
        jScrollPane8.setViewportView(salidaTokens);

        jTabbedPane2.addTab("--Tabla de tokens--", jScrollPane8);

        jScrollPane3.setViewportView(jTabbedPane2);

        resultados.setEditable(false);
        resultados.setColumns(20);
        resultados.setRows(5);
        resultados.setText("----Generando Codigo -----\n");
        jScrollPane6.setViewportView(resultados);

        jTabbedPane3.addTab("--Resultados de compilacion--", jScrollPane6);

        jScrollPane5.setViewportView(jTabbedPane3);

        jToolBar1.setRollover(true);

        nuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/1.png"))); // NOI18N
        nuevo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nuevo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoActionPerformed(evt);
            }
        });
        jToolBar1.add(nuevo);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/2.png"))); // NOI18N
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton1);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/3.png"))); // NOI18N
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton3);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/4.png"))); // NOI18N
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton4);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/5.png"))); // NOI18N
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton5);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/6.png"))); // NOI18N
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton6);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/7.png"))); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton2);

        salidaSimbolos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Identificador", "Tipo de dato", "Valor", "varConst"
            }
        ));
        jScrollPane2.setViewportView(salidaSimbolos);

        jTabbedPane4.addTab("Tabla de simbolos", jScrollPane2);

        jScrollPane1.setViewportView(jTabbedPane4);

        jPanel6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel1.setText("Renglón: 1 | Columna: 1");

        jLabel2.setText("Hora local: 15:00");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(jLabel2))
        );

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane4.setViewportView(jTextArea1);

        jTabbedPane1.addTab("tab1", jScrollPane4);

        Archivo.setText("Archivo");

        Nuevo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        Nuevo.setText("Nuevo");
        Nuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NuevoActionPerformed(evt);
            }
        });
        Archivo.add(Nuevo);

        Abrir.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        Abrir.setText("Abrir");
        Abrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AbrirActionPerformed(evt);
            }
        });
        Archivo.add(Abrir);

        Cerrar.setText("Cerrar");
        Cerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CerrarActionPerformed(evt);
            }
        });
        Archivo.add(Cerrar);
        Archivo.add(jSeparator1);

        Guardar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        Guardar.setText("Guardar");
        Guardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                GuardarActionPerformed(evt);
            }
        });
        Archivo.add(Guardar);

        Guardar_como.setText("Guardar como");
        Guardar_como.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Guardar_comoActionPerformed(evt);
            }
        });
        Archivo.add(Guardar_como);
        Archivo.add(jSeparator2);

        Salir.setText("Salir");
        Salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalirActionPerformed(evt);
            }
        });
        Archivo.add(Salir);

        jMenuBar1.add(Archivo);

        Editar.setText("Editar");

        Copiar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        Copiar.setText("Copiar");
        Editar.add(Copiar);

        Cortar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        Cortar.setText("Cortar");
        Editar.add(Cortar);

        Pegar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        Pegar.setText("Pegar");
        Editar.add(Pegar);

        jMenuBar1.add(Editar);

        Compilar.setText("Compilar");

        compilar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        compilar.setText("Compilar");
        Compilar.add(compilar);

        Compilar_correr.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F10, 0));
        Compilar_correr.setText("Compilar y correr");
        Compilar.add(Compilar_correr);

        jMenuBar1.add(Compilar);

        Herramientas.setText("Herramientas");

        menuItemConfiguracion.setText("Configuracion");
        menuItemConfiguracion.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuItemConfiguracionMouseClicked(evt);
            }
        });
        menuItemConfiguracion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemConfiguracionActionPerformed(evt);
            }
        });
        Herramientas.add(menuItemConfiguracion);

        jMenuBar1.add(Herramientas);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(8, 8, 8))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 776, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTabbedPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(62, 62, 62))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("programa");

        getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void GuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_GuardarActionPerformed

    }//GEN-LAST:event_GuardarActionPerformed
private void Salir() {
    // Iterar sobre todas las pestañas abiertas
    for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
        // CORRECCIÓN: Obtener como Component para evitar el error de casteo inicial
        Component componenteTab = jTabbedPane1.getComponentAt(i);
        JPanel panelActual = null;
        JTextArea textAreaActual = null;

        // Caso A: El componente es un JScrollPane (muy común en NetBeans)
        if (componenteTab instanceof JScrollPane) {
            JScrollPane scroll = (JScrollPane) componenteTab;
            if (scroll.getViewport().getView() instanceof JPanel) {
                panelActual = (JPanel) scroll.getViewport().getView();
            } else if (scroll.getViewport().getView() instanceof JTextArea) {
                textAreaActual = (JTextArea) scroll.getViewport().getView();
            }
        } 
        // Caso B: El componente es directamente un JPanel
        else if (componenteTab instanceof JPanel) {
            panelActual = (JPanel) componenteTab;
        }

        // Si tenemos el panel, intentamos buscar el JTextArea según tu lógica de BorderLayout
        if (panelActual != null && textAreaActual == null) {
            LayoutManager layout = panelActual.getLayout();
            if (layout instanceof BorderLayout) {
                Component centerComp = ((BorderLayout) layout).getLayoutComponent(BorderLayout.CENTER);
                if (centerComp instanceof JScrollPane) {
                    textAreaActual = (JTextArea) ((JScrollPane) centerComp).getViewport().getView();
                } else if (centerComp instanceof JTextArea) {
                    textAreaActual = (JTextArea) centerComp;
                }
            }
        }
    }
    
    guardarEstadoAplicacion(); 
    continuarSalir();
}

// Método auxiliar para confirmar y cerrar el programa
    private void continuarSalir() {
        int respuestaFinal = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro que quieres salir?",
                "Confirmación",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (respuestaFinal == JOptionPane.YES_OPTION) {
            System.exit(0); // Cerrar el programa si confirma
        }
    }

    // Método para cerrar la pestaña seleccionada en jTabbedPane1
    private void cerrarPestañaSeleccionada() {
        // Obtener el índice de la pestaña seleccionada
        int indiceSeleccionado = jTabbedPane1.getSelectedIndex();

        if (indiceSeleccionado != -1) { // Verificamos si hay alguna pestaña seleccionada
            // Mostramos un cuadro de diálogo de confirmación
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Estás seguro de que deseas cerrar esta pestaña?",
                    "Cerrar pestaña",
                    JOptionPane.YES_NO_OPTION
            );

            // Si el usuario confirma que desea cerrar la pestaña
            if (opcion == JOptionPane.YES_OPTION) {
                jTabbedPane1.removeTabAt(indiceSeleccionado); // Cerrar la pestaña seleccionada
            }
        } else {
            // Si no hay pestaña seleccionada
            JOptionPane.showMessageDialog(this, "No hay ninguna pestaña seleccionada.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void aplicarConfiguracion() {
        // Aplicar configuración de fuente
        Font nuevaFuente = new Font(Configuracion.fuenteSeleccionada, Configuracion.estiloSeleccionado, Configuracion.tamañoSeleccionado);

        // Aplicar la fuente a los JTextArea en las pestañas
        for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
            JPanel panel = (JPanel) jTabbedPane1.getComponentAt(i);
            JScrollPane scrollPane = (JScrollPane) panel.getComponent(1);
            JTextArea textArea = (JTextArea) scrollPane.getViewport().getView();
            textArea.setFont(nuevaFuente);
        }

        // Aplicar la fuente a otros componentes si es necesario
        jTextArea1.setFont(nuevaFuente);

        // Aplicar configuración de idioma
        aplicarIdioma();

        // Actualizar la ruta de trabajo
        if (!Configuracion.rutaTrabajo.isEmpty()) {
            lastDirectory = Configuracion.rutaTrabajo;
        }
    }

    private void aplicarIdioma() {
        if (Configuracion.idiomaSeleccionado.equals("Español")) {
            Archivo.setText("Archivo");
            Nuevo.setText("Nuevo");
            Abrir.setText("Abrir");
            Cerrar.setText("Cerrar");
            Guardar.setText("Guardar");
            Guardar_como.setText("Guardar como");
            Salir.setText("Salir");
            Editar.setText("Editar");
            Copiar.setText("Copiar");
            Cortar.setText("Cortar");
            Pegar.setText("Pegar");
            Compilar.setText("Compilar");
            compilar.setText("Compilar");
            Compilar_correr.setText("Compilar y correr");
            Herramientas.setText("Herramientas");
            menuItemConfiguracion.setText("Configuración");
            jLabel1.setText("Renglón: 1 | Columna: 1");
            jLabel2.setText("Hora local: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
            etiquetaCopiar = "Copiar";
            etiquetaCortar = "Cortar";
            etiquetaPegar = "Pegar";

            // Actualiza las etiquetas del menú contextual
            actualizarEtiquetasMenuContextual(etiquetaCopiar, etiquetaCortar, etiquetaPegar);

        } else if (Configuracion.idiomaSeleccionado.equals("Ingles")) {
            Archivo.setText("File");
            Nuevo.setText("New");
            Abrir.setText("Open");
            Cerrar.setText("Close");
            Guardar.setText("Save");
            Guardar_como.setText("Save As");
            Salir.setText("Exit");
            Editar.setText("Edit");
            Copiar.setText("Copy");
            Cortar.setText("Cut");
            Pegar.setText("Paste");
            Compilar.setText("Compile");
            compilar.setText("Compile");
            Compilar_correr.setText("Compile and Run");
            Herramientas.setText("Tools");
            menuItemConfiguracion.setText("Configuration");
            jLabel1.setText("Line: 1 | Column: 1");
            jLabel2.setText("Local time: " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
            etiquetaCopiar = "Copy";
            etiquetaCortar = "Cut";
            etiquetaPegar = "Paste";

            // Actualiza las etiquetas del menú contextual
            actualizarEtiquetasMenuContextual(etiquetaCopiar, etiquetaCortar, etiquetaPegar);
        }
    }

    private void actualizarEtiquetasMenuContextual(String copiar, String cortar, String pegar) {
        // Actualizar las etiquetas del menú contextual principal
        menuItemCopiar.setText(copiar);
        menuItemCortar.setText(cortar);
        menuItemPegar.setText(pegar);

        // Actualizar las etiquetas en todos los menús contextuales de las pestañas
        for (JPopupMenu menu : contextMenus) {
            for (Component comp : menu.getComponents()) {
                if (comp instanceof JMenuItem) {
                    JMenuItem item = (JMenuItem) comp;
                    String actionCommand = item.getActionCommand();

                    if (actionCommand.equals("COPY")) {
                        item.setText(copiar);
                    } else if (actionCommand.equals("CUT")) {
                        item.setText(cortar);
                    } else if (actionCommand.equals("PASTE")) {
                        item.setText(pegar);
                    }
                }
            }
        }
    }

    private void AnalisisLexico() {
        JPanel panelActual = obtenerPanelActual();
        String rutaActual = archivosPestañas.get(panelActual).getAbsolutePath();
        Scanner scanner;
        Functions.clearDataInTable(salidaTokens);
        // saveFile(); 
        try {
            BufferedReader codigoFuente = new BufferedReader(new FileReader(rutaActual));
            scanner = new Scanner(codigoFuente);
            Token token = scanner.yylex();
            while (token != null) {
                Object[] arrayToken = {token.getLexicalComp(), token.getLexeme(), "[" + token.getLine() + "," + token.getColumn() + "]"};
                Functions.addRowDataInTable(salidaTokens, arrayToken);
                listaTokens.add(token);
                token = scanner.yylex();
            }
        } catch (IOException ex) {
            //Logger.getLogger(MicroPascal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void generarAnalizadorLexico() {
        String archivoLexicoJava = System.getProperty("user.dir") + "/src/main/Scanner.java";
        File LexicoJava = new File(archivoLexicoJava);
        if (LexicoJava.exists()) {
            LexicoJava.delete();
        }
        String archivoLexicoFlex = System.getProperty("user.dir") + "/src/main/Lexico.flex";
        try {
            jflex.Main.generate(new String[]{archivoLexicoFlex});
            resultados.append("***** GENERACION CORRECTA DEL ANALIZADOR LÉXICO *****\n");
        } catch (Exception ex) {
            System.out.println("***** ERROR DE GENERACION DEL ANALIZADOR LÉXICO *****");
            System.exit(1);
        }
    }


    private void SalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalirActionPerformed
        // TODO add your handling code here:
        Salir();
    }//GEN-LAST:event_SalirActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        Salir();
    }//GEN-LAST:event_formWindowClosing

    private void nuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoActionPerformed
        agregarPestaña();
    }//GEN-LAST:event_nuevoActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        saveFile();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        abrirArchivo();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void NuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NuevoActionPerformed
        agregarPestaña();
    }//GEN-LAST:event_NuevoActionPerformed

    private void AbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AbrirActionPerformed
        abrirArchivo();
    }//GEN-LAST:event_AbrirActionPerformed

    private void Guardar_comoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Guardar_comoActionPerformed
        saveFile();
    }//GEN-LAST:event_Guardar_comoActionPerformed

    private void CerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CerrarActionPerformed
        cerrarPestañaSeleccionada();
    }//GEN-LAST:event_CerrarActionPerformed

    private void menuItemConfiguracionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuItemConfiguracionMouseClicked

    }//GEN-LAST:event_menuItemConfiguracionMouseClicked

    private void menuItemConfiguracionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemConfiguracionActionPerformed
        Configuracion x = new Configuracion(this);
        x.setVisible(true);
    }//GEN-LAST:event_menuItemConfiguracionActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        System.out.println("Cantidad de errores registrados ANTES de limpiar: " + gestionErrores.size());

        listaTokens.clear();
        gestionErrores.clear();
        tablaSimbolos.clear();
        idDeclaracion.clear();
        idAsignacion.clear();
        idSentencia.clear();
        idProgramaCompleto.clear();

        Functions.clearDataInTable(salidaTokens);
        DefaultTableModel modelo = (DefaultTableModel) salidaSimbolos.getModel();
        modelo.setRowCount(0);

        resultados.setText("");

        generarAnalizadorLexico();
        AnalisisLexico();
        if (!verificarBalanceLlaves()) {
            // Obtenemos la línea del último token para que el error no sea "Línea 0"
            int ultimaLinea = 1;
            int ultimaColumna = 1;
            if (!listaTokens.isEmpty()) {
                Token ultimo = listaTokens.get(listaTokens.size() - 1);
                ultimaLinea = ultimo.getLine();
                ultimaColumna = ultimo.getColumn();
            }

            // Creamos el token falso con la ubicación del final del archivo
            Token tokenFalso = new Token("}", "ERROR_LLAVES", ultimaLinea, ultimaColumna); 
            listaTokens.add(tokenFalso);
        }
        AnalisisSintactico();
        AnalisisSemantico();  

        mostrarTablaSimbolos();
        pintarErrores();

        int sizeErrors = gestionErrores.size();
        System.out.println("Cantidad TOTAL de errores (lexicos, sintacticos, semanticos) DESPUES de todos los analisis: " + sizeErrors);

        if (sizeErrors > 0) {
            imprimirErrores();
        } else {
            resultados.append("Compilación aparentemente exitosa (sin errores semánticos detectados).\n"); 

            // -------------- GENERACIÓN DE CÓDIGO INTERMEDIO --------------
            
            JPanel panelActual = obtenerPanelActual();
            File archivoActual = archivosPestañas.get(panelActual);

            if (archivoActual == null) {
                JOptionPane.showMessageDialog(this,
                        "Debes guardar o abrir el archivo antes de generar el código ensamblador.",
                        "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 1. Calcular la ruta EXACTA para el archivo .3D de forma segura
            String rutaArchivoOriginal = archivoActual.getAbsolutePath();
            String rutaArchivo3D;

            int indexPunto = rutaArchivoOriginal.lastIndexOf(".");
            if (indexPunto > 0) {
                rutaArchivo3D = rutaArchivoOriginal.substring(0, indexPunto) + ".3D";
            } else {
                rutaArchivo3D = rutaArchivoOriginal + ".3D";
            }

            // 2. Preparar los motores
            generador3D.iniciaizaTempEtiq();
            generadorASM.inicializarTempEtiq();

            // IMPORTANTE: Decirle al generador dónde va a escribir ANTES de convertir
            generador3D.crearArchivoCodigo3D(rutaArchivo3D); 

            // 3. Ejecutar la Traducción
            ConversionCodigoIntermedio(); 
            generador3D.cerrarArchivoCodigo3D(); // Recuerda cerrarlo al terminar

             // -------------- GENERACIÓN DE CÓDIGO INTERMEDIO --------------
             
            ThreeAddressToAsmConverter asm86 = new ThreeAddressToAsmConverter();
            try {
                File archivo3D = new File(rutaArchivo3D);
                if (!archivo3D.exists()) {
                    resultados.append("Error: El archivo de código intermedio " + rutaArchivo3D + " no fue generado.\n");
                } else {
                    asm86.convert(rutaArchivo3D);
                    resultados.append("Conversión a ensamblador completada con éxito.\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                resultados.append("Error durante la conversión a ensamblador: " + ex.getMessage() + "\n");
            }
            
            // -------------- FIN DE LA SECCIÓN --------------
 
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
                                    
        // 1. Crear la ventana de selección de archivos
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Seleccionar archivo optimizado (.3D)");

        // =================================================================
        // 2. RECUPERAR MEMORIA: Usamos tu variable 'lastDirectory'
        if (lastDirectory != null && !lastDirectory.isEmpty()) {
            fileChooser.setCurrentDirectory(new java.io.File(lastDirectory));
        }
        // =================================================================

        // 3. Crear el filtro para .3D y .txt
        javax.swing.filechooser.FileNameExtensionFilter filtro = new javax.swing.filechooser.FileNameExtensionFilter("Código de 3 Direcciones (*.3D)", "3D", "3d");
        fileChooser.setFileFilter(filtro);

        // 4. Mostrar la ventana
        int seleccion = fileChooser.showOpenDialog(this);

        // 5. Si el usuario selecciona un archivo y le da a "Abrir"
        if (seleccion == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File archivoSeleccionado = fileChooser.getSelectedFile();
            
            // =================================================================
            // 6. GUARDAR MEMORIA: Actualizamos tu variable 'lastDirectory' como String
            lastDirectory = fileChooser.getCurrentDirectory().getAbsolutePath();
            // =================================================================

            String rutaEntrada = archivoSeleccionado.getAbsolutePath();

            try {
                // 7. Instanciar y ejecutar el convertidor
                main.ThreeAddressToAsmConverter convertidor = new main.ThreeAddressToAsmConverter();
                convertidor.convert(rutaEntrada); 
                
                // 8. Mensaje de éxito
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "¡Conversión exitosa!\nEl código optimizado fue traducido a Ensamblador.\nRevisa la carpeta donde está el archivo original.", 
                    "Motor ASM Autónomo", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                // 9. Manejo de errores
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Hubo un error al intentar convertir el archivo:\n" + e.getMessage(), 
                    "Error de Conversión", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void AnalisisSintactico() {
            String si = "SI PARENTESIS_APERTURA (PARENTESIS_APERTURA)* (NO)? (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL) (MENOR_QUE | MENOR_IGUAL | IGUAL_A | DIFERENTE_DE | MAYOR_IGUAL | MAYOR_QUE) (NO)? (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL) (PARENTESIS_CIERRE)* ((Y | O) (NO)? (PARENTESIS_APERTURA)* (NO)? (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL) (MENOR_QUE | MENOR_IGUAL | IGUAL_A | DIFERENTE_DE | MAYOR_IGUAL | MAYOR_QUE) (NO)? (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL) (PARENTESIS_CIERRE)*)* PARENTESIS_CIERRE LLAVE_APERTURA (Sentencias)* LLAVE_CIERRE";
            String sino = "SINO PARENTESIS_APERTURA (PARENTESIS_APERTURA)* (NO)? (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL) (MENOR_QUE | MENOR_IGUAL | IGUAL_A | DIFERENTE_DE | MAYOR_IGUAL | MAYOR_QUE) (NO)? (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL) (PARENTESIS_CIERRE)* ((Y | O) (NO)? (PARENTESIS_APERTURA)* (NO)? (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL) (MENOR_QUE | MENOR_IGUAL | IGUAL_A | DIFERENTE_DE | MAYOR_IGUAL | MAYOR_QUE) (NO)? (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL) (PARENTESIS_CIERRE)*)* PARENTESIS_CIERRE LLAVE_APERTURA (Sentencias | PSi)* LLAVE_CIERRE";
            String finale = "FINAL LLAVE_APERTURA (Sentencias)* LLAVE_CIERRE";

            System.out.println("\n------------------------ ANÁLISIS SINTÁCTICO ------------------------");
            // Eliminar nulos
            listaTokens.removeIf(token -> token == null);

            // Creamos el objeto de tipo Grammar, pasando como parámetro el ArrayList de Tokens y el ArrayList de errores
            Grammar gramatica = new Grammar(listaTokens, gestionErrores);

            // Eliminar errores encontrados por el analizador léxico en el programa fuente para que no interfieran con las agrupaciones
            gramatica.delete(new String[]{"Error", "Error_1", "Error_2", "Error_3"}, 1); // Error 1

            String operandos = "(IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL | VERDADERO | FALSO | CADENA)";
            String opRel = "(MENOR_QUE | MENOR_IGUAL | IGUAL_A | DIFERENTE_DE | MAYOR_IGUAL | MAYOR_QUE)";
            String opLog = "(Y | O)";
            
            //Permite que los bloques válidos acepten piezas que ya fueron procesadas o separadas
            //String bloque = "(Sentencias | PROGRAMA | si | elseIf | else | PSi)*";
            String bloque = "(Sentencias | si | elseIf | else | PSi)*";
            
            // Macros para facilitar la lectura de las funciones
            String tiposFuncion = "(INICIO | NUM | FLOT | TEXTO | LOGICO)";
            
            String parametrosDecl = "(" + tiposFuncion + " IDENTIFICADOR (COMA " + tiposFuncion + " IDENTIFICADOR)*)?";

            //String condicion = "(PARENTESIS_APERTURA)* (NO)? " + operandos + " " + opRel + " (NO)? " + operandos + " (PARENTESIS_CIERRE)* (" + opLog + " (NO)? (PARENTESIS_APERTURA)* (NO)? " + operandos + " " + opRel + " (NO)? " + operandos + " (PARENTESIS_CIERRE)*)*";
            String condBasica = "(NO)? " + operandos + " " + opRel + " (NO)? " + operandos;
            String condicion = condBasica + " (" + opLog + " " + condBasica + ")*";

            String opArit = "(SUMA | RESTA | MULTIPLICACION | DIVISION | MODULO)";
            String expAritmetica = "(PARENTESIS_APERTURA)* " + operandos + " (PARENTESIS_CIERRE)* (" + opArit + " (PARENTESIS_APERTURA)* " + operandos + " (PARENTESIS_CIERRE)*)*";
            String argImpresion = "(IDENTIFICADOR | CADENA) (SUMA (IDENTIFICADOR | CADENA))*";

            // 2. Agrupaciones base
            gramatica.loopForFunExecUntilChangeNotDetected(() -> {
                gramatica.group("NUMEROENTERO", "(PARENTESIS_APERTURA)* (NUMEROENTERO | NUMERODECIMAL | IDENTIFICADOR) (PARENTESIS_CIERRE)* (" + opArit + " (PARENTESIS_APERTURA)* (NUMEROENTERO | NUMERODECIMAL | IDENTIFICADOR) (PARENTESIS_CIERRE)*)+", true);

                gramatica.group("PEscribirLinea", "ESCRIBIR_LINEA PARENTESIS_APERTURA " + argImpresion + " PARENTESIS_CIERRE FIN_LINEA", true, idSentencia);
                gramatica.group("PEscribir", "ESCRIBIR PARENTESIS_APERTURA " + argImpresion + " PARENTESIS_CIERRE FIN_LINEA", true, idSentencia);
                gramatica.group("PLeer", "LEER PARENTESIS_APERTURA IDENTIFICADOR PARENTESIS_CIERRE FIN_LINEA", true, idSentencia);

                gramatica.group("PIncremento", "IDENTIFICADOR INCREMENTO FIN_LINEA", true);
                gramatica.group("PDecremento", "IDENTIFICADOR DECREMENTO FIN_LINEA", true);
                gramatica.group("PRetornar", "RETORNAR " + operandos + "? FIN_LINEA", true, idSentencia);

                // Declaraciones
                gramatica.group("PDeclaracion", "(NUM | TEXTO | LOGICO | FLOT) IDENTIFICADOR (OPERADORASIGNACION (" + expAritmetica + "))? (COMA IDENTIFICADOR (OPERADORASIGNACION (" + expAritmetica + "))?)* FIN_LINEA", true, idDeclaracion);
                gramatica.group("PDeclaracion", "(NUM | TEXTO | LOGICO | FLOT) IDENTIFICADOR OPERADORASIGNACION " + condicion + " FIN_LINEA", true, idDeclaracion);
                gramatica.group("PDeclaracion", "(NUM | TEXTO | LOGICO | FLOT) IDENTIFICADOR OPERADORASIGNACION PLlamadaFuncion", true, idDeclaracion);

                // Asignaciones normales y abreviadas
                String operadoresAsignacion = "(OPERADORASIGNACION | ASIGNACIONSUMA | ASIGNACIONRESTA | ASIGNACIONMULTIPLICACION | ASIGNACIONDIVISION)";

                gramatica.group("PAsignacion", "IDENTIFICADOR " + operadoresAsignacion + " " + expAritmetica + " FIN_LINEA", true, idAsignacion);
                gramatica.group("PAsignacion", "IDENTIFICADOR " + operadoresAsignacion + " " + condicion + " FIN_LINEA", true, idAsignacion);
                gramatica.group("PAsignacion", "IDENTIFICADOR " + operadoresAsignacion + " PLlamadaFuncion", true, idAsignacion);
                gramatica.group("PAsignacion", "IDENTIFICADOR " + operadoresAsignacion + " (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL | CADENA | VERDADERO | FALSO) FIN_LINEA", true, idAsignacion);

                //gramatica.group("PLlamadaFuncion", "IDENTIFICADOR PARENTESIS_APERTURA " + operandos + " (COMA " + operandos + ")* PARENTESIS_CIERRE FIN_LINEA", true, idSentencia);
                gramatica.group("PLlamadaFuncion", "IDENTIFICADOR PARENTESIS_APERTURA (" + operandos + " (COMA " + operandos + ")*)? PARENTESIS_CIERRE FIN_LINEA", true, idSentencia);
            });

            gramatica.group("PSalir", "SALIR FIN_LINEA", true);
            //gramatica.group("PParaInside", "PDeclaracion " + condicion + " FIN_LINEA IDENTIFICADOR (INCREMENTO | DECREMENTO)", true);
            // Ahora acepta tanto declarar la variable ahí mismo, como solo asignarle un valor inicial
            gramatica.group("PParaInside", "(PDeclaracion | PAsignacion) " + condicion + " FIN_LINEA IDENTIFICADOR (INCREMENTO | DECREMENTO)", true);


            // 3. Estructuras de Control de Flujo 
            gramatica.loopForFunExecUntilChangeNotDetected(() -> {
            //gramatica.group("Sentencias", "PEscribir | PEscribirLinea | PLeer | PFlot | PLogico | PDeclaracion | PTexto | PRetornar | PAsignacion | PAsigSuma | PAsigResta | PAsigMulti | PAsigDiv | PIncremento | PDecremento | PSi | PElegir | PMientras | PPara | DeclaracionFuncion | PLlamadaFuncion", true);
            gramatica.group("Sentencias", "PEscribir | PEscribirLinea | PLeer | PFlot | PLogico | PDeclaracion | PTexto | PRetornar | PAsignacion | PAsigSuma | PAsigResta | PAsigMulti | PAsigDiv | PIncremento | PDecremento | PSi | PElegir | PMientras | PPara | PLlamadaFuncion", true);
            //gramatica.group("Sentencias", "PEscribir | PEscribirLinea | PLeer | PFlot | PLogico | PDeclaracion | PTexto | PRetornar | PAsignacion | PAsigSuma | PAsigResta | PAsigMulti | PAsigDiv | PIncremento | PDecremento | PSi | PElegir | PMientras | PPara | PLlamadaFuncion | PSalir", true);
            
            // Usamos "bloque" en lugar de "(Sentencias)*" para que los anidamientos no exploten
            gramatica.group("else", "FINAL LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true);
            gramatica.group("elseIf", "SINO PARENTESIS_APERTURA " + condicion + " PARENTESIS_CIERRE LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true, idCondiciones);
            gramatica.group("si", "SI PARENTESIS_APERTURA " + condicion + " PARENTESIS_CIERRE LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true, idCondiciones);
            
            gramatica.group("si", "si elseIf* else?", true);
            gramatica.group("si", "si elseIf*", true);
            gramatica.group("si", "si else?", true);
            gramatica.group("PSi", "si", true, idSentencia);
            
            gramatica.group("PDefecto", "DEFECTO DOSPUNTOS " + bloque + " PSalir", true);
            gramatica.group("PCaso", "CASO " + operandos + " DOSPUNTOS " + bloque + " PSalir", true);
            
            // MAGIA DE FUSIÓN DE CASOS PARA EL ELEGIR (Para soportar switches con muchos casos)
            gramatica.group("CuerpoElegir", "(PCaso)+ (PDefecto)?"); 
            gramatica.group("CuerpoElegir", "CuerpoElegir CuerpoElegir"); 
            gramatica.group("PElegir", "ELEGIR PARENTESIS_APERTURA IDENTIFICADOR PARENTESIS_CIERRE LLAVE_APERTURA CuerpoElegir LLAVE_CIERRE", true, idElegir);
            
            gramatica.group("PMientras", "MIENTRAS PARENTESIS_APERTURA " + condicion + " PARENTESIS_CIERRE LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true, idCondiciones);
            gramatica.group("PPara", "PARA PARENTESIS_APERTURA PParaInside PARENTESIS_CIERRE LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true, idSentencia);
            
            //gramatica.group("DeclaracionFuncion", "(INICIO | NUM | FLOT | TEXTO | LOGICO) IDENTIFICADOR PARENTESIS_APERTURA ((INICIO | NUM | FLOT | TEXTO | LOGICO) IDENTIFICADOR)? (COMA (INICIO | NUM | FLOT | TEXTO | LOGICO) IDENTIFICADOR)* PARENTESIS_CIERRE LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true, idProgramaCompleto);
            // REGLA PRINCIPAL DE DECLARACIÓN DE FUNCIÓN
            gramatica.group("DeclaracionFuncion", tiposFuncion + " IDENTIFICADOR PARENTESIS_APERTURA " + parametrosDecl + " PARENTESIS_CIERRE LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true, idProgramaCompleto);
        });
            
            gramatica.loopForFunExecUntilChangeNotDetected(() -> {

               // ========================================================================
               // -------------------------------------- ERRORES DE SINTAXIS --------------------------------------------
               // ======================================================================== 


            // ==========================================================
            //  MACROS GLOBALES PARA ERRORES SINTÁCTICOS
            //  Se declaran una sola vez y se usan en todos lados
            // ==========================================================
            // String sents = "(Sentencias | PROGRAMA)*";
            //String sents = "(Sentencias | PROGRAMA | IDENTIFICADOR | ELEGIR | CASO | DEFECTO | SALIR | MIENTRAS | PARA | SI | SINO | FINAL | LLAVE_APERTURA | LLAVE_CIERRE)*";
            String sents = "(Sentencias | IDENTIFICADOR | ELEGIR | CASO | DEFECTO | SALIR | MIENTRAS | PARA | SI | SINO | FINAL | LLAVE_APERTURA | LLAVE_CIERRE)*";

            String condRaw = "(IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL | CADENA | VERDADERO | FALSO) (MENOR_QUE | MENOR_IGUAL | IGUAL_A | DIFERENTE_DE | MAYOR_IGUAL | MAYOR_QUE) (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL | CADENA | VERDADERO | FALSO)";

            String decFor = "(PROGRAMA | Sentencias | PDeclaracion | PAsignacion)";

            String bloqueElegir = "(PROGRAMA | Sentencias | PDeclaracion | PAsignacion | PEscribirLinea | PEscribir | PLeer | PLlamadaFuncion | PSalir | PDefecto | PCaso | CuerpoElegir | SALIR | DEFECTO | CASO | LLAVE_CIERRE)";

            // ==========================================================


                // ==========================================================
                // ERRORES DE SINTAXIS: CICLO PARA (CABECERAS AISLADAS)
                // ==========================================================
                // --- Errores de Estructura Exterior ---
                gramatica.group("PPara", "PARA PParaInside PARENTESIS_CIERRE LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 240, "[Línea #] Error Sintáctico 240: Falta el paréntesis de apertura '(' al inicio del ciclo 'para'");
                gramatica.group("PPara", "PARA PARENTESIS_APERTURA PParaInside LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 241, "[Línea #] Error Sintáctico 241: Falta el paréntesis de cierre ')' al final del ciclo 'para'");
                gramatica.group("PPara", "PARA PARENTESIS_APERTURA PParaInside PARENTESIS_CIERRE " + sents + " LLAVE_CIERRE", true, 242, "[Línea #] Error Sintáctico 242: Falta la llave de apertura '{' en el ciclo 'para'");
                gramatica.group("PPara", "PARA PARENTESIS_APERTURA PParaInside PARENTESIS_CIERRE LLAVE_APERTURA " + sents, true, 243, "[Línea #] Error Sintáctico 243: Falta la llave de cierre '}' en el ciclo 'para'");


                String condFor = "(IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL) (MENOR_QUE | MENOR_IGUAL | IGUAL_A | DIFERENTE_DE | MAYOR_IGUAL | MAYOR_QUE) (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL)";
                String incFor = "(PIncremento | PDecremento | IDENTIFICADOR INCREMENTO | IDENTIFICADOR DECREMENTO)";

                // 1. Falta el iterador final (++) (Error 244)
                gramatica.group("PPara", "PARA PARENTESIS_APERTURA " + decFor + " " + condFor + " FIN_LINEA PARENTESIS_CIERRE", true, 244, "[Línea #] Error Sintáctico 244: Falta el iterador (ej. i++) al final de la configuración del 'para'");

                // 2. Falta el punto y coma después de la condición (Error 245)
                gramatica.group("PPara", "PARA PARENTESIS_APERTURA " + decFor + " " + condFor + " " + incFor + " PARENTESIS_CIERRE", true, 245, "[Línea #] Error Sintáctico 245: Falta el punto y coma ';' después de la condición en el 'para'");

                // 3. Falta la condición de evaluación (Error 246)
                gramatica.group("PPara", "PARA PARENTESIS_APERTURA " + decFor + " FIN_LINEA " + incFor + " PARENTESIS_CIERRE", true, 246, "[Línea #] Error Sintáctico 246: Falta la condición de evaluación (ej. i < 10) en el 'para'");

                // 4. Falta la variable en el incremento, solo pusieron ++ (Error 247)
                gramatica.group("PPara", "PARA PARENTESIS_APERTURA " + decFor + " " + condFor + " FIN_LINEA (INCREMENTO | DECREMENTO) PARENTESIS_CIERRE", true, 247, "[Línea #] Error Sintáctico 247: Falta la variable a incrementar/decrementar en el 'para'");
               // ==========================================================


              // ==========================================================
              // ERRORES DE SINTAXIS: ELEGIR (SWITCH), CASO Y DEFECTO
              // ==========================================================
              // El "Súper Comodín": Atrapa lo que sea que siga, esté crudo o ya agrupado
                String valCaso = "(NUMEROENTERO | NUMERODECIMAL | CADENA | IDENTIFICADOR)";

                // --- ELEGIR (Errores de Estructura Exterior) ---
                // 1. Falta paréntesis de apertura '('
                gramatica.group("PElegir", "ELEGIR IDENTIFICADOR PARENTESIS_CIERRE LLAVE_APERTURA", true, 248, "[Línea #] Error Sintáctico 248: Falta el paréntesis de apertura '(' en 'elegir'");

                // 2. Falta paréntesis de cierre ')'
                gramatica.group("PElegir", "ELEGIR PARENTESIS_APERTURA IDENTIFICADOR LLAVE_APERTURA", true, 249, "[Línea #] Error Sintáctico 249: Falta el paréntesis de cierre ')' en 'elegir'");

                // 3. Variable vacía
                gramatica.group("PElegir", "ELEGIR PARENTESIS_APERTURA PARENTESIS_CIERRE LLAVE_APERTURA", true, 250, "[Línea #] Error Sintáctico 250: Falta la variable a evaluar dentro de 'elegir()'");

                // 4. Falta llave de apertura '{'
                gramatica.group("PElegir", "ELEGIR PARENTESIS_APERTURA IDENTIFICADOR PARENTESIS_CIERRE " + bloqueElegir, true, 251, "[Línea #] Error Sintáctico 251: Falta la llave de apertura '{' en 'elegir'");

                // --- CASO ---
                // 5. Falta el valor a evaluar (ej: caso :)
                gramatica.group("PCaso", "CASO DOSPUNTOS", true, 252, "[Línea #] Error Sintáctico 252: Falta el valor a evaluar en el 'caso'");

                // 6. Faltan los dos puntos ':' 
                gramatica.group("PCaso", "CASO " + valCaso + " " + bloqueElegir, true, 253, "[Línea #] Error Sintáctico 253: Faltan los dos puntos ':' al final del 'caso'");

                // --- DEFECTO ---
                // 7. Faltan los dos puntos ':'
                gramatica.group("PDefecto", "DEFECTO " + bloqueElegir, true, 254, "[Línea #] Error Sintáctico 254: Faltan los dos puntos ':' al final de 'defecto'");

                // --- SALIR (Break) ---
                // 8. Falta punto y coma
                gramatica.group("PSalir", "SALIR " + bloqueElegir, true, 255, "[Línea #] Error Sintáctico 255: Falta el punto y coma ';' después de 'salir'");
                // ==========================================================


                // ==========================================================
                // ERRORES DE SINTAXIS: CICLO MIENTRAS (WHILE)
                // ==========================================================
                // Usamos sents = "(Sentencias | PROGRAMA)*" que ya declaraste arriba

                // 1. Falta el paréntesis de apertura '('
                gramatica.group("PMientras", "MIENTRAS (" + condicion + " | " + condRaw + ") PARENTESIS_CIERRE LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 256, "[Línea #] Error Sintáctico 256: Falta el paréntesis de apertura '(' en el ciclo 'mientras'");

                // 2. Falta el paréntesis de cierre ')'
                gramatica.group("PMientras", "MIENTRAS PARENTESIS_APERTURA (" + condicion + " | " + condRaw + ") LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 257, "[Línea #] Error Sintáctico 257: Falta el paréntesis de cierre ')' en el ciclo 'mientras'");

                // 3. Falta la llave de apertura '{'
                gramatica.group("PMientras", "MIENTRAS PARENTESIS_APERTURA (" + condicion + " | " + condRaw + ") PARENTESIS_CIERRE " + sents + " LLAVE_CIERRE", true, 258, "[Línea #] Error Sintáctico 258: Falta la llave de apertura '{' en el ciclo 'mientras'");

                // 4. Falta la llave de cierre '}' (Usará el obstáculo para detonar)
                gramatica.group("PMientras", "MIENTRAS PARENTESIS_APERTURA (" + condicion + " | " + condRaw + ") PARENTESIS_CIERRE LLAVE_APERTURA " + sents, true, 259, "[Línea #] Error Sintáctico 259: Falta la llave de cierre '}' en el ciclo 'mientras'");

                // 5. Condición vacía
                gramatica.group("PMientras", "MIENTRAS PARENTESIS_APERTURA PARENTESIS_CIERRE LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 260, "[Línea #] Error Sintáctico 260: Falta la condición a evaluar dentro de 'mientras()'");
                // ==========================================================


                // ==========================================================
                // ERRORES DE SINTAXIS: CONDICIONALES (SI, SINO, FINAL)
                // ==========================================================
                // Reutilizamos sents y condRaw que declaramos para el mientras

                // --- BLOQUE 'SI' (IF) ---
                // 1. Falta el paréntesis de apertura '('
                gramatica.group("si", "SI (" + condicion + " | " + condRaw + ") PARENTESIS_CIERRE LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 261, "[Línea #] Error Sintáctico 261: Falta el paréntesis de apertura '(' en la condición 'si'");
                // 2. Falta el paréntesis de cierre ')'
                gramatica.group("si", "SI PARENTESIS_APERTURA (" + condicion + " | " + condRaw + ") LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 262, "[Línea #] Error Sintáctico 262: Falta el paréntesis de cierre ')' en la condición 'si'");
                // 3. Falta la llave de apertura '{'
                gramatica.group("si", "SI PARENTESIS_APERTURA (" + condicion + " | " + condRaw + ") PARENTESIS_CIERRE " + sents + " LLAVE_CIERRE", true, 263, "[Línea #] Error Sintáctico 263: Falta la llave de apertura '{' en el bloque 'si'");
                // 4. Falta la llave de cierre '}'
                gramatica.group("si", "SI PARENTESIS_APERTURA (" + condicion + " | " + condRaw + ") PARENTESIS_CIERRE LLAVE_APERTURA " + sents, true, 264, "[Línea #] Error Sintáctico 264: Falta la llave de cierre '}' en el bloque 'si'");
                // 5. Condición vacía
                gramatica.group("si", "SI PARENTESIS_APERTURA PARENTESIS_CIERRE LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 265, "[Línea #] Error Sintáctico 265: Falta la condición a evaluar dentro de 'si()'");

                // --- BLOQUE 'SINO' (ELSE IF) ---
                // 8. Falta el paréntesis de apertura '('
                gramatica.group("elseIf", "SINO (" + condicion + " | " + condRaw + ") PARENTESIS_CIERRE LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 268, "[Línea #] Error Sintáctico 268: Falta el paréntesis de apertura '(' en la condición 'sino'");
                // 9. Falta el paréntesis de cierre ')'
                gramatica.group("elseIf", "SINO PARENTESIS_APERTURA (" + condicion + " | " + condRaw + ") LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 269, "[Línea #] Error Sintáctico 269: Falta el paréntesis de cierre ')' en la condición 'sino'");
                // 10. Falta la llave de apertura '{'
                gramatica.group("elseIf", "SINO PARENTESIS_APERTURA (" + condicion + " | " + condRaw + ") PARENTESIS_CIERRE " + sents + " LLAVE_CIERRE", true, 270, "[Línea #] Error Sintáctico 270: Falta la llave de apertura '{' en el bloque 'sino'");
                // 11. Falta la llave de cierre '}'
                gramatica.group("elseIf", "SINO PARENTESIS_APERTURA (" + condicion + " | " + condRaw + ") PARENTESIS_CIERRE LLAVE_APERTURA " + sents, true, 271, "[Línea #] Error Sintáctico 271: Falta la llave de cierre '}' en el bloque 'sino'");
                // 12. Condición vacía
                gramatica.group("elseIf", "SINO PARENTESIS_APERTURA PARENTESIS_CIERRE LLAVE_APERTURA " + sents + " LLAVE_CIERRE", true, 272, "[Línea #] Error Sintáctico 272: Falta la condición a evaluar dentro de 'sino()'");

                // --- BLOQUE 'FINAL' (ELSE) ---
                // 6. Falta la llave de apertura '{'
                gramatica.group("else", "FINAL " + sents + " LLAVE_CIERRE", true, 266, "[Línea #] Error Sintáctico 266: Falta la llave de apertura '{' en el bloque 'final' (else)");
                // 7. Falta la llave de cierre '}'
                gramatica.group("else", "FINAL LLAVE_APERTURA " + sents, true, 267, "[Línea #] Error Sintáctico 267: Falta la llave de cierre '}' en el bloque 'final' (else)");
                // ==========================================================


               // ==========================================================
               // ERRORES DE SINTAXIS: ESCRIBIR Y ESCRIBIR_LINEA
               // ==========================================================
                // 1. Falta el punto y coma ';'
                gramatica.group("PEscribirLinea", "ESCRIBIR_LINEA PARENTESIS_APERTURA " + argImpresion + " PARENTESIS_CIERRE", true, 201, "[Línea #] Error Sintáctico 201: Falta el punto y coma ';' al final de 'escribirLinea'");
                gramatica.group("PEscribir", "ESCRIBIR PARENTESIS_APERTURA " + argImpresion + " PARENTESIS_CIERRE", true, 202, "[Línea #] Error Sintáctico 202: Falta el punto y coma ';' al final de 'escribir'");

                // 2. Falta el paréntesis de cierre ')'
                gramatica.group("PEscribirLinea", "ESCRIBIR_LINEA PARENTESIS_APERTURA " + argImpresion + " FIN_LINEA", true, 203, "[Línea #] Error Sintáctico 203: Falta el paréntesis de cierre ')' en 'escribirLinea'");
                gramatica.group("PEscribir", "ESCRIBIR PARENTESIS_APERTURA " + argImpresion + " FIN_LINEA", true, 204, "[Línea #] Error Sintáctico 204: Falta el paréntesis de cierre ')' en 'escribir'");

                // 3. Falta el paréntesis de apertura '('
                gramatica.group("PEscribirLinea", "ESCRIBIR_LINEA " + argImpresion + " PARENTESIS_CIERRE FIN_LINEA", true, 205, "[Línea #] Error Sintáctico 205: Falta el paréntesis de apertura '(' en 'escribirLinea'");
                gramatica.group("PEscribir", "ESCRIBIR " + argImpresion + " PARENTESIS_CIERRE FIN_LINEA", true, 206, "[Línea #] Error Sintáctico 206: Falta el paréntesis de apertura '(' en 'escribir'");

                // 4. Faltan AMBOS paréntesis
                gramatica.group("PEscribirLinea", "ESCRIBIR_LINEA " + argImpresion + " FIN_LINEA", true, 207, "[Línea #] Error Sintáctico 207: Faltan los paréntesis '(' ')' en 'escribirLinea'");
                gramatica.group("PEscribir", "ESCRIBIR " + argImpresion + " FIN_LINEA", true, 208, "[Línea #] Error Sintáctico 208: Faltan los paréntesis '(' ')' en 'escribir'");

                // 5. Dejaron los paréntesis vacíos
                gramatica.group("PEscribirLinea", "ESCRIBIR_LINEA PARENTESIS_APERTURA PARENTESIS_CIERRE FIN_LINEA", true, 209, "[Línea #] Error Sintáctico 209: 'escribirLinea' no puede estar vacío, requiere un texto o variable");
                gramatica.group("PEscribir", "ESCRIBIR PARENTESIS_APERTURA PARENTESIS_CIERRE FIN_LINEA", true, 210, "[Línea #] Error Sintáctico 210: 'escribir' no puede estar vacío, requiere un texto o variable");

                // 6. Concatenación incompleta (ej. escribir("hola" + ); )
                gramatica.group("PEscribirLinea", "ESCRIBIR_LINEA PARENTESIS_APERTURA (IDENTIFICADOR | CADENA) SUMA PARENTESIS_CIERRE FIN_LINEA", true, 211, "[Línea #] Error Sintáctico 211: Concatenación incompleta en 'escribirLinea', falta un valor después del '+'");
                gramatica.group("PEscribir", "ESCRIBIR PARENTESIS_APERTURA (IDENTIFICADOR | CADENA) SUMA PARENTESIS_CIERRE FIN_LINEA", true, 212, "[Línea #] Error Sintáctico 212: Concatenación incompleta en 'escribir', falta un valor después del '+'");
                // ==========================================================


                // ==========================================================
                // ERRORES DE SINTAXIS: LEER
                // ==========================================================
                // 1. Falta el punto y coma ';'
                gramatica.group("PLeer", "LEER PARENTESIS_APERTURA IDENTIFICADOR PARENTESIS_CIERRE", true, 213, "[Línea #] Error Sintáctico 213: Falta el punto y coma ';' al final de 'leer'");

                // 2. Falta el paréntesis de cierre ')'
                gramatica.group("PLeer", "LEER PARENTESIS_APERTURA IDENTIFICADOR FIN_LINEA", true, 214, "[Línea #] Error Sintáctico 214: Falta el paréntesis de cierre ')' en 'leer'");

                // 3. Falta el paréntesis de apertura '('
                gramatica.group("PLeer", "LEER IDENTIFICADOR PARENTESIS_CIERRE FIN_LINEA", true, 215, "[Línea #] Error Sintáctico 215: Falta el paréntesis de apertura '(' en 'leer'");

                // 4. Faltan AMBOS paréntesis (ej. leer variable;)
                gramatica.group("PLeer", "LEER IDENTIFICADOR FIN_LINEA", true, 216, "[Línea #] Error Sintáctico 216: Faltan los paréntesis '(' ')' en 'leer'");

                // 5. Dejaron los paréntesis vacíos (ej. leer();)
                gramatica.group("PLeer", "LEER PARENTESIS_APERTURA PARENTESIS_CIERRE FIN_LINEA", true, 217, "[Línea #] Error Sintáctico 217: 'leer' no puede estar vacío, requiere el nombre de una variable");
                 // ==========================================================


                 // ==========================================================
                // ERRORES DE SINTAXIS: ASIGNACIONES MATEMÁTICAS (+=, -=, *=, /=)
                // ==========================================================
                // 1. Falta el punto y coma ';' al final
                gramatica.group("PAsigSuma", "IDENTIFICADOR ASIGNACIONSUMA (NUMEROENTERO | NUMERODECIMAL | IDENTIFICADOR)", true, 218, "[Línea #] Error Sintáctico 218: Falta el punto y coma ';' al final de la asignación '+='");
                gramatica.group("PAsigResta", "IDENTIFICADOR ASIGNACIONRESTA (NUMEROENTERO | NUMERODECIMAL | IDENTIFICADOR)", true, 219, "[Línea #] Error Sintáctico 219: Falta el punto y coma ';' al final de la asignación '-='");
                gramatica.group("PAsigMulti", "IDENTIFICADOR ASIGNACIONMULTIPLICACION (NUMEROENTERO | NUMERODECIMAL | IDENTIFICADOR)", true, 220, "[Línea #] Error Sintáctico 220: Falta el punto y coma ';' al final de la asignación '*='");
                gramatica.group("PAsigDiv", "IDENTIFICADOR ASIGNACIONDIVISION (NUMEROENTERO | NUMERODECIMAL | IDENTIFICADOR)", true, 221, "[Línea #] Error Sintáctico 221: Falta el punto y coma ';' al final de la asignación '/='");

                // 2. Falta el valor a asignar (dejaron el operador huérfano, ej. variable += ;)
                gramatica.group("PAsigSuma", "IDENTIFICADOR ASIGNACIONSUMA FIN_LINEA", true, 222, "[Línea #] Error Sintáctico 222: Falta el valor a sumar en la asignación '+='");
                gramatica.group("PAsigResta", "IDENTIFICADOR ASIGNACIONRESTA FIN_LINEA", true, 223, "[Línea #] Error Sintáctico 223: Falta el valor a restar en la asignación '-='");
                gramatica.group("PAsigMulti", "IDENTIFICADOR ASIGNACIONMULTIPLICACION FIN_LINEA", true, 224, "[Línea #] Error Sintáctico 224: Falta el valor a multiplicar en la asignación '*='");
                gramatica.group("PAsigDiv", "IDENTIFICADOR ASIGNACIONDIVISION FIN_LINEA", true, 225, "[Línea #] Error Sintáctico 225: Falta el valor a dividir en la asignación '/='");
                 // ==========================================================


                 // ==========================================================
                // ERRORES DE SINTAXIS: INCREMENTOS, DECREMENTOS Y RETORNAR
                // ==========================================================
                // 1. Incremento incompleto (falta ;)
                gramatica.group("PIncremento", "IDENTIFICADOR INCREMENTO", true, 232, "[Línea #] Error Sintáctico 232: Falta el punto y coma ';' después del incremento '++'");

                // 2. Decremento incompleto (falta ;)
                gramatica.group("PDecremento", "IDENTIFICADOR DECREMENTO", true, 233, "[Línea #] Error Sintáctico 233: Falta el punto y coma ';' después del decremento '--'");

                // 3. Retornar incompleto (falta ;)
                gramatica.group("PRetornar", "RETORNAR (" + expAritmetica + " | " + condicion + " | (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL | CADENA | VERDADERO | FALSO))", true, 234, "[Línea #] Error Sintáctico 234: Falta el punto y coma ';' al final del 'retornar'");
                gramatica.group("PRetornar", "RETORNAR", true, 234, "[Línea #] Error Sintáctico 234: Falta el punto y coma ';' al final del 'retornar' (o está vacío)");
                // ==========================================================


                // ==========================================================
                // ERRORES DE SINTAXIS: DECLARACIONES Y ASIGNACIONES NORMALES
                // ==========================================================
                String tiposVar = "(NUM | TEXTO | LOGICO | FLOT)";

                // --- DECLARACIONES ---
                // 4. Falta el punto y coma ';' al final
                gramatica.group("PDeclaracion", tiposVar + " IDENTIFICADOR OPERADORASIGNACION (" + expAritmetica + " | " + condicion + " | (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL | CADENA | VERDADERO | FALSO))", true, 229, "[Línea #] Error Sintáctico 229: Falta el punto y coma ';' al final de la declaración");
                gramatica.group("PDeclaracion", tiposVar + " IDENTIFICADOR", true, 229, "[Línea #] Error Sintáctico 229: Falta el punto y coma ';' al final de la declaración");

                // 5. Falta el nombre de la variable
                gramatica.group("PDeclaracion", tiposVar + " OPERADORASIGNACION (" + expAritmetica + " | " + condicion + " | (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL | CADENA | VERDADERO | FALSO)) FIN_LINEA", true, 230, "[Línea #] Error Sintáctico 230: Falta el nombre de la variable en la declaración");

                // 6. Falta el valor después del igual
                gramatica.group("PDeclaracion", tiposVar + " IDENTIFICADOR OPERADORASIGNACION FIN_LINEA", true, 231, "[Línea #] Error Sintáctico 231: Declaración con inicialización vacía, falta el valor después del '='");

                // --- ASIGNACIONES (=) ---
                // 1. Falta el punto y coma ';' al final
                gramatica.group("PAsignacion", "IDENTIFICADOR OPERADORASIGNACION (" + expAritmetica + " | " + condicion + " | (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL | CADENA | VERDADERO | FALSO))", true, 226, "[Línea #] Error Sintáctico 226: Falta el punto y coma ';' al final de la asignación");

                // 2. Falta el valor (asignación vacía)
                gramatica.group("PAsignacion", "IDENTIFICADOR OPERADORASIGNACION FIN_LINEA", true, 227, "[Línea #] Error Sintáctico 227: Asignación vacía, falta el valor a la derecha del '='");

                // 3. Falta la variable (lado izquierdo vacío)
                gramatica.group("PAsignacion", "OPERADORASIGNACION (" + expAritmetica + " | " + condicion + " | (IDENTIFICADOR | NUMEROENTERO | NUMERODECIMAL | CADENA | VERDADERO | FALSO)) FIN_LINEA", true, 228, "[Línea #] Error Sintáctico 228: Falta la variable a la que se le va a asignar el valor (lado izquierdo del '=')");
                // ==========================================================

                // ==========================================================
                // ERRORES DE SINTAXIS: DECLARACIÓN DE FUNCIONES
                // ==========================================================

                // 1. Falta el tipo de retorno (ej. sumar(num a, num b) { ... })
                gramatica.group("DeclaracionFuncion", "IDENTIFICADOR PARENTESIS_APERTURA " + parametrosDecl + " PARENTESIS_CIERRE LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true, 273, "[Línea #] Error Sintáctico 273: Falta el tipo de dato que retorna la función (usa 'inicio' si no retorna nada)");

                // 2. Falta el nombre de la función (ej. num (num a) { ... })
                gramatica.group("DeclaracionFuncion", tiposFuncion + " PARENTESIS_APERTURA " + parametrosDecl + " PARENTESIS_CIERRE LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true, 274, "[Línea #] Error Sintáctico 274: Falta el nombre (identificador) de la función");

                // 3. Falta paréntesis de apertura (ej. num sumar num a) { ... })
                gramatica.group("DeclaracionFuncion", tiposFuncion + " IDENTIFICADOR " + parametrosDecl + " PARENTESIS_CIERRE LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true, 275, "[Línea #] Error Sintáctico 275: Falta el paréntesis de apertura '(' en la declaración de la función");

                // 4. Falta paréntesis de cierre (ej. num sumar(num a { ... })
                gramatica.group("DeclaracionFuncion", tiposFuncion + " IDENTIFICADOR PARENTESIS_APERTURA " + parametrosDecl + " LLAVE_APERTURA " + bloque + " LLAVE_CIERRE", true, 276, "[Línea #] Error Sintáctico 276: Falta el paréntesis de cierre ')' en los parámetros de la función");

                // 5. Falta llave de apertura (ej. num sumar(num a, num b) cuerpo... })
                gramatica.group("DeclaracionFuncion", tiposFuncion + " IDENTIFICADOR PARENTESIS_APERTURA " + parametrosDecl + " PARENTESIS_CIERRE " + bloque + " LLAVE_CIERRE", true, 277, "[Línea #] Error Sintáctico 277: Falta la llave de apertura '{' al inicio de la función");

                // 6. Falta llave de cierre
                gramatica.group("DeclaracionFuncion", tiposFuncion + " IDENTIFICADOR PARENTESIS_APERTURA " + parametrosDecl + " PARENTESIS_CIERRE LLAVE_APERTURA " + bloque, true, 278, "[Línea #] Error Sintáctico 278: Falta la llave de cierre '}' al final de la función");
                
                // ==========================================================
                // ERRORES DE SINTAXIS: LLAMADA A FUNCIONES
                // ==========================================================
                String argsLlamada = "(" + operandos + " (COMA " + operandos + ")*)?";

                // 1. Falta el punto y coma ';' al final
                gramatica.group("PLlamadaFuncion", "IDENTIFICADOR PARENTESIS_APERTURA " + argsLlamada + " PARENTESIS_CIERRE", true, 279, "[Línea #] Error Sintáctico 279: Falta el punto y coma ';' al final de la llamada a la función");

                // 2. Falta paréntesis de apertura
                gramatica.group("PLlamadaFuncion", "IDENTIFICADOR " + argsLlamada + " PARENTESIS_CIERRE FIN_LINEA", true, 280, "[Línea #] Error Sintáctico 280: Falta el paréntesis de apertura '(' en la llamada a la función");

                // 3. Falta paréntesis de cierre
                gramatica.group("PLlamadaFuncion", "IDENTIFICADOR PARENTESIS_APERTURA " + argsLlamada + " FIN_LINEA", true, 281, "[Línea #] Error Sintáctico 281: Falta el paréntesis de cierre ')' en la llamada a la función");

                });

//            gramatica.loopForFunExecUntilChangeNotDetected(() -> {
//                // "PROGRAMA se compone de Sentencias pegadas, o de un PROGRAMA pegado a Sentencias"
//                //gramatica.group("PROGRAMA", "Sentencias | PROGRAMA Sentencias", true);
//                 gramatica.group("PROGRAMA", "(Sentencias | PROGRAMA)");
//                 gramatica.group("PROGRAMA", "(PROGRAMA)+");
//            });

            // ==========================================================
            //                                      AGRUPACIÓN PRINCIPAL
            // ==========================================================
            gramatica.loopForFunExecUntilChangeNotDetected(() -> {
                 gramatica.group("PROGRAMA", "DeclaracionFuncion");
                 gramatica.group("PROGRAMA", "(PROGRAMA)+");
            });
            
            // ==========================================================
            //                              CAZADOR DE CÓDIGO SUELTO
            // ==========================================================
//            gramatica.loopForFunExecUntilChangeNotDetected(() -> {
//                 // 1. Atrapa sentencias y asignaciones completas que quedaron fuera
//                 gramatica.group("Error_Global", "Sentencias", true, 299, "[Línea #] Error Sintáctico 299: Entorno inválido. Las operaciones deben ir DENTRO de una función.");
//                 gramatica.group("Error_Global", "PDeclaracion", true, 299, "[Línea #] Error Sintáctico 299: Entorno inválido. Las variables deben declararse DENTRO de una función.");
//                 gramatica.group("Error_Global", "PAsignacion", true, 299, "[Línea #] Error Sintáctico 299: Entorno inválido. Las asignaciones deben ir DENTRO de una función.");
//                 
//                 // 2. Atrapa palabras al azar, identificadores o variables incompletas
//                 gramatica.group("Error_Global", "IDENTIFICADOR", true, 299, "[Línea #] Error Sintáctico 299: Código suelto o palabra no reconocida. Todo debe estar DENTRO de una función.");
//                 
//                 // 3. Atrapa números o textos flotando en la nada
//                 gramatica.group("Error_Global", "NUMEROENTERO | NUMERODECIMAL | CADENA | VERDADERO | FALSO", true, 299, "[Línea #] Error Sintáctico 299: Valor suelto inválido. Faltan llaves de función.");
//                 
//                 // 4. Atrapa intentos fallidos de crear funciones o variables
//                 gramatica.group("Error_Global", "NUM | FLOT | TEXTO | LOGICO | INICIO", true, 299, "[Línea #] Error Sintáctico 299: Declaración de función incompleta o mal formada.");
//                 
//                 // 5. Atrapa símbolos matemáticos sueltos
//                 gramatica.group("Error_Global", "SUMA | RESTA | MULTIPLICACION | DIVISION | OPERADORASIGNACION | FIN_LINEA", true, 299, "[Línea #] Error Sintáctico 299: Símbolo suelto fuera de un entorno válido.");
//            }); 

            gramatica.show();
        }

    private boolean verificarBalanceLlaves() {
    int balance = 0;
    for (Token t : listaTokens) {
        if (t.getLexicalComp().equals("LLAVE_APERTURA")) balance++;
        if (t.getLexicalComp().equals("LLAVE_CIERRE")) balance--;
    }
    return balance == 0;
}

    public void imprimirErrores() {
        // Se asume que este método solo se llama si gestionErrores NO está vacía.
        // Por lo tanto, el 'if (gestionErrores.isEmpty())' es redundante aquí.
        for (ErrorLSSL error : gestionErrores) {
            resultados.append(error.toString() + "\n");
        }
    }

    private void mostrarTablaSimbolos() {
        for (Simbolo simbolo : tablaSimbolos.values()) { //crear un arreglo de objetos con los strings anteriores 
            Object[] data = new Object[]{simbolo.getIdent(), simbolo.getTipo(), simbolo.getValor(), simbolo.getVarconst()};
            //Agregar un renglon en la JTable (salidaSimbolos) con el objeto de strings creado
            Functions.addRowDataInTable(salidaSimbolos, data);
        }
    }

public void AnalisisSemantico() {
    
        for (int k = 0; k < listaTokens.size(); k++) {
            Token t = listaTokens.get(k);
            
            // Buscamos un paréntesis de apertura
            if (t.getLexicalComp().equals("PARENTESIS_APERTURA")) {
                // Verificamos si los dos tokens anteriores forman la firma de una función
                if (k >= 2 
                    && listaTokens.get(k - 1).getLexicalComp().equals("IDENTIFICADOR")
                    && (listaTokens.get(k - 2).getLexicalComp().equals("NUM") || 
                        listaTokens.get(k - 2).getLexicalComp().equals("FLOT") || 
                        listaTokens.get(k - 2).getLexicalComp().equals("TEXTO") || 
                        listaTokens.get(k - 2).getLexicalComp().equals("LOGICO") ||
                        listaTokens.get(k - 2).getLexicalComp().equals("INICIO"))) {
                    
                    String nombreFuncion = listaTokens.get(k - 1).getLexeme();
                    String tipoRetorno = listaTokens.get(k - 2).getLexeme().toLowerCase();
                    
                    // 1. EXTRAER Y GUARDAR LOS TIPOS DE LOS PARÁMETROS
                    StringBuilder tiposEsperados = new StringBuilder();
                    int j = k + 1;
                    while (j < listaTokens.size() && !listaTokens.get(j).getLexicalComp().equals("PARENTESIS_CIERRE")) {
                        Token tokenActual = listaTokens.get(j);
                        if (tokenActual.getLexicalComp().equals("IDENTIFICADOR")) {
                            String lexemaParam = tokenActual.getLexeme();
                            String tipoParam = "indefinido";
                            
                            if (j > 0) {
                                String compAnterior = listaTokens.get(j - 1).getLexicalComp();
                                if (compAnterior.equals("NUM") || compAnterior.equals("FLOT") || compAnterior.equals("TEXTO") || compAnterior.equals("LOGICO")) {
                                    tipoParam = listaTokens.get(j - 1).getLexeme().toLowerCase();
                                }
                            }
                            tiposEsperados.append(tipoParam).append(","); // Construimos la firma: "num,texto,"
                            
                            if (!tablaSimbolos.containsKey(lexemaParam)) {
                                Simbolo paramSimb = new Simbolo(tipoParam, lexemaParam, "0", "param");
                                tablaSimbolos.put(lexemaParam, paramSimb);
                            }
                        }
                        j++;
                    }

                    // 2. REGISTRAR LA FUNCIÓN GUARDANDO SU FIRMA EN 'VALOR'
                    if (!tablaSimbolos.containsKey(nombreFuncion)) {
                        String firma = tiposEsperados.length() > 0 ? tiposEsperados.substring(0, tiposEsperados.length() - 1) : "vacio";
                        Simbolo funcionSimb = new Simbolo(tipoRetorno, nombreFuncion, firma, "funcion");
                        tablaSimbolos.put(nombreFuncion, funcionSimb);
                    }
                }
            }
        }
    
        for (Production id : idDeclaracion) {
            String tipoDeclaradoLexema = id.lexemeRank(0).toLowerCase();
            int i = 1;
            while (i < id.getSizeTokens()) {
                String nombreVariable = id.lexemeRank(i);

                if (i + 1 < id.getSizeTokens() && id.lexemeRank(i + 1).equals("=")) {
                    int inicioExpresion = i + 2;
                    int finExpresion = inicioExpresion;

                    // --- SOLUCIÓN BUG LÍNEA 57: Ignorar comas dentro de paréntesis ---
                    int parentesisAbiertos = 0;
                    for (int j = inicioExpresion; j < id.getSizeTokens(); j++) {
                        String lexemaActual = id.lexemeRank(j);

                        if (lexemaActual.equals("(")) {
                            parentesisAbiertos++;
                        } else if (lexemaActual.equals(")")) {
                            parentesisAbiertos--;
                        }

                        // Solo cortamos si encontramos una coma o punto y coma FUERA de paréntesis
                        if (parentesisAbiertos == 0 && (lexemaActual.equals(",") || lexemaActual.equals(";"))) {
                            finExpresion = j;
                            break;
                        }
                        if (j == id.getSizeTokens() - 1) {
                            finExpresion = id.getSizeTokens();
                        }
                    }
                    // ------------------------------------------------------------------

                    if (inicioExpresion >= finExpresion && !(finExpresion == id.getSizeTokens() && id.lexemeRank(id.getSizeTokens() - 1).equals(";"))) {
                        gestionErrores.add(new ErrorLSSL(800, "[Línea #] Error semántico {}: Declaración con asignación mal formada o vacía para " + nombreVariable, id, true));
                        if (finExpresion < id.getSizeTokens() && (id.lexemeRank(finExpresion).equals(",") || id.lexemeRank(finExpresion).equals(";"))) {
                            i = finExpresion + 1;
                        } else {
                            i = id.getSizeTokens();
                        }
                        continue;
                    }

                    if (tablaSimbolos.containsKey(nombreVariable)) {
                        gestionErrores.add(new ErrorLSSL(82, "[Línea #] Error semántico {}: La variable " + nombreVariable + " ya ha sido declarada previamente", id, true));
                        validarExpresion(id, tipoDeclaradoLexema, nombreVariable, inicioExpresion, finExpresion, true);
                    } else {

                        boolean asignacionValida = true;
                        int numTokensExpresion = finExpresion - inicioExpresion;

                        // --- NUEVO: VALIDACIONES DE TIPO AL DECLARAR (Para un solo token) ---
                        if (numTokensExpresion == 1) {
                            String tokenComponenteValor = id.lexicalCompRank(inicioExpresion);
                            String tokenLexemaValor = id.lexemeRank(inicioExpresion);

                            switch (tipoDeclaradoLexema) {
                                case "num":
                                    if (tokenComponenteValor.equals("CADENA") || tokenComponenteValor.equals("TEXTO")) {
                                        gestionErrores.add(new ErrorLSSL(100, "[Línea #] Error semántico {}: Incompatibilidad. No puedes inicializar la variable numérica '" + nombreVariable + "' con el texto " + tokenLexemaValor, id, true));
                                        asignacionValida = false;
                                    } else if (tokenComponenteValor.equals("NUMERODECIMAL")) {
                                        gestionErrores.add(new ErrorLSSL(101, "[Línea #] Error semántico {}: Pérdida de precisión. No puedes inicializar '" + nombreVariable + "' con el decimal " + tokenLexemaValor + ". Declárala como 'flot'.", id, true));
                                        asignacionValida = false;
                                    } else if (tokenComponenteValor.equals("VERDADERO") || tokenComponenteValor.equals("FALSO")) {
                                        gestionErrores.add(new ErrorLSSL(102, "[Línea #] Error semántico {}: Incompatibilidad. No puedes inicializar la variable numérica '" + nombreVariable + "' con un valor lógico (" + tokenLexemaValor + ")", id, true));
                                        asignacionValida = false;
                                    }
                                    break;

                                case "flot":
                                    if (tokenComponenteValor.equals("CADENA") || tokenComponenteValor.equals("TEXTO")) {
                                        gestionErrores.add(new ErrorLSSL(103, "[Línea #] Error semántico {}: Incompatibilidad. No puedes inicializar la variable flotante '" + nombreVariable + "' con el texto " + tokenLexemaValor, id, true));
                                        asignacionValida = false;
                                    } else if (tokenComponenteValor.equals("VERDADERO") || tokenComponenteValor.equals("FALSO")) {
                                        gestionErrores.add(new ErrorLSSL(104, "[Línea #] Error semántico {}: Incompatibilidad. No puedes inicializar la variable flotante '" + nombreVariable + "' con un valor lógico", id, true));
                                        asignacionValida = false;
                                    }
                                    break;

                                case "texto":
                                    if (tokenComponenteValor.equals("NUMEROENTERO") || tokenComponenteValor.equals("NUMERODECIMAL")) {
                                        gestionErrores.add(new ErrorLSSL(105, "[Línea #] Error semántico {}: Incompatibilidad. No puedes inicializar la variable de texto '" + nombreVariable + "' directamente con el número " + tokenLexemaValor + ". Escríbelo entre comillas.", id, true));
                                        asignacionValida = false;
                                    } else if (tokenComponenteValor.equals("VERDADERO") || tokenComponenteValor.equals("FALSO")) {
                                        gestionErrores.add(new ErrorLSSL(106, "[Línea #] Error semántico {}: Incompatibilidad. No puedes inicializar la variable de texto '" + nombreVariable + "' con el valor lógico " + tokenLexemaValor, id, true));
                                        asignacionValida = false;
                                    }
                                    break;

                                case "logico":
                                    if (tokenComponenteValor.equals("CADENA") || tokenComponenteValor.equals("TEXTO")) {
                                        gestionErrores.add(new ErrorLSSL(107, "[Línea #] Error semántico {}: Incompatibilidad. No puedes inicializar la variable lógica '" + nombreVariable + "' con un texto.", id, true));
                                        asignacionValida = false;
                                    } else if (tokenComponenteValor.equals("NUMEROENTERO") || tokenComponenteValor.equals("NUMERODECIMAL")) {
                                        gestionErrores.add(new ErrorLSSL(108, "[Línea #] Error semántico {}: Incompatibilidad. No puedes inicializar la variable lógica '" + nombreVariable + "' con un número. Usa 'verdadero' o 'falso'.", id, true));
                                        asignacionValida = false;
                                    }
                                    break;
                            }

                            // Si pasó la prueba de tipos directos y es un IDENTIFICADOR, dejamos que validarExpresion se encargue de revisar si existe en la tabla de símbolos
                            if (asignacionValida && tokenComponenteValor.equals("IDENTIFICADOR")) {
                                asignacionValida = validarExpresion(id, tipoDeclaradoLexema, nombreVariable, inicioExpresion, finExpresion, true);
                            }
                        } else {
                            // Si es una expresión matemática o función, validarExpresion hace el trabajo
                            asignacionValida = validarExpresion(id, tipoDeclaradoLexema, nombreVariable, inicioExpresion, finExpresion, true);
                        }
                        // ----------------------------------------------------------------------------

                        if (asignacionValida) {
                            String valorAsignadoStr = id.lexemeRank(inicioExpresion, finExpresion - 1);
                            Simbolo simb = new Simbolo(tipoDeclaradoLexema, nombreVariable, valorAsignadoStr, "var");
                            tablaSimbolos.put(nombreVariable, simb);
                        } else {
                            Simbolo simb = new Simbolo(tipoDeclaradoLexema, nombreVariable, null, "var");
                            tablaSimbolos.put(nombreVariable, simb);
                        }
                    }
                    i = finExpresion;
                    if (i < id.getSizeTokens() && (id.lexemeRank(i).equals(",") || id.lexemeRank(i).equals(";"))) {
                        i++;
                    } else if (i == id.getSizeTokens()) {
                        // OK
                    } else {
                        i = id.getSizeTokens();
                    }
                } else {
                    if (tablaSimbolos.containsKey(nombreVariable)) {
                        gestionErrores.add(new ErrorLSSL(82, "[Línea #] Error semántico {}: La variable " + nombreVariable + " ya ha sido declarada previamente", id, true));
                    } else {
                        Simbolo simb = new Simbolo(tipoDeclaradoLexema, nombreVariable, null, "var");
                        tablaSimbolos.put(nombreVariable, simb);
                    }
                    if (i + 1 < id.getSizeTokens()) {
                        if (id.lexemeRank(i + 1).equals(",")) {
                            i += 2;
                        } else if (id.lexemeRank(i + 1).equals(";")) {
                            i += 2;
                        } else {
                            i = id.getSizeTokens();
                        }
                    } else {
                        i = id.getSizeTokens();
                    }
                }
            }
        }        

        for (Production id : idAsignacion) {
            String nombreVariable = id.lexemeRank(0);
            String operadorUsado = id.lexicalCompRank(1); // OPERADORASIGNACION, ASIGNACIONSUMA, etc.

            if (tablaSimbolos.containsKey(nombreVariable)) {
                Simbolo variable = tablaSimbolos.get(nombreVariable);
                String tipoVariable = variable.getTipo().toLowerCase();

                // Si NO es un "=" normal, significa que es un +=, -=, *= o /=
                if (!operadorUsado.equals("OPERADORASIGNACION")) {
                    if (!tipoVariable.equals("num") && !tipoVariable.equals("flot")) {
                        gestionErrores.add(new ErrorLSSL(112, "[Línea #] Error semántico {}: No puedes usar operadores matemáticos (" + operadorUsado + ") en la variable '" + nombreVariable + "' porque es de tipo '" + tipoVariable + "'", id, true));
                        continue;
                    }
                }

                int inicioExpresion = 2; // Inicia justo después del operador
                int finExpresion = id.getSizeTokens() - 1; // Termina antes del FIN_LINEA (;)

                if (inicioExpresion >= finExpresion) {
                    gestionErrores.add(new ErrorLSSL(801, "[Línea #] Error semántico {}: Expresión de asignación vacía para " + nombreVariable, id, true));
                    continue;
                }

                // Validamos lo que está a la derecha del operador
                boolean asignacionValida = validarExpresion(id, tipoVariable, nombreVariable, inicioExpresion, finExpresion, false);

                if (asignacionValida) {
                    String valorAsignadoStr = id.lexemeRank(inicioExpresion, finExpresion - 1);
                    variable.setValor(valorAsignadoStr);
                }
            } else {
                gestionErrores.add(new ErrorLSSL(84, "[Línea #] Error semántico {}: La variable " + nombreVariable + " NO ha sido declarada previamente", id, true));
            }
        }
        
   
        for (Production id : idSentencia) {
            switch (id.getName()) {
                case "DeclaracionFuncion":
                case "PPara":
                    // 1. Verificamos que TODOS los identificadores usados en el bucle existan
                    for (int k = 0; k < id.getSizeTokens(); k++) {
                        if (id.lexicalCompRank(k).equals("IDENTIFICADOR")) {
                            String lexema = id.lexemeRank(k);
                            if (!tablaSimbolos.containsKey(lexema)) {
                                gestionErrores.add(new ErrorLSSL(85, "[Línea #] Error semántico {}: La variable '" + lexema + "' usada en el bucle PARA NO ha sido declarada", id, true));
                            }
                        }

                        // 2. Validamos que la variable que se incrementa/decrementa (ej. i++) sea un número
                        if (id.lexicalCompRank(k).equals("INCREMENTO") || id.lexicalCompRank(k).equals("DECREMENTO")) {
                            // El identificador siempre está justo una posición antes del ++ o -- (k - 1)
                            if (k > 0 && id.lexicalCompRank(k - 1).equals("IDENTIFICADOR")) {
                                String nombreVarBucle = id.lexemeRank(k - 1);

                                if (tablaSimbolos.containsKey(nombreVarBucle)) {
                                    String tipoVarBucle = tablaSimbolos.get(nombreVarBucle).getTipo().toLowerCase();
                                    // Si intenta incrementar un texto o un lógico, ¡Error!
                                    if (!tipoVarBucle.equals("num") && !tipoVarBucle.equals("flot")) {
                                        gestionErrores.add(new ErrorLSSL(113, "[Línea #] Error semántico {}: El iterador del bucle PARA ('" + nombreVarBucle + "') debe ser numérico, no de tipo '" + tipoVarBucle + "'", id, true));
                                    }
                                }
                            }
                        }
                    }
                    break;
                case "PEscribir":
                case "PEscribirLinea":
                    for (int k = 2; k < id.getSizeTokens() - 2; k++) {
                        if (id.lexicalCompRank(k).equals("IDENTIFICADOR")) {
                            if (!tablaSimbolos.containsKey(id.lexemeRank(k))) {
                                gestionErrores.add(new ErrorLSSL(85, "[Línea #] Error semántico {}: La variable " + id.lexemeRank(k) + " a imprimir NO ha sido declarada", id, true));
                            }
                        }
                    }
                    break;
                case "PLeer":
                default:
                    break;
            }
        }
        
        // Evaluador universal de condiciones
        if (idCondiciones != null) {
            for (Production id : idCondiciones) {
                for (int k = 0; k < id.getSizeTokens(); k++) {
                    if (id.lexicalCompRank(k).equals("IDENTIFICADOR")) {
                        String lexema = id.lexemeRank(k);
                        if (!lexema.equals("sino") && !tablaSimbolos.containsKey(lexema)) {
                            gestionErrores.add(new ErrorLSSL(85, "[Línea #] Error semántico {}: La variable '" + lexema + "' usada en la condición NO ha sido declarada", id, true));
                        }
                    }
                }
            }
        }
        
        // Evaluador universal de ELEGIR (Switch-Case)
        if (idElegir != null) {
            for (Production id : idElegir) {
                // En la regla: ELEGIR ( IDENTIFICADOR ) ... el identificador siempre está en la posición 2
                if (id.getSizeTokens() > 2 && id.lexicalCompRank(2).equals("IDENTIFICADOR")) {
                    String nombreVar = id.lexemeRank(2);

                    if (!tablaSimbolos.containsKey(nombreVar)) {
                        gestionErrores.add(new ErrorLSSL(85, "[Línea #] Error semántico {}: La variable '" + nombreVar + "' evaluada en el ELEGIR NO ha sido declarada", id, true));
                    }
                }
            }
        }
        
        // ========================================================================
        // --- VALIDACIONES AVANZADAS (TIPOS DE ARGUMENTOS, IDENTIDAD Y RETORNOS) ---
        // ========================================================================
        if (idProgramaCompleto != null) {
            for (Production func : idProgramaCompleto) {
                String tipoFuncion = func.lexemeRank(0).toLowerCase();
                String nombreFuncion = func.lexemeRank(1);
                boolean tieneRetorno = false;

                // --- HERRAMIENTAS PARA RASTREAR EL CONTEXTO (PARA EL 'SALIR') ---
                java.util.Stack<String> pilaContextos = new java.util.Stack<>();
                String ultimoBloqueDetectado = "funcion";

                for (int i = 0; i < func.getSizeTokens(); i++) {
                    String comp = func.lexicalCompRank(i);
                    // AQUÍ EXTRAEMOS LA LÍNEA EXACTA DEL TOKEN
                    int lineaActual = func.getTokens().get(i).getLine(); 
                    
                    // RASTREADOR DE CONTEXTO
                    if (comp.equals("MIENTRAS") || comp.equals("PARA")) ultimoBloqueDetectado = "bucle";
                    if (comp.equals("ELEGIR")) ultimoBloqueDetectado = "switch";
                    if (comp.equals("SI") || comp.equals("SINO") || comp.equals("FINAL")) ultimoBloqueDetectado = "condicional";

                    if (comp.equals("LLAVE_APERTURA")) {
                        pilaContextos.push(ultimoBloqueDetectado);
                        ultimoBloqueDetectado = "bloque"; 
                    } else if (comp.equals("LLAVE_CIERRE")) {
                        if (!pilaContextos.isEmpty()) pilaContextos.pop();
                    }

                    // TRAMPA DE 'SALIR'
                    if (comp.equals("SALIR")) {
                        if (!pilaContextos.contains("bucle") && !pilaContextos.contains("switch")) {
                            // Quemamos la lineaActual en el String y mandamos 'func' para que Java sea feliz
                            gestionErrores.add(new ErrorLSSL(130, "[Línea " + lineaActual + "] Error semántico: 'salir' huérfano. La instrucción 'salir' (break) solo es válida dentro de un ciclo ('mientras', 'para') o un bloque 'elegir'.", func, true));
                        }
                    }

                    if (comp.equals("RETORNAR") || comp.equals("SALIR")) {
                        if (comp.equals("RETORNAR")) tieneRetorno = true;
                        
                        boolean devuelveValor = (i + 1 < func.getSizeTokens() && !func.lexicalCompRank(i + 1).equals("FIN_LINEA"));
                        
                        if (comp.equals("RETORNAR")) {
                            if (tipoFuncion.equals("inicio") && devuelveValor) {
                                gestionErrores.add(new ErrorLSSL(120, "[Línea " + lineaActual + "] Error semántico: Incompatibilidad. La función '" + nombreFuncion + "' es 'inicio' (void) y no debe retornar valores.", func, true));
                            } else if (!tipoFuncion.equals("inicio") && !devuelveValor) {
                                gestionErrores.add(new ErrorLSSL(121, "[Línea " + lineaActual + "] Error semántico: Incompatibilidad. La función '" + nombreFuncion + "' es '" + tipoFuncion + "' y requiere retornar un valor.", func, true));
                            }
                        }

                        // DETECCIÓN DE CÓDIGO MUERTO
                        int posSiguiente = i + 1;
                        while(posSiguiente < func.getSizeTokens() && !func.lexicalCompRank(posSiguiente).equals("FIN_LINEA")) posSiguiente++;
                        
                        if (posSiguiente < func.getSizeTokens() && func.lexicalCompRank(posSiguiente).equals("FIN_LINEA")) {
                            posSiguiente++; 
                            if (posSiguiente < func.getSizeTokens() && !func.lexicalCompRank(posSiguiente).equals("LLAVE_CIERRE") && !func.lexicalCompRank(posSiguiente).equals("CASO") && !func.lexicalCompRank(posSiguiente).equals("DEFECTO")) {
                                int lineaMuerta = func.getTokens().get(posSiguiente).getLine(); // Línea del token basura
                                gestionErrores.add(new ErrorLSSL(129, "[Línea " + lineaMuerta + "] Error semántico: Código inalcanzable. Has escrito instrucciones después de un '" + comp.toLowerCase() + "' que nunca se ejecutarán.", func, true));
                            }
                        }
                    }

                    // VALIDAR LLAMADAS A FUNCIONES
                    if (comp.equals("IDENTIFICADOR") && i + 1 < func.getSizeTokens() && func.lexicalCompRank(i + 1).equals("PARENTESIS_APERTURA")) {
                        String nombreLlamada = func.lexemeRank(i);
                        if (i > 0 && func.lexicalCompRank(i - 1).matches("INICIO|NUM|FLOT|TEXTO|LOGICO")) continue;

                        if (tablaSimbolos.containsKey(nombreLlamada)) {
                            if (!tablaSimbolos.get(nombreLlamada).getVarconst().equals("funcion")) {
                                gestionErrores.add(new ErrorLSSL(125, "[Línea " + lineaActual + "] Error semántico: Crisis de Identidad. '" + nombreLlamada + "' es una VARIABLE, no puedes llamarla como una función.", func, true));
                            } else {
                                String firma = tablaSimbolos.get(nombreLlamada).getValor();
                                String[] tiposRequeridos = firma.equals("vacio") ? new String[0] : firma.split(",");
                                java.util.List<String> tiposEnviados = new java.util.ArrayList<>();
                                
                                int j = i + 2;
                                while (j < func.getSizeTokens() && !func.lexicalCompRank(j).equals("PARENTESIS_CIERRE")) {
                                    String compArg = func.lexicalCompRank(j);
                                    String lexArg = func.lexemeRank(j);
                                    
                                    if (compArg.equals("NUMEROENTERO")) tiposEnviados.add("num");
                                    else if (compArg.equals("NUMERODECIMAL")) tiposEnviados.add("flot");
                                    else if (compArg.equals("CADENA") || compArg.equals("TEXTO")) tiposEnviados.add("texto");
                                    else if (compArg.equals("VERDADERO") || compArg.equals("FALSO")) tiposEnviados.add("logico");
                                    else if (compArg.equals("IDENTIFICADOR")) {
                                        tiposEnviados.add(tablaSimbolos.containsKey(lexArg) ? tablaSimbolos.get(lexArg).getTipo() : "desconocido");
                                    }
                                    
                                    while (j < func.getSizeTokens() && !func.lexicalCompRank(j).equals("COMA") && !func.lexicalCompRank(j).equals("PARENTESIS_CIERRE")) j++;
                                    if (func.lexicalCompRank(j).equals("COMA")) j++;
                                }
                                
                                if (tiposRequeridos.length != tiposEnviados.size()) {
                                    gestionErrores.add(new ErrorLSSL(122, "[Línea " + lineaActual + "] Error semántico: La función '" + nombreLlamada + "' exige " + tiposRequeridos.length + " parámetros, pero recibió " + tiposEnviados.size() + ".", func, true));
                                } else {
                                    for (int k = 0; k < tiposRequeridos.length; k++) {
                                        if (!tiposEnviados.get(k).equals("desconocido") && !tiposRequeridos[k].equals(tiposEnviados.get(k))) {
                                            if (tiposRequeridos[k].equals("texto") || tiposRequeridos[k].equals("logico") || 
                                               ((tiposRequeridos[k].equals("num") || tiposRequeridos[k].equals("flot")) && (tiposEnviados.get(k).equals("texto") || tiposEnviados.get(k).equals("logico")))) {
                                                gestionErrores.add(new ErrorLSSL(126, "[Línea " + lineaActual + "] Error semántico: Tipo de argumento incorrecto. El parámetro número " + (k + 1) + " de '" + nombreLlamada + "' espera un valor '" + tiposRequeridos[k] + "', pero se envió un '" + tiposEnviados.get(k) + "'.", func, true));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (!tipoFuncion.equals("inicio") && !tieneRetorno) {
                    // Este sí se queda con '#', porque el error es de toda la función, así que apuntamos a la cabecera
                    gestionErrores.add(new ErrorLSSL(123, "[Línea #] Error semántico {}: Falta instrucción retornar. '" + nombreFuncion + "' debe devolver un valor.", func, true));
                }
            }
        }
        
        // 4. VERIFICAR QUE EXISTA LA FUNCIÓN PRINCIPAL
        if (!tablaSimbolos.containsKey("principal") || !tablaSimbolos.get("principal").getVarconst().equals("funcion")) {
            if (idProgramaCompleto != null && !idProgramaCompleto.isEmpty()) {
                gestionErrores.add(new ErrorLSSL(124, "[Línea #] Error semántico {}: Punto de entrada faltante. Todo programa debe declarar la función 'inicio principal()'.", idProgramaCompleto.get(0), true));
            }
        }
    }

// Pequeño helper para que el semántico no se confunda con palabras clave
private boolean esPalabraReservada(String lexema) {
    return lexema.matches("si|sino|mientras|repetir|hasta|para|caso|defecto|mostrar|leer|retornar|clase|metodo|entero|decimal|logico|string|true|false|null|verdadero|falso");
}
    
private boolean validarExpresion(Production id, String tipoVarIzquierda, String nombreVarIzquierda, int inicioExpresion, int finExpresion, boolean esDeclaracion) {
        tipoVarIzquierda = tipoVarIzquierda.toLowerCase();
        int numTokensExpresion = finExpresion - inicioExpresion;

        if (numTokensExpresion <= 0) {
            gestionErrores.add(new ErrorLSSL(801, "[Línea #] Error semántico {}: Expresión vacía para " + nombreVarIzquierda, id, true));
            return false;
        }

        // 1. CASO DE UN SOLO TOKEN (Valor simple o asignación directa)
        if (numTokensExpresion == 1) {
        String tokenComponente = id.lexicalCompRank(inicioExpresion);
        String tokenLexema = id.lexemeRank(inicioExpresion);

        // Evaluamos según el tipo con el que se declaró la variable (num, flot, texto, logico)
        switch (tipoVarIzquierda) {

            // REGLAS PARA VARIABLES 'num' (Enteros)
            case "num":
                if (tokenComponente.equals("NUMEROENTERO")) {
                    return true;
                } else if (tokenComponente.equals("CADENA") || tokenComponente.equals("TEXTO")) {
                    gestionErrores.add(new ErrorLSSL(90, "[Línea #] Error semántico {}: Incompatibilidad de tipos. No se puede asignar el texto " + tokenLexema + " a la variable numérica '" + nombreVarIzquierda + "'", id, true));
                    return false;
                } else if (tokenComponente.equals("NUMERODECIMAL")) {
                    gestionErrores.add(new ErrorLSSL(91, "[Línea #] Error semántico {}: Pérdida de precisión. No se puede asignar el decimal " + tokenLexema + " a la variable entera '" + nombreVarIzquierda + "'. Usa 'flot'.", id, true));
                    return false;
                } else if (tokenComponente.equals("VERDADERO") || tokenComponente.equals("FALSO")) {
                    gestionErrores.add(new ErrorLSSL(92, "[Línea #] Error semántico {}: Incompatibilidad de tipos. No se puede asignar un valor lógico (" + tokenLexema + ") a la variable numérica '" + nombreVarIzquierda + "'", id, true));
                    return false;
                }
                if (tokenComponente.equals("IDENTIFICADOR")) {
                    return validarIdentificadorEnExpresion(id, tokenLexema, nombreVarIzquierda, "num");
                }
                break;

            // REGLAS PARA VARIABLES 'flot' (Decimales)
            case "flot":
                if (tokenComponente.equals("NUMERODECIMAL") || tokenComponente.equals("NUMEROENTERO")) {
                    return true; // Permitimos enteros en flotantes porque 5 cabe en 5.0
                } else if (tokenComponente.equals("CADENA") || tokenComponente.equals("TEXTO")) {
                    gestionErrores.add(new ErrorLSSL(90, "[Línea #] Error semántico {}: Incompatibilidad de tipos. No se puede asignar el texto " + tokenLexema + " a la variable flotante '" + nombreVarIzquierda + "'", id, true));
                    return false;
                } else if (tokenComponente.equals("VERDADERO") || tokenComponente.equals("FALSO")) {
                    gestionErrores.add(new ErrorLSSL(92, "[Línea #] Error semántico {}: Incompatibilidad de tipos. No se puede asignar un valor lógico a la variable flotante '" + nombreVarIzquierda + "'", id, true));
                    return false;
                }
                if (tokenComponente.equals("IDENTIFICADOR")) {
                    return validarIdentificadorEnExpresion(id, tokenLexema, nombreVarIzquierda, "flot", "num");
                }
                break;

            // REGLAS PARA VARIABLES 'texto' (Strings)
            case "texto":
                if (tokenComponente.equals("CADENA") || tokenComponente.equals("TEXTO")) {
                    return true;
                } else if (tokenComponente.equals("NUMEROENTERO") || tokenComponente.equals("NUMERODECIMAL")) {
                    gestionErrores.add(new ErrorLSSL(93, "[Línea #] Error semántico {}: Incompatibilidad de tipos. No se puede asignar el número " + tokenLexema + " directamente a la variable de texto '" + nombreVarIzquierda + "'. Escríbelo entre comillas.", id, true));
                    return false;
                } else if (tokenComponente.equals("VERDADERO") || tokenComponente.equals("FALSO")) {
                    gestionErrores.add(new ErrorLSSL(94, "[Línea #] Error semántico {}: Incompatibilidad de tipos. No se puede asignar el valor lógico " + tokenLexema + " a la variable de texto '" + nombreVarIzquierda + "'", id, true));
                    return false;
                }
                if (tokenComponente.equals("IDENTIFICADOR")) {
                    return validarIdentificadorEnExpresion(id, tokenLexema, nombreVarIzquierda, "texto");
                }
                break;

            // REGLAS PARA VARIABLES 'logico' (Booleanos)
            case "logico":
                if (tokenComponente.equals("VERDADERO") || tokenComponente.equals("FALSO")) {
                    return true;
                } else if (tokenComponente.equals("CADENA") || tokenComponente.equals("TEXTO")) {
                    gestionErrores.add(new ErrorLSSL(95, "[Línea #] Error semántico {}: Incompatibilidad de tipos. No se puede asignar un texto a la variable lógica '" + nombreVarIzquierda + "'", id, true));
                    return false;
                } else if (tokenComponente.equals("NUMEROENTERO") || tokenComponente.equals("NUMERODECIMAL")) {
                    gestionErrores.add(new ErrorLSSL(96, "[Línea #] Error semántico {}: Incompatibilidad de tipos. No se puede asignar un número a la variable lógica '" + nombreVarIzquierda + "'. Usa 'verdadero' o 'falso'.", id, true));
                    return false;
                }
                if (tokenComponente.equals("IDENTIFICADOR")) {
                    return validarIdentificadorEnExpresion(id, tokenLexema, nombreVarIzquierda, "logico");
                }
                break;
        }

        // Fallback genérico por si entra un token extraño
        gestionErrores.add(new ErrorLSSL(81, "[Línea #] Error semántico {}: Valor '" + tokenLexema + "' no compatible con el tipo " + tipoVarIzquierda + " de la variable " + nombreVarIzquierda, id, true));
        return false;
    }

        // 2. CASO DE EXPRESIONES COMPLEJAS (Más de 1 token)
        if (tipoVarIzquierda.equals("num") || tipoVarIzquierda.equals("flot")) {
            for (int k = inicioExpresion; k < finExpresion; k++) {
                String comp = id.lexicalCompRank(k);
                String lex = id.lexemeRank(k);

                if (comp.equals("IDENTIFICADOR")) {
                    
                    if (k + 1 < finExpresion && id.lexicalCompRank(k + 1).equals("PARENTESIS_APERTURA")) {
                        // ===============================================================
                        //                 VALIDAR ASIGNACIÓN DE FUNCIONES VACÍAS (VOID)
                        // ===============================================================
                        if (tablaSimbolos.containsKey(lex) && tablaSimbolos.get(lex).getTipo().equals("inicio")) {
                            gestionErrores.add(new ErrorLSSL(127, "[Línea #] Error semántico {}: Asignación inválida. La función '" + lex + "' es de tipo 'inicio' (void) y no devuelve ningún valor para asignar.", id, true));
                            return false;
                        }
                        // ===============================================================

                        // Es una función válida, nos saltamos los tokens de los parámetros
                        while (k < finExpresion && !id.lexicalCompRank(k).equals("PARENTESIS_CIERRE")) {
                            k++;
                        }
                        continue; 
                    }
                    if (tipoVarIzquierda.equals("flot")) {
                        if (!validarIdentificadorEnExpresion(id, lex, nombreVarIzquierda, "flot", "num")) return false;
                    } else {
                        if (!validarIdentificadorEnExpresion(id, lex, nombreVarIzquierda, "num")) return false;
                    }
                } else if (comp.equals("NUMEROENTERO")) {
                } else if (comp.equals("NUMERODECIMAL")) {
                    if (tipoVarIzquierda.equals("num")) {
                        gestionErrores.add(new ErrorLSSL(81, "[Línea #] Error semántico {}: No se puede usar NUMERODECIMAL en expresión para variable num " + nombreVarIzquierda, id, true));
                        return false;
                    }
                } else if (comp.equals("SUMA") || comp.equals("RESTA") || comp.equals("MULTIPLICACION") || comp.equals("DIVISION") || comp.equals("MODULO") || comp.equals("PARENTESIS_APERTURA") || comp.equals("PARENTESIS_CIERRE")) {
                    
                    // ===============================================================
                    // NUEVO DLC: DETECCIÓN DE DIVISIÓN POR CERO EN COMPILACIÓN
                    // ===============================================================
                    if (comp.equals("DIVISION") || comp.equals("MODULO")) {
                        int lookAhead = k + 1;
                        // Saltamos paréntesis de apertura si los hay, ej: 10 / (0)
                        while(lookAhead < finExpresion && id.lexicalCompRank(lookAhead).equals("PARENTESIS_APERTURA")) lookAhead++;
                        if (lookAhead < finExpresion && id.lexicalCompRank(lookAhead).equals("NUMEROENTERO") && id.lexemeRank(lookAhead).equals("0")) {
                            gestionErrores.add(new ErrorLSSL(128, "[Línea #] Error semántico {}: División por cero detectada. La operación matemática colapsará el programa en tiempo de ejecución.", id, true));
                            return false;
                        }
                    }
                    // ===============================================================

                    if (comp.equals("MODULO") && tipoVarIzquierda.equals("flot")) {
                        gestionErrores.add(new ErrorLSSL(81, "[Línea #] Error semántico {}: Operador MODULO no permitido para tipo flot en variable " + nombreVarIzquierda, id, true));
                        return false;
                    }
                } else {
                    gestionErrores.add(new ErrorLSSL(81, "[Línea #] Error semántico {}: Token inesperado '" + lex + "' en expresión numérica para " + nombreVarIzquierda, id, true));
                    return false;
                }
            }
            return true;
        } else if (tipoVarIzquierda.equals("texto")) {
            boolean esperandoOperando = true;
            for (int k = inicioExpresion; k < finExpresion; k++) {
                String comp = id.lexicalCompRank(k);
                String lex = id.lexemeRank(k);
                if (esperandoOperando) {
                    if (comp.equals("CADENA")) {
                        // OK
                    } else if (comp.equals("NUMEROENTERO") || comp.equals("NUMERODECIMAL")) {
                        // OK
                    } else if (comp.equals("IDENTIFICADOR")) {
                        if (!validarIdentificadorEnExpresion(id, lex, nombreVarIzquierda, "texto")) {
                            return false;
                        }
                    } else {
                        gestionErrores.add(new ErrorLSSL(81, "[Línea #] Error semántico {}: Se esperaba CADENA o IDENTIFICADOR de tipo texto, se encontró '" + lex + "' para " + nombreVarIzquierda, id, true));
                        return false;
                    }
                    esperandoOperando = false;
                } else {
                    if (comp.equals("SUMA")) {
                        // OK
                    } else {
                        gestionErrores.add(new ErrorLSSL(81, "[Línea #] Error semántico {}: Se esperaba operador '+' para concatenación, se encontró '" + lex + "' para " + nombreVarIzquierda, id, true));
                        return false;
                    }
                    esperandoOperando = true;
                }
            }
            if (esperandoOperando && numTokensExpresion > 0) {
                gestionErrores.add(new ErrorLSSL(81, "[Línea #] Error semántico {}: Expresión de concatenación incompleta para " + nombreVarIzquierda, id, true));
                return false;
            }
            return true;
        } else if (tipoVarIzquierda.equals("logico")) {
            for (int k = inicioExpresion; k < finExpresion; k++) {
                String comp = id.lexicalCompRank(k);
                String lex = id.lexemeRank(k);
                if (comp.equals("IDENTIFICADOR")) {
                    if (!validarIdentificadorEnExpresion(id, lex, nombreVarIzquierda, "logico", "num", "flot")) {
                        return false;
                    }
                } else if (comp.equals("VERDADERO") || comp.equals("FALSO") || comp.equals("NUMEROENTERO") || comp.equals("NUMERODECIMAL")) {
                    /*OK*/ 
                } else if (comp.equals("PARENTESIS_APERTURA") || comp.equals("PARENTESIS_CIERRE")
                        || comp.equals("MENOR_QUE") || comp.equals("MAYOR_QUE") || comp.equals("IGUAL_A")
                        || comp.equals("DIFERENTE_DE") || comp.equals("MENOR_IGUAL") || comp.equals("MAYOR_IGUAL")
                        || comp.equals("O") || comp.equals("Y") || comp.equals("NO")) { // Corregido OR a O
                    /*OK*/ 
                } else {
                    gestionErrores.add(new ErrorLSSL(81, "[Línea #] Error semántico {}: Token inesperado '" + lex + "' en expresión lógica para " + nombreVarIzquierda, id, true));
                    return false;
                }
            }
            return true;
        }

        gestionErrores.add(new ErrorLSSL(80, "[Línea #] Error semántico {}: Tipo de variable " + tipoVarIzquierda + " no manejado para asignación de expresiones complejas a " + nombreVarIzquierda, id, true));
        return false;
    }

    private boolean validarIdentificadorEnExpresion(Production id, String nombreIdAEvaluar, String nombreVarIzquierda, String... tiposEsperados) {
        if (!tablaSimbolos.containsKey(nombreIdAEvaluar)) {
            gestionErrores.add(new ErrorLSSL(84, "[Línea #] Error semántico {}: La variable " + nombreIdAEvaluar + " usada en expresión para " + nombreVarIzquierda + " NO ha sido declarada", id, true));
            return false;
        }
        Simbolo simboloId = tablaSimbolos.get(nombreIdAEvaluar);
        String tipoId = simboloId.getTipo().toLowerCase();
        
        if (simboloId.getValor() == null || simboloId.getValor().isEmpty()) {
            gestionErrores.add(new ErrorLSSL(115, "[Línea #] Error semántico {}: La variable '" + nombreIdAEvaluar + "' se está usando en la expresión, pero no ha sido inicializada con ningún valor.", id, true));
            return false;
        }

        for (String tipoEsperado : tiposEsperados) {
            if (tipoId.equals(tipoEsperado.toLowerCase())) {
                return true;
            }
        }

        String errorMsgBase = "[Línea #] Error semántico {}: La variable " + nombreIdAEvaluar + " (tipo " + tipoId + ")";
        int errorCode = 83;

        if (java.util.Arrays.asList(tiposEsperados).size() == 1 && tiposEsperados[0].equals("string") && !tipoId.equals("string")) {
            errorMsgBase += " no es de tipo string y no puede ser concatenada directamente a " + nombreVarIzquierda;
            errorCode = 87;
        } else if (java.util.Arrays.asList(tiposEsperados).size() == 1 && tiposEsperados[0].equals("decimal") && !tipoId.equals("decimal")) {
            errorMsgBase += " no es de tipo decimal y no puede usarse directamente en una expresión para la variable decimal " + nombreVarIzquierda;
            errorCode = 89;
        } else if (java.util.Arrays.asList(tiposEsperados).size() == 1 && tiposEsperados[0].equals("entero") && !tipoId.equals("entero")) {
            errorMsgBase += " no es de tipo entero y no puede usarse directamente en una expresión para la variable entera " + nombreVarIzquierda;
            errorCode = 90;
        } else {
            errorMsgBase += " no es compatible con la operación/asignación para " + nombreVarIzquierda + ". Se esperaban tipos: " + java.util.Arrays.toString(tiposEsperados);
        }
        gestionErrores.add(new ErrorLSSL(errorCode, errorMsgBase, id, true));
        return false;
    }


    private void ConversionCodigoIntermedio() {
        try {
            // En lugar de tomar solo la primera función (.get(0)),
            // recorremos TODAS las funciones que se guardaron en el programa.
            for (int i = 0; i < idProgramaCompleto.size(); i++) {
                tokens = idProgramaCompleto.get(i);
                posicionTokenActual = 0;

                // Procesar la función actual
                GeneracionCodigoIntermedio("FIN");
            }

            // Confirmación
            resultados.append("CONVERSION A CODIGO INTERMEDIO COMPLETADA\n");

        } catch (Exception e) {
            resultados.append("Error durante el recorrido de tokens en 3D: " + e.getMessage() + "\n");
        }
    }

    // (1) Empareja “{” con su “}” correspondiente
    private int findMatchingBrace(int idx) {
        String comp = tokens.lexicalCompRank(idx);
        if (!"LLAVE_APERTURA".equals(comp)) {
            throw new RuntimeException(
                    "findMatchingBrace esperaba 'LLAVE_APERTURA' en idx=" + idx
                    + " pero encontró " + comp + " (lexeme=" + tokens.lexemeRank(idx) + ")");
        }
        int depth = 1;
        for (int k = idx + 1; k < tokens.getSizeTokens(); k++) {
            String t = tokens.lexicalCompRank(k);
            if ("LLAVE_APERTURA".equals(t)) {
                depth++;
            } else if ("LLAVE_CIERRE".equals(t)) {
                depth--;
                if (depth == 0) {
                    return k;
                }
            }
        }
        throw new RuntimeException("No matching '}' para la '{' en token index " + idx);
    }

// (2) Procesa un bloque [start..end) de tokens con tu lógica normal
    private void procesaBloque(int start, int end) {
        int saved = posicionTokenActual;
        posicionTokenActual = start;
        while (posicionTokenActual < end) {
            ConversionCodigoIntermedio();  // O GeneracionCodigoIntermedio según tu nomenclatura
        }
        posicionTokenActual = saved;
    }

// (3) Posición del siguiente token de referencia (devuelve el índice donde lo encontró)
    public int posSigToken(Production tokens, int posicionActual, String tokenReferencia) {
        int max = tokens.getSizeTokens();
        int y = posicionActual;
        String ref = tokenReferencia.toUpperCase();
        while (y < max && !ref.equals(tokens.lexicalCompRank(y))) {
            y++;
        }
        if (y == max) {
            throw new RuntimeException("No encontré el token '" + tokenReferencia
                    + "' después de la posición " + posicionActual);
        }
        return y;
    }

    /**
     * Busca el índice de la primera aparición de ese componente léxico,
     * empezando en from
     */
    private int findTokenIndex(String compLex, int from) {
        for (int i = from; i < tokens.getSizeTokens(); i++) {
            if (tokens.lexicalCompRank(i).equals(compLex)) {
                return i;
            }
        }
        throw new RuntimeException("No encontré el token '" + compLex + "' después de la posición " + from);
    }

    private String parseExpresion() {
        String operandoIzquierdo = parseTermino();

        while (posicionTokenActual < tokens.getSizeTokens()
                && (tokens.lexicalCompRank(posicionTokenActual).equals("SUMA")
                || tokens.lexicalCompRank(posicionTokenActual).equals("RESTA"))) {

            String operador = tokens.lexemeRank(posicionTokenActual); // Guardará "+" o "-"
            posicionTokenActual++;
            String operandoDerecho = parseTermino();

            if (operandoIzquierdo == null || operandoDerecho == null) {
                return null;
            }

            String tempDestino = generador3D.nuevaTemp();
            generador3D.gc(operador, operandoIzquierdo, operandoDerecho, tempDestino);
            operandoIzquierdo = tempDestino;
        }
        return operandoIzquierdo;
    }

    private String parseTermino() {
        String operandoIzquierdo = parseFactor();

        while (posicionTokenActual < tokens.getSizeTokens()
                && (tokens.lexicalCompRank(posicionTokenActual).equals("MULTIPLICACION")
                || tokens.lexicalCompRank(posicionTokenActual).equals("DIVISION")
                || tokens.lexicalCompRank(posicionTokenActual).equals("MODULO"))) { // ¡Agregué el módulo!

            String operador = tokens.lexemeRank(posicionTokenActual); // Guardará "*", "/" o "%"
            posicionTokenActual++;
            String operandoDerecho = parseFactor();

            if (operandoIzquierdo == null || operandoDerecho == null) {
                return null;
            }

            String tempDestino = generador3D.nuevaTemp();
            generador3D.gc(operador, operandoIzquierdo, operandoDerecho, tempDestino);
            operandoIzquierdo = tempDestino;
        }
        return operandoIzquierdo;
    }

private void generarCodigoParaSentenciaActual() {
        if (posicionTokenActual >= tokens.getSizeTokens()) {
            return;
        }

        String tokenActualLexema = tokens.lexemeRank(posicionTokenActual);
        String tokenActualComp = tokens.lexicalCompRank(posicionTokenActual);

        switch (tokenActualComp) {

            // --- DECLARACIÓN DE FUNCIONES Y VARIABLES ---
            case "INICIO":
            case "NUM":
            case "FLOT":
            case "TEXTO":
            case "LOGICO": {
                posicionTokenActual++; // Consumir el tipo

                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("IDENTIFICADOR")) {
                    String nombre = tokens.lexemeRank(posicionTokenActual);
                    posicionTokenActual++; // Consumir el identificador

                    // ¿Es una función? (Tiene paréntesis de apertura)
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_APERTURA")) {
                        generador3D.gc("LABEL", "", "", nombre); // ej. principal: o sumar:
                        
                        posicionTokenActual++; // Consumir '('
                        
                        // --- MAGIA: EXTRAER PARÁMETROS PARA EL CÓDIGO 3D ---
                        java.util.List<String> parametrosFuncion = new java.util.ArrayList<>();
                        while (posicionTokenActual < tokens.getSizeTokens() && !tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_CIERRE")) {
                            if (tokens.lexicalCompRank(posicionTokenActual).equals("IDENTIFICADOR")) {
                                parametrosFuncion.add(tokens.lexemeRank(posicionTokenActual));
                            }
                            posicionTokenActual++;
                        }
                        
                        // Generar 3D para "recibir" los parámetros
                        for (int i = parametrosFuncion.size() - 1; i >= 0; i--) {
                            generador3D.out.println("recibir " + parametrosFuncion.get(i) + ";");
                        }
                        // --------------------------------------------------
                        
                        // Avanzamos hasta abrir la llave del cuerpo de la función
                        while (posicionTokenActual < tokens.getSizeTokens() && !tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_APERTURA")) {
                            posicionTokenActual++;
                        }
                        if (posicionTokenActual < tokens.getSizeTokens()) {
                            posicionTokenActual++; // Consumir '{'
                        }
                    }
                    // ¿Es una variable? (Puede tener '=' o ';')
                    else {
                        if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("OPERADORASIGNACION")) {
                            posicionTokenActual++; // Consumir '='
                            String resultadoExpr = parseExpresion(); 
                            if (resultadoExpr != null) {
                                generador3D.gc("ASIGNACION", resultadoExpr, "", nombre);
                            }
                        }
                        // Avanzamos hasta el fin de línea
                        while (posicionTokenActual < tokens.getSizeTokens() && !tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) {
                            posicionTokenActual++;
                        }
                        if (posicionTokenActual < tokens.getSizeTokens()) {
                            posicionTokenActual++; // Consumir ';'
                        }
                    }
                }
                break;
            }

            // --- ASIGNACIONES DIRECTAS Y LLAMADAS A FUNCIONES ---
            case "IDENTIFICADOR": {
                // Filtro especial: ignorar 'principal'
                if (tokenActualLexema.equals("principal")) {
                    posicionTokenActual++;
                    break;
                }

                String nombreIdentificador = tokenActualLexema;
                
                // Mirar el siguiente token
                if (posicionTokenActual + 1 >= tokens.getSizeTokens()) {
                    posicionTokenActual++; 
                    break;
                }

                String siguienteTokenComp = tokens.lexicalCompRank(posicionTokenActual + 1);

                if (siguienteTokenComp.equals("OPERADORASIGNACION")) {
                    // x = 10;
                    posicionTokenActual += 2; // Consumir IDENTIFICADOR y "="
                    String resultadoExpr = parseExpresion(); 
                    if (resultadoExpr != null) {
                        generador3D.gc("ASIGNACION", resultadoExpr, "", nombreIdentificador);
                    }
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) posicionTokenActual++;
                } 
                else if (siguienteTokenComp.equals("INCREMENTO")) {
                    // x++;
                    String temp = generador3D.nuevaTemp();
                    generador3D.gc("+", nombreIdentificador, "1", temp);
                    generador3D.gc("ASIGNACION", temp, "", nombreIdentificador);
                    posicionTokenActual += 2;
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) posicionTokenActual++;
                } 
                else if (siguienteTokenComp.equals("DECREMENTO")) {
                    // x--;
                    String temp = generador3D.nuevaTemp();
                    generador3D.gc("-", nombreIdentificador, "1", temp);
                    generador3D.gc("ASIGNACION", temp, "", nombreIdentificador);
                    posicionTokenActual += 2;
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) posicionTokenActual++;
                } 
                else if (siguienteTokenComp.equals("PARENTESIS_APERTURA")) {
                    // Llamada a función: ej. saludar(); o funcion(a, b);
                    String nombreFuncion = nombreIdentificador;
                    posicionTokenActual += 2; // Consumir IDENTIFICADOR y "("
                    
                    java.util.List<String> argumentos = new java.util.ArrayList<>();
                    while (posicionTokenActual < tokens.getSizeTokens() && !tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_CIERRE")) {
                        if (!tokens.lexicalCompRank(posicionTokenActual).equals("COMA")) {
                            String arg = parseExpresion(); // Evaluar argumento
                            if (arg != null) argumentos.add(arg);
                        } else {
                            posicionTokenActual++; // Consumir ","
                        }
                    }
                    if (posicionTokenActual < tokens.getSizeTokens()) posicionTokenActual++; // Consumir ")"
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) posicionTokenActual++; // Consumir ";"
                    
                    // Generar 3D de la llamada
                    for (String arg : argumentos) {
                        generador3D.out.println("param " + arg + ";");
                    }
                    generador3D.out.println("call " + nombreFuncion + ", " + argumentos.size() + ";");
                }
                else {
                    posicionTokenActual++; // No es asignación válida ni llamada
                }
                break;
            }

            // --- LECTURA DE DATOS ---
            case "LEER": {
                posicionTokenActual++; // Consumir "leer"

                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_APERTURA")) {
                    posicionTokenActual++; // Consumir "("
                    
                    // Extraer la variable donde se guardará el valor
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("IDENTIFICADOR")) {
                        String varLeer = tokens.lexemeRank(posicionTokenActual);
                        generador3D.gc("leer", "", "", varLeer); // Imprime "leer variable;" en el C3D
                        posicionTokenActual++; // Consumir la variable
                    }
                    
                    // Consumir el cierre
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_CIERRE")) posicionTokenActual++;
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) posicionTokenActual++;
                }
                break;
            }

            // --- IMPRESIÓN (Escribir en consola) ---
            case "ESCRIBIR_LINEA": 
            case "ESCRIBIR":
            case "MOSTRAR": {
                posicionTokenActual++; // Consumir comando

                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_APERTURA")) {
                    posicionTokenActual++; // Consumir "("
                    
                    String argumentoParaMostrar = parseExpresion(); 

                    if (argumentoParaMostrar != null) {
                        generador3D.gc("mostrar", "", "", argumentoParaMostrar);
                    }
                    
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_CIERRE")) posicionTokenActual++;
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) posicionTokenActual++;
                }
                break;
            }

            // --- CICLO MIENTRAS ---
            case "MIENTRAS": {
                posicionTokenActual++; // Consumir "MIENTRAS"
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_APERTURA")) posicionTokenActual++;

                // Capturar Condición
                int inicioCondicion = posicionTokenActual;
                int finCondicion = -1;
                int contadorParentesis = 0;
                for (int i = posicionTokenActual; i < tokens.getSizeTokens(); i++) {
                    if (tokens.lexicalCompRank(i).equals("PARENTESIS_APERTURA")) contadorParentesis++;
                    else if (tokens.lexicalCompRank(i).equals("PARENTESIS_CIERRE")) {
                        if (contadorParentesis == 0) { finCondicion = i - 1; posicionTokenActual = i; break; }
                        contadorParentesis--;
                    }
                }

                if (finCondicion == -1) return; // Error de paréntesis

                StringBuilder sbCond = new StringBuilder();
                for (int i = inicioCondicion; i <= finCondicion; i++) {
                    sbCond.append(tokens.lexemeRank(i));
                }
                String conditionString = sbCond.toString().trim();
                posicionTokenActual++; // Consumir ")"

                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_APERTURA")) posicionTokenActual++;

                // Etiquetas C3D
                String L_condicion = generador3D.nuevaEtiq();
                String L_cuerpo_mientras = generador3D.nuevaEtiq();
                String L_fin_mientras = generador3D.nuevaEtiq();

                generador3D.gc("LABEL", "", "", L_condicion);
                generador3D.gc("IF", conditionString, "", L_cuerpo_mientras);
                generador3D.gc("GOTO", "", "", L_fin_mientras);
                generador3D.gc("LABEL", "", "", L_cuerpo_mientras);

                // Procesar cuerpo recursivamente
                int anidamientoLlaves = 0;
                while (posicionTokenActual < tokens.getSizeTokens()) {
                    String compLex = tokens.lexicalCompRank(posicionTokenActual);
                    if (compLex.equals("LLAVE_APERTURA")) anidamientoLlaves++;
                    else if (compLex.equals("LLAVE_CIERRE")) {
                        if (anidamientoLlaves == 0) break; // Fin del mientras
                        anidamientoLlaves--;
                    }
                    generarCodigoParaSentenciaActual();
                }

                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_CIERRE")) posicionTokenActual++;

                generador3D.gc("GOTO", "", "", L_condicion);
                generador3D.gc("LABEL", "", "", L_fin_mientras);
                break;
            }
            
// --- ESTRUCTURA CONDICIONAL (SI / SINO / FINAL) ---
            case "SI": {
                String L_fin_total = generador3D.nuevaEtiq(); // Etiqueta para salir de toda la estructura

                // Bucle mágico: Procesa el "si" principal y todos los "sino" (else if) que sigan
                while (posicionTokenActual < tokens.getSizeTokens() && 
                      (tokens.lexicalCompRank(posicionTokenActual).equals("SI") || 
                       tokens.lexicalCompRank(posicionTokenActual).equals("SINO"))) {
                    
                    posicionTokenActual++; // Consumir "si" o "sino"
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_APERTURA")) posicionTokenActual++;
                    
                    // Extraer la condición
                    int inicioCond = posicionTokenActual;
                    int finCond = -1;
                    int anidados = 0;
                    for (int i = posicionTokenActual; i < tokens.getSizeTokens(); i++) {
                        if (tokens.lexicalCompRank(i).equals("PARENTESIS_APERTURA")) anidados++;
                        else if (tokens.lexicalCompRank(i).equals("PARENTESIS_CIERRE")) {
                            if (anidados == 0) { finCond = i - 1; posicionTokenActual = i; break; }
                            anidados--;
                        }
                    }
                    
                    if (finCond == -1) return; // Error de paréntesis
                    
                    StringBuilder sbCond = new StringBuilder();
                    for (int i = inicioCond; i <= finCond; i++) sbCond.append(tokens.lexemeRank(i));
                    String condicion = sbCond.toString().trim();
                    posicionTokenActual++; // Consumir ")"
                    
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_APERTURA")) posicionTokenActual++;
                    
                    String L_cuerpo = generador3D.nuevaEtiq();
                    String L_siguiente = generador3D.nuevaEtiq(); // Etiqueta para saltar al siguiente 'sino' o 'final'
                    
                    generador3D.gc("IF", condicion, "", L_cuerpo);
                    generador3D.gc("GOTO", "", "", L_siguiente);
                    generador3D.gc("LABEL", "", "", L_cuerpo);
                    
                    // Procesar el código dentro de las llaves
                    int llaves = 0;
                    while (posicionTokenActual < tokens.getSizeTokens()) {
                        String comp = tokens.lexicalCompRank(posicionTokenActual);
                        if (comp.equals("LLAVE_APERTURA")) llaves++;
                        else if (comp.equals("LLAVE_CIERRE")) {
                            if (llaves == 0) break;
                            llaves--;
                        }
                        generarCodigoParaSentenciaActual();
                    }
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_CIERRE")) posicionTokenActual++;
                    
                    generador3D.gc("GOTO", "", "", L_fin_total); // Si ejecutó el cuerpo, saltar al final
                    generador3D.gc("LABEL", "", "", L_siguiente); // Aquí llega si la condición fue falsa
                }
                
                // Procesar el bloque FINAL (else) si existe
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("FINAL")) {
                    posicionTokenActual++; // Consumir "final"
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_APERTURA")) posicionTokenActual++;
                    
                    int llaves = 0;
                    while (posicionTokenActual < tokens.getSizeTokens()) {
                        String comp = tokens.lexicalCompRank(posicionTokenActual);
                        if (comp.equals("LLAVE_APERTURA")) llaves++;
                        else if (comp.equals("LLAVE_CIERRE")) {
                            if (llaves == 0) break;
                            llaves--;
                        }
                        generarCodigoParaSentenciaActual();
                    }
                    if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_CIERRE")) posicionTokenActual++;
                }
                
                generador3D.gc("LABEL", "", "", L_fin_total); // Punto de encuentro final
                break;
            }
            
// --- CICLO PARA (FOR) ---
            case "PARA": {
                posicionTokenActual++; // Consumir "para"
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_APERTURA")) posicionTokenActual++;
                
                // 1. Inicialización (ej. num i = 0 ;)
                // Revisar si el usuario incluyó el tipo de dato (num, entero, etc.) y saltarlo
                String compActual = tokens.lexicalCompRank(posicionTokenActual);
                if (compActual.equals("NUM") || compActual.equals("ENTERO") || compActual.equals("FLOT") || compActual.equals("LOGICO")) {
                    posicionTokenActual++; 
                }
                
                String varName = tokens.lexemeRank(posicionTokenActual);
                posicionTokenActual += 2; // saltar la variable y el "="
                String initVal = tokens.lexemeRank(posicionTokenActual);
                generador3D.gc("=", initVal, "", varName);
                
                while(!tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) posicionTokenActual++;
                posicionTokenActual++; // saltar ";"
                
                // Etiquetas C3D
                String L_condicion = generador3D.nuevaEtiq();
                String L_cuerpo = generador3D.nuevaEtiq();
                String L_actualizacion = generador3D.nuevaEtiq();
                String L_fin = generador3D.nuevaEtiq();
                
                // 2. Condición (ej. i < 3 ;)
                generador3D.gc("LABEL", "", "", L_condicion);
                String varCond = tokens.lexemeRank(posicionTokenActual);
                String opCond = tokens.lexemeRank(posicionTokenActual + 1);
                String limite = tokens.lexemeRank(posicionTokenActual + 2);
                generador3D.gc("IF", varCond + opCond + limite, "", L_cuerpo);
                generador3D.gc("GOTO", "", "", L_fin);
                while(!tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) posicionTokenActual++;
                posicionTokenActual++; // saltar ";"
                
                // 3. Guardar la Actualización para el final (ej. i ++)
                String varUpd = tokens.lexemeRank(posicionTokenActual);
                String opUpd = tokens.lexicalCompRank(posicionTokenActual + 1); // INCREMENTO o DECREMENTO
                while(!tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_CIERRE")) posicionTokenActual++;
                posicionTokenActual++; // saltar ")"
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_APERTURA")) posicionTokenActual++;
                
                // 4. Generar Cuerpo
                generador3D.gc("LABEL", "", "", L_cuerpo);
                int llaves = 0;
                while (posicionTokenActual < tokens.getSizeTokens()) {
                    String comp = tokens.lexicalCompRank(posicionTokenActual);
                    if (comp.equals("LLAVE_APERTURA")) llaves++;
                    else if (comp.equals("LLAVE_CIERRE")) {
                        if (llaves == 0) break;
                        llaves--;
                    }
                    generarCodigoParaSentenciaActual();
                }
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_CIERRE")) posicionTokenActual++;
                
                // 5. Imprimir Actualización y volver a evaluar
                generador3D.gc("LABEL", "", "", L_actualizacion);
                String temp = generador3D.nuevaTemp();
                if (opUpd.equals("INCREMENTO") || opUpd.equals("++")) {
                    generador3D.gc("+", varUpd, "1", temp);
                } else {
                    generador3D.gc("-", varUpd, "1", temp);
                }
                generador3D.gc("ASIGNACION", temp, "", varUpd);
                generador3D.gc("GOTO", "", "", L_condicion);
                
                generador3D.gc("LABEL", "", "", L_fin);
                break;
            }
            
            // --- ESTRUCTURA DE SELECCIÓN (ELEGIR / CASO / DEFECTO) ---
            case "ELEGIR": {
                posicionTokenActual++; // Consumir "elegir"
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_APERTURA")) posicionTokenActual++;

                // 1. Capturar variable de control (ej: elegir(opcion) )
                String varControl = tokens.lexemeRank(posicionTokenActual);
                posicionTokenActual++; 
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_CIERRE")) posicionTokenActual++;
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_APERTURA")) posicionTokenActual++;

                String L_fin_elegir = generador3D.nuevaEtiq();
                String L_defecto = null; // Guardará la etiqueta del bloque "defecto", si existe

                // Listas para el pre-escaneo
                List<String> valoresCasos = new ArrayList<>();
                List<String> etiquetasCasos = new ArrayList<>();
                List<Integer> iniciosCuerpoCaso = new ArrayList<>();

                int posEscaner = posicionTokenActual;
                
                // 2. PRE-ESCANEO: Buscar todos los 'caso' y el 'defecto'
                int anidamientoLlaves = 0;
                while (posEscaner < tokens.getSizeTokens()) {
                    String compLex = tokens.lexicalCompRank(posEscaner);
                    
                    if (compLex.equals("LLAVE_APERTURA")) anidamientoLlaves++;
                    else if (compLex.equals("LLAVE_CIERRE")) {
                        if (anidamientoLlaves == 0) break; // Fin del bloque elegir
                        anidamientoLlaves--;
                    }
                    
                    // Solo registrar casos que estén en el nivel principal del elegir
                    if (anidamientoLlaves == 0) {
                        if (compLex.equals("CASO")) {
                            String valorCaso = tokens.lexemeRank(posEscaner + 1);
                            valoresCasos.add(valorCaso);
                            etiquetasCasos.add(generador3D.nuevaEtiq());
                            
                            // Buscar dónde empieza el código de este caso (después de los dos puntos)
                            int posDospuntos = posEscaner + 2;
                            while (posDospuntos < tokens.getSizeTokens() && !tokens.lexicalCompRank(posDospuntos).equals("DOSPUNTOS")) posDospuntos++;
                            iniciosCuerpoCaso.add(posDospuntos + 1);
                        } 
                        else if (compLex.equals("DEFECTO")) {
                            L_defecto = generador3D.nuevaEtiq();
                            int posDospuntos = posEscaner + 1;
                            while (posDospuntos < tokens.getSizeTokens() && !tokens.lexicalCompRank(posDospuntos).equals("DOSPUNTOS")) posDospuntos++;
                            // Lo tratamos como un caso más, pero sin valor a evaluar
                            valoresCasos.add("DEFECTO"); 
                            etiquetasCasos.add(L_defecto);
                            iniciosCuerpoCaso.add(posDospuntos + 1);
                        }
                    }
                    posEscaner++;
                }

                // 3. GENERAR SALTOS CONDICIONALES (IF var == caso GOTO Lx)
                for (int i = 0; i < valoresCasos.size(); i++) {
                    if (!valoresCasos.get(i).equals("DEFECTO")) {
                        generador3D.gc("IF", varControl + "==" + valoresCasos.get(i), "", etiquetasCasos.get(i));
                    }
                }
                
                // Si ninguna condición se cumplió, saltar al defecto (si existe) o al final
                if (L_defecto != null) generador3D.gc("GOTO", "", "", L_defecto);
                else generador3D.gc("GOTO", "", "", L_fin_elegir);

                // 4. GENERAR LOS CUERPOS DE CADA CASO
                for (int i = 0; i < etiquetasCasos.size(); i++) {
                    generador3D.gc("LABEL", "", "", etiquetasCasos.get(i));
                    posicionTokenActual = iniciosCuerpoCaso.get(i);
                    
                    // Procesar hasta encontrar el siguiente "caso", "defecto", o "}" en nivel 0
                    int anidamientoCuerpo = 0;
                    while (posicionTokenActual < tokens.getSizeTokens()) {
                        String compCuerpo = tokens.lexicalCompRank(posicionTokenActual);
                        
                        // Si encontramos "salir" (break), saltamos al final
                        if (compCuerpo.equals("SALIR") && anidamientoCuerpo == 0) {
                            posicionTokenActual++; // consumir salir
                            if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) posicionTokenActual++;
                            generador3D.gc("GOTO", "", "", L_fin_elegir);
                            break; // Terminamos de procesar el cuerpo de este caso
                        }
                        
                        if (compCuerpo.equals("LLAVE_APERTURA")) anidamientoCuerpo++;
                        else if (compCuerpo.equals("LLAVE_CIERRE")) {
                            if (anidamientoCuerpo == 0) break; // Llegamos al fin del ELEGIR (fall-through sin salir)
                            anidamientoCuerpo--;
                        }
                        
                        // Si llegamos a otro caso sin haber visto un 'salir' (Fall-through simulado)
                        if (anidamientoCuerpo == 0 && (compCuerpo.equals("CASO") || compCuerpo.equals("DEFECTO"))) {
                            break; 
                        }
                        
                        generarCodigoParaSentenciaActual();
                    }
                }

                // 5. FINALIZAR
                generador3D.gc("LABEL", "", "", L_fin_elegir);
                
                // Asegurarnos de que el puntero salte la llave de cierre del ELEGIR
                posicionTokenActual = posEscaner;
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("LLAVE_CIERRE")) posicionTokenActual++;
                
                break;
            }
            
            // --- RETORNAR ---
            case "RETORNAR": {
                posicionTokenActual++; // Consumir "retornar"
                if (posicionTokenActual < tokens.getSizeTokens() && !tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) {
                    String valorRetorno = parseExpresion();
                    if (valorRetorno != null) {
                        generador3D.out.println("retornar " + valorRetorno + ";");
                    }
                }
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("FIN_LINEA")) {
                    posicionTokenActual++; // Consumir ';'
                }
                break;
            }

            // --- IGNORAR TOKENS SUELTOS PARA NO BLOQUEARSE ---
            case "LLAVE_APERTURA":
            case "LLAVE_CIERRE":
            case "FIN_LINEA":
            case "FIN":
                posicionTokenActual++;
                break;

            default:
                // Si llegamos aquí, el token no importaba para C3D o era parte de una regla no soportada
                posicionTokenActual++;
                break;
        }
    }

private String parseFactor() {
        if (posicionTokenActual >= tokens.getSizeTokens()) {
            return null; 
        }

        String componenteLexicoActual = tokens.lexicalCompRank(posicionTokenActual);
        String lexemaActual = tokens.lexemeRank(posicionTokenActual);
        String resultadoFactor = null;

        switch (componenteLexicoActual) {
            case "NUMEROENTERO": 
            case "NUMERODECIMAL":
            case "CADENA":       
            case "VERDADERO":    
            case "FALSO":
                resultadoFactor = lexemaActual;
                posicionTokenActual++; 
                break;

            case "IDENTIFICADOR":
                resultadoFactor = lexemaActual;
                posicionTokenActual++; 
                
                // ¿Es una llamada a función dentro de una matemática? (ej. num total = sumar(a,b) + 5;)
                if (posicionTokenActual < tokens.getSizeTokens() && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_APERTURA")) {
                    posicionTokenActual++; // Consumir "("
                    
                    java.util.List<String> argumentos = new java.util.ArrayList<>();
                    while (posicionTokenActual < tokens.getSizeTokens() && !tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_CIERRE")) {
                        if (!tokens.lexicalCompRank(posicionTokenActual).equals("COMA")) {
                            String arg = parseExpresion(); 
                            if (arg != null) argumentos.add(arg);
                        } else {
                            posicionTokenActual++;
                        }
                    }
                    if (posicionTokenActual < tokens.getSizeTokens()) posicionTokenActual++; // Consumir ")"
                    
                    // Generar 3D de la llamada (Retorna el valor en un temporal)
                    for (String arg : argumentos) {
                        generador3D.out.println("param " + arg + ";");
                    }
                    String tempRetorno = generador3D.nuevaTemp();
                    generador3D.out.println(tempRetorno + " = call " + resultadoFactor + ", " + argumentos.size() + ";");
                    
                    resultadoFactor = tempRetorno; // El factor ahora es el temporal con el resultado de la función
                }
                break;

            case "PARENTESIS_APERTURA": 
                posicionTokenActual++; // Consumir el "("
                resultadoFactor = parseExpresion();

                if (resultadoFactor != null && posicionTokenActual < tokens.getSizeTokens() 
                        && tokens.lexicalCompRank(posicionTokenActual).equals("PARENTESIS_CIERRE")) {
                    posicionTokenActual++; // Consumir el ")"
                } else {
                    return null; // Error: Falta paréntesis de cierre
                }
                break;

            default:
                // Error sintáctico en la expresión
                return null; 
        }

        return resultadoFactor;
    }

public void GeneracionCodigoIntermedio(String tokenFinal) {
        // Recorrer los tokens hasta llegar al final indicado (por defecto "FIN")
        while (posicionTokenActual < tokens.getSizeTokens() 
                && !tokens.lexicalCompRank(posicionTokenActual).equals(tokenFinal)) {
            
            // Guardamos la posición para evitar bucles infinitos
            int posAntes = posicionTokenActual;
            
            // Delegamos el trabajo de entender el token al método especialista
            generarCodigoParaSentenciaActual();
            
            // Escudo Anti-Bucles: Si el método no supo qué hacer y no avanzó, lo forzamos
            if (posicionTokenActual == posAntes) {
                System.err.println("Token ignorado/saltado: " + tokens.lexemeRank(posicionTokenActual));
                posicionTokenActual++;
            }
        }
    }

    public Simbolo insertarIdentificador(String tipo, String ident, String valor, String varconst) {
        Simbolo simb = new Simbolo();
        simb.setIdent(ident);
        simb.setValor(valor);
        simb.setTipo(tipo);
        simb.setVarconst(varconst);
        tablaSimbolos.put(ident, simb);
        return (simb);
    }

    public void ConversionCodigoEnsamblador() {
        JPanel panelActual = obtenerPanelActual();
        File archivoActual = archivosPestañas.get(panelActual);
        if (archivoActual == null) {
            JOptionPane.showMessageDialog(this,
                    "Debes guardar o abrir el archivo antes de generar el código ensamblador.",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String rutaArchivo = archivoActual.getAbsolutePath();

        String nombreArchivo = archivoActual.getName();

        posicionTokenActual = 0;
        tokens = idProgramaCompleto.get(0);
        Functions.clearDataInTable(salidaSimbolos);
        tablaSimbolos.clear();
        resultados.append("\n\nInicia generacion de codigo ensamblador.....");
        GeneracionCodigoASM("FIN");
        generadorASM.convertirDeclaracionesASM(tablaSimbolos);
        mostrarTablaSimbolos();
        //------- A PARTIR DE AQUI SE EMPIEZA A GUARDAR EL CODIGO ENSAMBLADOR EN EL ARCHIVO .ASM
        //se crea el archivo para el ensamblador
        generadorASM.crearArchivoCodigoASM(rutaArchivo);
        System.out.println("creando archivo: " + rutaArchivo);
        //se guardan las instrucciones que se consideran la parte del encabezado en ensamblador
        generadorASM.guardarEncabezadoASM();
        //se guarda el arraylist temporal de los simbolos que quedaron en formato ensamblador
        generadorASM.guardarDeclaracionesASM();
        //crea la seccion .code
        generadorASM.guardarSegmentoDatos();
        //se guarda el código del cuerpo en ensamblador que quedo almacenado en el arraylist temporal
        generadorASM.guardarCuerpoASM();
        //se guardan las instrucciones que se consideran la parte final del codigo ensamblador
        generadorASM.guardarFinalASM();
        //se cierra el archivo ensamblador listo para ejecutarse por ejemplo con el emulador: emu8086
        generadorASM.cerrarArchivoCodigoASM();
        resultados.append("\n\nConversión a código ASM completada....");
        /**
         * ********* TERMINA LA GENERACIÓN DE CÓDIGO ENSAMBLADOR ***********
         */
    }

    public void GeneracionCodigoASM(String tokenFin) {
        while (!tokens.lexicalCompRank(posicionTokenActual).equals(tokenFin)) {
            switch (tokens.lexicalCompRank(posicionTokenActual)) {
                case "MOSTRAR":
                    if (tokens.lexicalCompRank(posicionTokenActual + 2).equals("IDENTIFICADOR")) {
                        if (tablaSimbolos.get(tokens.lexemeRank(posicionTokenActual + 2)).isTipoNumeric()) //es un tipo numerico
                        {
                            generadorASM.CuerpoTemporalASM(tokens.lexicalCompRank(posicionTokenActual) + "_NUMERO", "", "", tokens.lexemeRank(posicionTokenActual + 2));
                        } else //es un tipo cadena    
                        {
                            generadorASM.CuerpoTemporalASM(tokens.lexicalCompRank(posicionTokenActual) + "_CADENA", "", "", tokens.lexemeRank(posicionTokenActual + 2));
                        }
                    } //generar una variable temporal, asociarle el valor del parametro de writeln o write
                    //e insertar la declaracion en ensamblador acorde con el tipo de dato del parametro
                    else //el valor a mostrar es de caracteres
                    if (tokens.lexicalCompRank(posicionTokenActual + 2).equals("TEXTO")) {
                        String varTemp = generadorASM.nuevaTemp(); //se genera un identificador temporal para declarar la cadena a mostrar
                        //Se rellena la estructura del simbolo (identificador)
                        Simbolo simb = insertarIdentificador("string", varTemp, tokens.lexemeRank(posicionTokenActual + 2), "var");
                        //agregar en la seccion de declaracion de ensamblador la variable varTemp con su valor
                        generadorASM.CuerpoTemporalASM(tokens.lexemeRank(posicionTokenActual) + "_CADENA", "", "", simb.getIdent());
                    }
                    //if (tokens.lexicalCompRank(posicionTokenActual+2).equals("TokenIdentificador"))
                    //else //este caso sería un valor directo numerico
                    posicionTokenActual = posSigToken(tokens, posicionTokenActual, "FIN_LINEA");
                    break;

                case "INICIO":
                    posicionTokenActual++;
                    break;
                case "LLAVE_APERTURA":
                    posicionTokenActual++;
                    break;
                case "LLAVE_CIERRE":
                    posicionTokenActual++;
                    break;
                default:
                    posicionTokenActual++;
                    break;
            }
        }
    }

    /**
     * Genera código 3D para la expresión aritmética en tokens[desde..hasta] y
     * devuelve el temporal (o literal) resultante.
     */
    private String generaExpresionAritmetica(int desde, int hasta) {
        return generaExpresionAritmetica(desde, hasta, null);
    }

    /**
     * Genera código 3D para la expresión aritmética en tokens[desde..hasta]. Si
     * dest != null y la expresión consta de un único operador binario, el
     * resultado se emitirá directamente en la variable dest.
     *
     * @param desde Índice del primer token de la expresión.
     * @param hasta Índice del último token de la expresión.
     * @param dest Variable destino (puede ser null para usar temporales
     * siempre).
     * @return El nombre del temporal (o dest) donde quedó el resultado.
     */
    private String generaExpresionAritmetica(int desde, int hasta, String dest) {
        // 0) Si no hay tokens en el rango, devolvemos dest (o lanzamos)
        if (hasta < desde) {
            if (dest != null) {
                return dest;
            } else {
                throw new RuntimeException(
                        "Expresión aritmética vacía en rango [" + desde + "," + hasta + "]");
            }
        }

        // 1) Extraer los lexemas de tokens a una lista auxiliar
        List<String> expr = new ArrayList<>();
        for (int i = desde; i <= hasta; i++) {
            expr.add(tokens.lexemeRank(i));
        }

        // 2) Pasada * y /
        for (int i = 0; i < expr.size();) {
            String op = expr.get(i);
            if (op.equals("*") || op.equals("/")) {
                String l = expr.get(i - 1), r = expr.get(i + 1);
                // Si solo hay tres elementos y dest está dado, usar dest
                String tmp = (dest != null && expr.size() == 3) ? dest
                        : generador3D.nuevaTemp();
                generador3D.gc(op, l, r, tmp);
                // Reemplazar en la lista: [l, op, r] → tmp
                expr.set(i - 1, tmp);
                expr.remove(i);   // quita op
                expr.remove(i);   // quita r
                i = Math.max(1, i - 1);
            } else {
                i++;
            }
        }

        // 3) Pasada + y -
        for (int i = 0; i < expr.size();) {
            String op = expr.get(i);
            if (op.equals("+") || op.equals("-")) {
                String l = expr.get(i - 1), r = expr.get(i + 1);
                String tmp = (dest != null && expr.size() == 3) ? dest
                        : generador3D.nuevaTemp();
                generador3D.gc(op, l, r, tmp);
                expr.set(i - 1, tmp);
                expr.remove(i);
                expr.remove(i);
                i = Math.max(1, i - 1);
            } else {
                i++;
            }
        }

        // 4) Si quedó un solo literal/identificador y tenemos dest distinto,
        //    emitimos dest = expr.get(0)
        if (expr.size() == 1 && dest != null && !expr.get(0).equals(dest)) {
            generador3D.gc("=", expr.get(0), "", dest);
        }

        // 5) Devolver el resultado (sea temporal o dest)
        return expr.get(0);
    }
    
// --------------------------------------------------------------------------------
// PINTAR ERRORES
// --------------------------------------------------------------------------------
private void pintarErrores() {
    //System.out.println("--- INICIANDO PINTADO DE ERRORES ---");

    // 1. BUSCAR EL EDITOR (Cualquier tipo de componente de texto)
    javax.swing.text.JTextComponent editorActivo = null;

    try {
        java.awt.Component componentePestana = jTabbedPane1.getSelectedComponent();
        // Imprimimos qué hay en la pestaña para depurar
        System.out.println("DEBUG: Componente raíz de la pestaña: " + componentePestana.getClass().getName());
        
        editorActivo = buscarEditorTexto(componentePestana);

        if (editorActivo == null) {
            System.out.println("! ALERTA: No encontré ningún Editor de Texto en la pestaña actual.");
            // Intento desesperado: usar el jTextArea1 por defecto si existe, o jTextPane1
            // Descomenta la siguiente línea si quieres probar el fallback:
            // editorActivo = jTextArea1; 
            return; // Si no hay editor, salimos para no crashear
        }
        
        //System.out.println("DEBUG: Editor encontrado: " + editorActivo.getClass().getName());
       // System.out.println("-> El editor tiene: " + editorActivo.getDocument().getDefaultRootElement().getElementCount() + " líneas.");
        
    } catch (Exception e) {
        System.out.println("Error buscando editor: " + e.getMessage());
        return;
    }

    // 2. CONFIGURAR PINTOR
    try {
        javax.swing.text.Highlighter highlighter = editorActivo.getHighlighter();
        highlighter.removeAllHighlights();
    
        javax.swing.text.Highlighter.HighlightPainter painter = 
                new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(new java.awt.Color(255, 100, 100)); // Rojo
        
        java.util.regex.Pattern patron = java.util.regex.Pattern.compile("\\[Línea\\s+(\\d+)\\]");

        // 3. PINTAR
        for (ErrorLSSL error : gestionErrores) {
            String mensaje = error.toString();
            java.util.regex.Matcher matcher = patron.matcher(mensaje);
            
            if (matcher.find()) {
                int lineaError = Integer.parseInt(matcher.group(1));
                
                if (lineaError > 0) {
                    try {
                        javax.swing.text.Element root = editorActivo.getDocument().getDefaultRootElement();
                        int lineaReal = lineaError - 1; 
                        
                        if (lineaReal < root.getElementCount()) {
                            javax.swing.text.Element lineElement = root.getElement(lineaReal);
                            int start = lineElement.getStartOffset();
                            int end = lineElement.getEndOffset();
                            
                            // Ajuste: Evitar pintar el último caracter si es salto de línea
                            if (end > start) {
                                highlighter.addHighlight(start, end - 1, painter);
                                //System.out.println("   -> ¡PINTADA línea " + lineaError + "!");
                            }
                        } else {
                            //System.out.println("   -> Error: La línea " + lineaError + " no existe (Max: " + root.getElementCount() + ")");
                        }
                    } catch (Exception ex) {
                        //System.out.println("   -> Excepción pintando: " + ex.getMessage());
                    }
                }
            }
        }
    } catch (Exception e) {
        //System.out.println("CRASH GENERAL: " + e.getMessage());
    }
    //System.out.println("--- FIN PINTADO ---");
}



// --------------------------------------------------------------------------------
// FUNCIÓN AUXILIAR RECURSIVA (Busca JTextComponent, no solo JTextArea)
// --------------------------------------------------------------------------------
private javax.swing.text.JTextComponent buscarEditorTexto(java.awt.Component comp) {
    // Si es null, regresamos
    if (comp == null) return null;

    // ¿Es un componente de texto? (TextArea, TextPane, EditorPane...)
    if (comp instanceof javax.swing.text.JTextComponent) {
        return (javax.swing.text.JTextComponent) comp;
    }
    
    // Si es un ScrollPane, miramos adentro
    if (comp instanceof javax.swing.JScrollPane) {
        return buscarEditorTexto(((javax.swing.JScrollPane) comp).getViewport().getView());
    }
    
    // Si es un contenedor genérico (Panel, SplitPane, etc.), buscamos en sus hijos
    if (comp instanceof java.awt.Container) {
        for (java.awt.Component hijo : ((java.awt.Container) comp).getComponents()) {
            javax.swing.text.JTextComponent encontrado = buscarEditorTexto(hijo);
            if (encontrado != null) {
                // TRUCO: A veces las librerías ponen números de línea (JTextArea pequeño) 
                // al lado del código. Si encontramos uno muy pequeño (menos de 50px de ancho), lo ignoramos.
                if (encontrado.getWidth() > 0 && encontrado.getWidth() < 60) {
                    continue; // Ignoramos barras laterales estrechas
                }
                return encontrado;
            }
        }
    }
    return null;
}


class ButtonTabComponent extends JPanel {
        private final JTabbedPane pane;

        public ButtonTabComponent(final JTabbedPane pane) {
            super(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 0));
            this.pane = pane;
            setOpaque(false);
            
            JLabel label = new JLabel() {
                public String getText() {
                    int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                    if (i != -1) return pane.getTitleAt(i);
                    return null;
                }
            };
            add(label);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            
            JButton button = new JButton("x");
            button.setPreferredSize(new Dimension(17, 17));
            button.setFont(new Font("Tahoma", Font.BOLD, 10));
            button.setContentAreaFilled(false);
            button.setFocusable(false);
            button.setBorderPainted(false);
            
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setForeground(java.awt.Color.RED);
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setForeground(java.awt.Color.BLACK);
                }
            });

            button.addActionListener(e -> {
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1) pane.remove(i);
            });
            add(button);
        }
    }

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(COMPILA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(COMPILA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(COMPILA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(COMPILA.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new COMPILA().setVisible(true);

            }
        });
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Abrir;
    private javax.swing.JMenu Archivo;
    private javax.swing.JMenuItem Cerrar;
    private javax.swing.JMenu Compilar;
    private javax.swing.JMenuItem Compilar_correr;
    private javax.swing.JMenuItem Copiar;
    private javax.swing.JMenuItem Cortar;
    private javax.swing.JMenu Editar;
    private javax.swing.JMenuItem Guardar;
    private javax.swing.JMenuItem Guardar_como;
    private javax.swing.JMenu Herramientas;
    private javax.swing.JMenuItem Nuevo;
    private javax.swing.JMenuItem Pegar;
    private javax.swing.JMenuItem Salir;
    private javax.swing.JMenuItem compilar;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem menuItemConfiguracion;
    private javax.swing.JButton nuevo;
    private javax.swing.JTextArea resultados;
    private javax.swing.JTable salidaSimbolos;
    private javax.swing.JTable salidaTokens;
    // End of variables declaration//GEN-END:variables
}
