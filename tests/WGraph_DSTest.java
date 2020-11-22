package ex1.tests;

import ex1.src.WGraph_DS;
import ex1.src.node_info;
import ex1.src.weighted_graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class WGraph_DSTest {

    weighted_graph g;
    @BeforeEach
    void setGraph(){
        g = new WGraph_DS();
        g.addNode(0);
        g.addNode(1);
        g.addNode(2);
        g.addNode(3);
        g.addNode(4);
        g.connect(0,1,1);
        g.connect(0,2,2);
        g.connect(0,3,3);
        g.connect(1,2,1);
        g.connect(1,4,3);
    }

    @Test
    void getNode() {
        node_info v1 = g.getNode(5);
        assertNull(v1);
        node_info v2 = g.getNode(0);
        assertTrue(v2.getKey() == 0);
    }

    @Test
    void hasEdge() {
        assertTrue(g.hasEdge(1, 0));
        assertFalse(g.hasEdge(1, 5));
    }

    @Test
    void getEdge() {
        double w14 = g.getEdge(1,4);
        double w41 = g.getEdge(4,1);
        assertEquals(3, w14);
        assertEquals(w14, w41);
    }

    @Test
    void connect() {
        g.removeEdge(0,2);
        assertFalse(g.hasEdge(2,0));
        g.removeEdge(2,1);
        g.connect(0,2,2);
        double w = g.getEdge(0,2);
        assertEquals(w,2);
        assertFalse(g.hasEdge(0,4));
    }

    @Test
    void getV() {
        Collection<node_info> v = g.getV();
        int count = 0;
        for (node_info i : v) {
            assertNotNull(i);
            count++;
        }
        assertEquals(5, count);
    }

    @Test
    void removeNode() {
        g.removeNode(1);
        g.removeNode(0);
        assertFalse(g.hasEdge(1,2));
        g.connect(2,4,2);
        int e = g.edgeSize();
        assertEquals(1,e);
        assertEquals(3,g.nodeSize());
    }

    @Test
    void removeEdge() {
        g.removeEdge(0,3);
        double w = g.getEdge(0,3);
        assertEquals(w,-1);
        assertTrue(g.hasEdge(1,4));
    }

    @Test
    void nodeSize() {
        g.removeNode(2);
        g.removeNode(7);
        g.removeNode(1);
        g.removeNode(1);
        int s = g.nodeSize();
        assertEquals(3,s);
    }

    @Test
    void edgeSize() {
        int e =  g.edgeSize();
        assertEquals(5, e);
    }
}