package edu.stevens;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Rect extends Shape {
	private String LastX, LastY, strokeWidth, lineColor;
	public Rect (String x,String y,String LastX, String LastY,String color,String lineColor, String strokeWidth){
		super(x,y,color);
		this.LastX = LastX;
		this.LastY = LastY;
		this.strokeWidth = strokeWidth;
		this.lineColor = lineColor;
	}
	
	public void paint() throws Exception {
		try {
	         BufferedWriter out = new BufferedWriter(new FileWriter
	         ("printfile.ps",true));
	         out.write("newpath\n"+
	        		   x + " " + y +" moveto %put the start point \n" +
	        		   LastX + " " + y +" rlineto %construct the line\n" +
	        		   LastX + " " + LastY + " rlineto %construct the line\n" +
	        		   x + " " + LastY + " rlineto %construct the line\n" +
	        		   "closepath \n"+
	        		   color + "  setrgbcolor %set the color of the rect \n"+
	        		   "fill\n" +
	        		   lineColor + "  setrgbcolor %set the color of the rect \n"+
	        		   strokeWidth + " setlinewidth %set the line's strokewide\n" +
	        		   "stroke \n");
	         out.close();
	      }
	      catch (IOException e) {
	         System.out.println("exception occoured"+ e);
	      }
	}
}
