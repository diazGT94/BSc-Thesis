/*
*Interfaz desarrollada para pruebas realizadas con el prototipo 4 del Megaproyecto Ciencia Aplicada Al Deporte
*Permite el despliegue de tiempo de reacción, tiempo de disparo1 y tiempo de disparo2 en la modalidad Fosa y Skeet.
*@author Alejandro Díaz
*Universidad del valle de Guatemala
*/


import processing.serial.*;  //Librería que permite la comunicación Serial con Arduino
import java.util.*; //Librería para el entorno gráifco
import java.text.*; //Libreria para generer el archivo .csv

Serial puerto;
//Variables para el color de los botones.
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

//Variables para desplegar los tiempos en la interfaz
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
boolean agregarRD1=false;
boolean agregarRD2=false;

PrintWriter archivo;
DateFormat fecha= new SimpleDateFormat("yyMMdd_HHmm");
DateFormat hora=new SimpleDateFormat("hh:mm:ss");
String nombreArchivo;

void setup()
{
  String portName =  Serial.list()[0];
  puerto = new Serial(this, portName, 9600);
  size (1000, 700);
  //Se crea el archivo .csv con cada una de sus columnas.
  Date now= new Date();
  nombreArchivo = fecha.format(now);
  archivo = createWriter(nombreArchivo + ".csv");
  archivo.print("Tiempo de Reaccion"+","+"Tiempo de Disparo 1"+","+"Tiempo de Disparo2");
  archivo.print("\n");
}

void draw()
{  
  
  background(27, 126, 122); //Color de Fondo Verde Turquesa
  fill(255, 255, 255); //Color de las letras
  textSize(12); //Tamaño de la letra
  textAlign(CENTER, TOP); // alineación del texto

 //Boton para activar modalidad Skeet
  fill(R, G, B);
  rect(50, 50, 80, 40, 5);
  fill(255, 255, 255);
  text("SKEET", 90, 95);

 //Boton para activar la modalidad Fosa
  fill(R1, G1, B1);
  rect(200, 50, 80, 40, 5);
  fill(255, 255, 255);
  text("FOSA", 240, 95);

 //Boton para indicar si hubo un acierto en el disco.
  fill(R2, G2, B2);
  rect(350, 50, 80, 40, 5);
  fill(255, 255, 255);
  text("ACIERTO", 390, 95);
  
  //Condicion para que el boton de acierto regrese a color rojo un instante de tiempo despues de ser presionado
  if (millis()-start>1000)
  {
    R2=255; 
    G2=0; 
    B2=0;
  }  

   //Boton para desplegar los resultados
  fill(R3, G3, B3);
  rect(500, 50, 80, 40, 5);
  fill(255, 255, 255);
  text("RESULTADOS", 540, 95);

//Boton para conectar ambos Arduino.
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
  
  //Textos indicadores
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
  
  //Verifica que exista un dato en el puerto serial
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
      if(inBuffer.contains("R")) //Si el dato contiene R, corresponde al tiempo de reaccion
      {
        borrar=false;
        reaccion=inBuffer;
        reaccion=reaccion.replace('R',' ');
        react=true;
        agregarR=true;
       }
      if(inBuffer.contains("U")) //Si el dato contien U, correspondel al tiempo del primer disparo
      {
        disparo1=inBuffer;
        disparo1=inBuffer.replace('U',' ');
        d1=true;
        agregarD1=true;
        
      }
      if(inBuffer.contains("X")) //Si el dato contiene X, corresponde al tiempo del segundo disparo
      {
        disparo2=inBuffer;
        disparo2=inBuffer.replace('X',' ');
        d2=true;
        agregarD2=true;
      }
      //Cada vez que se lanza un disco, se borran los displays.
      if(inBuffer.contains("A"))
      {
        borrar=true;
      }
      //Condicionales para desplegar los resulados, L=Discos Lanzados, W=Discos Acertados Z=Disparos realizados
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
      //Si el dato recibido contiene M la conexión fue exitosa
      if(inBuffer.contains("M"))
      {
        coneccion=true;      
      }
    }
  }
  //Se borran los datos cada vez que se lanza un disco
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

  //Se despliega la modalidad de la que se están obteniendo datos
  if(mod==true)
  {
    fill(0,0,0);
    textSize(18);
    text(modalidad,130,165);
  }
  
  //Se despliega el tiempo de reaccion
  if(react==true)
  {
    fill(0,0,0);
    textSize(18);
    text(reaccion,325,165);

  }
  
  //Se despliega el tiempo del primer disparo
  if(d1==true)
  {
    fill(0,0,0);
    textSize(18);
    text(disparo1,525,165);  
  }
  
  //Se despliega el tiempo del segundo disparo
  if(d2==true)
  {
    fill(0,0,0);
    textSize(18);
    text(disparo2,725,165); 
  }
  
  //Se despliega la cantidad de discos lanzados
  if(lan==true)
  {
    fill(0,0,0);
    textSize(18);
    text(lanzados,275,315);   
  }
  
  //Se despliega la cantidad de discos acertados
  if(acer==true)
  {
    fill(0,0,0);
    textSize(18);
    text(acertados,275,415);   
  }
  
  //Se despliega la cantidad de disparos Realizados
  if(real==true)
  {
    fill(0,0,0);
    textSize(18);
    text(realizados,275,515);   
  }
  
  //Se agregan los datos al archivo .CSV
 if(agregarR==true)
  {
    if(agregarD1==true)
    {
      agregarRD1=true;
    }
    if(agregarD2==true)
    {
       agregarRD2=true;
       agregarRD1=false;
    }
  }
  //Si solo se realizó un disparo, se agrega el tiempo de reacción y el de el primer disparo
  if(agregarRD1==true)
  {
    agregarReaccion=trim(reaccion);
    agregarDisparo1=trim(disparo1);
    archivo.print(agregarReaccion + "," + agregarDisparo1);
    archivo.print("\n");
    agregarRD1=false;
    agregarR=false;
    agregarD1=false;
    
  }
  //Si se realizaron dos disparos, se agregan el tiempo de reacción, del primer y segundo disparo
  if(agregarRD2==true)
  {
    agregarReaccion=trim(reaccion);
    agregarDisparo1=trim(disparo1);
    agregarDisparo2=trim(disparo2);
    archivo.print(agregarReaccion + "," + agregarDisparo1 + "," + agregarDisparo2);
    archivo.print("\n");
    agregarRD2=false;
    agregarR=false;
    agregarD1=false;
    agregarD2=false;
  }
}


//Método que envia los caracteres correspondientes al Arduino Uno de acuerdo a las acciones que se desan realizar
void mousePressed()
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

void keyPressed()
{
  archivo.flush();
  archivo.close();
  exit();
}