/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backups;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author pokevik
 */
public class Backups {

    //atributos:
    private String palabra;
    private int numDeBackups;
    private String dirDondeHaceBackup;
    private String dirHacerBackupDe;

    //contructor:
    public Backups() throws FileNotFoundException, IOException {

        FileReader fr = new FileReader("Configuracion.txt");
        BufferedReader bf = new BufferedReader(fr);

        String sCadena;

        while ((sCadena = bf.readLine()) != null) {
            //en cada interacion del bulcle es una linea del .txt
//            System.out.println(sCadena);
          
            if ( !sCadena.equals("") || sCadena.contains("-")) {
                
            if (sCadena.contains("-Palabra antes de la fecha")) {
                this.palabra = darValor("-Palabra antes de la fecha", sCadena);
//                System.out.println(palabra);

            } else if (sCadena.contains("-Numero de backups maximas")) {
                this.numDeBackups = Integer.parseInt(darValor("-Numero de backups maximas", sCadena));
//                    System.out.println(numDeBackups);

            } else if (sCadena.contains("-Direccion donde quieres hacer los backup")) {
                this.dirDondeHaceBackup = darValor("-Direccion donde quieres hacer los backup", sCadena);
//                System.out.println(dirDondeHaceBackup);
//                System.out.println(new File(dirDondeHaceBackup).isDirectory());

            } else if (sCadena.contains("-Direccion de lo que hacer backup")) {
                this.dirHacerBackupDe = darValor("-Direccion de lo que hacer backup", sCadena);
//                    System.out.println(dirHacerBackupDe);
//                    System.out.println(new File(dirHacerBackupDe).isDirectory());
            }
 
        }
      }
    }

    //metodos copiados de: http://devtroce.com/2010/06/30/copiar-ficheros-y-directorios-con-java/
    private static void CopiarDirectorio(File dirOrigen, File dirDestino) throws Exception {
        try {
//            System.out.println(dirOrigen.isDirectory());
            if (dirOrigen.isDirectory()) {
                if (!dirDestino.exists()) {
                    dirDestino.mkdir();
                }

                String[] hijos = dirOrigen.list();
                for (String hijo : hijos) {
                    CopiarDirectorio(new File(dirOrigen, hijo), new File(dirDestino, hijo));
                } // end for
            } else {
                Copiar(dirOrigen, dirDestino);
            } // end if
        } catch (Exception e) {
            System.err.println("Error");
        } // end try
    } // end CopiarDirectorio

    private static void Copiar(File dirOrigen, File dirDestino) throws Exception {

        InputStream in = new FileInputStream(dirOrigen);
        OutputStream out = new FileOutputStream(dirDestino);

        byte[] buffer = new byte[1024];
        int len;

        try {
            // recorrer el array de bytes y recomponerlo
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            } // end while
            out.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            in.close();
            out.close();
        } // end ty
    } // end Copiar
    
    //metodo copiado de: http://stackoverflow.com/questions/3775694/deleting-folder-from-java
    private static boolean deleteDirectory(File directory) {
    if(directory.exists()){
        File[] files = directory.listFiles();
        if(null!=files){
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
    }
    return(directory.delete());
}
    
    
    //Propios:
    private static String darValor(String palabra, String cadena) {
        String dev = "";

        for (int i = palabra.length()+1; i < cadena.length(); i++) {
            dev += cadena.charAt(i);
        }

        return dev;
    }

    public void hacerBackup() throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("'" + palabra + " '" + "dd.MM.yyyy" + " " + "HH.mm.ss");

        File cBack = new File(dirDondeHaceBackup + "/" + sdf.format(new Date())); // Carpeta a crear

        File dir = new File(dirDondeHaceBackup);

        Date fAnt = new Date(); // Donde almacenara la fecha más antigua

        Date car = new Date(); // Fecha de la carpeta selecionada por la inteacion del bucle

        String[] aryCarp = dir.list();

        if (dir.list().length >= numDeBackups) {

            //Compara fechas y devuelve en fAnt la fecja más antigua
            for (String name : aryCarp) {
                try {
                    car = new Date(sdf.parse(name).getTime());
                } catch (ParseException ex) {
                    System.err.println("Error en el coversor");
                }

                if (fAnt.getTime() >= car.getTime()) {
                    fAnt = car;
                }
            }

            //Seleciona la carpeta con la fecha mas antigua y la elimina:
            File del = new File(dirDondeHaceBackup + "/" + sdf.format(fAnt));
            deleteDirectory(del);
        }
            //Crear los Files para usare el metodo 
        File origen = new File(dirHacerBackupDe);

        File destino = new File(dirDondeHaceBackup + "/" + cBack.getName());
        
//        cBack.mkdir();
        CopiarDirectorio(origen, destino);//crea la carpeta y copia
    }
    
    public String toString() {
        return "Backups{" + "palabra=" + palabra + ", numDeBackups=" + numDeBackups + ", dirDondeHaceBackup=" + dirDondeHaceBackup + ", dirHacerBackupDe=" + dirHacerBackupDe + '}';
    }

    
}
