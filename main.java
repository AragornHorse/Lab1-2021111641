
public class main {


    public static void main(String[] args){
        graph g = new graph("./example.gio");

        System.out.println(g.queryBridgeWords("how", "going"));
        System.out.println(g.queryBridgeWords("like", "very"));
        // System.out.println(g.queryBridgeWords("help", "!"));
        System.out.println(g.queryBridgeWords("say", "about"));

        System.out.println(g.generateNewText("i like very much"));
        System.out.println(g.generateNewText("how about for a beers"));
        System.out.println(g.generateNewText("i am"));

        g.visual("ori");

        System.out.println(g.calcShortestPath("how", "for"));
        g.visual("short");
        // System.out.println(g.calcShortestPath("but", "that"));
        // g.visual("short");
        // System.out.println(g.calcShortestPath("say", "about"));
        // g.visual("short");

        System.out.println(g.randomWalk(20));

        g.visual("random walk");

    }
}
