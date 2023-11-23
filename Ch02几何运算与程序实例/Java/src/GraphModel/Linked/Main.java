package GraphModel.Linked;

import processing.core.PApplet;
import wblut.hemesh.HE_MeshOp;

import java.util.LinkedList;
public class Main extends PApplet {
        public static void main(String[] args) {
            PApplet.main("GraphModel.Linked.Main");
        }
    Graph graph;
        public void setup(){
            size(800,800);
            // 边
            int[][] edges = {{0, 6}, {0, 2}, {0, 1}, {0, 5},
                    {3, 4}, {3, 5}, {4, 5}, {4, 6}, {7, 8},
                    {9, 10}, {9, 11}, {9, 12}, {11, 12}};

            graph=Graph.getGraph(this,800,800,edges);
            CC cc = new CC(graph);
            // M是连通分量的个数
            int M = cc.count();
            System.out.println(M + "个连通分量");
            LinkedList<Integer>[] components = (LinkedList<Integer>[]) new LinkedList[M];
            for (int i = 0; i < M; i++) {
                components[i] = new LinkedList<>();
            }
            // 将同一个id的顶点归属到同一个链表中
            for (int v = 0; v < graph.vertexNum; v++) {
                components[cc.id(v)].add(v);
            }
            // 打印每个连通分量中的顶点
            for (int i = 0; i < M; i++) {
                for (int v : components[i]) {
                    System.out.print(v+ " ");
                }
                System.out.println();
            }
        }
        public void draw(){
            background(255);
            graph.draw(this);
        }
}
