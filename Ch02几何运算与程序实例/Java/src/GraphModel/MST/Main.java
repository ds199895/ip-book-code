package GraphModel.MST;

import processing.core.PApplet;

public class Main extends PApplet {
    public boolean[]dispatch;
    Graph g;

    public static void main(String[] args) {
        PApplet.main("GraphModel.MST.Main");
    }

    public void setup(){
        size(800,800);
        //TSP&MST
        g= Graph.getWeightedGraph("E://example_graph.txt",this,width,height);
        dispatch=MST.opti(g);
    }

    public void draw(){
        background(255);

        if(drawSolution) {
            for (int i = 0; i < g.edgesNum; i++) {
                if (dispatch[i]) {
                    pushStyle();
                    strokeWeight(5);
                    stroke(255, 0, 0,120);
                    line((float) g.edges[i].start.x, (float) g.edges[i].start.y, (float) g.edges[i].end.x, (float) g.edges[i].end.y);
                    popStyle();
                }
            }
            g.draw(this);
        }else{
            g.draw(this);
        }
    }
    boolean drawSolution=false;
    public void keyPressed(){
        if(key=='s'){
            drawSolution=true;
        }else if(key=='f'){
            drawSolution=false;
        }
    }
}
