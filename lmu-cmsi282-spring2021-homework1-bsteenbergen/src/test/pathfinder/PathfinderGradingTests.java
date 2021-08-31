package test.pathfinder;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.rules.Timeout;
import org.junit.runner.Description;
import main.pathfinder.*;
import java.util.*;

/**
 * Unit tests for Maze Pathfinder. Tests include completeness and
 * optimality.
 */
public class PathfinderGradingTests {
    
    // =================================================
    // Test Configurations
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite, plus, tests to make sure
    // you're not implementing anything too computationally
    // crazy
    // [!] WARNING: Comment out these next two lines to use
    //     the debugger! Otherwise, it'll stop after 1 secs.
    @Rule
    public Timeout globalTimeout = Timeout.seconds(1);
    
    // Each time you pass a test, you get a point! Yay!
    // [!] Requires JUnit 4+ to run
    @Rule
    public TestWatcher watchman = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            passed++;
        }
    };
    
    // Grade record-keeping
    static int possible = 0, passed = 0;
    
    // the @Before method is run before every @Test
    @Before
    public void init () {
        possible++;
    }
    
    // Used for grading, reports the total number of tests
    // passed over the total possible
    @AfterClass
    public static void gradeReport () {
        System.out.println("============================");
        System.out.println("Tests Complete");
        System.out.println(passed + " / " + possible + " passed!");
        if ((1.0 * passed / possible) >= 0.9) {
            System.out.println("[!] Nice job!"); // Automated acclaim!
        }
        System.out.println("============================");
    }
    
    
    public static String[] createBigBoiMaze (int sizeX, int sizeY, MazeState initial, MazeState goal, Set<MazeState> keys, Set<MazeState> mud, Set<MazeState> walls) {
        String[] result = new String[sizeY];
        for (int r = 0; r < sizeY; r++) {
            String row = "";
            for (int c = 0; c < sizeX; c++) {
                MazeState current = new MazeState(r, c);
                if (r == 0 || r == sizeY-1 || c == 0 || c == sizeX-1 || walls.contains(current)) {
                    row += "X";
                } else if (current.equals(initial)) {
                    row += "I";
                } else if (current.equals(goal)) {
                    row += "G";
                } else if (keys.contains(current)) {
                    row += "K";
                } else if (mud.contains(current)) {
                    row += "M";
                } else {
                    row += ".";
                }
            }
            result[r] = row;
        }
        return result;
    }
    
    // =================================================
    // Unit Tests
    // =================================================
    
    @Test
    public void testPathfinder_t0() {
        String[] maze = {
            "XXXXXXX",
            "XI...KX",
            "X.....X",
            "X.X.XGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        // result will be a 2-tuple (isSolution, cost) where
        // - isSolution = 0 if it is not, 1 if it is
        // - cost = numerical cost of proposed solution
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]); // Test that result is a solution
        assertEquals(6, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t1() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.MMM.X",
            "X.XKXGX",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(14, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t2() {
        String[] maze = {
            "XXXXXXX",
            "XI.G..X",
            "X.MMM.X",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(10, result[1]); // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t3() {
        String[] maze = {
            "XXXXXXX",
            "XI.G.KX",
            "X.MMM.X",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(6, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t4() {
        String[] maze = {
            "XXXXXXX",
            "XI....X",
            "X.MXMGX",
            "X.XKX.X",
            "XXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution); // Ensure that Pathfinder knows when there's no solution
    }
    
    @Test
    public void testPathfinder_t5() {
        String[] maze = {
            "XXXXXXXXX",
            "XXXXKXXXX",
            "XXXX.XXXX",
            "XI...M.GX",
            "XXXX.XX.X",
            "XXXXK...X",
            "XXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(10, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t6() {
        String[] maze = {
            "XXXXXXXXX",
            "XXXXKXXXX",
            "XXXXMXXXX",
            "XI.....GX",
            "XXXXMXX.X",
            "XXXXK...X",
            "XXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(12, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t7() {
        String[] maze = {
            "XXXXXXXXX",
            "XXXXKXXXX",
            "XXXX.XXXX",
            "XI.....GX",
            "XXXX.XXMX",
            "XXXX...KX",
            "XXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(10, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t8() {
        String[] maze = {
            "XXXXXXXXX",
            "XXXXKXXXX",
            "XXXX.XXXX",
            "XI.....GX",
            "XXXX.XXMX",
            "XXXX...KX",
            "XXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(10, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t9() {
        String[] maze = {
            "XXXXXXXXX",
            "XXXXKXXXX",
            "XXXX.XXXX",
            "XI..G..KX",
            "XXXX.XXKX",
            "XXXX....X",
            "XXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(7, result[1]);  // Ensure that the solution is optimal
    }
    @Test
    public void testPathfinder_t10() {
        String[] maze = {
            "XXXXXXXXXX",
            "X...K....X",
            "XIXXXXXXGX",
            "X..MK....X",
            "XXXXXXXXXX",
            "XXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(9, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t11() {
        String[] maze = {
            "XXXXXXXXXX",
            "X...K...MX",
            "XIXXXXXXGX",
            "X...K....X",
            "XXXXXXXXXX",
            "XXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(9, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t12() {
        String[] maze = {
            "XXXXXXXXXXX",
            "X........KX",
            "X..XXXXXXXX",
            "X....I....X",
            "XX...G..MMX",
            "XKX.....MKX",
            "XXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(15, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t13() {
        String[] maze = {
            "XXXXXXXXXXX",
            "X.........X",
            "X.........X",
            "X...MIM...X",
            "X..MMMMM..X",
            "X..K.GMK..X",
            "XXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(8, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t14() {
        String[] maze = {
            "XXXXXXXXXXX",
            "X.........X",
            "X....K....X",
            "X...MIM...X",
            "X..MMMMM..X",
            "X...MGMK..X",
            "XXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(6, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t15() {
        String[] maze = {
            "XXXXXXXXXXX",
            "X.........X",
            "X.........X",
            "X...MMI...X",
            "X..MMMMM..X",
            "X..MKGMK..X",
            "XXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(9, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t16() {
        String[] maze = {
            "XXXXX",
            "XIGKX",
            "XXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(3, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t17() {
        String[] maze = {
            "XXXXX",
            "XGIKX",
            "XXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(3, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t18() {
        String[] maze = {
            "XXXX",
            "XGIX",
            "XXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_t19() {
        String[] maze = {
            "XXXX",
            "XKIX",
            "XXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_t20() {
        String[] maze = {
            "XXXXXXXXXXX",
            "X..X.....KX",
            "X..XXXXX..X",
            "X....I.XXXX",
            "XX...G.XMMX",
            "XKX....XMKX",
            "XXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_t21() {
        String[] maze = {
            "XXXXXXXXXXX",
            "X..X.....GX",
            "X..XXXXX..X",
            "X....I.XXXX",
            "XX...K.XMMX",
            "XKX....XMKX",
            "XXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_t22() {
        String[] maze = {
            "XXXXXXXXXXX",
            "X..X.....IX",
            "X..XXXXX..X",
            "X....G.XXXX",
            "XX...K.XMMX",
            "XKX....XMKX",
            "XXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution);
    }
    
    @Test
    public void testPathfinder_t23() {
        String[] maze = {
            "XXXXXXXXXXX",
            "XMMMMMMMK.X",
            "XMMMMMMMMMX",
            "XMMMMIMMMGX",
            "XMMMMMMMMMX",
            "XMMMMMMMKMX",
            "XXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(18, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t24() {
        String[] maze = {
            "XXXXXXXXXXX",
            "XMMMMMMMK.X",
            "XMMMMMMMXXX",
            "XMMMMIMMMGX",
            "XMMMMMMMMXX",
            "XMMMMMMMKMX",
            "XXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(20, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t25() {
        String[] maze = {
            "XXXXXXXXXXXXXXXXXXXXXXX",
            "XI..M...M...MK..M...M.X",
            "X.M...M...M.MKM...M..GX",
            "XXXXXXXXXXXXXXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(29, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t26() {
        String[] maze = {
            "XXXXXXXXXXXXXXXXXXXXXXX",
            "XI..M...M...M...M...MKX",
            "X.M...M...M.MGM...M..KX",
            "XXXXXXXXXXXXXXXXXXXXXXX"
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(41, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t27() {
        String[] maze = { 
            //           11111111112
            // 012345678901234567890
            "XXXXXXXXXXXXXXXXXXXXX", // 0
            "X..X....M....X..M.XIX", // 1
            "X...X..M.M..M..MM..MX", // 2
            "XMM..XMMMMM..X....X.X", // 3
            "X..M..X......XK.M...X", // 4
            "XX.......XXXXXMXXMM.X", // 5
            "X.XXXKMM...KXKX.X...X", // 6
            "X...X.MM....MXXX..XXX", // 7
            "XXM.X..MMMMMX..M..XKX", // 8
            "X.X........GX..M....X", // 9
            "XXXXXXXXXXXXXXXXXXXXX"  // 10
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(30, result[1]);  // Ensure that the solution is optimal
        
        // Not doing anything funky with global vars... were you?
        solution = Pathfinder.solve(prob);
        result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(30, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testPathfinder_t28() {
        String[] maze = { 
            //           11111111112
            // 012345678901234567890
            "XXXXXXXXXXXXXXXXXXXXX", // 0
            "X..X....M....X..M.XIX", // 1
            "X...X..M.M..M..MM..MX", // 2
            "XMM..XMMMMM..X....X.X", // 3
            "X..M..X......XK.M...X", // 4
            "XX....XX.XXXXXMXXMM.X", // 5
            "X.XXXKMMX..KXKX.X...X", // 6
            "X...X.MM....MXXXX.XXX", // 7
            "XXM.X..MMMMMX..M.XXKX", // 8
            "X.X........GX..M....X", // 9
            "XXXXXXXXXXXXXXXXXXXXX"  // 10
        };
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        assertNull(solution);
        
        // Not doing anything funky with global vars... were you?
        solution = Pathfinder.solve(prob);
        assertNull(solution);
    }
    
    // =================================================
    // Unit Tests
    // =================================================
    
    @Test
    public void testBIGPathfinder_t0() {
        MazeState initial = new MazeState(1, 1);
        MazeState goal = new MazeState(998, 998);
        Set<MazeState> keys = new HashSet<>(
            Arrays.asList(new MazeState(998, 1))
        );
        Set<MazeState> mud = new HashSet<>();
        Set<MazeState> walls = new HashSet<>();
        String[] maze = createBigBoiMaze(1000, 1000, initial, goal, keys, mud, walls);
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(1994, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testBIGPathfinder_t1() {
        MazeState initial = new MazeState(500, 500);
        MazeState goal = new MazeState(998, 998);
        Set<MazeState> keys = new HashSet<>(
            Arrays.asList(new MazeState(998, 1), new MazeState(501, 500))
        );
        Set<MazeState> mud = new HashSet<>();
        Set<MazeState> walls = new HashSet<>();
        String[] maze = createBigBoiMaze(1000, 1000, initial, goal, keys, mud, walls);
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        int[] result = prob.testSolution(solution);
        assertEquals(1, result[0]);  // Test that result is a solution
        assertEquals(996, result[1]);  // Ensure that the solution is optimal
    }
    
    @Test
    public void testBIGPathfinder_t2() {
        MazeState initial = new MazeState(1, 1);
        MazeState goal = new MazeState(98, 98);
        Set<MazeState> keys = new HashSet<>(
            Arrays.asList(new MazeState(98, 1))
        );
        Set<MazeState> mud = new HashSet<>();
        Set<MazeState> walls = new HashSet<>(
            Arrays.asList(new MazeState(98, 97), new MazeState(97, 98))
        );
        String[] maze = createBigBoiMaze(100, 100, initial, goal, keys, mud, walls);
        MazeProblem prob = new MazeProblem(maze);
        List<String> solution = Pathfinder.solve(prob);
        
        assertNull(solution);
    }
    
}
