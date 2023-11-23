package calCenter;

import gurobi.*;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;

public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main("calCenter.Main");
    }
    PeasyCam cam;
    int num=3000;
    PVector[] pts=new PVector[num];
    PVector cent=null;
    PVector near;
    float MaxHeight=3000;
    public void setup(){
        size(2000,1500,OPENGL);
        cam=new PeasyCam(this,200);
        for(int i=0;i<num;i++){
            pts[i]=new PVector(random(width),random(height),random(MaxHeight));
        }

        try{
           cent=findCenter();
           near=pts[selNearPoint()];
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(){
        background(255);
//        directionalLight(255, 255, 255, 1, 1, -1);
//        directionalLight(127, 127, 127, -1, -1, 1);
        for(int i=0;i<num;i++){
            pushStyle();
            fill(255,255,255);
            pushMatrix();
            translate(pts[i].x,pts[i].y,pts[i].z);
            box(20);
//            ellipse(pts[i].x,pts[i].y,10,10);
            popMatrix();
            popStyle();
        }
        if(cent!=null) {
            pushStyle();
            noStroke();
            fill(255,60,50,80);
            pushMatrix();
            translate(cent.x,cent.y,cent.z);
            box(50);
//            ellipse(cent.x, cent.y, 15, 15);
            popMatrix();
            popStyle();


            pushStyle();
            noStroke();
            fill(50,120,255,80);
            pushMatrix();
            translate(near.x,near.y,near.z);
            box(20);
            popMatrix();
//            ellipse(near.x, near.y, 15, 15);
            popStyle();

        }


    }
    private int selNearPoint(){
        int id = -1;
        if(cent!=null) {
            double minDis=Double.MAX_VALUE;
            for (int i = 0; i < num; i++) {
                if(dist(pts[i].x, pts[i].y, pts[i].z,cent.x, cent.y,cent.z)<minDis){
                    minDis=dist(pts[i].x, pts[i].y, pts[i].z,cent.x, cent.y,cent.z);
                    id=i;
                }
            }
        }
        return id;
    }
    private PVector findCenter() throws GRBException {
        GRBEnv env=new GRBEnv();
        env.set("logFile","calCenter.log");

        env.start();

        GRBModel model=new GRBModel(env);

        GRBVar x=model.addVar(0,width,0,GRB.CONTINUOUS,"x");
        GRBVar y=model.addVar(0,height,0,GRB.CONTINUOUS,"y");
        GRBVar z=model.addVar(0,MaxHeight,0,GRB.CONTINUOUS,"z");

        GRBQuadExpr expr=new GRBQuadExpr();
        for(int i=0;i<num;i++){
            expr.addTerm(1,x,x);
            expr.addTerm(-2*pts[i].x,x);
            expr.addTerm(1,y,y);
            expr.addTerm(-2*pts[i].y,y);
            expr.addTerm(1,z,z);
            expr.addTerm(-2*pts[i].z,z);
        }
        model.setObjective(expr,GRB.MINIMIZE);
        model.optimize();

        System.out.println(x.get(GRB.StringAttr.VarName)+": "+x.get(GRB.DoubleAttr.X));
        System.out.println(y.get(GRB.StringAttr.VarName)+": "+y.get(GRB.DoubleAttr.X));
        System.out.println(z.get(GRB.StringAttr.VarName)+": "+z.get(GRB.DoubleAttr.X));

        System.out.println("Obj: "+model.get(GRB.DoubleAttr.ObjVal));
        PVector cen=new PVector((float) x.get(GRB.DoubleAttr.X),(float) y.get(GRB.DoubleAttr.X),(float) z.get(GRB.DoubleAttr.X));
        model.dispose();
        env.dispose();

        return cen;
    }

}
