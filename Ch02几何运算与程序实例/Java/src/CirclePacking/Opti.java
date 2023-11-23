package CirclePacking;

import IO.Output;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import gurobi.*;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
import processing.core.PApplet;

import java.util.ArrayList;

public class Opti extends GRBCallback {
    int num=10;
    double r_min=10;
    int w=1000;
    int h=800;
    private GRBVar []xs=new GRBVar[num];
    private GRBVar []ys=new GRBVar[num];
    private GRBVar []rs=new GRBVar[num];
    PApplet app=new PApplet();
    ArrayList<GRBVar[]>vars=new ArrayList<>();
    double[]xd=null;
    double[]yd=null;
    double[]rd=null;
    public ArrayList<double[][]>catches=new ArrayList<>();
    public double[][]solution=new double[3][num];
    public Opti(GRBVar[]vxs,GRBVar[]vys,GRBVar[]vrs){
//        this.app=app;
        xs=vxs;
        ys=vys;
        rs=vrs;
    }
    void setGRB() throws GRBException {

        //创建环境,创建一个空的模型
        GRBEnv env = new GRBEnv("E://Circle.log");//////////////
        GRBModel model = new GRBModel(env);
        model.set(GRB.IntParam.NonConvex,2);
        model.set(GRB.DoubleParam.TimeLimit,15);
        // 添加变量，圆心坐标,圆的半径（下界，上界，线性目标函数，变量类型，变量名称）
//        GRBVar []xs=new GRBVar[num];
//        GRBVar []ys=new GRBVar[num];
//        GRBVar []rs=new GRBVar[num];

        // Set objective,设定优化目标
        GRBQuadExpr obj = new GRBQuadExpr();
        for (int i=0; i<num; i++) {
            xs[i] = model.addVar(0, w, 0, GRB.CONTINUOUS, "x");
            ys[i] = model.addVar(0, h, 0, GRB.CONTINUOUS, "y");
            rs[i]=model.addVar(r_min, h, 0, GRB.CONTINUOUS, "r");
            obj.addConstant(w * h);
            obj.addTerm(-1*app.PI,rs[i],rs[i]);
        }
        model.setObjective(obj,GRB.MINIMIZE);



        for (int i=0; i<num; i++) {
            // 向模型添加线性约束条件,Add constraint: x_i-r_i≥minX
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1,xs[i]);
            expr.addTerm(-1,rs[i]);
            model.addConstr(expr, GRB.GREATER_EQUAL, 0, "c1");

            // 向模型添加线性约束条件,Add constraint: x_i+r_i≤maxX
            GRBLinExpr expr2 = new GRBLinExpr();
            expr2.addTerm(1,xs[i]);
            expr2.addTerm(1,rs[i]);
            model.addConstr(expr2, GRB.LESS_EQUAL, w, "c2");

            // 向模型添加线性约束条件,Add constraint: y_i-r_i≥minX
            GRBLinExpr expr3 = new GRBLinExpr();
            expr3.addTerm(1,ys[i]);
            expr3.addTerm(-1,rs[i]);
            model.addConstr(expr3, GRB.GREATER_EQUAL, 0, "c3");

            // 向模型添加线性约束条件,Add constraint: y_i+r_i≤maxY
            GRBLinExpr expr4 = new GRBLinExpr();
            expr4.addTerm(1,ys[i]);
            expr4.addTerm(1,rs[i]);
            model.addConstr(expr4, GRB.LESS_EQUAL, h, "c4");
        }

        //任意两个圆不相交
        for (int i=0; i<num; i++) {
            for (int j=i+1; j<num; j++) {
                GRBQuadExpr obj1 = new GRBQuadExpr();
                obj1.addTerm(1, xs[i], xs[i]);
                obj1.addTerm(-2, xs[i], xs[j]);
                obj1.addTerm(1, xs[j], xs[j]);
                obj1.addTerm(1, ys[i], ys[i]);
                obj1.addTerm(-2, ys[i],ys[j]);
                obj1.addTerm(1, ys[j], ys[j]);
                obj1.addTerm(-1,rs[i],rs[i]);
                obj1.addTerm(-2,rs[i],rs[j]);
                obj1.addTerm(-1,rs[j],rs[j]);
                model.addQConstr(obj1,GRB.GREATER_EQUAL,0,"o1");
            }
        }

        model.setCallback(new Opti(xs,ys,rs));
        //优化模型，求解
        model.optimize();



        for (int i=0; i<num; i++) {
            solution[0][i]=xs[i].get(GRB.DoubleAttr.X);
            solution[1][i]=ys[i].get(GRB.DoubleAttr.X);
            solution[2][i]=rs[i].get(GRB.DoubleAttr.X);

        }

        //清理数据
        model.dispose();
        env.dispose();
        System.out.println(catches.size());
    }
    @Override
    protected void callback() {
        try {
            if (where == GRB.CB_MIPSOL) {
                // Found an integer feasible solution - does it visit every node?
                xd=getSolution(xs);
                yd=getSolution(ys);
                rd=getSolution(rs);
                System.out.println("call back"+xd[0]);
//                app.background(0);
//                app.pushStyle();
//                app.fill(255);
                String s="callback\n";
                double[][]temp=new double[3][xd.length];
                temp[0]=xd;
                temp[1]=yd;
                temp[2]=rd;
                System.out.println(temp[0][0]);
                catches.add(temp);
                for (int i=0; i<num; i++) {
                    s+="circle\n"+(float)xd[i]+","+(float)yd[i]+","+(float)rd[i]*2+"\n";
                    System.out.println((float)xd[i]+","+(float)yd[i]+","+(float)rd[i]*2+","+(float)rd[i]*2);
//                    app.ellipse((float)xd[i],(float)yd[i],(float)rd[i]*2,(float)rd[i]*2);
                }
                Output.Output("E://circles.xml",s,true);

//                app.popStyle();
            }
        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " +
                    e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
