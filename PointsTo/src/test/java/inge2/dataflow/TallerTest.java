package inge2.dataflow;

import inge2.dataflow.pointstoanalysis.Node;
import inge2.dataflow.pointstoanalysis.PointsToAnalysis;
import inge2.dataflow.pointstoanalysis.PointsToGraph;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TallerTest {
    @Test
    public void testUnionPTG() {
        PointsToGraph g1 = new PointsToGraph();
        PointsToGraph g2 = new PointsToGraph();
        Set<Node> n1 = new HashSet<>();
        n1.add(new Node("N1"));
        Set<Node> n2 = new HashSet<>();
        n2.add(new Node("N2"));
        g1.setNodesForVariable("A", n1);
        g2.setNodesForVariable("A", n2);
        g1.union(g2);
        assertEquals(2, g1.getNodesForVariable("A").size());
    }

    @Test
    public void testTarget1() {
        Launcher.analyzeClass("inge2.dataflow.targets.Target1");
        PointsToAnalysis pointsToAnalysis = Launcher.getLastPointsToAnalysis();
        PointsToGraph pointsToGraph = pointsToAnalysis.getLastPointsToGraph();

        Set<Node> xNodes = pointsToGraph.getNodesForVariable("x");
        assertEquals(1, xNodes.size());
    }

    @Test
    public void testTarget2() {
        Launcher.analyzeClass("inge2.dataflow.targets.Target2");
        PointsToAnalysis pointsToAnalysis = Launcher.getLastPointsToAnalysis();

        assertTrue(pointsToAnalysis.mayAlias("x", "f1", "x"), "x.f1 == x? SI");
        assertTrue(pointsToAnalysis.mayAlias("x", "z"), "x may-alias z? SI");
    }

    @Test
    public void testTarget3() {
        Launcher.analyzeClass("inge2.dataflow.targets.Target3");
        PointsToAnalysis pointsToAnalysis = Launcher.getLastPointsToAnalysis();

        assertFalse(pointsToAnalysis.mayAlias("x", "y"), "x may-alias y? NO");
        assertTrue(pointsToAnalysis.mayAlias("x", "f1", "y"), "x.f1 may-alias y? SI");
    }

    @Test
    public void testTarget4() {
        Launcher.analyzeClass("inge2.dataflow.targets.Target4");
        PointsToAnalysis pointsToAnalysis = Launcher.getLastPointsToAnalysis();

        assertFalse(pointsToAnalysis.mayAlias("x", "y"), "x may-alias y? NO");
        assertTrue(pointsToAnalysis.mayAlias("x", "f1", "y"), "x.f1 may-alias y? SI");
    }

    @Test
    public void testTarget5() {
        Launcher.analyzeClass("inge2.dataflow.targets.Target5");
        PointsToAnalysis pointsToAnalysis = Launcher.getLastPointsToAnalysis();

        assertFalse(pointsToAnalysis.mayAlias("t", "x"), "t may-alias x? NO");
        assertTrue(pointsToAnalysis.mayAlias("t", "y"), "t may-alias y? SI");
    }

    @Test
    public void testTarget6() {
        Launcher.analyzeClass("inge2.dataflow.targets.Target6");
        PointsToAnalysis pointsToAnalysis = Launcher.getLastPointsToAnalysis();

        assertTrue(pointsToAnalysis.mayAlias("a", "c"), "a may-alias c? SI");
        assertFalse(pointsToAnalysis.mayAlias("a", "b"), "a may-alias b? NO");
        assertTrue(pointsToAnalysis.mayAlias("a", "f1", "e"), "a.f1 may-alias e? SI");
        assertFalse(pointsToAnalysis.mayAlias("a", "f1", "b"), "a.f1 may-alias b? NO");
    }
}