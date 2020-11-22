package ex1.src;

import java.util.*;

public class WGraph_DS implements weighted_graph, java.io.Serializable {
    //equals, hashC, getEdge
    private class NodeInfo implements node_info, java.io.Serializable {

        private int key; // the node key
        private String info; // meta data - the definition of the node data
        private double tag; // help us to mark the node for to implement function - default: tag = 0;

        public NodeInfo() {
            this.key = hashCode(); // because we want unique key for each node, we will use hashCode
            this.info = "";
            this.tag = 0;
        }

        public NodeInfo(int key) { // for addNode method
            this.key = key;
            this.info = "";
            this.tag = 0;
        }

        public NodeInfo(node_info other) { // copy constructor
            this.key = other.getKey();
            this.info = other.getInfo();
            this.tag = other.getTag();
        }

        /**
         * Each node_data have a unique key.
         *
         * @return the key (id) associated with this node.
         */
        @Override
        public int getKey() {
            return this.key;
        }

        /** This method define the node data
         *
         * @return the remark (meta data) associated with this node.
         */
        @Override
        public String getInfo() {
            return this.info;
        }

        /**
         * Allows changing the remark (meta data) associated with this node.
         *
         * @param s
         */
        @Override
        public void setInfo(String s) {
            this.info = s;
        }

        /**
         * Temporal data which will be used in WGraph_Algo class
         *
         * @return the tag associated with this node.
         */
        @Override
        public double getTag() {
            return this.tag;
        }

        /**
         * Allow setting the tag value for temporal marking an node
         *
         * @param t - the new value of the tag
         */
        @Override
        public void setTag(double t) {
            this.tag = t;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            NodeInfo nodeInfo = (NodeInfo) o;
            return key == nodeInfo.key;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    } // end private class

    private HashMap<Integer, node_info> nodes; // collection of the graph nodes
    private HashMap<Integer, HashMap<node_info, Double>> wGraph; // collection of node neighbors and the weight of the edge
    private int mcCounter = 0;

    public WGraph_DS() {
        nodes = new HashMap<Integer, node_info>();
        wGraph = new HashMap<Integer, HashMap<node_info, Double>>();
        mcCounter = 0;
    }

    /**
     * This method gets a graph from WGraph_Algo class.
     * I create a new HashMap of nodes, and another one for the neighbors and the edges weight.
     * Then I started to add nodes to the nodes hashMap by the key of each node info, and
     * copy constructor in NodeInfo class, that deep coping the primitives.
     * Then I added nodes to the neighbors hashMap by the key of each node info, and
     * used the addNode method to create the HashMap inside this HashMap. Then for each node
     * in the HashMap of neighbors I connect him with the same weight as is in the original graph.
     * Also I create a new mcCounter that gets the original mcCounter value.
     *
     * @param other
     */
    public WGraph_DS(weighted_graph other) { // copy constructor
        this.nodes = new HashMap<Integer, node_info>();
        this.wGraph = new HashMap<Integer, HashMap<node_info, Double>>();
        for (node_info original : other.getV()) {
            this.nodes.put(original.getKey(), new NodeInfo(original));
            this.wGraph.put(original.getKey(), new HashMap<node_info, Double>());
            for (node_info i : other.getV(original.getKey())) {
                double w = getEdge(i.getKey(), original.getKey());
                wGraph.get(original.getKey()).put(i, w);
            }
        }
        this.mcCounter = other.getMC();
    }

    /**
     * Each node_data have a unique key.
     *
     * @param key - the node_id
     * @return the node_data by the node_id, null if none.
     */
    @Override
    public node_info getNode(int key) {
        if (!nodes.containsKey(key))
            return null;
        return nodes.get(key);
    }

    /**
     * This method check if two nodes are adjacent
     *
     * @param node1
     * @param node2
     * @return true iff (if and only if) there is an edge between node1 and node2
     */
    @Override
    public boolean hasEdge(int node1, int node2) {
        if (node1 != node2) {
            if (nodes.containsKey(node1) && nodes.containsKey(node2)) {
                if (wGraph.get(node1).containsKey(getNode(node2)) && wGraph.get(node2).containsKey(getNode(node1)))
                    return true;
            }
        }
        return false;
    }

    /**
     * Every edge has weight (double).
     *
     * @param node1
     * @param node2
     * @return the edge weight between node1 and node2. -1 if there is no such edge.
     */
    @Override
    public double getEdge(int node1, int node2) {
        if (node1 != node2) {
            if (nodes.containsKey(node1) && nodes.containsKey(node2)) {
                if (wGraph.get(node1).containsKey(getNode(node2)) && wGraph.get(node2).containsKey(getNode(node1)))
                    return wGraph.get(node1).get(getNode(node2));
            }
        }
        return -1;
    }

    /**
     * This method add a new node to the graph with the given node_data (n).
     * Because this method do change the graph we'll do mcCounter++
     *
     * @param key
     */
    @Override
    public void addNode(int key) {
        if (nodes.containsKey(key))
            return;
        node_info node = new NodeInfo(key);
        nodes.put(node.getKey(), node);
        wGraph.put(node.getKey(), new HashMap<node_info, Double>());
        mcCounter++;
    }

    /**
     * Connect an edge between node1 and node2, with an edge with weight >=0.
     * Because this method do change the graph we'll do mcCounter++.
     * If the edge node1-node2 already exists - the method simply updates the weight of the edge.
     *
     * @param node1
     * @param node2
     * @param w
     */
    @Override
    public void connect(int node1, int node2, double w) {
        if (node1 == node2)
            return;
        if (nodes.containsKey(node1) && nodes.containsKey(node2)) {
            if (hasEdge(node1, node2)) {
                wGraph.get(node1).remove(getNode(node2));
                wGraph.get(node2).remove(getNode(node1));
                mcCounter++;
            }
            wGraph.get(node1).put(getNode(node2), w);
            wGraph.get(node2).put(getNode(node1), w);
            mcCounter++;
        }
    }

    /**
     * This method return a pointer (shallow copy) for the
     * collection representing all the nodes in the graph.
     *
     * @return Collection<node_data>
     */
    @Override
    public Collection<node_info> getV() {
        return nodes.values();
    }

    /**
     * This method return a collection of the
     * collection representing all the nodes connected to node_id
     *
     * @param node_id
     * @return Collection<node_data>
     */
    @Override
    public Collection<node_info> getV(int node_id) {
        if (!nodes.containsKey(node_id))
            return null;
        Collection<node_info> nei = new ArrayList<node_info>();
        for (node_info i : wGraph.get(node_id).keySet())
            nei.add(i);
        return nei;
    }

    /**
     * This method delete the node (with the given id) from the graph -
     * and removes all edges which starts or ends at this node.
     * Because this method do change the graph we'll do mcCounter++
     *
     * @param key
     * @return the data of the removed node (null if none).
     */
    @Override
    public node_info removeNode(int key) {
        if (!nodes.containsKey(key))
            return null;
        node_info n = getNode(key);
        for (node_info i : getV(key)) {
            removeEdge(key, i.getKey());
            mcCounter++;
        }
        wGraph.remove(key);
        nodes.remove(key);
        mcCounter++;
        return n;
    }

    /**
     * This method delete the edge from the graph
     * Because this method do change the graph we'll do mcCounter++
     *
     * @param node1
     * @param node2
     */
    @Override
    public void removeEdge(int node1, int node2) {
        if (node1 == node2)
            return;
        if (!nodes.containsKey(node1) || !nodes.containsKey(node2))
            return;
        if (!wGraph.get(node1).containsKey(getNode(node2)) && !wGraph.get(node2).containsKey(getNode(node1)))
            return;
        wGraph.get(node1).remove(getNode(node2));
        wGraph.get(node2).remove(getNode(node1));
        mcCounter++;
    }

    /**
     * This method gives us the node_size of the graph
     *
     * @return the number of nodes in the graph.
     */
    @Override
    public int nodeSize() {
        return wGraph.size();
    }

    /**
     * This method gives us the edge_size of the graph
     * Note: I used a claim for undirected graph that count edges,
     *       find all nei of a single node and iterate all the nodes, then div by 2
     * @return the number of edges in weighted undirected graph.
     */
    @Override
    public int edgeSize() {
        int sum = 0;
        for (node_info i : getV()) {
            sum += getV(i.getKey()).size();
        }
        return sum / 2;
    }

    /**
     * This method count the number of changes in the graph.
     * Any change in the inner state of the graph should cause an increment in the ModeCount
     *
     * @return the counter I added in the methods that does changes in the graph
     */
    @Override
    public int getMC() {
        return mcCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WGraph_DS wGraph_ds = (WGraph_DS) o;
        return mcCounter == wGraph_ds.mcCounter &&
                nodes.equals(wGraph_ds.nodes) &&
                wGraph.equals(wGraph_ds.wGraph);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodes, wGraph, mcCounter);
    }
}

class nodeComp implements Comparator<node_info> {

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     * <p>
     * The implementor must ensure that {@code sgn(compare(x, y)) ==
     * -sgn(compare(y, x))} for all {@code x} and {@code y}.  (This
     * implies that {@code compare(x, y)} must throw an exception if and only
     * if {@code compare(y, x)} throws an exception.)<p>
     * <p>
     * The implementor must also ensure that the relation is transitive:
     * {@code ((compare(x, y)>0) && (compare(y, z)>0))} implies
     * {@code compare(x, z)>0}.<p>
     * <p>
     * Finally, the implementor must ensure that {@code compare(x, y)==0}
     * implies that {@code sgn(compare(x, z))==sgn(compare(y, z))} for all
     * {@code z}.<p>
     * <p>
     * It is generally the case, but <i>not</i> strictly required that
     * {@code (compare(x, y)==0) == (x.equals(y))}.  Generally speaking,
     * any comparator that violates this condition should clearly indicate
     * this fact.  The recommended language is "Note: this comparator
     * imposes orderings that are inconsistent with equals."<p>
     * <p>
     * In the foregoing description, the notation
     * {@code sgn(}<i>expression</i>{@code )} designates the mathematical
     * <i>signum</i> function, which is defined to return one of {@code -1},
     * {@code 0}, or {@code 1} according to whether the value of
     * <i>expression</i> is negative, zero, or positive, respectively.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is less than, equal to, or greater than the
     * second.
     * @throws NullPointerException if an argument is null and this
     *                              comparator does not permit null arguments
     * @throws ClassCastException   if the arguments' types prevent them from
     *                              being compared by this comparator.
     */
    @Override
    public int compare(node_info o1, node_info o2) {
        if (o1 instanceof node_info && o2 instanceof node_info) {
            if (((node_info) o1).getTag() == ((node_info) o2).getTag())
                return 0;
            else if (((node_info) o1).getTag() > ((node_info) o2).getTag())
                return 1;
            else
                return -1;
        }
        return -1;
    }
}
