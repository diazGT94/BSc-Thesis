/*
 * Interfaz Desarrollada para el meaproyecto ciencia aplicada al deporte
 * permite visualizar los tiempos de reacción, del primer y segundo disparo
 * de las modalidades Fosa y Skeet de tiro con armas de caza.
 * Adicionlamente permite almacenar esta información en un archivo de Excel.
 */
/**
 * @author Alejandro Diaz
 * Universidad del Valle de Guatemala
 */


import jssc.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.JOptionPane;
//Librerias para establecer la comunicación con Arduino
import com.panamahitek.ArduinoException;
import com.panamahitek.PanamaHitek_Arduino;
//Liberria para Generar el Archivo de Excel
import org.apache.poi.hssf.usermodel.*;

public class GUI extends javax.swing.JFrame {

    //Objeto que permite establecer comunicación con Arduino
    PanamaHitek_Arduino arduino = new PanamaHitek_Arduino(); 
    //Variables utilizadas para almacenar los resultados y desplegarlos en pantalla
    public static String dato = "";
    public static String reaccion = "";
    public static String disparo1 = "";
    public static String disparo2 = "";
    public static String lanzados = "";
    public static String acertados="";
    public static String realizados="";
    public static String nombre = "";
    //Variable para establecer el nombre a las pestañas de Excel
    public static Date fecha = new Date();
    //Variables para determinar la modalidad en la que se estan obteniendo datos.
    public boolean fosa=false;
    public boolean skeet=false;
    //Variabales para llevar control de resultados.
    public int aciertos=0;
    public int Dlanzados=0;
    public int Drealizados=0;
    public int ESkeet=0;
    public int EFosa=0;
    
    public static int i = 0;
    public static int m =0;
    public static int p=0;
    public static int j=0;
    //Variables para inicializar el archivo de Excel y almacenar los datos.
    public HSSFWorkbook libro;
    public HSSFSheet hoja;
    public HSSFRow fila;
    public HSSFCell celda;
    public FileOutputStream elFichero;
    public String data[] = new String[1000];
    public String posicion[] = new String[1000];
    SerialPortEventListener listener;

    public GUI() {

        this.listener = (SerialPortEvent spe) -> {
            try {
                //Se revisa si existe algún mensaje serial en Arduino
                if (arduino.isMessageAvailable()) {
                    //Se imprime el mensaje recibido en la consola
                    dato = arduino.printMessage();
                    System.out.println(dato);
                    //El valor M establece que existe conección entre ambos módulos
                    if (dato.equals("M")) {
                        Conectar.setBackground(Color.green);
                        Skeet.setBackground(Color.red);
                        Fosa.setBackground(Color.red);
                    }
                    //Valor de confirmación que el otro micrcontrolador opera en modalidad Fosa.
                    if (dato.equals("FOSA")) {
                        Fosa.setBackground(Color.green);
                        Skeet.setBackground(Color.red);
                        fosa=true;
                        skeet=false;
                    }
                    //Valor de confirmación que el otro micrcontrolador opera en modalidad Skeet
                    if (dato.equals("SKEET")) {
                        Skeet.setBackground(Color.green);
                        Fosa.setBackground(Color.red);
                        fosa=false;
                        skeet=true;
                    }
                    //Valor de confirmación que se realizó el lanzamiento de 1 o 2 discos
                    if (dato.equals("A")) {
                        reaccion = "";
                        disparo1 = "";
                        disparo2 = "";
                        Display_Reaccion.setText(reaccion);
                        Display_Disparo1.setText(disparo1);
                        Display_Disparo2.setText(disparo2);
                        if(fosa==true)
                        {
                            posicion[p]=Integer.toString(EFosa);
                        }
                        if(skeet==true)
                        {
                            posicion[p]=Integer.toString(ESkeet);
                        }
                        p=p+1;
                                           
                    }
                    //Valor del tiempo de reacción se susituye el caracter identificador por ":"
                    if (dato.contains("R")) {
                        String temp = dato.replace("R", ":");
                        reaccion = reaccion + temp;
                        if(reaccion.startsWith("0"))
                        {
                            Display_Reaccion.setText(reaccion);
                        }
                        else
                        {
                            reaccion="0"+reaccion;
                            Display_Reaccion.setText(reaccion);
                        }
                        //Si se recibe todo el valor el dato se agrega a la matriz de resultados.
                        if (reaccion.length()>7)
                        {
                            data[i]=reaccion;
                            i=i+1;
                            //Mensaje de confirmación que se agrego el dato a la matriz.
                            System.out.println("Dato Agregado");
                        }
                    }
                    
                    //Valor de tiempo de disparo 1 se sustituye le caracter identificador por ":"
                    if (dato.contains("U")) {
                        String temp2 = dato.replace("U", ":");
                        disparo1 = disparo1 + temp2;
                        if(disparo1.startsWith("0"))
                        {
                            Display_Disparo1.setText(disparo1);
                        }
                        else
                        {
                            disparo1="0"+disparo1;
                            Display_Disparo1.setText(disparo1);
                        }
                        //Si se recibe todo el valor el dato se agrega a la matriz de resultados. 
                       if(disparo1.length()>7)
                        {
                            data[i]=disparo1;
                            //Se corre la matriz dos posiciones ya que no siempre se realizan dos disparos.
                            //Si no se realiza el segundo disparo, en el documento de excel la celda queda vacia.
                            i=i+2;
                            System.out.println("Dato Agregado2");
                            //Condicionales para llevar el control de discos lanzados y disparos realizados.
                             if(fosa==true)
                            {
                                Dlanzados=Dlanzados+1;
                                Drealizados=Drealizados+1;
                            }
                            if(skeet==true)
                            {
                                Dlanzados=Dlanzados+1;
                                Drealizados=Drealizados+1;
                            }
                        }

                    }
                    //Valor de tiempo de disparo 1 se sustituye le caracter identificador por ":"
                    if (dato.contains("X")) {
                        String temp3 = dato.replace("X", ":");
                        disparo2 = disparo2 + temp3;
                        if(disparo2.startsWith("0"))
                        {
                            Display_Disparo2.setText(disparo2);
                        }
                        else
                        {
                            disparo2="0"+disparo2;
                            Display_Disparo2.setText(disparo2);
                        }
                        if(disparo2.length()>7)
                        {
                            //Se retorna una posición de la matriz para almacenar el segundo disparo.
                            //Los datos ocupan tres posiciones en la matriz sin importar si se realiza o no el segundo disparo.
                            i=i-1;
                            data[i]=disparo2;
                            i=i+1;
                            System.out.println("Dato Agregado3");
                            if(skeet==true)
                            {
                                Dlanzados=Dlanzados+1;
                                Drealizados=Drealizados+1;
                            }
                        }

                    }
                                       
                }

            }
            //Mensaje para desplegar que no se logro establecer comunciación con el Arduino
            catch (SerialPortException | ArduinoException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "No se logró iniciar la comunicación");
                
            }
        };

        try {
            
            arduino.arduinoRXTX("COM4", 9600, listener);

        }
        //Mensaje para desplegar que no se logro establecer comunciación con el Arduino
            catch (ArduinoException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "No se logró iniciar la comunicación revise que el cable se encuentre conectado.");
        }

        initComponents();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Fosa = new javax.swing.JButton();
        Skeet = new javax.swing.JButton();
        Acierto = new javax.swing.JButton();
        Resultados = new javax.swing.JButton();
        Conectar = new javax.swing.JButton();
        LabelTiempoR = new javax.swing.JLabel();
        LabelTiempoD2 = new javax.swing.JLabel();
        LabelTiempoD1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Display_Reaccion = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        Display_Disparo1 = new javax.swing.JTextPane();
        jScrollPane6 = new javax.swing.JScrollPane();
        Display_Disparo2 = new javax.swing.JTextPane();
        LabelRealizados = new javax.swing.JLabel();
        LabelAcertados = new javax.swing.JLabel();
        LabelLanzados = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Disparos_Realizados = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        Discos_Acertados = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        Discos_Lanzados = new javax.swing.JTextPane();
        Nombre = new javax.swing.JTextField();
        LabelNameAtleta = new javax.swing.JLabel();
        Excel = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CDAG TIRO CON ARMAS DE CAZA");
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(29, 218, 213));
        setFocusCycleRoot(false);
        setFocusTraversalPolicyProvider(true);
        setMaximumSize(new java.awt.Dimension(800, 450));
        setMinimumSize(new java.awt.Dimension(800, 450));

        Fosa.setText("Fosa");
        Fosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FosaActionPerformed(evt);
            }
        });

        Skeet.setText("Skeet");
        Skeet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SkeetActionPerformed(evt);
            }
        });

        Acierto.setText("Acierto");
        Acierto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AciertoActionPerformed(evt);
            }
        });

        Resultados.setText("Resultados");
        Resultados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResultadosActionPerformed(evt);
            }
        });

        Conectar.setText("Conectar");
        Conectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConectarActionPerformed(evt);
            }
        });

        LabelTiempoR.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        LabelTiempoR.setText("TIEMPO DE REACCION");

        LabelTiempoD2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        LabelTiempoD2.setText("TIEMPO DE DISPARO 2");

        LabelTiempoD1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        LabelTiempoD1.setText("TIEMPO DE DISPARO 1");

        Display_Reaccion.setBackground(new java.awt.Color(204, 204, 204));
        Display_Reaccion.setBorder(new javax.swing.border.MatteBorder(null));
        Display_Reaccion.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        Display_Reaccion.setAlignmentX(2.5F);
        Display_Reaccion.setAlignmentY(2.5F);
        jScrollPane1.setViewportView(Display_Reaccion);

        Display_Disparo1.setBackground(new java.awt.Color(204, 204, 204));
        Display_Disparo1.setBorder(new javax.swing.border.MatteBorder(null));
        Display_Disparo1.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jScrollPane5.setViewportView(Display_Disparo1);

        Display_Disparo2.setBackground(new java.awt.Color(204, 204, 204));
        Display_Disparo2.setBorder(new javax.swing.border.MatteBorder(null));
        Display_Disparo2.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jScrollPane6.setViewportView(Display_Disparo2);

        LabelRealizados.setText("Disparos Realizados");

        LabelAcertados.setText("Discos Acertados");

        LabelLanzados.setText("Discos Lanzados");

        jScrollPane2.setViewportView(Disparos_Realizados);

        jScrollPane3.setViewportView(Discos_Acertados);

        jScrollPane4.setViewportView(Discos_Lanzados);

        LabelNameAtleta.setText("Nombre de Atleta");

        Excel.setText("Enviar Datos");
        Excel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExcelActionPerformed(evt);
            }
        });
        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(78, 78, 78)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(LabelTiempoR)
                        .addGap(80, 80, 80)
                        .addComponent(LabelTiempoD1)
                        .addGap(53, 53, 53)
                        .addComponent(LabelTiempoD2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(140, 140, 140)
                        .addComponent(LabelLanzados)
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addComponent(Fosa)
                        .addGap(10, 10, 10)
                        .addComponent(Skeet)
                        .addGap(10, 10, 10)
                        .addComponent(Acierto)
                        .addGap(10, 10, 10)
                        .addComponent(Resultados)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Conectar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(Excel)
                                    .addComponent(LabelNameAtleta))
                                .addGap(9, 9, 9))
                            .addComponent(Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(35, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(140, 140, 140)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(LabelAcertados)
                        .addGap(26, 26, 26)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 407, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(LabelRealizados)
                        .addGap(13, 13, 13)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(90, 407, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Fosa)
                    .addComponent(Skeet)
                    .addComponent(Acierto)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Resultados)
                        .addComponent(Conectar)
                        .addComponent(Nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(6, 6, 6)
                .addComponent(LabelNameAtleta)
                .addGap(5, 5, 5)
                .addComponent(Excel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelTiempoR)
                    .addComponent(LabelTiempoD1)
                    .addComponent(LabelTiempoD2))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelRealizados)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelAcertados)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LabelLanzados)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
//A cada botón se le agrega un identificador que se le envia al otro microcontrolador Cuando este es presionado.
    private void FosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FosaActionPerformed
        try {
            arduino.sendData("F");
        } catch (ArduinoException | SerialPortException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_FosaActionPerformed

    private void ConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConectarActionPerformed

        try {
            arduino.sendData("Z");
        } catch (ArduinoException | SerialPortException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ConectarActionPerformed

    private void SkeetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SkeetActionPerformed
        try {
            arduino.sendData("S");
        } catch (ArduinoException | SerialPortException ex) {
            Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_SkeetActionPerformed

    private void AciertoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AciertoActionPerformed
            aciertos=aciertos+1;

    }//GEN-LAST:event_AciertoActionPerformed
//Método para Desplegar los resultados en la GUI.
    private void ResultadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResultadosActionPerformed
            Disparos_Realizados.setText(Integer.toString(Drealizados));
            Discos_Lanzados.setText(Integer.toString(Dlanzados));
            Discos_Acertados.setText(Integer.toString(aciertos));
            
    }//GEN-LAST:event_ResultadosActionPerformed

 //Método que crea el documento de Excel
    private void ExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExcelActionPerformed
        // Se obtiene el nombre con el que se llamará o se llama el excel.
        nombre=Nombre.getText();
        String archivo = nombre+".xls";
        File fichero = new File ("C:\\Users\\Alejo\\Documents\\10mo Semestre\\Megaproyecto\\Códigos\\Interfaz Java\\Interfaz\\"+archivo);
        //Se verifica si exsite el archivo.
        //Si este existe se abre y se crea una nueva pestaña.
        //Si no existe se crea un archivo nuevo.
        if(fichero.exists())
        {
            JOptionPane.showMessageDialog(null, "Abriendo Archivo Existente");
            try {
                String sheet_name = fecha.toString();
                //Se divide la fecha ya que solo interesa el día, mes y la hora.
                sheet_name=sheet_name.substring(0, 19);
                sheet_name=sheet_name.replace(':', ' ');
                FileInputStream file = new FileInputStream(new File("C:\\Users\\Alejo\\Documents\\10mo Semestre\\Megaproyecto\\Códigos\\Interfaz Java\\Interfaz\\"+archivo));
                libro = new HSSFWorkbook(file);
                //Se crea una nueva hoja en el archivo existente
                hoja = libro.createSheet(sheet_name);
                //Se le colocan los títulos a las celdas
                fila = hoja.createRow(0);
                celda = fila.createCell(0);
                celda.setCellValue("Tiempo de Reaccion");
                celda = fila.createCell(1);
                celda.setCellValue("Tiempo de Disparo1");
                celda = fila.createCell(2);
                celda.setCellValue("Tiempo de Disparo2");
                celda = fila.createCell(3);
                celda = fila.createCell(4);
                celda = fila.createCell(5);
                celda.setCellValue("Platos Acertados");
                celda = fila.createCell(6);
                celda.setCellValue(aciertos);
                celda = fila.createCell(7);
                celda.setCellValue("Platos Lanzados");
                celda = fila.createCell(8);
                celda.setCellValue(Dlanzados);
                celda = fila.createCell(9);
                celda.setCellValue("Disparos Realizados");
                celda = fila.createCell(10);
                celda.setCellValue(Drealizados);
                
                //Se recorre la matriz de resultados y se copian los valores en cada una de las celdas correspondientes.
                for (int x = 1; x<=i;x++)
                {
                    fila = hoja.createRow(x);
                    for(int j=0;j<=2;j++)
                    {
                        celda = fila.createCell(j);
                        celda.setCellValue(data[m]);
                        m=m+1;            
                    }
                }

                //Se cierra el archivo
                file.close();
                FileOutputStream new_file = new FileOutputStream(new File("C:\\Users\\Alejo\\Documents\\10mo Semestre\\Megaproyecto\\Códigos\\Interfaz Java\\Interfaz\\"+archivo));
                libro.write(new_file);
                new_file.close();
                //Se le indica al usuario que la operación fue exitosa.
                JOptionPane.showMessageDialog(null, "Archivo Existente Modificado");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "No se puedo realizar la operación, revise que el archivo este cerrado");
            } catch (IOException ex) {
                Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "No se puedo realizar la operación, revise que el archivo este cerrado");
            }
               
                
        }
        else
        {
            String sheet_name = fecha.toString();
            sheet_name=sheet_name.substring(0, 19);
            sheet_name=sheet_name.replace(':', ' ');
            libro = new HSSFWorkbook();
            hoja = libro.createSheet(sheet_name);
            fila = hoja.createRow(0);
            celda = fila.createCell(0);
                celda.setCellValue("Tiempo de Reaccion");
                celda = fila.createCell(1);
                celda.setCellValue("Tiempo de Disparo1");
                celda = fila.createCell(2);
                celda.setCellValue("Tiempo de Disparo2");
                celda = fila.createCell(3);
                celda = fila.createCell(4);
                celda = fila.createCell(5);
                celda.setCellValue("Platos Acertados");
                celda = fila.createCell(6);
                celda.setCellValue(aciertos);
                celda = fila.createCell(7);
                celda.setCellValue("Platos Lanzados");
                celda = fila.createCell(8);
                celda.setCellValue(Dlanzados);
                celda = fila.createCell(9);
                celda.setCellValue("Disparos Realizados");
                celda = fila.createCell(10);
                celda.setCellValue(Drealizados);
                
                 for (int x = 1; x<=i;x++)
                {
                    fila = hoja.createRow(x);
                    for(int j=0;j<=2;j++)
                    {
                        celda = fila.createCell(j);
                        celda.setCellValue(data[m]);
                        m=m+1;            
                    }

                }
            try {
                try (FileOutputStream Fichero = new FileOutputStream(nombre+".xls")) {
                    libro.write(Fichero); //Se general el fichero
                    JOptionPane.showMessageDialog(null, "Generando Nuevo Archivo Excel");
                    //Se cierra el archivo
                } //Se general el fichero
            JOptionPane.showMessageDialog(null, "Nuevo Archivo Generado");  
        } catch (HeadlessException | IOException e) {
        }
        }
    }//GEN-LAST:event_ExcelActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        //</editor-fold>
        java.awt.EventQueue.invokeLater(() -> {
            new GUI().setVisible(true);
        });

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton Acierto;
    private static javax.swing.JButton Conectar;
    private javax.swing.JTextPane Discos_Acertados;
    private javax.swing.JTextPane Discos_Lanzados;
    private javax.swing.JTextPane Disparos_Realizados;
    private javax.swing.JTextPane Display_Disparo1;
    private javax.swing.JTextPane Display_Disparo2;
    private javax.swing.JTextPane Display_Reaccion;
    private javax.swing.JButton Excel;
    private static javax.swing.JButton Fosa;
    private javax.swing.JLabel LabelAcertados;
    private javax.swing.JLabel LabelLanzados;
    private javax.swing.JLabel LabelNameAtleta;
    private javax.swing.JLabel LabelRealizados;
    private javax.swing.JLabel LabelTiempoD1;
    private javax.swing.JLabel LabelTiempoD2;
    private javax.swing.JLabel LabelTiempoR;
    private javax.swing.JTextField Nombre;
    private static javax.swing.JButton Resultados;
    private static javax.swing.JButton Skeet;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    // End of variables declaration//GEN-END:variables

}
