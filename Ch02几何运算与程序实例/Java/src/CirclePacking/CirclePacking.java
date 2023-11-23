package CirclePacking;

import gurobi.*;
import processing.core.PApplet;
import processing.core.PVector;

public class CirclePacking extends PApplet{

    public static void main(String[] args) {
        PApplet.main("CirclePacking.CirclePacking");

    }
    int num=10;
    Opti opti;
    int w=1000;
    int h=800;
    public void setup(){
        size(w,h);
//        background(0);
//        stroke(255);
//        fill(200);

        GRBVar []xs=new GRBVar[num];
        GRBVar []ys=new GRBVar[num];
        GRBVar []rs=new GRBVar[num];
        opti=new Opti(xs,ys,rs);
        try {
            opti.setGRB();
        } catch (GRBException e) {
            e.printStackTrace();
        }
        println(opti.catches);
    }
    int count=0;
    public void draw(){
        if(opti.xd!=null) {
            pushStyle();
//                app.fill(255);
            for (int i = 0; i < num; i++) {
                System.out.println((float) opti.xd[i] + "," + (float) opti.yd[i] + "," + (float) opti.rd[i] * 2 + "," + (float) opti.rd[i] * 2);
                ellipse((float) opti.xd[i], (float) opti.yd[i], (float) opti.rd[i] * 2, (float) opti.rd[i] * 2);
            }
            popStyle();
        }
        for(int i=0;i<num;i++) {
            ellipse((float) opti.solution[0][i], (float) opti.solution[1][i], (float)opti.solution[2][i] * 2, (float) opti.solution[2][i] * 2);
        }
    }

}
