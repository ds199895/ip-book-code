package RectanglePacking;

import processing.core.PApplet;
import processing.core.PConstants;

public class Rect {
    float x,y,w, d;

    public Rect(float x,float y,float w,float d){
        this.x=x;
        this.y=y;
        this.w=w;
        this.d=d;
    }

    public void draw(PApplet app){
        app.rectMode(PConstants.CORNER);
        app.rect(this.x,this.y,this.w,this.d);
    }
}
