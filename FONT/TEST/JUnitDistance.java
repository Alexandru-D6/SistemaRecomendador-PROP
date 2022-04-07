package TEST;
import Domain.Distance.*;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.ArrayList;


// Tester per la classe DistanceStrategy (i tots els fills)
// Una DistanceStrategy, donat un vector de distancies parcials, calcula la distancia total
// (es a dir, calcula el modul del vector de distancies parcials)


// Objecte de la prova: DistanceStrategy
// Altres elements integrats: cap
// Fitxers de dades necessaris: cap
// Valors estudiats: Proves de tipus "Caixa Gris" amb valors positius, negatius, zeros, nulls, NaN i Infinit.
// Efectes estudiats: Correctesa del valor de retorn i temps d'execucio.
// Operativa: Executar aquest joc de proves des de la terminal utilitzant "make junit"

public class JUnitDistance {
    
    final static double DELTA = 0.0000001;
    ArrayList<Double> vec;
    DistanceStrategy ds;
    
    @Before
    public void init() {
        vec = new ArrayList<Double>();
    }
    
    /*
    ====================
     EUCLIDEAN DISTANCE
    ====================
    */
    
    // Comprova que EuclideanDistance calcula correctament un cas senzill
    @Test
    public void EuclideanBasicTest() {
        ds = new EuclideanDistance();
        vec.add(1.0);
        vec.add(2.0);
        vec.add(3.0);
        double expected = 1*1+2*2+3*3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA); 
    }
    
    // Comprova que EuclideanDistance calcula correctament un cas amb nombres negatius
    @Test
    public void EuclideanNegatives() {
        ds = new EuclideanDistance();
        vec.add(-50.0);
        vec.add(25.0);
        vec.add(3.0);
        double expected = 50*50+25*25+3*3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA); 
    }
    
    // Comprova que EuclideanDistance calcula correctament un cas amb NaN
    @Test
    public void EuclideanNaN() {
        ds = new EuclideanDistance();
        vec.add(-50.0);
        vec.add(Double.NaN);
        vec.add(25.0);
        vec.add(Double.NaN);
        vec.add(3.0);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        double expected = 50*50+25*25+3*3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA); 
    }
    
    // Comprova que EuclideanDistance calcula correctament un cas on tots els elements son NaN
    @Test
    public void EuclideanOnlyNaN() {
        ds = new EuclideanDistance();
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        
        assertTrue(Double.isNaN(ds.computeDistance(vec)));
    }
    
    // Comprova que EuclideanDistance calcula correctament un cas on tots els elements son 0
    @Test
    public void EuclideanZero() {
        ds = new EuclideanDistance();
        vec.add(0.0);
        double expected = 0;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA); 
    }
    
    // Comprova que EuclideanDistance calcula correctament un cas on el vector es buit
    @Test
    public void EuclideanEmpty() {
        ds = new EuclideanDistance();
        
        assertTrue(Double.isNaN(ds.computeDistance(vec)));
    }
    
    // Comprova que EuclideanDistance calcula correctament un cas on el vector es null
    @Test(expected = NullPointerException.class)
    public void EuclideanNull() {
        ds = new EuclideanDistance();
        vec = null;
        
        ds.computeDistance(vec);
    }
    
    // Comprova que EuclideanDistance calcula correctament un cas amb infinits
    @Test
    public void EuclideanInfinits() {
        ds = new EuclideanDistance();
        vec.add(Double.POSITIVE_INFINITY);
        vec.add(Double.NEGATIVE_INFINITY);
        vec.add(0.0);
        vec.add(Double.POSITIVE_INFINITY);
     
        assertTrue(ds.computeDistance(vec) == Double.POSITIVE_INFINITY);
    }
    
    // Comprova la velocitat de calcul de la distancia Euclidean
    @Test(timeout = 1000)
    public void EuclideanSpeed() {
        ds = new EuclideanDistance();
        for (int i = 0; i < 1000000; i++) {
            vec.add(i, (double) i);
        }
        
        ds.computeDistance(vec);
        assertTrue(Double.isFinite(ds.computeDistance(vec)));
    }
    
    
    
    /*
    ====================
     MANHATTAN DISTANCE
    ====================
    */
    
    // Comprova que ManhattanDistance calcula correctament un cas senzill
    @Test
    public void ManhattanBasicTest() {
        ds = new ManhattanDistance();
        
        vec.add(1.0);
        vec.add(2.0);
        vec.add(3.0);
        double expected = 1+2+3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que ManhattanDistance calcula correctament un cas amb nombres negatius
    @Test
    public void ManhattanNegatives() {
        ds = new ManhattanDistance();
        
        vec.add(-50.0);
        vec.add(25.0);
        vec.add(3.0);
        double expected = 50+25+3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que ManhattanDistance calcula correctament un cas amb NaN
    @Test
    public void ManhattanNaN() {
        ds = new ManhattanDistance();
        
        vec.add(-50.0);
        vec.add(Double.NaN);
        vec.add(500.0);
        vec.add(Double.NaN);
        vec.add(3.0);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        double expected = 50+500+3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que ManhattanDistance calcula correctament un cas on tots els elements son NaN
    @Test
    public void ManhattanOnlyNaN() {
        ds = new ManhattanDistance();
        
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        
        assertTrue(Double.isNaN(ds.computeDistance(vec)));
    }
    
    // Comprova que ManhattanDistance calcula correctament un cas on tots els elements son 0
    @Test
    public void ManhattanZero() {
        ds = new ManhattanDistance();
        
        vec.add(0.0);
        double expected = 0;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que ManhattanDistance calcula correctament un cas on el vector es buit
    @Test
    public void ManhattanEmpty() {
        ds = new ManhattanDistance();
        
        assertTrue(Double.isNaN(ds.computeDistance(vec)));
    }
    
    // Comprova que ManhattanDistance calcula correctament un cas on el vector es null
    @Test(expected = NullPointerException.class)
    public void ManhattanNull() {
        ds = new ManhattanDistance();
        vec = null;
        
        ds.computeDistance(vec);
    }
    
    // Comprova que ManhattanDistance calcula correctament un cas amb infinits
    @Test
    public void ManhattanInfinits() {
        ds = new ManhattanDistance();
        
        vec.add(Double.POSITIVE_INFINITY);
        vec.add(Double.NEGATIVE_INFINITY);
        vec.add(0.0);
        vec.add(Double.POSITIVE_INFINITY);
        
        assertTrue(ds.computeDistance(vec) == Double.POSITIVE_INFINITY);
    }
    
    // Comprova la velocitat de calcul de la distancia Manhattan
    @Test(timeout = 1000)
    public void ManhattanSpeed() {
        ds = new ManhattanDistance();
        for (int i = 0; i < 1000000; i++) {
            vec.add(i, (double) i);
        }
        
        ds.computeDistance(vec);
        assertTrue(Double.isFinite(ds.computeDistance(vec)));
    }
    
    
    
    /*
    ==================
     AVERAGE DISTANCE
    ==================
    */
    
    // Comprova que AverageDistance calcula correctament un cas senzill
    @Test
    public void AverageBasicTest() {
        ds = new AverageDistance();
        
        vec.add(1.0);
        vec.add(2.0);
        vec.add(3.0);
        double expected = (1.0+2.0+3.0)/3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que AverageDistance calcula correctament un cas amb nombres negatius
    @Test
    public void AverageNegatives() {
        ds = new AverageDistance();
        
        vec.add(-50.0);
        vec.add(25.0);
        vec.add(-53.0);
        double expected = (50.0+25.0+53.0)/3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que AverageDistance calcula correctament un cas amb NaN
    @Test
    public void AverageNaN() {
        ds = new AverageDistance();
        
        vec.add(-50.0);
        vec.add(Double.NaN);
        vec.add(500.0);
        vec.add(Double.NaN);
        vec.add(3.0);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        double expected = (50.0+500.0+3.0)/3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que AverageDistance calcula correctament un cas on tots els elements son NaN
    @Test
    public void AverageOnlyNaN() {
        ds = new AverageDistance();
        
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        
        assertTrue(Double.isNaN(ds.computeDistance(vec)));
    }
    
    // Comprova que AverageDistance calcula correctament un cas on tots els elements son 0
    @Test
    public void AverageZero() {
        ds = new AverageDistance();
        
        vec.add(0.0);
        double expected = 0;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que AverageDistance calcula correctament un cas on el vector es buit
    @Test
    public void AverageEmpty() {
        ds = new AverageDistance();
        
        assertTrue(Double.isNaN(ds.computeDistance(vec)));
    }
    
    // Comprova que AverageDistance calcula correctament un cas on el vector es null
    @Test(expected = NullPointerException.class)
    public void AverageNull() {
        ds = new AverageDistance();
        vec = null;
        
        ds.computeDistance(vec);
    }
    
    // Comprova que AverageDistance calcula correctament un cas amb infinits
    @Test
    public void AverageInfinits() {
        ds = new AverageDistance();
        
        vec.add(Double.POSITIVE_INFINITY);
        vec.add(Double.NEGATIVE_INFINITY);
        vec.add(0.0);
        vec.add(Double.POSITIVE_INFINITY);
        
        assertTrue(ds.computeDistance(vec) == Double.POSITIVE_INFINITY);
    }
    
    // Comprova la velocitat de calcul de la distancia Average
    @Test(timeout = 1000)
    public void AverageSpeed() {
        ds = new AverageDistance();
        for (int i = 0; i < 1000000; i++) {
            vec.add(i, (double) i);
        }
        
        ds.computeDistance(vec);
        assertTrue(Double.isFinite(ds.computeDistance(vec)));
    }
    
    
    
    /*
    =========================
     AVERAGE SQUARE DISTANCE
    =========================
    */
    
    // Comprova que AverageSquareDistance calcula correctament un cas senzill
    @Test
    public void AverageSquareBasicTest() {
        ds = new AverageSquareDistance();
        
        vec.add(1.0);
        vec.add(2.0);
        vec.add(3.0);
        double expected = (1.0*1.0+2.0*2.0+3.0*3.0)/3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que AverageSquareDistance calcula correctament un cas amb nombres negatius
    @Test
    public void AverageSquareNegatives() {
        ds = new AverageSquareDistance();
        
        vec.add(-50.0);
        vec.add(25.0);
        vec.add(-53.0);
        double expected = (50.0*50.0+25.0*25.0+53.0*53.0)/3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que AverageSquareDistance calcula correctament un cas amb NaN
    @Test
    public void AverageSquareNaN() {
        ds = new AverageSquareDistance();
        
        vec.add(-50.0);
        vec.add(Double.NaN);
        vec.add(500.0);
        vec.add(Double.NaN);
        vec.add(3.0);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        double expected = (50.0*50.0+500.0*500.0+3.0*3.0)/3;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que AverageSquareDistance calcula correctament un cas on tots els elements son NaN
    @Test
    public void AverageSquareOnlyNaN() {
        ds = new AverageSquareDistance();
        
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        vec.add(Double.NaN);
        
        assertTrue(Double.isNaN(ds.computeDistance(vec)));
    }
    
    // Comprova que AverageSquareDistance calcula correctament un cas on tots els elements son 0
    @Test
    public void AverageSquareZero() {
        ds = new AverageSquareDistance();
        
        vec.add(0.0);
        double expected = 0;
        
        assertEquals(expected, ds.computeDistance(vec), DELTA);
    }
    
    // Comprova que AverageSquareDistance calcula correctament un cas on el vector es buit
    @Test
    public void AverageSquareEmpty() {
        ds = new AverageSquareDistance();
        
        assertTrue(Double.isNaN(ds.computeDistance(vec)));
    }
    
    // Comprova que AverageSquareDistance calcula correctament un cas on el vector es null
    @Test(expected = NullPointerException.class)
    public void AverageSquareNull() {
        ds = new AverageSquareDistance();
        vec = null;
        
        ds.computeDistance(vec);
    }
    
    // Comprova que AverageSquareDistance calcula correctament un cas amb infinits
    @Test
    public void AverageSquareInfinits() {
        ds = new AverageSquareDistance();
        
        vec.add(Double.POSITIVE_INFINITY);
        vec.add(Double.NEGATIVE_INFINITY);
        vec.add(0.0);
        vec.add(Double.POSITIVE_INFINITY);
        
        assertTrue(ds.computeDistance(vec) == Double.POSITIVE_INFINITY);
    }
    
    // Comprova la velocitat de calcul de la distancia AverageSquare
    @Test(timeout = 1000)
    public void AverageSquareSpeed() {
        ds = new AverageSquareDistance();
        for (int i = 0; i < 1000000; i++) {
            vec.add(i, (double) i);
        }
        
        ds.computeDistance(vec);
        assertTrue(Double.isFinite(ds.computeDistance(vec)));
    }
}
