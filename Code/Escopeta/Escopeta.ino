/*Código desarrollado que permite la detección de tiempos de reacción, primer disparo y segundo disparo
 * en las modalidades de Skeet y Fosa del Megaproyecto de Ciencia Aplicada Al Deporte
 * Elaborado Por: Alejandro Díaz
 * Universidad del Valle de Guatemala
 */
#include <SoftwareSerial.h>

//Declaracion de variables booleanas
boolean Fosa=false;
boolean Skeet=false;
boolean Simple=false;
boolean Doble=false;
boolean Coneccion=false;
boolean DX = false;
boolean DY = false;
boolean DZ = false;
boolean DVelX = false;
boolean DVelY = false;
boolean DVelZ = false;
boolean cronReaction_Time = false;
boolean cronShoot_time=false;
boolean Reaction_Display=false;
boolean Display_One=false;
boolean Display_Two=false;

//Declaración de Variables enteras
int LED=13;
int Plato=0;
int temporal=0;
int temporal1=0;
int EstacionSkeet=1;
int X = 0;
int Y = 0;
int Z = 0;
int Xini = 0;
int Yini = 0;
int Zini = 0;
int movimiento = 0;
int minutos = 0;
int segundos = 0;
int decimas = 0;
int centesimas=0;
int datoRecibido=0;

//Delcaración de Variables tipo String para concatenar los tiempos para su envío.
String ind ="";
String Reaccion1="";
String Reaccion2="";
String Reaccion3="";
String Reaccion4="";
String Reaccion5="";
String ReaccionT="";
 
String Disparo11="";
String Disparo12="";
String Disparo13="";
String Disparo14="";
String Disparo15="";
String Disparo1T="";

String Disparo21="";
String Disparo22="";
String Disparo23="";
String Disparo24="";
String Disparo25="";
String Disparo2T="";

unsigned long milisegundos = 0;

SoftwareSerial BT(4,2); //RX | TX


void setup() 
{
   pinMode(LED, OUTPUT);
   BT.begin(9600);
}


//Método elaborado como cronometro, es capaz de contar minutos, segundos y decimas de segundo.
void cronometro()
{
  milisegundos = millis();
  if (milisegundos % 100 == 0)
  {
    decimas = decimas + 1;
    if (decimas == 10)
    {
      decimas = 0;
      segundos = segundos + 1;
    }
    if (segundos == 60)
    {
      segundos = 0;
      minutos = minutos + 1;
    }
    if (minutos == 60)
    {
      minutos = 0;
    }
  }
}

//Método que lee constantemente los valores en los ejes X,Y,Z del acelerometro.
void leerDatos()
{
  X = analogRead(A0);
  Y = analogRead(A1);
  Z = analogRead(A2);

  X = map(X,0,1023,0,4500);
  Y = map (Y,0,1023,0,4500);
  Z = map (Z,0,1023,0,4500);
}


//Método reinicia la detección de un disparo.
void Reset_Shoot_Time()
{      
      DX = false;
      DY = false;
      DZ = false;
}
//Método que reinicia la detección del tiempo de reacción
void Reset_Reaction_Time()
{
      cronReaction_Time = false;
      Reset_Shoot_Time();
}
//Método para actulizar los valores de los ejes del acelerometro
void Actualizar_Valores()
{
      Xini =  X;
      Yini = Y;
      Zini = Z;
}
//Metodo que reinicia el coronometro
void Reset_Cron()
{
      digitalWrite(LED,LOW);
      milisegundos = 0;
      decimas = 0;
      segundos = 0;
      minutos = 0;
      cronShoot_time = false;
      movimiento=0;
      BT.println(" ");
}

//Metodo para imprimir los valores del Cronometro
//Los datos son concatenados en un solo String para no tener problemas con su envio.
//A cada dato se le agrega un caracter identificador que permite establecer que dato se esta enviando.
void display_timer()
{
  String sep=":";
  if(Reaction_Display==true)
  {
    ind = "R";
    Reaccion1=minutos+ind;
    Reaccion2=segundos+ind;
    Reaccion3=decimas+ind;
    Reaccion4=((milisegundos%10000)%1000/100);
    Reaccion5=(((milisegundos%10000)%1000)%100/10);
    ReaccionT=Reaccion1+Reaccion2+Reaccion3+Reaccion4+Reaccion5;
    Reaction_Display=false;
  }
  if(Display_One==true)
  {
    ind = "U" ; 
    Disparo11=minutos+ind;
    Disparo12=segundos+ind;
    Disparo13=decimas+ind;
    Disparo14=((milisegundos%10000)%1000/100);
    Disparo15=(((milisegundos%10000)%1000)%100/10);
    Disparo1T=Disparo11+Disparo12+Disparo13+Disparo14+Disparo15;
    Display_One=false;
  }
  if(Display_Two==true)
  {

    ind = "X";
    Disparo21=minutos+ind;
    Disparo22=segundos+ind;
    Disparo23=decimas+ind;
    Disparo24=((milisegundos%10000)%1000/100);
    Disparo25=(((milisegundos%10000)%1000)%100/10);
    Disparo2T=Disparo21+Disparo22+Disparo23+Disparo24+Disparo25;
    Display_Two=false;
  }
  ind = "";
}


//Método que permiete la obtención del tiempo de reacción. Al existir una variacion detectada por el acelerometro,
//se obtiene el tiempo actual del cronometro y se almacena cmo tiempo de reacción.
void reaction_time()
{
    if (X > Xini + 100 || X < Xini - 100)
    {
      DX = true;
    }
    if (Y > Yini + 100 || Y < Yini - 100)
    {
      DY = true;
    }
    if (Z > Zini + 100 || Z < Zini - 100)
    {
      DZ = true;
    }
    if (DX == true || DY == true || DZ == true)
    {
      Reaction_Display=true;
      display_timer();
      movimiento=1;
      leerDatos();       //Se lee el valor actual del acelerometro
      Actualizar_Valores(); //Se establece el valor leido como nuevo marco de referencia
      Reset_Reaction_Time();
     }  
}

//Método que permiete la obtención del tiempo del primer disparo. Al existir una variacion detectada por el acelerometro,
//se obtiene el tiempo actual del cronometro y se almacena cmo tiempo de disparo 1.
void shoot_time1()
{
    if (X > Xini + 800 || X < Xini - 800)
    {
      DX = true;
    }
    if (Y > Yini + 800 || Y < Yini - 800)
    {
      DY = true;
    }
    if (Z > Zini + 800 || Z < Zini - 800)
    {
      DZ = true;
    }
    if ((DX == true || DY == true || DZ == true))
    {
      Display_One=true;
      display_timer();
      temporal=segundos;
      movimiento=2;
      leerDatos();
      Actualizar_Valores();
      Reset_Shoot_Time();
    }
}

//Método que permiete la obtención del tiempo del segundo disparo. Al existir una variacion detectada por el acelerometro,
//se obtiene el tiempo actual del cronometro y se almacena cmo tiempo de disparo 2.
void shoot_time2()
{
    if (X > Xini + 2000|| X < Xini - 2000)
    {
      DX = true;
    }
    if (Y > Yini + 2000 || Y < Yini - 2000)
    {
      DY = true;
    }
    if (Z > Zini + 2000 || Z < Zini - 2000)
    {
      DZ = true;
    }
    if ((DX == true || DY == true || DZ == true))
    {
      Display_Two=true;
      display_timer();
      temporal1=segundos;
      movimiento=3;
      Reset_Shoot_Time();
    }
}

//Método que permite la obtención de tiempo de reacción y disparo de los lanzamientos Simples en la modalidad de Skeet.
void Skeet_Simple()
{
  if (cronReaction_Time == true)
  { 
    leerDatos();
    cronometro();
    reaction_time();
  }
  if(cronShoot_time == true && movimiento==1)
  {
    leerDatos();
    cronometro();
    shoot_time1();
    if (movimiento==2)
    {
      EstacionSkeet=EstacionSkeet+1;
      Reset_Cron();
      movimiento=0;
      Plato=1;
      BT.println(ReaccionT);
      delay(100);
      BT.println("\n");
      delay(10);
      BT.println("\n");
      delay(10);
      BT.println("\n");
      delay(30);
      BT.println(Disparo1T);
      
    }
  }
}

//Método que permite la obtención de tiempo de reacción y disparo de los lanzamientos Dobles en la modalidad de Skeet.
void Skeet_Doble()
{
   if (cronReaction_Time == true)
  { 
    leerDatos();
    cronometro();
    reaction_time();
  }
  if(cronShoot_time == true && movimiento==1)
  {
    leerDatos();
    cronometro();
    shoot_time1();
 }
  if(cronShoot_time == true && movimiento==2)
  {
    leerDatos();
    cronometro();
    shoot_time2();
    if (movimiento==3)
    {
      EstacionSkeet=EstacionSkeet+1;
      movimiento=0;
      BT.println(ReaccionT);
      delay(100);
      BT.println("\n");
      BT.println("\n");
      BT.println("\n");
      delay(30);
      BT.println(Disparo1T);
      delay(100);
      BT.println("\n");
      delay(10);
      BT.println("\n");
      delay(10);
      BT.println("\n");
      delay(30);
      BT.println(Disparo2T);
      Reset_Cron();  
      Plato=3;
        
    }
  }
}



void loop() 
{
   leerDatos();
   if(BT.available())
   {
      datoRecibido=BT.read();
      //BT.println(datoRecibido);
   }

   //Condicion Para iniciar el cronometro
   if(datoRecibido==49)
    {
      cronReaction_Time = true;
      cronShoot_time = true;
      BT.println("A\n");
      digitalWrite(LED,HIGH);
      Actualizar_Valores();
      Simple=true;
      Doble=true;
      datoRecibido=0;
    }

    //Condicion Para Activar la modalidad Fosa
    if (datoRecibido==50)
    {
      Fosa=true;
      BT.println("FOSA") ;  
      datoRecibido=0;
      Skeet=false;
    }

    //Condicion para activar la modalidad Skeet
    if(datoRecibido==51)
    {
      Skeet=true;
      BT.println("SKEET");  
      datoRecibido=0;
      Fosa=false;
     }
    
    if(datoRecibido==52)
    {
      BT.println("M");
      datoRecibido=0; 
    }
    
    

   //Condición utilizad para la modalidad Skeet
    if(Skeet==true)
    {
      if (EstacionSkeet==1 || EstacionSkeet==3 || EstacionSkeet==5 || EstacionSkeet==7 || EstacionSkeet ==8 || EstacionSkeet ==9 || EstacionSkeet ==11|| EstacionSkeet ==16 || EstacionSkeet ==17)
      {
        Skeet_Simple();
      }
      if(EstacionSkeet==2 || EstacionSkeet==4 || EstacionSkeet==6 || EstacionSkeet==10 || EstacionSkeet==12  || EstacionSkeet==13 || EstacionSkeet==14 ||EstacionSkeet==15)
      {
        Skeet_Doble();
      }
           
      if(EstacionSkeet>17)
      {
        EstacionSkeet=1;  
      }      
    }

    //Condicion para la modalidad Fosa.
    if(Fosa==true)
    {
      //Se obtiene el tiempo de reacción   
      if (cronReaction_Time == true)
      { 
        leerDatos();
        cronometro();
        reaction_time();
      }
      //Se obtiene el tiempo del primer disparo
      if(cronShoot_time == true && movimiento==1)
      {
        leerDatos();
        cronometro();
        shoot_time1();
        cronometro();
     }
     //Se obtiene el tiempo del segundo disparo
     if(cronShoot_time==true && movimiento==2)
     {
         leerDatos();
         cronometro();
         shoot_time2();
         if (movimiento==3)
         {
            //Condicional para verificar si el tiempo obtenido del segundo disparo es una dato valido o no
            //Si este no supera por más de un segundo al del primer disparo es valido, de lo contrario este valor se descarta.
            if((temporal1-1==temporal || temporal1==temporal))
            {
              BT.println(ReaccionT);
              delay(100);
              BT.println("\n");
              BT.println("\n");
              BT.println("\n");
              delay(30);
              BT.println(Disparo1T);
              delay(100);
              BT.println("\n");
              delay(10);
              BT.println("\n");
              delay(10);
              BT.println("\n");
              delay(30);
              BT.println(Disparo2T);
              Reset_Cron();
            }
            else
            {
              delay(30);
              BT.println(ReaccionT);
              delay(100);
              BT.println("\n");
              delay(10);
              BT.println("\n");
              delay(10);
              BT.println("\n");
              delay(30);
              BT.println(Disparo1T); 
              Reset_Cron(); 
            }
         }
    }
  }
}


