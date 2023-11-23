package Toilet;

import processing.core.PApplet;
import gurobi.*;

public class ToiletRevolution extends PApplet {
    public static void main(String[] args) {PApplet.main("To.ToiletRevolution");}
    int width = 1500;
    int height = 1350;
    int M = width*height;

    double[] toiletResult = new double[4];
    double[] toiletSResult = new double[4];
    double[] sinkResult = new double[4];
    double[] sinkSResult = new double[4];
    double[] showerResult = new double[4];
    double[] showerSResult = new double[4];

    int doorX = 0;
    int doorY = 0;
    int doorW = 600;
    int doorH = 600;

    int drainPipeX = 1450;
    int drainPipeY = 1300;
    int flowPipeX = 100;
    int flowPipeY = 0;


    public void setup(){
        size(1500,1350);
        try{
            GRBEnv env = new GRBEnv("ToiletRevolution.log");
            GRBModel model = new GRBModel(env);
            model.set(GRB.DoubleParam.TimeLimit, 5);
            model.set(GRB.IntParam.NonConvex,2);

            //Create Vars
            //0==x,1==y,2==w,3==h
            GRBVar[] toilet = new GRBVar[4];
            GRBVar[] toiletS = new GRBVar[4];
            GRBVar[] sink = new GRBVar[4];
            GRBVar[] sinkS = new GRBVar[4];
            GRBVar[] shower = new GRBVar[4];
            GRBVar[] showerS = new GRBVar[4];

            //1==topWall,2==rightWall,3==bottomWall,4==leftWall
            GRBVar[] toiletWall = new GRBVar[4];
            GRBVar[] sinkWall = new GRBVar[4];
            GRBVar[] showerWall = new GRBVar[4];

            GRBVar[][] overlappingRight = new GRBVar[3][3];
            GRBVar[][] overlappingLeft = new GRBVar[3][3];
            GRBVar[][] overlappingTop = new GRBVar[3][3];
            GRBVar[][] overlappingBottom = new GRBVar[3][3];

            GRBVar[] doorRight = new GRBVar[3];
            GRBVar[] doorLeft = new GRBVar[3];
            GRBVar[] doorTop = new GRBVar[3];
            GRBVar[] doorBottom = new GRBVar[3];

            //add Vars
            toilet[0] = model.addVar(0,width,0.0,GRB.CONTINUOUS,"toiletX");
            toilet[1] = model.addVar(0,height,0.0,GRB.CONTINUOUS,"toiletY");
            toilet[2] = model.addVar(350,700,0.0,GRB.CONTINUOUS,"toiletW");
            toilet[3] = model.addVar(350,700,0.0,GRB.CONTINUOUS,"toiletH");

            toiletS[0] = model.addVar(0,width,0.0,GRB.CONTINUOUS,"toiletSX");
            toiletS[1] = model.addVar(0,height,0.0,GRB.CONTINUOUS,"toiletSY");
            toiletS[2] = model.addVar(750,GRB.INFINITY,0.0,GRB.CONTINUOUS,"toiletSW");
            toiletS[3] = model.addVar(750,GRB.INFINITY,0.0,GRB.CONTINUOUS,"toiletSH");

            sink[0] = model.addVar(0,width,0.0,GRB.CONTINUOUS,"sinkX");
            sink[1] = model.addVar(0,height,0.0,GRB.CONTINUOUS,"sinkY");
            sink[2] = model.addVar(350,560,0.0,GRB.CONTINUOUS,"sinkW");
            sink[3] = model.addVar(350,560,0.0,GRB.CONTINUOUS,"sinkH");

            sinkS[0] = model.addVar(0,width,0.0,GRB.CONTINUOUS,"sinkSX");
            sinkS[1] = model.addVar(0,height,0.0,GRB.CONTINUOUS,"sinkSY");
            sinkS[2] = model.addVar(600,GRB.INFINITY,0.0,GRB.CONTINUOUS,"sinkSW");
            sinkS[3] = model.addVar(600,GRB.INFINITY,0.0,GRB.CONTINUOUS,"sinkSH");

            shower[0] = model.addVar(0,width,0.0,GRB.CONTINUOUS,"showerX");
            shower[1] = model.addVar(0,height,0.0,GRB.CONTINUOUS,"showerY");
            shower[2] = model.addVar(320,600,0.0,GRB.CONTINUOUS,"showerW");
            shower[3] = model.addVar(320,600,0.0,GRB.CONTINUOUS,"showerH");

            showerS[0] = model.addVar(0,width,0.0,GRB.CONTINUOUS,"showerSX");
            showerS[1] = model.addVar(0,height,0.0,GRB.CONTINUOUS,"showerSY");
            showerS[2] = model.addVar(780,GRB.INFINITY,0.0,GRB.CONTINUOUS,"showerSW");
            showerS[3] = model.addVar(780,GRB.INFINITY,0.0,GRB.CONTINUOUS,"showerSH");

            for(int i = 0;i<4;i++){
                toiletWall[i] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"toiletWall"+String.valueOf(i));
                sinkWall[i] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"sinkWall"+String.valueOf(i));
                showerWall[i] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"showerWall"+String.valueOf(i));
            }

            for(int i = 0;i<3;i++){
                doorRight[i] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"doorRight"+String.valueOf(i));
                doorLeft[i] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"doorLeft"+String.valueOf(i));
                doorTop[i] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"doorTop"+String.valueOf(i));
                doorBottom[i] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"doorBottom"+String.valueOf(i));
                for(int j = 0;j<3;j++){
                    overlappingRight[i][j] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"overlappingRight"+String.valueOf(i)+String.valueOf(j));
                    overlappingLeft[i][j] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"overlappingLeft"+String.valueOf(i)+String.valueOf(j));
                    overlappingTop[i][j] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"overlappingTop"+String.valueOf(i)+String.valueOf(j));
                    overlappingBottom[i][j] = model.addVar(0.0,1.0,0.0,GRB.BINARY,"overlappingBottom"+String.valueOf(i)+String.valueOf(j));
                }
            }

            /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //add multiObjective
            model.set(GRB.IntAttr.ModelSense, GRB.MAXIMIZE);

            GRBLinExpr objn;
            objn = new GRBLinExpr();
            for(int i = 2;i<4;i++){
                objn.addTerm(1.0,toilet[i]);
                objn.addTerm(1.0,sink[i]);
                objn.addTerm(1.0,shower[i]);
                objn.addTerm(1.0,toiletS[i]);
                objn.addTerm(1.0,sinkS[i]);
                objn.addTerm(1.0,showerS[i]);
            }
            model.setObjectiveN(objn,0,10,1.0,100,0.01,"BestUse");


            objn = new GRBLinExpr();
            objn.addTerm(1.0,shower[2]);
            objn.addTerm(1.0,shower[3]);
            model.setObjectiveN(objn,1,9,1,50,0.001,"WetPartition");

            objn = new GRBLinExpr();
            objn.addTerm(1.0,toilet[2]);
            objn.addTerm(1.0,toilet[3]);
            model.setObjectiveN(objn,2,5,1.5,50,0.03,"toiletUse");

            objn = new GRBLinExpr();
            objn.addTerm(1.0,sink[2]);
            objn.addTerm(1.0,sink[3]);
            model.setObjectiveN(objn,3,5,1.5,50,0.03,"sinkUse");

            objn = new GRBLinExpr();
            objn.addConstant(width);
            objn.addTerm(-1.0,sink[0]);
            model.setObjectiveN(objn,4,5,1,50,0.03,"sinkNearDoor");

            objn = new GRBLinExpr();
            objn.addConstant(width);
            objn.addTerm(-1.0,toilet[0]);
            model.setObjectiveN(objn,5,5,1,50,0.03,"toiletNearDoor");

            objn = new GRBLinExpr();
            objn.addConstant(-3*flowPipeX);
            objn.addConstant(-3*flowPipeY);
            objn.addTerm(1.0,toilet[0]);
            objn.addTerm(1.0,toilet[1]);
            objn.addTerm(0.5,toilet[2]);
            objn.addTerm(0.5,toilet[3]);
            objn.addTerm(1.0,sink[0]);
            objn.addTerm(1.0,sink[1]);
            objn.addTerm(0.5,sink[2]);
            objn.addTerm(0.5,sink[3]);
            objn.addTerm(1.0,shower[0]);
            objn.addTerm(1.0,shower[1]);
            objn.addTerm(0.5,shower[2]);
            objn.addTerm(0.5,shower[3]);
            model.setObjectiveN(objn,6,3,1,100,0.02,"flowPipeLength");

            objn = new GRBLinExpr();
            objn.addConstant(-2*drainPipeX);
            objn.addConstant(-2*drainPipeY);
            objn.addTerm(1.0,sink[0]);
            objn.addTerm(-1.0,sink[1]);
            objn.addTerm(0.5,sink[2]);
            objn.addTerm(-0.5,sink[3]);
            objn.addTerm(1.0,shower[0]);
            objn.addTerm(-1.0,shower[1]);
            objn.addTerm(0.5,shower[2]);
            objn.addTerm(-0.5,shower[3]);
            model.setObjectiveN(objn,7,3,1,100,0.02,"drainPipeLength");

            objn = new GRBLinExpr();
            objn.addConstant(-drainPipeX);
            objn.addConstant(-drainPipeY);
            objn.addTerm(1.0,toilet[0]);
            objn.addTerm(-1.0,toilet[1]);
            objn.addTerm(0.5,toilet[2]);
            objn.addTerm(-0.5,toilet[3]);
            model.setObjectiveN(objn,8,4,3,100,0.02,"ToiletDrainPipeLength");

            GRBLinExpr expr1;
            GRBQuadExpr qexpr1;
            GRBLinExpr expr2;
            GRBQuadExpr qexpr2;

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////


            if(width>height){
                qexpr1 = new GRBQuadExpr();
                qexpr1.addTerm(1.0,toilet[1],toilet[1]);
                qexpr1.addTerm(-2.0,toilet[1],sink[1]);
                qexpr1.addTerm(1.0,sink[1],sink[1]);
                qexpr1.addConstant(-60000);
                model.addQConstr(qexpr1,GRB.LESS_EQUAL,0,"111");
            }else{
                qexpr1 = new GRBQuadExpr();
                qexpr1.addTerm(1.0,toilet[0],toilet[0]);
                qexpr1.addTerm(-2.0,toilet[0],sink[0]);
                qexpr1.addTerm(1.0,sink[0],sink[0]);
                qexpr1.addConstant(-60000);
                model.addQConstr(qexpr1,GRB.LESS_EQUAL,0,"111");
            }


            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //toilet in boundary

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[0]);
            expr1.addTerm(1.0,toilet[2]);
            expr1.addConstant(-width);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"TXinBoundary");
            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toiletS[0]);
            expr1.addTerm(1.0,toiletS[2]);
            expr1.addConstant(-width);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"TSXinBoundary");
            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[1]);
            expr1.addTerm(1.0,toilet[3]);
            expr1.addConstant(-height);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"TYinBoundary");
            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toiletS[1]);
            expr1.addTerm(1.0,toiletS[3]);
            expr1.addConstant(-height);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"TSYinBoundary");

            /////////////////////////////////////////////
            //toilet on wall[0]
            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[1]);
            expr1.addTerm(M,toiletWall[0]);
            expr1.addConstant(-M);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"TY = 0");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toiletS[1]);
            expr1.addTerm(M,toiletWall[0]);
            expr1.addConstant(-M);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"TSY = 0");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[2]);
            expr1.addTerm(M,toiletWall[0]);
            expr1.addConstant(-400-M);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"toiletWon0");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[3]);
            expr1.addTerm(-M,toiletWall[0]);
            expr1.addConstant(-600+M);
            model.addConstr(expr1,GRB.GREATER_EQUAL,0,"toiletHon0");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toiletS[3]);
            expr1.addTerm(-1.0,toilet[3]);
            expr1.addConstant(M-500);
            expr1.addTerm(-M,toiletWall[0]);
            model.addConstr(expr1,GRB.GREATER_EQUAL,0.0,"tSBiggerThanT0");

            qexpr1 = new GRBQuadExpr();
            qexpr1.addTerm(1.0,toiletS[0],toiletWall[0]);
            qexpr1.addTerm(0.5,toiletS[2],toiletWall[0]);
            qexpr1.addTerm(-0.5,toilet[2],toiletWall[0]);
            qexpr1.addTerm(-1,toilet[0],toiletWall[0]);
            model.addQConstr(qexpr1,GRB.EQUAL,0,"toiletSameSpace0");


            /////////////////////////////////////////////
            //toilet on wall[1]
            qexpr1 = new GRBQuadExpr();
            qexpr1.addTerm(1.0,toilet[0],toiletWall[1]);
            qexpr1.addTerm(1.0,toilet[2],toiletWall[1]);
            qexpr1.addTerm(-width,toiletWall[1]);
            model.addQConstr(qexpr1,GRB.EQUAL,0,"TX + TW = W ");

            qexpr1 = new GRBQuadExpr();
            qexpr1.addTerm(1.0,toiletS[0],toiletWall[1]);
            qexpr1.addTerm(1.0,toiletS[2],toiletWall[1]);
            qexpr1.addTerm(-width,toiletWall[1]);
            model.addQConstr(qexpr1,GRB.EQUAL,0,"TSX + TSW = W ");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[3]);
            expr1.addTerm(M,toiletWall[1]);
            expr1.addConstant(-400-M);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"toiletHon1");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[2]);
            expr1.addTerm(-M,toiletWall[1]);
            expr1.addConstant(-600+M);
            model.addConstr(expr1,GRB.GREATER_EQUAL,0,"toiletWon1");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toiletS[2]);
            expr1.addTerm(-1.0,toilet[2]);
            expr1.addConstant(M-500);
            expr1.addTerm(-M,toiletWall[1]);
            model.addConstr(expr1,GRB.GREATER_EQUAL,0.0,"tSBiggerThanT1");

            qexpr1 = new GRBQuadExpr();
            qexpr1.addTerm(1.0,toiletS[1],toiletWall[1]);
            qexpr1.addTerm(0.5,toiletS[3],toiletWall[1]);
            qexpr1.addTerm(-0.5,toilet[3],toiletWall[1]);
            qexpr1.addTerm(-1,toilet[1],toiletWall[1]);
            model.addQConstr(qexpr1,GRB.EQUAL,0,"toiletSameSpace1");

            /////////////////////////////////////////////
            //toilet on wall[2]
            qexpr1 = new GRBQuadExpr();
            qexpr1.addTerm(1.0,toilet[1],toiletWall[2]);
            qexpr1.addTerm(1.0,toilet[3],toiletWall[2]);
            qexpr1.addTerm(-height,toiletWall[2]);
            model.addQConstr(qexpr1,GRB.EQUAL,0,"TY + TH = H ");

            qexpr1 = new GRBQuadExpr();
            qexpr1.addTerm(1.0,toiletS[1],toiletWall[2]);
            qexpr1.addTerm(1.0,toiletS[3],toiletWall[2]);
            qexpr1.addTerm(-height,toiletWall[2]);
            model.addQConstr(qexpr1,GRB.EQUAL,0,"TSY + TSH = H ");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[2]);
            expr1.addTerm(M,toiletWall[2]);
            expr1.addConstant(-400-M);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"toiletWon2");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[3]);
            expr1.addTerm(-M,toiletWall[2]);
            expr1.addConstant(-600+M);
            model.addConstr(expr1,GRB.GREATER_EQUAL,0,"toiletHon2");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toiletS[3]);
            expr1.addTerm(-1.0,toilet[3]);
            expr1.addConstant(M-500);
            expr1.addTerm(-M,toiletWall[2]);
            model.addConstr(expr1,GRB.GREATER_EQUAL,0.0,"tSBiggerThanT2");

            qexpr1 = new GRBQuadExpr();
            qexpr1.addTerm(1.0,toiletS[0],toiletWall[2]);
            qexpr1.addTerm(0.5,toiletS[2],toiletWall[2]);
            qexpr1.addTerm(-0.5,toilet[2],toiletWall[2]);
            qexpr1.addTerm(-1,toilet[0],toiletWall[2]);
            model.addQConstr(qexpr1,GRB.EQUAL,0,"toiletSameSpace2");

            /////////////////////////////////////////////
            //toilet on wall[3]
            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[0]);
            expr1.addTerm(M,toiletWall[3]);
            expr1.addConstant(-M);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"TX = 0");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toiletS[0]);
            expr1.addTerm(M,toiletWall[3]);
            expr1.addConstant(-M);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"TSX = 0");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[3]);
            expr1.addTerm(M,toiletWall[3]);
            expr1.addConstant(-400-M);
            model.addConstr(expr1,GRB.LESS_EQUAL,0,"toiletHon3");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toilet[2]);
            expr1.addTerm(-M,toiletWall[3]);
            expr1.addConstant(-600+M);
            model.addConstr(expr1,GRB.GREATER_EQUAL,0,"toiletWon3");

            expr1 = new GRBLinExpr();
            expr1.addTerm(1.0,toiletS[2]);
            expr1.addTerm(-1.0,toilet[2]);
            expr1.addConstant(M-500);
            expr1.addTerm(-M,toiletWall[3]);
            model.addConstr(expr1,GRB.GREATER_EQUAL,0.0,"tSBiggerThanT3");

            qexpr1 = new GRBQuadExpr();
            qexpr1.addTerm(1.0,toiletS[1],toiletWall[3]);
            qexpr1.addTerm(0.5,toiletS[3],toiletWall[3]);
            qexpr1.addTerm(-0.5,toilet[3],toiletWall[3]);
            qexpr1.addTerm(-1,toilet[1],toiletWall[3]);
            model.addQConstr(qexpr1,GRB.EQUAL,0,"toiletSameSpace3");

            //toiletWall
            expr1 = new GRBLinExpr();
            for(int i = 0;i<4;i++){
                expr1.addTerm(1.0,toiletWall[i]);
            }
            model.addConstr(expr1,GRB.EQUAL,1.0,"toiletWall");



            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //sink in boundary


            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[0]);
            expr2.addTerm(1.0,sink[2]);
            expr2.addConstant(-width);
            model.addConstr(expr2,GRB.LESS_EQUAL,0,"SXinBoundary");
            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sinkS[0]);
            expr2.addTerm(1.0,sinkS[2]);
            expr2.addConstant(-width);
            model.addConstr(expr2,GRB.LESS_EQUAL,0,"SSXinBoundary");
            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[1]);
            expr2.addTerm(1.0,sink[3]);
            expr2.addConstant(-height);
            model.addConstr(expr2,GRB.LESS_EQUAL,0,"SYinBoundary");
            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sinkS[1]);
            expr2.addTerm(1.0,sinkS[3]);
            expr2.addConstant(-height);
            model.addConstr(expr2,GRB.LESS_EQUAL,0,"SSYinBoundary");

            /////////////////////////////////////////////
            //sink on wall[0]
            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[1]);
            expr2.addTerm(M,sinkWall[0]);
            expr2.addConstant(-M);
            model.addConstr(expr2,GRB.LESS_EQUAL,0,"SY = 0");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sinkS[1]);
            expr2.addTerm(M,sinkWall[0]);
            expr2.addConstant(-M);
            model.addConstr(expr2,GRB.LESS_EQUAL,0,"SSY = 0");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[2]);
            expr2.addTerm(-M,sinkWall[0]);
            expr2.addConstant(-460+M);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0,"sinkWon0>=460");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sinkS[3]);
            expr2.addTerm(-1.0,sink[3]);
            expr2.addConstant(M-600);
            expr2.addTerm(-M,sinkWall[0]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0.0,"sSBiggerThanS0");

            qexpr2 = new GRBQuadExpr();
            qexpr2.addTerm(1.0,sinkS[0],sinkWall[0]);
            qexpr2.addTerm(0.5,sinkS[2],sinkWall[0]);
            qexpr2.addTerm(-0.5,sink[2],sinkWall[0]);
            qexpr2.addTerm(-1,sink[0],sinkWall[0]);
            model.addQConstr(qexpr2,GRB.EQUAL,0,"sinkSameSpace0");


            /////////////////////////////////////////////
            //sink on wall[1]
            qexpr2 = new GRBQuadExpr();
            qexpr2.addTerm(1.0,sink[0],sinkWall[1]);
            qexpr2.addTerm(1.0,sink[2],sinkWall[1]);
            qexpr2.addTerm(-width,sinkWall[1]);
            model.addQConstr(qexpr2,GRB.EQUAL,0,"SX + SW = W ");

            qexpr2 = new GRBQuadExpr();
            qexpr2.addTerm(1.0,sinkS[0],sinkWall[1]);
            qexpr2.addTerm(1.0,sinkS[2],sinkWall[1]);
            qexpr2.addTerm(-width,sinkWall[1]);
            model.addQConstr(qexpr2,GRB.EQUAL,0,"sSX + sSW = W ");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[3]);
            expr2.addTerm(-M,sinkWall[1]);
            expr2.addConstant(-460+M);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0,"sinkHon1>=460");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sinkS[2]);
            expr2.addTerm(-1.0,sink[2]);
            expr2.addConstant(M-600);
            expr2.addTerm(-M,sinkWall[1]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0.0,"sSBiggerThanS1");

            qexpr2 = new GRBQuadExpr();
            qexpr2.addTerm(1.0,sinkS[1],sinkWall[1]);
            qexpr2.addTerm(0.5,sinkS[3],sinkWall[1]);
            qexpr2.addTerm(-0.5,sink[3],sinkWall[1]);
            qexpr2.addTerm(-1,sink[1],sinkWall[1]);
            model.addQConstr(qexpr2,GRB.EQUAL,0,"sinkSameSpace1");

            /////////////////////////////////////////////
            //sink on wall[2]
            qexpr2 = new GRBQuadExpr();
            qexpr2.addTerm(1.0,sink[1],sinkWall[2]);
            qexpr2.addTerm(1.0,sink[3],sinkWall[2]);
            qexpr2.addTerm(-height,sinkWall[2]);
            model.addQConstr(qexpr2,GRB.EQUAL,0,"SY + SH = H ");

            qexpr2 = new GRBQuadExpr();
            qexpr2.addTerm(1.0,sinkS[1],sinkWall[2]);
            qexpr2.addTerm(1.0,sinkS[3],sinkWall[2]);
            qexpr2.addTerm(-height,sinkWall[2]);
            model.addQConstr(qexpr2,GRB.EQUAL,0,"SSY + SSH = H ");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[2]);
            expr2.addTerm(-M,sinkWall[2]);
            expr2.addConstant(-460+M);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0,"sinkWon2>=460");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sinkS[3]);
            expr2.addTerm(-1.0,sink[3]);
            expr2.addConstant(M-600);
            expr2.addTerm(-M,sinkWall[2]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0.0,"sSBiggerThanS2");

            qexpr2 = new GRBQuadExpr();
            qexpr2.addTerm(1.0,sinkS[0],sinkWall[2]);
            qexpr2.addTerm(0.5,sinkS[2],sinkWall[2]);
            qexpr2.addTerm(-0.5,sink[2],sinkWall[2]);
            qexpr2.addTerm(-1,sink[0],sinkWall[2]);
            model.addQConstr(qexpr2,GRB.EQUAL,0,"sinkSameSpace2");

            /////////////////////////////////////////////
            //sink on wall[3]
            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[0]);
            expr2.addTerm(M,sinkWall[3]);
            expr2.addConstant(-M);
            model.addConstr(expr2,GRB.LESS_EQUAL,0,"SX = 0");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sinkS[0]);
            expr2.addTerm(M,sinkWall[3]);
            expr2.addConstant(-M);
            model.addConstr(expr2,GRB.LESS_EQUAL,0,"SSX = 0");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[3]);
            expr2.addTerm(-M,sinkWall[3]);
            expr2.addConstant(-460+M);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0,"sinkHon3>=460");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sinkS[2]);
            expr2.addTerm(-1.0,sink[2]);
            expr2.addConstant(M-600);
            expr2.addTerm(-M,sinkWall[3]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0.0,"sSBiggerThanS3");

            qexpr2 = new GRBQuadExpr();
            qexpr2.addTerm(1.0,sinkS[1],sinkWall[3]);
            qexpr2.addTerm(0.5,sinkS[3],sinkWall[3]);
            qexpr2.addTerm(-0.5,sink[3],sinkWall[3]);
            qexpr2.addTerm(-1,sink[1],sinkWall[3]);
            model.addQConstr(qexpr2,GRB.EQUAL,0,"sinkSameSpace3");


            //sinkWall
            expr2 = new GRBLinExpr();
            for(int i = 0;i<4;i++){
                expr2.addTerm(1.0,sinkWall[i]);
            }
            model.addConstr(expr2,GRB.EQUAL,1.0,"sinkWall");





            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //shower in boundary
            GRBLinExpr expr3;
            GRBQuadExpr qexpr3;

            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,shower[0]);
            expr3.addTerm(1.0,shower[2]);
            expr3.addConstant(-width);
            model.addConstr(expr3,GRB.LESS_EQUAL,0,"ShXinBoundary");
            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,showerS[0]);
            expr3.addTerm(1.0,showerS[2]);
            expr3.addConstant(-width);
            model.addConstr(expr3,GRB.LESS_EQUAL,0,"ShSXinBoundary");
            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,shower[1]);
            expr3.addTerm(1.0,shower[3]);
            expr3.addConstant(-height);
            model.addConstr(expr3,GRB.LESS_EQUAL,0,"ShYinBoundary");
            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,showerS[1]);
            expr3.addTerm(1.0,showerS[3]);
            expr3.addConstant(-height);
            model.addConstr(expr3,GRB.LESS_EQUAL,0,"ShSYinBoundary");

            /////////////////////////////////////////////
            //shower on wall[0]
            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,shower[1]);
            expr3.addTerm(M,showerWall[0]);
            expr3.addConstant(-M);
            model.addConstr(expr3,GRB.LESS_EQUAL,0,"ShY = 0");

            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,showerS[1]);
            expr3.addTerm(M,showerWall[0]);
            expr3.addConstant(-M);
            model.addConstr(expr3,GRB.LESS_EQUAL,0,"ShSY = 0");

            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,shower[3]);
            expr3.addTerm(-M,showerWall[0]);
            expr3.addConstant(-400+M);
            model.addConstr(expr3,GRB.GREATER_EQUAL,0,"showerHon0>=400");

            qexpr3 = new GRBQuadExpr();
            qexpr3.addTerm(1.0,showerS[0],showerWall[0]);
            qexpr3.addTerm(0.5,showerS[2],showerWall[0]);
            qexpr3.addTerm(-0.5,shower[2],showerWall[0]);
            qexpr3.addTerm(-1,shower[0],showerWall[0]);
            model.addQConstr(qexpr3,GRB.EQUAL,0,"showerSameSpace0");


            /////////////////////////////////////////////
            //shower on wall[1]
            qexpr3 = new GRBQuadExpr();
            qexpr3.addTerm(1.0,shower[0],showerWall[1]);
            qexpr3.addTerm(1.0,shower[2],showerWall[1]);
            qexpr3.addTerm(-width,showerWall[1]);
            model.addQConstr(qexpr3,GRB.EQUAL,0,"ShX + ShW = W ");

            qexpr3 = new GRBQuadExpr();
            qexpr3.addTerm(1.0,showerS[0],showerWall[1]);
            qexpr3.addTerm(1.0,showerS[2],showerWall[1]);
            qexpr3.addTerm(-width,showerWall[1]);
            model.addQConstr(qexpr3,GRB.EQUAL,0,"shSX + shSW = W ");

            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,shower[2]);
            expr3.addTerm(M,showerWall[1]);
            expr3.addConstant(-400-M);
            model.addConstr(expr3,GRB.GREATER_EQUAL,0,"showerWon0>=400");

            qexpr3 = new GRBQuadExpr();
            qexpr3.addTerm(1.0,showerS[1],showerWall[1]);
            qexpr3.addTerm(0.5,showerS[3],showerWall[1]);
            qexpr3.addTerm(-0.5,shower[3],showerWall[1]);
            qexpr3.addTerm(-1,shower[1],showerWall[1]);
            model.addQConstr(qexpr3,GRB.EQUAL,0,"showerSameSpace1");

            /////////////////////////////////////////////
            //shower on wall[2]
            qexpr3 = new GRBQuadExpr();
            qexpr3.addTerm(1.0,shower[1],showerWall[2]);
            qexpr3.addTerm(1.0,shower[3],showerWall[2]);
            qexpr3.addTerm(-height,showerWall[2]);
            model.addQConstr(qexpr3,GRB.EQUAL,0,"ShY + ShH = H ");

            qexpr3 = new GRBQuadExpr();
            qexpr3.addTerm(1.0,showerS[1],showerWall[2]);
            qexpr3.addTerm(1.0,showerS[3],showerWall[2]);
            qexpr3.addTerm(-height,showerWall[2]);
            model.addQConstr(qexpr3,GRB.EQUAL,0,"ShSY + ShSH = H ");

            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,shower[3]);
            expr3.addTerm(-M,showerWall[2]);
            expr3.addConstant(-400+M);
            model.addConstr(expr3,GRB.GREATER_EQUAL,0,"showerHon0>=400");


            qexpr3 = new GRBQuadExpr();
            qexpr3.addTerm(1.0,showerS[0],showerWall[2]);
            qexpr3.addTerm(0.5,showerS[2],showerWall[2]);
            qexpr3.addTerm(-0.5,shower[2],showerWall[2]);
            qexpr3.addTerm(-1,shower[0],showerWall[2]);
            model.addQConstr(qexpr3,GRB.EQUAL,0,"showerSameSpace2");

            /////////////////////////////////////////////
            //shower on wall[3]
            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,shower[0]);
            expr3.addTerm(M,showerWall[3]);
            expr3.addConstant(-M);
            model.addConstr(expr3,GRB.LESS_EQUAL,0,"ShX = 0");

            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,showerS[0]);
            expr3.addTerm(M,showerWall[3]);
            expr3.addConstant(-M);
            model.addConstr(expr3,GRB.LESS_EQUAL,0,"ShSX = 0");

            expr3 = new GRBLinExpr();
            expr3.addTerm(1.0,shower[2]);
            expr3.addTerm(-M,showerWall[3]);
            expr3.addConstant(-400+M);
            model.addConstr(expr3,GRB.GREATER_EQUAL,0,"showerWon0>=400");

            qexpr3 = new GRBQuadExpr();
            qexpr3.addTerm(1.0,showerS[1],showerWall[3]);
            qexpr3.addTerm(0.5,showerS[3],showerWall[3]);
            qexpr3.addTerm(-0.5,shower[3],showerWall[3]);
            qexpr3.addTerm(-1,shower[1],showerWall[3]);
            model.addQConstr(qexpr3,GRB.EQUAL,0,"showerSameSpace3");


            //showerWall
            expr3 = new GRBLinExpr();
            for(int i = 0;i<4;i++){
                expr3.addTerm(1.0,showerWall[i]);
            }
            model.addConstr(expr3,GRB.EQUAL,1.0,"showerWall");


            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //non-overlapping
            //toiletS - sink
            GRBLinExpr expr = new GRBLinExpr();

            expr.addTerm(1.0,toiletS[0]);
            expr.addTerm(-1.0,sink[2]);
            expr.addTerm(-1.0,sink[0]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingRight[0][1]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping1 01");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,toiletS[0]);
            expr.addTerm(1.0,toiletS[2]);
            expr.addTerm(-1.0,sink[0]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingLeft[0][1]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping2 01" );

            expr = new GRBLinExpr();
            expr.addTerm(1.0,toiletS[1]);
            expr.addTerm(-1.0,sink[3]);
            expr.addTerm(-1.0,sink[1]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingTop[0][1]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping3 01");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,toiletS[1]);
            expr.addTerm(1.0,toiletS[3]);
            expr.addTerm(-1.0,sink[1]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingBottom[0][1]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping4 01");

            //at least one direction
            expr = new GRBLinExpr();
            expr.addTerm(1.0,overlappingRight[0][1]);
            expr.addTerm(1.0,overlappingLeft[0][1]);
            expr.addTerm(1.0,overlappingTop[0][1]);
            expr.addTerm(1.0,overlappingBottom[0][1]);
            model.addConstr(expr,GRB.GREATER_EQUAL,1.0,"direction 01");


            ///////////////////////////
            //toiletS - shower
            expr = new GRBLinExpr();

            expr.addTerm(1.0,toiletS[0]);
            expr.addTerm(-1.0,shower[2]);
            expr.addTerm(-1.0,shower[0]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingRight[0][2]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping1 02");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,toiletS[0]);
            expr.addTerm(1.0,toiletS[2]);
            expr.addTerm(-1.0,shower[0]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingLeft[0][2]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping2 02");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,toiletS[1]);
            expr.addTerm(-1.0,shower[3]);
            expr.addTerm(-1.0,shower[1]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingTop[0][2]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping3 02");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,toiletS[1]);
            expr.addTerm(1.0,toiletS[3]);
            expr.addTerm(-1.0,shower[1]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingBottom[0][2]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping4 02");

            //at least one direction
            expr = new GRBLinExpr();
            expr.addTerm(1.0,overlappingRight[0][2]);
            expr.addTerm(1.0,overlappingLeft[0][2]);
            expr.addTerm(1.0,overlappingTop[0][2]);
            expr.addTerm(1.0,overlappingBottom[0][2]);
            model.addConstr(expr,GRB.GREATER_EQUAL,1.0,"direction 02");

            ///////////////////////////
            //sinkS - shower
            expr = new GRBLinExpr();

            expr.addTerm(1.0,sinkS[0]);
            expr.addTerm(-1.0,shower[2]);
            expr.addTerm(-1.0,shower[0]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingRight[1][2]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping1 12");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,sinkS[0]);
            expr.addTerm(1.0,sinkS[2]);
            expr.addTerm(-1.0,shower[0]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingLeft[1][2]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping2 12");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,sinkS[1]);
            expr.addTerm(-1.0,shower[3]);
            expr.addTerm(-1.0,shower[1]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingTop[1][2]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping3 12");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,sinkS[1]);
            expr.addTerm(1.0,sinkS[3]);
            expr.addTerm(-1.0,shower[1]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingBottom[1][2]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping4 12");

            //at least one direction
            expr = new GRBLinExpr();
            expr.addTerm(1.0,overlappingRight[1][2]);
            expr.addTerm(1.0,overlappingLeft[1][2]);
            expr.addTerm(1.0,overlappingTop[1][2]);
            expr.addTerm(1.0,overlappingBottom[1][2]);
            model.addConstr(expr,GRB.GREATER_EQUAL,1.0,"direction 12");



            ///////////////////////////
            //sinkS - toilet
            expr = new GRBLinExpr();

            expr.addTerm(1.0,sinkS[0]);
            expr.addTerm(-1.0,toilet[2]);
            expr.addTerm(-1.0,toilet[0]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingRight[1][1]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping1 11");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,sinkS[0]);
            expr.addTerm(1.0,sinkS[2]);
            expr.addTerm(-1.0,toilet[0]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingLeft[1][1]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping2 11");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,sinkS[1]);
            expr.addTerm(-1.0,toilet[3]);
            expr.addTerm(-1.0,toilet[1]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingTop[1][1]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping3 11");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,sinkS[1]);
            expr.addTerm(1.0,sinkS[3]);
            expr.addTerm(-1.0,toilet[1]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingBottom[1][1]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping4 11");

            //at least one direction
            expr = new GRBLinExpr();
            expr.addTerm(1.0,overlappingRight[1][1]);
            expr.addTerm(1.0,overlappingLeft[1][1]);
            expr.addTerm(1.0,overlappingTop[1][1]);
            expr.addTerm(1.0,overlappingBottom[1][1]);
            model.addConstr(expr,GRB.GREATER_EQUAL,1.0,"direction 11");


            ///////////////////////////
            //showerS - toilet
            expr = new GRBLinExpr();

            expr.addTerm(1.0,showerS[0]);
            expr.addTerm(-1.0,toilet[2]);
            expr.addTerm(-1.0,toilet[0]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingRight[2][1]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping1 21");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,showerS[0]);
            expr.addTerm(1.0,showerS[2]);
            expr.addTerm(-1.0,toilet[0]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingLeft[2][1]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping2 21");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,showerS[1]);
            expr.addTerm(-1.0,toilet[3]);
            expr.addTerm(-1.0,toilet[1]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingTop[2][1]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping3 21");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,showerS[1]);
            expr.addTerm(1.0,showerS[3]);
            expr.addTerm(-1.0,toilet[1]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingBottom[2][1]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping4 21");

            //at least one direction
            expr = new GRBLinExpr();
            expr.addTerm(1.0,overlappingRight[2][1]);
            expr.addTerm(1.0,overlappingLeft[2][1]);
            expr.addTerm(1.0,overlappingTop[2][1]);
            expr.addTerm(1.0,overlappingBottom[2][1]);
            model.addConstr(expr,GRB.GREATER_EQUAL,1.0,"direction 21");

            ///////////////////////////
            //showerS - sink
            expr = new GRBLinExpr();

            expr.addTerm(1.0,showerS[0]);
            expr.addTerm(-1.0,sink[2]);
            expr.addTerm(-1.0,sink[0]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingRight[2][2]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping1 22");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,showerS[0]);
            expr.addTerm(1.0,showerS[2]);
            expr.addTerm(-1.0,sink[0]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingLeft[2][2]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping2 22");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,showerS[1]);
            expr.addTerm(-1.0,sink[3]);
            expr.addTerm(-1.0,sink[1]);
            expr.addConstant(M);
            expr.addTerm(-M,overlappingTop[2][2]);
            model.addConstr(expr,GRB.GREATER_EQUAL,0.0,"nonOverlapping3 22");

            expr = new GRBLinExpr();
            expr.addTerm(1.0,showerS[1]);
            expr.addTerm(1.0,showerS[3]);
            expr.addTerm(-1.0,sink[1]);
            expr.addConstant(-M);
            expr.addTerm(M,overlappingBottom[2][2]);
            model.addConstr(expr,GRB.LESS_EQUAL,0.0,"nonOverlapping4 22");

            //at least one direction
            expr = new GRBLinExpr();
            expr.addTerm(1.0,overlappingRight[2][2]);
            expr.addTerm(1.0,overlappingLeft[2][2]);
            expr.addTerm(1.0,overlappingTop[2][2]);
            expr.addTerm(1.0,overlappingBottom[2][2]);
            model.addConstr(expr,GRB.GREATER_EQUAL,1.0,"direction 22");



            ////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //avoid door
            expr2= new GRBLinExpr();
            expr2.addTerm(1.0,toilet[0]);
            expr2.addConstant(-doorW);
            expr2.addConstant(-doorX);
            expr2.addConstant(M);
            expr2.addTerm(-M,doorRight[0]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0.0,"avoidRect1");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,toilet[0]);
            expr2.addTerm(1.0,toilet[2]);
            expr2.addConstant(-doorX);
            expr2.addConstant(-M);
            expr2.addTerm(M,doorLeft[0]);
            model.addConstr(expr2,GRB.LESS_EQUAL,0.0,"avoidRect2");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,toilet[1]);
            expr2.addConstant(-doorH);
            expr2.addConstant(-doorY);
            expr2.addConstant(M);
            expr2.addTerm(-M,doorTop[0]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0.0,"avoidRect3");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,toilet[1]);
            expr2.addTerm(1.0,toilet[3]);
            expr2.addConstant(-doorY);
            expr2.addConstant(-M);
            expr2.addTerm(M,doorBottom[0]);
            model.addConstr(expr2,GRB.LESS_EQUAL,0.0,"avoidRect4");

            //at least one direction
            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,doorRight[0]);
            expr2.addTerm(1.0,doorLeft[0]);
            expr2.addTerm(1.0,doorTop[0]);
            expr2.addTerm(1.0,doorBottom[0]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,1.0,"direction");

            ///////////////////////////////////
            expr2= new GRBLinExpr();
            expr2.addTerm(1.0,sink[0]);
            expr2.addConstant(-doorW);
            expr2.addConstant(-doorX);
            expr2.addConstant(M);
            expr2.addTerm(-M,doorRight[1]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0.0,"avoidRect11");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[0]);
            expr2.addTerm(1.0,sink[2]);
            expr2.addConstant(-doorX);
            expr2.addConstant(-M);
            expr2.addTerm(M,doorLeft[1]);
            model.addConstr(expr2,GRB.LESS_EQUAL,0.0,"avoidRect21");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[1]);
            expr2.addConstant(-doorH);
            expr2.addConstant(-doorY);
            expr2.addConstant(M);
            expr2.addTerm(-M,doorTop[1]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0.0,"avoidRect31");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,sink[1]);
            expr2.addTerm(1.0,sink[3]);
            expr2.addConstant(-doorY);
            expr2.addConstant(-M);
            expr2.addTerm(M,doorBottom[1]);
            model.addConstr(expr2,GRB.LESS_EQUAL,0.0,"avoidRect41");

            //at least one direction
            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,doorRight[1]);
            expr2.addTerm(1.0,doorLeft[1]);
            expr2.addTerm(1.0,doorTop[1]);
            expr2.addTerm(1.0,doorBottom[1]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,1.0,"direction1");

            ///////////////////////////////////
            expr2= new GRBLinExpr();
            expr2.addTerm(1.0,shower[0]);
            expr2.addConstant(-doorW);
            expr2.addConstant(-doorX);
            expr2.addConstant(M);
            expr2.addTerm(-M,doorRight[2]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0.0,"avoidRect12");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,shower[0]);
            expr2.addTerm(1.0,shower[2]);
            expr2.addConstant(-doorX);
            expr2.addConstant(-M);
            expr2.addTerm(M,doorLeft[2]);
            model.addConstr(expr2,GRB.LESS_EQUAL,0.0,"avoidRect22");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,shower[1]);
            expr2.addConstant(-doorH);
            expr2.addConstant(-doorY);
            expr2.addConstant(M);
            expr2.addTerm(-M,doorTop[2]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,0.0,"avoidRect32");

            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,shower[1]);
            expr2.addTerm(1.0,shower[3]);
            expr2.addConstant(-doorY);
            expr2.addConstant(-M);
            expr2.addTerm(M,doorBottom[2]);
            model.addConstr(expr2,GRB.LESS_EQUAL,0.0,"avoidRect42");

            //at least one direction
            expr2 = new GRBLinExpr();
            expr2.addTerm(1.0,doorRight[2]);
            expr2.addTerm(1.0,doorLeft[2]);
            expr2.addTerm(1.0,doorTop[2]);
            expr2.addTerm(1.0,doorBottom[2]);
            model.addConstr(expr2,GRB.GREATER_EQUAL,1.0,"direction2");


            model.optimize();

            for(int i = 0;i<4;i++){
                toiletResult[i] = toilet[i].get(GRB.DoubleAttr.X);
                toiletSResult[i] = toiletS[i].get(GRB.DoubleAttr.X);
                sinkResult[i] = sink[i].get(GRB.DoubleAttr.X);
                sinkSResult[i] = sinkS[i].get(GRB.DoubleAttr.X);
                showerResult[i] = shower[i].get(GRB.DoubleAttr.X);
                showerSResult[i] = showerS[i].get(GRB.DoubleAttr.X);
            }

            model.dispose();
            env.dispose();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void draw(){
        background(255);
        noStroke();




        fill(150,100,100,50);
        rect((float)toiletSResult[0],(float)toiletSResult[1],(float)toiletSResult[2],(float)toiletSResult[3]);
        fill(100,150,100,50);
        rect((float)sinkSResult[0],(float)sinkSResult[1],(float)sinkSResult[2],(float)sinkSResult[3]);
        fill(100,100,150,100);
        rect((float)showerSResult[0],(float)showerSResult[1],(float)showerSResult[2],(float)showerSResult[3]);

        fill(255,100,100);
        rect((float)toiletResult[0],(float)toiletResult[1],(float)toiletResult[2],(float)toiletResult[3]);
        fill(100,255,100);
        rect((float)sinkResult[0],(float)sinkResult[1],(float)sinkResult[2],(float)sinkResult[3]);
//        fill(100,100,255);
//        rect((float)showerResult[0],(float)showerResult[1],(float)showerResult[2],(float)showerResult[3]);

        fill(0,150);
        arc(doorX,doorY+30,2*doorW,2*doorH,0,HALF_PI);

        fill(0,0,255);
        rect(flowPipeX,flowPipeY,30,30);

        fill(255,50,50);
        stroke(0);
        strokeWeight(5);
        ellipse(drainPipeX,drainPipeY,100,100);

        fill(0);
        textSize(40);
        text("TOILE  "+(int)toiletResult[2]+"*"+(int)toiletResult[3],(float)toiletResult[0],(float)toiletResult[1]+50);
        text("SINK  "+(int)sinkResult[2]+"*"+(int)sinkResult[3],(float)sinkResult[0],(float)sinkResult[1]+50);
        text("SHOWER  "+(int)showerSResult[2]+"*"+(int)showerSResult[3],(float)showerSResult[0],(float)showerSResult[1]+50);


    }



}
