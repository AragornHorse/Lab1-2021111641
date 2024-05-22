import java.util.ArrayList;
import java.util.Random;

import javax.swing.SwingUtilities;


/*
 *   a node
 *   
 *   input:
 *       name, id
 */
class Node {
    
    // name, word
    String name;

    // node id
    int id;

    // edges into this node
    ArrayList<Edge> in_edges = new ArrayList<>();
    
    // edges out
    ArrayList<Edge> out_edges = new ArrayList<>();
    
    // a tool member used by bridge
    int bridge_w;

    /*
     *   name, id
     */
    public Node(String name, int id){
        this.name = name;
        this.id = id;
    }
}


/*
 *    an edge
 *    
 *    input:
 *        where it from, where it goes, edge id
 */
class Edge {

    // weight
    int w;

    // edge id
    int id;

    // where it from
    Node from;

    // where it goes
    Node to;

    // tool member used by random walk
    boolean is_pathed;

    // tool for short path visualization
    boolean is_path;

    /*
     *    from, to, id
     */
    public Edge(Node from, Node to, int id){
        this.from = from;
        this.to = to;
        this.w = 1;
        this.id = id;
    }


    /*
     *   adjust edge weight by dw
     */
    public void add_w(int dw){
        this.w += dw;
    }

    /*
     *    weight ++
     */
    public void add_w(){
        this.w ++;
    }
}


/*
 *   a graph
 * 
 *   input:
 *       path or txt
 */
public class graph {

    // gio
    gio io_interface;

    // nodes
    ArrayList<Node> nodes = new ArrayList<>();

    // edges
    ArrayList<Edge> edges = new ArrayList<>();

    // in the shortest path, the next step from i to j is to[i][j]
    Node[][] to = null;
    int[][] length = null;

    // node number
    int n_node;

    // edge number
    int n_edge;


    /*
     *   find the edge by string
     */
    public Edge find_edge(String from, String to){
        for(Edge e : this.edges){
            if(e.from.name.equals(from) && e.to.name.equals(to)){
                return e;
            }
        }
        return null;
    }


    /*
     *   find the edge by Node
     */
    public Edge find_edge(Node from, Node to){
        for(Edge e : this.edges){
            if(e.from.name.equals(from.name) && e.to.name.equals(to.name)){
                return e;
            }
        }
        return null;
    }


    /*
     *   find the node by name
     */
    public Node find_node(String name){

        if(this.nodes.size() <= 0){
            return null;
        }

        if(this.nodes.get(this.nodes.size() - 1).name.equals(name)){
            return this.nodes.get(this.nodes.size() - 1);
        }

        for(Node n : this.nodes){
            if(name.equals(n.name)){
                return n;
            }
        }
        return null;
    }


    /*
     *   get the bridge node between w1 and w2
     */
    public ArrayList<Node> get_bridge_nodes(String w1, String w2){
        Node n1 = find_node(w1);
        Node n2 = find_node(w2);
        ArrayList<Node> bridge_nodes = new ArrayList<>();

        if (n1 == null || n2 == null){    // node unexists
            bridge_nodes.add(new Node("No word1 or word2 in the graph!", -1));
        }else{         // search w1's neighbors and w2's neighbors
            for(Edge e1 : n1.out_edges){
                for(Edge e2 : n2.in_edges){
                    if(e1.to.name.equals(e2.from.name)){
                        bridge_nodes.add(e1.to);
                        e1.to.bridge_w = e1.w * e2.w;
                    }
                }
            }
        }
        return bridge_nodes;
    }


    /*
     *    init graph from gio
     */
    private void get_nodes_and_edges() {
        Node last = null, cur = null;
        Edge e;
        this.n_edge = 0;
        this.n_node = 0;
        String cur_s;

        // if new node, add into this.nodes
        // if new edge, add into this.edges and node.edge
        for(int i=0; i<this.io_interface.tokens.length; i++){
            cur_s = this.io_interface.tokens[i];
            cur = find_node(cur_s);
            if(cur == null) {   // new node
                cur = new Node(cur_s, this.n_node++);
                this.nodes.add(cur);
            }
            if(last != null){
                e = find_edge(last, cur);
                if(e == null){   // new edge
                    e = new Edge(last, cur, this.n_edge++);
                    this.edges.add(e);
                    last.out_edges.add(e);
                    cur.in_edges.add(e);
                }else{    // existing edge
                    e.add_w();
                }
            }
            last = cur;
        }

        // graph updated, previous short-path-search result is no more usable
        this.to = null;
        this.length = null;
    }


    /*
     *    print all bridge words between w1 and w2
     */
    public String queryBridgeWords(String word1, String word2){
        ArrayList<Node> bridge_nodes = this.get_bridge_nodes(word1, word2);
        String rst;

        if(bridge_nodes.size() <= 0){    // no bridge word
            rst = "No bridge words from " + word1 + " to " + word2 + "!";
        }else{      //   w1 or w2 is non-exist
            if(bridge_nodes.get(0).id < 0){
                rst = "No " + word1 + " or " + word2 + " in the graph!";
            }else{    // print all
                rst = new String();
                rst += "The bridge words from " + word1 + " to " + word2 + " are: ";
                int i = 0;
                for(i=0; i<bridge_nodes.size()-1; i++){
                    rst += "\"" + bridge_nodes.get(i).name + "\", ";
                }
                rst += "\"" + bridge_nodes.get(i).name + "\" .";
            }
        }
        return rst;
    }


    /*
     *   sample a bridge word between w1 and w2
     */
    public String sample_bridge_word(String w1, String w2){

        ArrayList<Node> bridge_nodes = this.get_bridge_nodes(w1, w2);

        if(bridge_nodes.size() <= 0){   // no bridge word
            return null;
        }
        if(bridge_nodes.get(0).id < 0){   // w1 or w2 is non-exist
            return null;
        }
        
        // sample one node based on w
        // p(b|a,c) = p(a,b)p(a,c) / p(a)p(a)p(c)
        int max = 0;
        for(Node n : bridge_nodes){
            max += n.bridge_w;
        }
        int randomNumber = new Random().nextInt(max + 1);
        for(Node n : bridge_nodes){
            randomNumber -= n.bridge_w;
            if (randomNumber <= 0){
                return n.name;
            }
        }
        return null;
    }
    
    /*
     *   initial with path or txt
     */
    public graph(String path_or_txt){
        this.io_interface = new gio(path_or_txt);
        get_nodes_and_edges();
    }


    /*
     *    initial with nothing
     */
    public graph(){
        this.io_interface = new gio();
        get_nodes_and_edges();
    }


    /*
     *    add new words into input by bridge word
     *    input:
     *        path or txt
     */
    public String generateNewText(String inputText){
        gio g1 = new gio(inputText);

        String rst = new String();

        rst += g1.tokens[0] + " ";

        String bridge;
        for(int i=1; i<g1.tokens.length; i++){
            bridge = this.sample_bridge_word(g1.tokens[i-1], g1.tokens[i]);
            if(bridge != null){
                rst += bridge + " ";
            }
            rst += g1.tokens[i] + " ";
        }

        return rst;
    }


    /*
     *     print graph
     */
    public void print(){
        for(Node n : this.nodes){
            System.out.printf("(%d:\"%s\":%d:%d), ", n.id, n.name, n.in_edges.size(), n.out_edges.size());
        }
        System.out.printf("\n");
        for(Edge e : this.edges){
            System.out.printf("(\"%s\": \"%s\" : %d), ", e.from.name, e.to.name, e.w);
        }
        System.out.printf("\n");
    }


    /*
     *    visualize
     */
    public void visual(String title){
        SwingUtilities.invokeLater(() -> {
            DirectedGraphVisualizer app = new DirectedGraphVisualizer(this, title);
            app.setVisible(true);
        });
        try {
            Thread.sleep(500);//暂停2秒钟
        }catch(InterruptedException e){
            // e.printstackTrace();
        }
    }


    /*
     *     short-path-search by prim between w1 and w2
     */
    public ArrayList<Node> get_shortest_path(String w1, String w2){
        Node n1 = find_node(w1);
        Node n2 = find_node(w2);
        if(n1 == null || n2 == null){    // w1 or w2 non-exist
            return null;
        }
        int n = this.n_node;

        if(this.to == null || this.length == null){      // no catched result
            int[][] length = new int[n][n];    // shortest distance between i and j is length[i][j]
            Node[][] to = new Node[n][n];     // if want to goto j from i, next step is to[i][j]
            
            // init length with edges
            for(int i=0; i<n; i++){
                for(int j=0; j<n; j++){
                    if(i == j){
                        length[i][j] = 0;
                        to[i][j] = this.nodes.get(i);
                    }else{
                        length[i][j] = -1;
                        to[i][j] = null;
                    }
                }
            }

            for(Edge e : this.edges){
                int i, j;
                i = e.from.id;
                j = e.to.id;
                length[i][j] = e.w;
                to[i][j] = e.to;
            }
            
            // prim
            for(int k=0; k<n; k++){
                for(int i=0; i<n; i++){
                    for(int j=0; j<n; j++){
                        if(length[i][k] < 0 || length[k][j] < 0){
                            continue;
                        }else{
                            // System.out.printf("%d %d %d\n", i, j, k);
                            int w = length[i][k] + length[k][j];
                            if(w < length[i][j] || length[i][j] < 0){
                                length[i][j] = w;
                                to[i][j] = to[i][k];
                            }
                        }
                    }
                }
            }

            // save in catch
            this.to = to;
            this.length = length;
        }

        // w1 can't reach w2
        if(this.length[n1.id][n2.id] < 0){
            return null;
        }

        // get path from w1 to w2
        ArrayList<Node> pth = new ArrayList<>();
        pth.add(n1);
        Node cur = n1;
        while(!cur.name.equals(n2.name)){
            cur = this.to[cur.id][n2.id];
            pth.add(cur);
        }

        // for(Node n_ : pth){
        //     System.out.printf("%d ", n_.id);
        // }
        this.label_npath(pth);
        return pth;
    }


    /*
     *   shortest path
     */
    public String calcShortestPath(String word1, String word2){
        ArrayList<Node> pth = get_shortest_path(word1, word2);
        if(pth == null){
            return "there is no path between " + word1 + " and " + word2 + ".";
        }
        String rst = new String();
        int i;
        for(i=0; i<pth.size() - 1; i++){
            rst += pth.get(i).name + " ";
        }
        rst += pth.get(i).name + ".";
        return rst;
    }

    /*
     *    shortest path from w to one of its neighbors
     */
    public ArrayList<Node> get_shortest_path(String w){
        Node n = find_node(w);
        if(n == null){    // w non-exist
            return null;
        }
        if(n.out_edges.size() <= 0){    // no neighbor
            return null;
        }
        ArrayList<Node> lst = new ArrayList<>();
        Edge e0 = n.out_edges.get(0);
        for(Edge e : n.out_edges){   // search minist weight
            if(e.w < e0.w){
                e0 = e;
            }
        }        
        lst.add(e0.to);
        this.label_npath(lst);
        return lst;
    }


    /*
     *   sample one edge from node
     */
    public Edge sample_edge(Node node){
        ArrayList<Edge> edges = node.out_edges;
        int max = 0;

        // p(b|a) = p(a,b) / p(a)
        for(Edge e : edges){
            max += e.w;
        }
        int rdm = new Random().nextInt(max + 1);
        for(Edge e : edges){
            rdm -= e.w;
            if(rdm <= 0){
                return e;
            }
        }
        return null;
    }


    /*
     *    predict next token by random walk
     */
    public ArrayList<Edge> random_walk_step(ArrayList<Edge> pth){

        // initial pth
        if(pth.size() <= 0){   
            for(Edge e : this.edges){
                e.is_pathed = false;   // init is_pathed
            }

            // randomly sample a start node having neighbors
            Node start = this.nodes.get(new Random().nextInt(this.n_node + 1));
            while(start.out_edges.size() <= 0){
                start = this.nodes.get(new Random().nextInt(this.n_node + 1));
            }

            // add an edge
            Edge new_edge = sample_edge(start);
            pth.add(new_edge);
            new_edge.is_pathed = true;
            return pth;
        }else{
            if(pth.get(pth.size() - 1) == null){    // this pth has been terminated
                return pth;
            }
            Edge new_edge = sample_edge(pth.get(pth.size() - 1).to);
            if(new_edge == null){       // no neighbors
                pth.add(null);
            }else{
                if(new_edge.is_pathed == true){    // repeated edge
                    pth.add(null);
                }else{        // next edge
                    pth.add(new_edge);
                }
            }
            return pth;
        }
    }


    /*
     *     random walk
     */
    public String randomWalk(){
        ArrayList<Edge> pth = new ArrayList<>();
        String rst = new String();
        for(int i=0; i<100; i++){
            pth = random_walk_step(pth);
            if(pth.get(pth.size() - 1) != null){
                rst += pth.get(pth.size() - 1).from.name + " ";
            }else{
                break;
            }
        }
        rst += pth.get(pth.size() - 1).to.name + ".";
        this.label_epath(pth);
        return rst;
    }

    public String randomWalk(int max_step){
        ArrayList<Edge> pth = new ArrayList<>();
        String rst = new String();
        for(int i=0; i<max_step; i++){
            pth = random_walk_step(pth);
            if(pth == null){
                continue;
            }
            if(pth.get(pth.size() - 1) != null){
                rst += pth.get(pth.size() - 1).from.name + " ";
            }else{
                break;
            }
        }
        if(pth.get(pth.size() - 1) != null){
            rst += pth.get(pth.size() - 1).to.name + ".";
        }else{
            rst += ".";
        }
        
        this.label_epath(pth);
        return rst;
    }

    public void label_npath(ArrayList<Node> pth){
        for(Edge e : this.edges){
            e.is_path = false;
        }
        if(pth == null){
            return;
        }
        for(int i=1; i<pth.size(); i++){
            find_edge(pth.get(i-1), pth.get(i)).is_path = true;
        }
    }

    public void label_epath(ArrayList<Edge> pth){
        for(Edge e : this.edges){
            e.is_path = false;
        }
        if(pth == null){
            return;
        }
        for(Edge e : pth){
            e.is_path = true;
        }
    }


    /*
     *   for debugging, useless
     */
    public static void main(String[] args) {
        graph g = new graph("./example.gio");
        // Tool.print_string_array(g.io_interface.tokens);
        g.print();

        System.out.println(g.queryBridgeWords("i", "you"));

        System.out.println(g.generateNewText("./input.gio"));
        System.out.println(g.get_shortest_path("i").get(0).name);
        
        System.out.println(g.calcShortestPath("i", "me"));
        
        System.out.println(g.calcShortestPath("you", "no"));

        System.out.println(g.randomWalk());

    }

}
