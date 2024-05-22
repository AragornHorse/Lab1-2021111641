import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DirectedGraphVisualizer extends JFrame {
    private static final long serialVersionUID = 1L;
    private final List<Node> nodes = new ArrayList<>();

    private int width = 800;
    private int heigth = 800;
    private int pad = 100;
    private int distance = 60;


    public DirectedGraphVisualizer(graph g, String title) {
        setTitle(title);
        setSize(this.width, this.heigth);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        MyPanel panel = new MyPanel();
        add(panel);

        int k=0;
        int fm = 0;
        for(int i=this.pad; i<this.width - this.pad; i+=this.distance){
            for(int j=this.pad; j<this.heigth - this.pad; j+=this.distance){
                if(k >= g.n_node){
                    break;
                }else{
                    int rd = this.distance / 2;
                    if(fm == 0){
                        nodes.add(new Node(
                            i + new Random().nextInt(rd) - rd / 2,
                            j + new Random().nextInt(rd) - rd / 2, 
                            g.nodes.get(k++).name
                        ));
                    }else{
                        nodes.add(new Node(
                            i + new Random().nextInt(rd) - rd / 2,
                            this.heigth - this.pad - (j + new Random().nextInt(rd) - rd / 2), 
                            g.nodes.get(k++).name
                        ));
                    }
                }
            }
            fm = 1 - fm;
        }
        

        for(int i=0; i<g.n_edge; i++){
            int from = g.edges.get(i).from.id;
            int to = g.edges.get(i).to.id;
            if(from >= k || to >= k){
                continue;
            }
            nodes.get(from).edges.add(new Edge(nodes.get(from), nodes.get(to), g.edges.get(i).w, g.edges.get(i).is_path));
        }

        panel.repaint();
    }

    private class MyPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Node node : nodes) {
                node.draw(g);
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(800, 600);
        }
    }

    private class Node {
        int x, y;
        String name;
        List<Edge> edges = new ArrayList<>();

        public Node(int x, int y, String name) {
            this.x = x;
            this.y = y;
            this.name = name;
        }

        public void draw(Graphics g) {
            Color c = g.getColor();
            g.setColor(new Color(220, 220, 220));
            g.fillOval(x - 10, y - 10, 20, 20); // 节点为圆形
            g.setColor(new Color(50, 50, 50));
            Font sz = g.getFont();
            g.setFont(new Font("Serif", Font.BOLD, 18));
            g.drawString(this.name, x - 10, y - 10);
            g.setFont(sz);
            g.setColor(c);

            for (Edge edge : edges) {
                edge.draw(g);
            }
        }
    }

    private class Edge {
        Node from, to;
        int w;
        boolean is_path;

        public Edge(Node from, Node to, int w, boolean is_path) {
            this.from = from;
            this.to = to;
            this.w = w;
            this.is_path = is_path;
        }

        public void draw(Graphics g) {
            Color c = g.getColor();
            g.setColor(new Color(100, 100, 100));
            if(this.is_path){
                g.setColor(new Color(250, 70, 70));
            }
            g.drawLine(from.x, from.y, to.x, to.y); 
            g.setColor(c);
            c = g.getColor();
            g.setColor(new Color(70, 70, 70));
            if(this.is_path){
                g.setColor(new Color(250, 70, 70));
            }
            g.drawString(String.format("%d", this.w), (3 * from.x + to.x) / 4 + 3, (3 * from.y + to.y) / 4 + 3);
            g.setColor(c);
        }
    }

    public static void main(String[] args) {
        graph g = new graph("./example.gio");

        SwingUtilities.invokeLater(() -> {
            DirectedGraphVisualizer app = new DirectedGraphVisualizer(g, " ");
            app.setVisible(true);
        });

        // g.get_shortest_path(null, null);
    }
}