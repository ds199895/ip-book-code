package IO;

import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
//import org.json.simple.JSONArray;

import java.io.File;

import java.io.FileOutputStream;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
//import com.alibaba.fastjson.*;
//import org.json.simple.JSONObject;

public class Output {

    public static void main(String[] args) throws Exception {
        String content = "This is the text content";
        Output("c:/newfile.txt",content,true);
    }

    //�����ֽ��������ı��ļ�
    public static void Output(String fileName,String content,boolean add) throws Exception{
         FileWriter out = new FileWriter(fileName, add);
        //���ļ�д��
        out.write(content);
        //ˢ��IO�ڴ���
        out.flush();
        //�ر�
        out.close();
    }
    public void sendInfo(int port){
//        JSONArray jsonArray = new JSONArray();
//        for(int i=0;i<templates.get(1).size();i++){
//            Point[] temp=templates.get(1).get(i);
//            ArrayList<Box> boxes=new ArrayList<>();
//            for(Point p:temp){
//                if(p.x==0&&p.y==0&&p.z==0){
//                    JSONObject jsonObject = new JSONObject();
//                    jsonObject.put("x",p.x);
//                    jsonObject.put("y",p.x);
//                    jsonObject.put("z",p.x);
//                    jsonObject.put("block","block"+str(i));
//                    jsonObject.put("template","solo");
//
//                    jsonArray.add(jsonObject);
//                }
//            }
//        }
//
//        String jsonOutput = jsonArray.toJSONString();
    }
//    public static void Output(String fileName,boolean add) throws IOException {
//        //�˴�����Ϊtrue����׷��
//        String content="This is the text content";
//        FileWriter out = new FileWriter(fileName, add);
//        out.write(content);
//    }
}