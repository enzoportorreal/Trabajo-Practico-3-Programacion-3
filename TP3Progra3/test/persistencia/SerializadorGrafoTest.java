package persistencia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

public class SerializadorGrafoTest {

    @Test
    public void guardarYCargarGrafoValido() throws Exception {
        // Preparar datos
        GrafoJSON original = new GrafoJSON();
        original.cantidadVertices = 3;
        original.aristas = new ArrayList<>();
        original.aristas.add(new int[]{1, 2});
        original.aristas.add(new int[]{2, 3});

        // Crear archivo temporal
        File archivoTemp = File.createTempFile("grafo_test", ".txt");
        archivoTemp.deleteOnExit();

        // Ejecutar guardado y carga
        SerializadorGrafo.guardar(original, archivoTemp.getAbsolutePath());
        GrafoJSON cargado = SerializadorGrafo.cargar(archivoTemp.getAbsolutePath());

        // Verificar
        assertEquals(3, cargado.cantidadVertices);
        assertNotNull(cargado.aristas);
        assertEquals(2, cargado.aristas.size());
        
        // Verificar que las aristas sean las mismas
        boolean arista12 = false;
        boolean arista23 = false;
        for (int[] arista : cargado.aristas) {
            if (arista[0] == 1 && arista[1] == 2) arista12 = true;
            if (arista[0] == 2 && arista[1] == 3) arista23 = true;
        }
        assertTrue(arista12);
        assertTrue(arista23);
    }

    @Test
    public void guardarYCargarGrafoSinAristas() throws Exception {
        GrafoJSON original = new GrafoJSON();
        original.cantidadVertices = 5;
        original.aristas = new ArrayList<>();

        File archivoTemp = File.createTempFile("grafo_vacio_test", ".txt");
        archivoTemp.deleteOnExit();

        SerializadorGrafo.guardar(original, archivoTemp.getAbsolutePath());
        GrafoJSON cargado = SerializadorGrafo.cargar(archivoTemp.getAbsolutePath());

        assertEquals(5, cargado.cantidadVertices);
        assertTrue(cargado.aristas.isEmpty());
    }
}