package GraphModel.MinimumCover;

import processing.core.PApplet;
import java.util.ArrayList;

public class Main extends PApplet {
    public ArrayList<boolean[]>dispatch;
    Graph g;

    public static void main(String[] args) {
        PApplet.main("GraphModel.MinimumCover.Main");
    }
    int num=1000;
    StringBuilder stringBuilder;
    public void setup(){
        size(2000,1500);
        //city exploration & MinimumCover

        for(int i=0;i<num;i++){
            int edgeNum=Math.round(random(5));
            for(int j=0;j<edgeNum;j++){
                int endIndex=Math.round(random(num));
                stringBuilder=new StringBuilder();
                stringBuilder.append(String.valueOf(i)+" "+ String.valueOf(endIndex)+" "+ String.valueOf(Math.round(random(5)))+"\n");
            }
        }
        g=Graph.getWeightedGraph("E://city_graph.txt",this,width,height);

        dispatch=MinimumCover.opti(g);
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
