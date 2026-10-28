package interfaz;

import java.awt.Font;
import java.awt.Insets;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import controlador.ControladorGrafo;

public class VentanaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private ControladorGrafo controlador;
    
    private PanelGrafo panelGrafo;
    private JTextArea areaGrafo;
    private JLabel lblResultado;

    private static final String TEXTO_INICIAL_RESULTADO = "Presione «Calcular conjunto dominante» para ver el resultado.";

    public VentanaPrincipal(ControladorGrafo controlador) {
        this.controlador = controlador;
        
        // 1. Configuración básica de la ventana
        setTitle("Conjunto dominante mínimo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 650); // Resolución fijada
        setResizable(false);
        setLocationRelativeTo(null);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        
        // APLICACIÓN DEL ABSOLUTE LAYOUT (Para compatibilidad con WindowBuilder)
        contentPane.setLayout(null); 

        // 2. Botonera (Izquierda)
        JButton btnAgregarVertice = new JButton("Agregar vértice");
        btnAgregarVertice.setBounds(20, 20, 200, 35);
        btnAgregarVertice.addActionListener(e -> agregarVertice());
        contentPane.add(btnAgregarVertice);

        JButton btnAgregarArista = new JButton("Agregar arista");
        btnAgregarArista.setBounds(20, 65, 200, 35);
        btnAgregarArista.addActionListener(e -> agregarArista());
        contentPane.add(btnAgregarArista);

        JButton btnCalcular = new JButton("Calcular conjunto dominante");
        btnCalcular.setBounds(20, 110, 200, 35);
        btnCalcular.addActionListener(e -> calcular());
        contentPane.add(btnCalcular);

        JButton btnNuevo = new JButton("Nuevo grafo");
        btnNuevo.setBounds(20, 155, 200, 35);
        btnNuevo.addActionListener(e -> nuevoGrafo());
        contentPane.add(btnNuevo);

        JButton btnGuardar = new JButton("Guardar grafo (Texto)");
        btnGuardar.setBounds(20, 200, 200, 35);
        btnGuardar.addActionListener(e -> guardarTexto());
        contentPane.add(btnGuardar);

        JButton btnCargar = new JButton("Cargar grafo (Texto)");
        btnCargar.setBounds(20, 245, 200, 35);
        btnCargar.addActionListener(e -> cargarTexto());
        contentPane.add(btnCargar);

        // 3. Panel de Visualización del Grafo (Centro)
        panelGrafo = new PanelGrafo();
        panelGrafo.setBorder(new TitledBorder("Visualización"));
        panelGrafo.setBounds(240, 15, 560, 520);
        contentPane.add(panelGrafo);

        // 4. Panel de Lista de Datos (Derecha)
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(810, 15, 260, 520);
        scrollPane.setBorder(new TitledBorder("Grafo cargado"));
        contentPane.add(scrollPane);

        areaGrafo = new JTextArea();
        areaGrafo.setEditable(false);
        areaGrafo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        areaGrafo.setMargin(new Insets(8, 8, 8, 8));
        scrollPane.setViewportView(areaGrafo);

        // 5. Panel de Resultados (Abajo)
        JPanel panelResultado = new JPanel();
        panelResultado.setBorder(new TitledBorder("Resultado"));
        panelResultado.setBounds(240, 545, 830, 55);
        panelResultado.setLayout(null);
        contentPane.add(panelResultado);

        lblResultado = new JLabel(TEXTO_INICIAL_RESULTADO);
        lblResultado.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblResultado.setBounds(15, 20, 800, 20);
        panelResultado.add(lblResultado);

        // Carga inicial
        actualizarVista(Collections.<Integer>emptySet());
    }

    // ---------- ACCIONES (Lógica conectada al Controlador) ----------
 
    private void agregarVertice() {
        controlador.agregarVertice();
        grafoCambio();
    }
 
    private void agregarArista() {
        if (controlador.cantidadVertices() < 2) {
            mostrarAviso("Se necesitan al menos dos vértices para agregar una arista.");
            return;
        }
 
        Integer[] vertices = controlador.vertices().toArray(new Integer[0]);
        JComboBox<Integer> primerExtremo = new JComboBox<>(vertices);
        JComboBox<Integer> segundoExtremo = new JComboBox<>(vertices);
        segundoExtremo.setSelectedIndex(1);
 
        Object[] contenido = { "Primer extremo:", primerExtremo, "Segundo extremo:", segundoExtremo };
        int opcion = JOptionPane.showConfirmDialog(this, contenido, "Agregar arista",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }
 
        int i = (Integer) primerExtremo.getSelectedItem();
        int j = (Integer) segundoExtremo.getSelectedItem();
        try {
            if (controlador.existeArista(i, j)) {
                mostrarAviso("La arista " + i + " - " + j + " ya existe.");
                return;
            }
            controlador.agregarArista(i, j);
            grafoCambio();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo agregar la arista",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void calcular() {
        if (controlador.cantidadVertices() == 0) {
            mostrarAviso("Agregue al menos un vértice antes de calcular.");
            return;
        }
        Set<Integer> conjunto = controlador.calcularConjuntoDominante();
        lblResultado.setText("Conjunto dominante (tamaño " + conjunto.size() + "): { "
                + unir(conjunto) + " }");
        actualizarVista(conjunto);
    }
 
    private void nuevoGrafo() {
        int opcion = JOptionPane.showConfirmDialog(this, "Se va a descartar el grafo actual. ¿Continuar?",
                "Nuevo grafo", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            controlador.nuevoGrafo();
            grafoCambio();
        }
    }
    
    private void guardarTexto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto", "txt"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".txt")) {
                ruta += ".txt"; 
            }
            try {
                controlador.guardarGrafo(ruta);
                mostrarAviso("Grafo guardado correctamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarTexto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos de Texto", "txt"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                controlador.cargarGrafo(chooser.getSelectedFile().getAbsolutePath());
                grafoCambio(); 
                mostrarAviso("Grafo cargado correctamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al cargar: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
 
    // ---------- VISTA ----------
 
    private void grafoCambio() {
        lblResultado.setText(TEXTO_INICIAL_RESULTADO);
        actualizarVista(Collections.<Integer>emptySet());
    }
 
    private void actualizarVista(Set<Integer> dominante) {
        panelGrafo.mostrar(controlador.vertices(), controlador.aristas(), dominante);
 
        StringBuilder texto = new StringBuilder();
        texto.append("Vértices (").append(controlador.cantidadVertices()).append("):\n")
                .append(unir(controlador.vertices())).append("\n\n");
        texto.append("Aristas (").append(controlador.cantidadAristas()).append("):\n");
        for (int[] arista : controlador.aristas()) {
            texto.append("  ").append(arista[0]).append(" - ").append(arista[1]).append("\n");
        }
        areaGrafo.setText(texto.toString());
        areaGrafo.setCaretPosition(0);
    }
 
    private String unir(Set<Integer> numeros) {
        return numeros.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }
 
    private void mostrarAviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }
}