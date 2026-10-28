package negocio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class SolverBacktrackingTest {

    private Solver solver;

    @Before
    public void setUp() {
        solver = new SolverBacktracking();
    }

    @Test
    public void grafoVacioTieneConjuntoDominanteVacio() {
        assertTrue(solver.resolver(new Grafo()).isEmpty());
    }

    @Test
    public void grafoSinAristasNecesitaTodosLosVertices() {
        Grafo g = new Grafo();
        for(int i=0; i<4; i++) g.agregarVertice();

        assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, 4)), solver.resolver(g));
    }

    @Test
    public void grafoCompletoSeDominaConUnSoloVertice() {
        Grafo g = new Grafo();
        for(int i=0; i<5; i++) g.agregarVertice();
        
        for (int i = 1; i <= 5; i++) {
            for (int j = i + 1; j <= 5; j++) {
                g.agregarArista(i, j);
            }
        }

        // El mínimo absoluto para un grafo completo es 1
        assertEquals(1, solver.resolver(g).size());
    }

    @Test
    public void verticeAisladoSiempreEstaEnElConjunto() {
        Grafo g = new Grafo();
        g.agregarVertice();
        g.agregarVertice();
        g.agregarVertice(); // Vértice 3 aislado
        g.agregarArista(1, 2);

        Set<Integer> resultado = solver.resolver(g);

        assertTrue(resultado.contains(3));
        assertEquals(2, resultado.size()); // El óptimo es el aislado + (1 o 2)
    }

    @Test
    public void resuelveElMinimoAbsolutoDeLaFigura1() {
        Grafo g = new Grafo();
        for (int i = 0; i < 6; i++) g.agregarVertice();
        
        int[][] aristas = { { 6, 4 }, { 4, 5 }, { 4, 3 }, { 5, 1 }, { 5, 2 }, { 1, 2 }, { 2, 3 } };
        for (int[] a : aristas) g.agregarArista(a[0], a[1]);

        Set<Integer> dominante = solver.resolver(g);
        
        // El óptimo absoluto demostrado en la Figura 1 tiene tamaño 2
        assertEquals(2, dominante.size());
    }
}