import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import java.util.*; 
import java.text.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Interfaz extends PApplet {

  



Serial puerto;
int R=255;
int G=0;
int B=0;

int R1=255;
int G1=0;
int B1=0;

int R2=255;
int G2=0;
int B2=0;

int R3=255;
int G3=0;
int B3=0;

int R4=255;
int G4=0;
int B4=0;  


int start=0;
int start1=0;
int start2=0;

String inBuffer="";
String modalidad="";
String reaccion="0:0:0:0";
String disparo1="0:0:0:0";
String disparo2="0:0:0:0";
String lanzados="";
String realizados="";
String acertados="";
String agregarReaccion="";
String agregarDisparo1="";
String agregarDisparo2="";




boolean mod=false;
boolean react=false;
boolean d1=false;
boolean d2=false;
boolean borrar=true;
boolean lan=false;
boolean real=false;
boolean acer=false;
boolean agregarR=false;
boolean agregarD1=false;
boolean agregarD2=false;
boolean disp1=false;
boolean disp2=false;
boolean coneccion=false;

PrintWriter archivo;
DateFormat fecha= new SimpleDateFormat("yyMMdd_HHmm");
DateFormat hora=new SimpleDateFormat("hh:mm:ss");
String nombreArchivo;

public void setup()
{
  String portName =  Serial.list()[0];
  puerto = new Serial(this, portName, 9600);
  
  Date now= new Date();
  nombreArchivo = fecha.format(now);
  archivo = createWriter(nombreArchivo + ".csv");
  archivo.print("Tiempo de Reaccion"+","+"Tiempo de Disparo 1"+","+"Tiempo de Disparo2");
  archivo.print("\n");
}

public void draw()
{  
  
  background(27, 126, 122); //Color de Fondo Verde Turquesa
  fill(255, 255, 255); //Color de las letras
  textSize(12);
  textAlign(CENTER, TOP); // alineaci\u00f3n del texto

  fill(R, G, B);
  rect(50, 50, 80, 40, 5);
  fill(255, 255, 255);
  text("SKEET", 90, 95);

  fill(R1, G1, B1);
  rect(200, 50, 80, 40, 5);
  fill(255, 255, 255);
  text("FOSA", 240, 95);

  fill(R2, G2, B2);
  rect(350, 50, 80, 40, 5);
  fill(255, 255, 255);
  text("ACIERTO", 390, 95);
  

  if (millis()-start>1000)
  {
    R2=255; 
    G2=0; 
    B2=0;
  }  

  fill(R3, G3, B3);
  rect(500, 50, 80, 40, 5);
  fill(255, 255, 255);
  text("RESULTADOS", 540, 95);

  fill(R4,G4,B4);
  rect(650,50,80,40,5);
  fill(255,255,255);
  text("CONECTAR",690,95);
  
  if(coneccion==false)
  {
    fill(255,0,0);
    rect(800,50,80,40,5);
  }
  if(coneccion==true)
  {
    fill(0,255,0);
    rect(800,50,80,40,5);
  }
  

  if (millis()-start1>1000)
  {
    R3=255; 
    G3=0; 
    B3=0;
  }  

  if(millis()-start2>1000)
  {
    R4=255;
    G4=0;
    B4=0;
  }
  fill(224, 224, 224);
  rect(75, 150, 125, 50, 5);
  fill(0, 0, 0);
  text("MODALIDAD", 140, 200);
  
  fill(224, 224, 224);
  rect(250, 150, 150, 50, 5);
  fill(0, 0, 0);
  text("TIEMPO DE REACCION", 325, 200);
  
  fill(224, 224, 224);
  rect(450, 150, 150, 50, 5);
  fill(0, 0, 0);
  text("TIEMPO DE DISPARO 1", 525, 200);
  
  fill(224, 224, 224);
  rect(650, 150, 150, 50, 5);
  fill(0, 0, 0);
  text("TIEMPO DE DISPARO 2", 725, 200);
  
  fill(224, 224, 224);
  rect(250, 300, 100, 50, 5);
  fill(0, 0, 0);
  text("DISCOS LANZADOS", 175, 310);
  
  fill(224, 224, 224);
  rect(250, 400, 100, 50, 5);
  fill(0, 0, 0);
  text("DISCOS ACERTADOS", 175, 410);
  
  fill(224, 224, 224);
  rect(250, 500, 100, 50, 5);
  fill(0, 0, 0);
  text("DISPAROS REALIZADOS", 175, 510);
  
  if (puerto.available()>0)
  {
    inBuffer=puerto.readStringUntil('\n');
    if(inBuffer!=null)
    {
      if(inBuffer.contains("F")|| inBuffer.contains("K"))
      {
        modalidad=inBuffer;
        mod=true;
      }
      if(inBuffer.contains("R"))
      {
        borrar=false;
        reaccion=inBuffer;
        reaccion=reaccion.replace('R',' ');
        react=true;
        agregarR=true;
       }
      if(inBuffer.contains("U"))
      {
        disparo1=inBuffer;
        disparo1=inBuffer.replace('U',' ');
        d1=true;
        agregarD1=true;
        
      }
      if(inBuffer.contains("X"))
      {
        disparo2=inBuffer;
        disparo2=inBuffer.replace('X',' ');
        d2=true;
        agregarD2=true;
      }
      if(inBuffer.contains("A"))
      {
        borrar=true;
      }
      if(inBuffer.contains("L"))
      {
        lanzados=inBuffer;
        lanzados=inBuffer.replace('L',' ');
        lan=true;
      }
      if(inBuffer.contains("W"))
      {
        acertados=inBuffer;
        acertados=acertados.replace('W',' ');
        acer=true;
      }
      if(inBuffer.contains("Z"))
      {
        realizados=inBuffer;
        realizados=realizados.replace('Z',' ');
        real=true;
      }
      if(inBuffer.contains("M"))
      {
        coneccion=true;      
      }
    }
  }
  
  if(borrar==true)
  {
    reaccion="0:0:0:0";
    disparo1="0:0:0:0";
    disparo2="0:0:0:0";
    fill(0,0,0);
    textSize(18);
    text(reaccion,325,165);
    text(disparo1,525,165);
    text(disparo2,725,165);   
  }

  if(mod==true)
  {
    fill(0,0,0);
    textSize(18);
    text(modalidad,130,165);
  }
  
  if(react==true)
  {
    fill(0,0,0);
    textSize(18);
    text(reaccion,325,165);

  }
  
  if(d1==true)
  {
    fill(0,0,0);
    textSize(18);
    text(disparo1,525,165);  
  }
  
  if(d2==true)
  {
    fill(0,0,0);
    textSize(18);
    text(disparo2,725,165); 
  }
  
  if(lan==true)
  {
    fill(0,0,0);
    textSize(18);
    text(lanzados,275,315);   
  }
  
  if(acer==true)
  {
    fill(0,0,0);
    textSize(18);
    text(acertados,275,415);   
  }
  
  if(real==true)
  {
    fill(0,0,0);
    textSize(18);
    text(realizados,275,515);   
  }
  if((agregarR==true & agregarD1==true))
  {
    
    agregarReaccion=trim(reaccion);
    agregarDisparo1=trim(disparo1);
    archivo.print(agregarReaccion + "," + agregarDisparo1);
    archivo.print("\n");
    agregarR=false;
    agregarD1=false;
  }
  if((agregarR==true & agregarD2==true))
  {
    agregarReaccion=trim(reaccion);
    agregarDisparo1=trim(disparo1);
    agregarDisparo2=trim(disparo2);
    archivo.print(agregarReaccion+","+agregarDisparo1+","+agregarDisparo2);
    archivo.print("\n");
    agregarR=false;
    agregarD1=false;
    agregarD2=false;
  }

}

public void mousePressed()
{
  if ((mouseX>50 & mouseX<130)&(mouseY>50 & mouseY<90))
  {
    R=0; 
    G=255; 
    B=0;
    R1=255; 
    G1=0;
    B1=0;
    puerto.write('S');
  }

  if ((mouseX>200 & mouseX<280)&(mouseY>50 & mouseY<90))
  {
    R=255; 
    G=0; 
    B=0;
    R1=0; 
    G1=255;
    B1=0;
    puerto.write('F');
  }

  if ((mouseX>350 & mouseX<430)&(mouseY>50 & mouseY<90))
  {
    start=millis();
    R2=0; 
    G2=255;
    B2=0;
    puerto.write('A');
  }

  if ((mouseX>500 & mouseX<580)&(mouseY>50 & mouseY<90))
  {
    start1=millis();
    R3=0; 
    G3=255;
    B3=0;
    puerto.write('R');
  }
  
  if((mouseX>650 & mouseX<730)& (mouseY>50 & mouseY<90))
  {
    start2=millis();
    R4=0;
    G4=255;
    B4=0;
    puerto.write('Z');
  }
}

public void keyPressed()
{
  archivo.flush();
  archivo.close();
  exit();
}
  public void settings() {  size (1000, 700); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Interfaz" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
