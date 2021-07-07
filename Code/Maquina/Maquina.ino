/*Código desarrollado para controlar los lanzamientos de los discos de las modalidades Fosa y Skeet 
 * en Megaproyecto Ciencia Aplicada al Deporte
 * Elaborado Por: Alejandro Díaz
 * Universidad del Valle de Guatemala
 */
 
#include <SoftwareSerial.h>

int Activar_cronometro=0;
int temporal=0;
int maquina1=13;
boolean conectar=false;
boolean EstadoAnterior=LOW;
boolean EstadoActual;
char valor =0;
SoftwareSerial Maquina(4,2); //RX TX

void setup() {
  
  Serial.begin(9600); //Permite el intercambio de información entre ordenador y Arduino
  Maquina.begin(9600); //Permite el intercambio de información entre ambos módulos Arduino

  //Configuración del Pin Utilizado para detectar el lanzamiento de un Disco
  pinMode(maquina1,INPUT);
  digitalWrite(maquina1, LOW);
}

void loop() {

//El dato recibido por el módulo Xbee es enviado por el puerto Serial Alambrico para ser desplegado
  if(Maquina.available())
  {
    Serial.print(String(Maquina.readString())); 
  }

//El dato recibido por el puerto serial, es verificado para determinar que acción desea ejecutar el operador, posteriormente se
//envia un valor identificador por el módulo Xbee.
  if(Serial.available()>0)
  {
   {
    valor = Serial.read(); 
   } 
  }
//Selección de Modalidad Fosa
  if(valor=='F')
  {
    Maquina.println(2);
    valor=0;
  }
//Seleccion de Modalidad Skeet
  if(valor=='S')
  {
    Maquina.println(3);
    valor=0;
  }
//Conexión entre ambos módulos  
  if(valor=='Z')
  {
    Maquina.println(4);
    valor=0;
    temporal = millis();  
    conectar=true;
  }


//Debounce utilizado para evitar que el ruido afecte la activación del cronometro  
  int lectura = digitalRead(maquina1);
  if(lectura != EstadoAnterior)
  {
     if(lectura != EstadoActual)
     {
        EstadoActual=lectura; 
        if (EstadoActual == HIGH)
        {
           Activar_cronometro = 1;
           Maquina.println(Activar_cronometro);   
        }
     } 
  }
  EstadoAnterior = lectura;
}
