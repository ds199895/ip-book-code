package GraphModel.CityExploration;//package GraphModel;

import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

public class Main extends PApplet {
    public ArrayList<boolean[]>dispatch;
    Graph g;
    CityExploration ct;
    List<Graph.node>nodesSolution;
    int cityStart=0;
    int cityEnd=6;
    public static void main(String[] args) {
        PApplet.main("GraphModel.CityExploration.Main");
    }

    public void setup(){
        size(800,800);
        //city exploration & MinimumCover
        g=Graph.getWeightedGraph("E://city_graph.txt",this,width,height);
        ct=new CityExploration(cityStart, cityEnd, 30);

        dispatch=ct.opti(g);
    }

    public void draw(){
        background(255);

        if(drawSolution) {
            for (int i = 0; i < g.edgesNum; i++) {
                if (dispatch.get(0)[i]) {
                    pushStyle();

                    strokeWeight(5);
                    stroke(255, 0, 0,120);
                    line((float) g.edges[i].start.x, (float) g.edges[i].start.y, (float) g.edges[i].end.x, (float) g.edges[i].end.y);
                    popStyle();
                }
            }
            g.draw(this);
            for (int i = 0; i < g.vertexNum; i++) {
                if (dispatch.get(1)[i]) {
                    pushStyle();
                    noStroke();
                    fill(255,0,0,120);
                    ellipse((float) g.nodes[i].x,(float)g.nodes[i].y,15,15);
                    popStyle();
                }
            }
            pushStyle();
            noStroke();
            fill(255,0,120,120);
            ellipse((float) g.nodes[cityStart].x,(float)g.nodes[cityStart].y,15,15);
            ellipse((float) g.nodes[cityEnd].x,(float)g.nodes[cityEnd].y,15,15);
            popStyle();

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
