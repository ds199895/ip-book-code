package RectanglePacking;

import gurobi.*;
//import org.twak.utils.ui.Show;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.Optional;

public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main("RectanglePacking.Main");
    }
    int n=10;
    Rect[]backRects=new Rect[4];
    Rect[]outRects;

    public void setup(){
        size(600,400);
        backRects=new Rect[]{
                new Rect(500,0,100,280),
                new Rect(0,300,300,100),
                new Rect(120,100,150,100)
        };

        try{
            outRects=optiRects();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(){
        background(255);
        beginShape();
        for(int i=0;i<backRects.length;i++){
            backRects[i].draw(this);
        }

        endShape(CLOSE);
        rectMode(CORNER);
        rect(120,100,150,100);
        pushStyle();
        for(int i=0;i<n;i++){
            fill(0x66FF0000);
            outRects[i].draw(this);
        }
        popStyle();
    }

    private Rect[] optiRects() throws GRBException {
        Rect[]rects_temp=new Rect[n];
        GRBEnv env=new GRBEnv();
        env.set("logFile","Main.log");

        GRBModel model=new GRBModel(env);
        model.set(GRB.IntParam.NonConvex,2);
        model.set(GRB.DoubleParam.TimeLimit,20);

        GRBVar[]x=new GRBVar[n];
        GRBVar[]y=new GRBVar[n];
        GRBVar[]w=new GRBVar[n];
        GRBVar[]d=new GRBVar[n];
        GRBVar[][][]OR1=new GRBVar[n][n][4];
        GRBVar[][][]OR2=new GRBVar[n][3][4];

        for(int i=0;i<n;i++){
            x[i]=model.addVar(0,width,0, GRB.CONTINUOUS,"x"+String.valueOf(i));
            y[i]=model.addVar(0,height,0, GRB.CONTINUOUS,"y"+String.valueOf(i));
            w[i]=model.addVar(0,width,0, GRB.CONTINUOUS,"w"+String.valueOf(i));
            d[i]=model.addVar(0,height,0, GRB.CONTINUOUS,"d"+String.valueOf(i));

            for(int j=i+1;j<n;j++){
                OR1[i][j][0]=model.addVar(0,1,0,GRB.BINARY,"OR1R"+String.valueOf(i)+String.valueOf(j));
                OR1[i][j][1]=model.addVar(0,1,0,GRB.BINARY,"OR1L"+String.valueOf(i)+String.valueOf(j));
                OR1[i][j][2]=model.addVar(0,1,0,GRB.BINARY,"OR1U"+String.valueOf(i)+String.valueOf(j));
                OR1[i][j][3]=model.addVar(0,1,0,GRB.BINARY,"OR1D"+String.valueOf(i)+String.valueOf(j));
            }
            for(int j=0;j<3;j++){
                OR2[i][j][0]=model.addVar(0,1,0,GRB.BINARY,"OR2R"+String.valueOf(i)+String.valueOf(j));
                OR2[i][j][1]=model.addVar(0,1,0,GRB.BINARY,"OR2L"+String.valueOf(i)+String.valueOf(j));
                OR2[i][j][2]=model.addVar(0,1,0,GRB.BINARY,"OR2U"+String.valueOf(i)+String.valueOf(j));
                OR2[i][j][3]=model.addVar(0,1,0,GRB.BINARY,"OR2D"+String.valueOf(i)+String.valueOf(j));
            }
        }

        GRBQuadExpr obj=new GRBQuadExpr();
//        obj.addConstant(width*height);
//        for(int i=0;i<3;i++){
//            obj.addConstant(-hw[i]*hd[i]);
//        }
        for(int i=0;i<n;i++){
            obj.addTerm(1,w[i],d[i]);
        }
        model.setObjective(obj,GRB.MAXIMIZE);

        float M=width*height;

        for(int i=0;i<n;i++){
            GRBLinExpr expr1=new GRBLinExpr();
            expr1.addTerm(1,x[i]);expr1.addTerm(1,w[i]);
            model.addConstr(expr1,GRB.LESS_EQUAL,width,"c1"+String.valueOf(i));

            GRBLinExpr expr2=new GRBLinExpr();
            expr2.addTerm(1,y[i]);expr2.addTerm(1,d[i]);
            model.addConstr(expr2,GRB.LESS_EQUAL,height,"c2"+String.valueOf(i));

            //Add constrain of area
            GRBQuadExpr exprArea1=new GRBQuadExpr();
            exprArea1.addTerm(1,w[i],d[i]);
            model.addQConstr(exprArea1,GRB.GREATER_EQUAL,8000,"area1"+String.valueOf(i));
;
            model.addQConstr(exprArea1,GRB.LESS_EQUAL,30000,"area2"+String.valueOf(i));

            //Add constrain of the length-width ratio
            GRBLinExpr exprPro1=new GRBLinExpr();
            exprPro1.addTerm(1,w[i]);
            exprPro1.addTerm(-1.5,d[i]);
            model.addConstr(exprPro1,GRB.LESS_EQUAL,0,"cProportion1"+String.valueOf(i));

            GRBLinExpr exprPro2=new GRBLinExpr();
            exprPro2.addTerm(1,d[i]);
            exprPro2.addTerm(-1.5,w[i]);
            model.addConstr(exprPro2,GRB.LESS_EQUAL,0,"cProportion2"+String.valueOf(i));

            //Do not intersect with each other
            for(int j=i+1;j<n;j++){
                GRBLinExpr expr3=new GRBLinExpr();
                expr3.addTerm(1,x[i]);expr3.addTerm(-1,w[j]);expr3.addTerm(-1,x[j]);expr3.addTerm(-M,OR1[i][j][0]);expr3.addConstant(M);
                model.addConstr(expr3,GRB.GREATER_EQUAL,0,"c3"+String.valueOf(i)+String.valueOf(j));

                GRBLinExpr expr4=new GRBLinExpr();
                expr4.addTerm(1,x[i]);expr4.addTerm(1,w[i]);expr4.addTerm(-1,x[j]);expr4.addTerm(M,OR1[i][j][1]);expr4.addConstant(-M);
                model.addConstr(expr4,GRB.LESS_EQUAL,0,"c4"+String.valueOf(i)+String.valueOf(j));

                GRBLinExpr expr5=new GRBLinExpr();
                expr5.addTerm(1,y[i]);expr5.addTerm(-1,d[j]);expr5.addTerm(-1,y[j]);expr5.addTerm(-M,OR1[i][j][2]);expr5.addConstant(M);
                model.addConstr(expr5,GRB.GREATER_EQUAL,0,"c5"+String.valueOf(i)+String.valueOf(j));

                GRBLinExpr expr6=new GRBLinExpr();
                expr6.addTerm(1,y[i]);expr6.addTerm(1,d[i]);expr6.addTerm(-1,y[j]);expr6.addTerm(M,OR1[i][j][3]);expr6.addConstant(-M);
                model.addConstr(expr6,GRB.LESS_EQUAL,0,"c6"+String.valueOf(i)+String.valueOf(j));

                GRBLinExpr exprOR1=new GRBLinExpr();
                exprOR1.addTerm(1,OR1[i][j][0]);
                exprOR1.addTerm(1,OR1[i][j][1]);
                exprOR1.addTerm(1,OR1[i][j][2]);
                exprOR1.addTerm(1,OR1[i][j][3]);
                model.addConstr(exprOR1,GRB.GREATER_EQUAL,1,"Cor1");
            }

            //Do not intersect with the holes
            for(int j=0;j<3;j++){
                GRBLinExpr expr7=new GRBLinExpr();
                expr7.addTerm(1,x[i]);expr7.addConstant(-backRects[j].w);expr7.addConstant(-backRects[j].x);expr7.addTerm(-M,OR2[i][j][0]);expr7.addConstant(M);
                model.addConstr(expr7,GRB.GREATER_EQUAL,0,"c7"+String.valueOf(i)+String.valueOf(j));

                GRBLinExpr expr8=new GRBLinExpr();
                expr8.addTerm(1,x[i]);expr8.addTerm(1,w[i]);expr8.addConstant(-backRects[j].x);expr8.addTerm(M,OR2[i][j][1]);expr8.addConstant(-M);
                model.addConstr(expr8,GRB.LESS_EQUAL,0,"c8"+String.valueOf(i)+String.valueOf(j));

                GRBLinExpr expr9=new GRBLinExpr();
                expr9.addTerm(1,y[i]);expr9.addConstant(-backRects[j].d);expr9.addConstant(-backRects[j].y);expr9.addTerm(-M,OR2[i][j][2]);expr9.addConstant(M);
                model.addConstr(expr9,GRB.GREATER_EQUAL,0,"c9"+String.valueOf(i)+String.valueOf(j));

                GRBLinExpr expr10=new GRBLinExpr();
                expr10.addTerm(1,y[i]);expr10.addTerm(1,d[i]);expr10.addConstant(-backRects[j].y);expr10.addTerm(M,OR2[i][j][3]);expr10.addConstant(-M);
                model.addConstr(expr10,GRB.LESS_EQUAL,0,"c10"+String.valueOf(i)+String.valueOf(j));

                GRBLinExpr exprOR2=new GRBLinExpr();
                exprOR2.addTerm(1,OR2[i][j][0]);
                exprOR2.addTerm(1,OR2[i][j][1]);
                exprOR2.addTerm(1,OR2[i][j][2]);
                exprOR2.addTerm(1,OR2[i][j][3]);
                model.addConstr(exprOR2,GRB.GREATER_EQUAL,1,"Cor2");
            }
        }
        model.optimize();

        for(int i=0;i<n;i++){
            rects_temp[i]=new Rect((float) x[i].get(GRB.DoubleAttr.X),(float) y[i].get(GRB.DoubleAttr.X),(float) w[i].get(GRB.DoubleAttr.X),(float) d[i].get(GRB.DoubleAttr.X));

            System.out.println(x[i].get(GRB.StringAttr.VarName)+" "+x[i].get(GRB.DoubleAttr.X));
            System.out.println(y[i].get(GRB.StringAttr.VarName)+" "+y[i].get(GRB.DoubleAttr.X));
            System.out.println(w[i].get(GRB.StringAttr.VarName)+" "+w[i].get(GRB.DoubleAttr.X));
            System.out.println(d[i].get(GRB.StringAttr.VarName)+" "+d[i].get(GRB.DoubleAttr.X));
        }

        return rects_temp;
    }

}
