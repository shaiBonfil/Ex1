package ex1;

import java.io.*;
import java.util.*;

public class WGraph_Algo implements weighted_graph_algorithms, java.io.Serializable {

    private weighted_graph wgAlgo;
    private Queue<node_info> q;
    private PriorityQueue<node_info> pQ;

    public WGraph_Algo() {
        wgAlgo = new WGraph_DS();
    }

    public WGraph_Algo(weighted_graph g) {
        wgAlgo = g;
    }

    /**
     * Init the graph on which this set of algorithms operates on.
     *
     * @param g
     */
    @Override
    public void init(weighted_graph g) {
        wgAlgo = g;
    }

    /**
     *
     *
     * @return the underlying graph of which this class works.
     */
    @Override
    public weighted_graph getGraph() {
        return this.wgAlgo;
    }

    /**
     * This method send the graph to the constructor in WGraph_DS,
     * and in WGraph_DS I implement the copy.
     *
     * @return a deep copy of this graph.
     */
    @Override
    public weighted_graph copy() { // deep copy of graph(Hashmap)
        weighted_graph nGraph = new WGraph_DS(this.wgAlgo);
        return nGraph;
    }

    /**
     * This method check if weighted undirected graph is connected or not, by using BFS algorithm.
     * Because we just want to check if the graph is connected we don't need to pay attention to the edge weight.
     * We set all the tags to zero, and then add the first node we get to a queue,
     * and set his tag to 1. I used private method that I called getRandomNode, to get the first node.
     * Because the target of the method is to check if the graph is connected, we have a counter that
     * counts every node that we add to the queue. We travel all the neighbors and if their tag is zero (unvisited),
     * we add the node to the queue and set it to 1.
     * If the counter has the same value as the graph nodeSize, then the graph is connected.
     *
     * @return true if and only if there is a valid path from every node to each
     *         other node.
     */
    @Override
    public boolean isConnected() {
        if (wgAlgo.nodeSize() == 1 || wgAlgo.nodeSize() == 0)
            return true;
        for (node_info i : wgAlgo.getV())
            i.setTag(0);
        node_info src = getRandomNode();
        q = new LinkedList<>();
        q.add(src); // enqueue random node to the queue that we can go into the while loop
        src.setTag(1);
        int counter = 1;
        while (!q.isEmpty()) {
            node_info temp = q.poll();
            for (node_info i : wgAlgo.getV(temp.getKey())) {
                if (i.getTag() == 0) { //if node is unvisited
                    q.add(i);
                    i.setTag(1);
                    counter++;
                }
                if (counter == wgAlgo.nodeSize()) // we had visited all the graph nodes therefore true
                    return true;
            }
        }
        return false;
    }

    private node_info getRandomNode() {
        for (node_info i : wgAlgo.getV()) {
            return i;
        }
        return null;
    }

    /**
     * This method find the shortest path between two nodes in weighted undirected graph,
     * by using Dijkstra algorithm. We set all the tags to infinity. Then add the
     * src node to a PriorityQueue and set his tag to zero. After that we start to
     * visit all the neighbors and if their tag is greater then the prev node tag+the edge weight between them (unvisited),
     * we add the node to the PriorityQueue and set the tag to be the prev node tag+the edge weight between them.
     * Because I choose to use PriorityQueue, I have a comparator that when I add a new node, it compare the tags and set
     * the order of the PriorityQueue from the lowest tag first until the greatest tag last.
     * Also I hold a list that every node I visited and went all over his neighbors I add to the list, so I will not
     * check him again. When the visited list contains the node info that have key that equals to dest,
     * we stop the travers and return the tag of the last node, because it is the sum of weight of the shortest path.
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return the length of the shortest path between src to dest. -1 if isn't a path between src and dest.
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        if (src == dest)
            return 0;
        if (wgAlgo.getNode(src) == null || wgAlgo.getNode(dest) == null)
            return -1;
        for (node_info i : wgAlgo.getV()) {
            i.setTag(Double.MAX_VALUE);
        }
        pQ = new PriorityQueue<>(getGraph().nodeSize(), new nodeComp());
        node_info n = wgAlgo.getNode(src);
        pQ.add(n);
        wgAlgo.getNode(src).setTag(0);
        List<node_info> visited = new ArrayList<>();
        while (!pQ.isEmpty()) {
            node_info temp = pQ.poll();
            for (node_info neighbor : wgAlgo.getV(temp.getKey())) {
                if (!visited.contains(neighbor.getKey())) {
                    double weight = temp.getTag() + wgAlgo.getEdge(neighbor.getKey(), temp.getKey());
                    if (weight < neighbor.getTag()) { //if node is unvisited
                        pQ.add(neighbor);
                        neighbor.setTag(weight);
                    }
                }
            }
            visited.add(temp);
            if (visited.contains(wgAlgo.getNode(dest)))
                return wgAlgo.getNode(dest).getTag();
        }
        return -1;
    }

    /**
     * This method find the shortest path between two nodes in weighted undirected graph,by using shortestPathDist method.
     * We add the dest node to a list, and while the size number of the neighbors are greater than zero, if the tag is equal
     * to one of the neighbors tag+the edge weight between them, add the neighbor to the list, and start to check his neighbors.
     * When the tag is equals to zero we stop, and then we need to reverse the list because we started from the dest.
     *
     * @param src  - start node
     * @param dest - end (target) node
     * @return  the the shortest path between src to dest - as an ordered List of nodes:
     *          src--> n1-->n2-->...dest
     */
    @Override
    public List<node_info> shortestPath(int src, int dest) {
        if (wgAlgo.getNode(src) == null || wgAlgo.getNode(dest) == null)
            return null;
        double dist = shortestPathDist(src, dest);
        List<node_info> shortList = new ArrayList<>();
        shortList.add(wgAlgo.getNode(dest));
        if (dest == src)
            return shortList;
        node_info temp = wgAlgo.getNode(dest);
        while (wgAlgo.getV(temp.getKey()).size() > 0) {
            for (node_info i : wgAlgo.getV(temp.getKey())) {
                double weight = i.getTag() + wgAlgo.getEdge(i.getKey(), temp.getKey());
                if (dist == weight) {
                    shortList.add(i);
                    temp = i;
                    dist = i.getTag();
                }
            }
            if (dist == 0)
                break;
        }
        Collections.reverse(shortList);
        return shortList;
    }

    /**
     * This method saves this weighted undirected graph to the given file name.
     *
     * @param file - the file name
     * @return true - iff the file was successfully saved
     */
    @Override
    public boolean save(String file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this.wgAlgo);
            fileOutputStream.close();
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * This method load a graph to this graph algorithm.
     * If the file was successfully loaded - the underlying graph
     * of this class will be changed to the loaded one.
     * If the graph was not loaded the original graph should remain "as is".
     *
     * @param file - file name
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            weighted_graph loadedG = (weighted_graph) objectInputStream.readObject();
            fileInputStream.close();
            objectInputStream.close();
            wgAlgo = loadedG;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WGraph_Algo that = (WGraph_Algo) o;
        return wgAlgo.equals(that.wgAlgo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wgAlgo);
    }
}
