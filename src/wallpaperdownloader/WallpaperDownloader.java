
package wallpaperdownloader;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONArray;




public class WallpaperDownloader {

    //Defining the interface to use it later to set the image downloaded
    public static interface User32 extends Library {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);
        boolean SystemParametersInfo(int a, int b, String s, int c);
    }
    

    public static void main(String[] args) throws MalformedURLException, IOException, ParseException, org.json.simple.parser.ParseException {
        //Checking if the file with the path exists, if not it creates one.
        if(new File("data.txt").exists()){
            System.out.println("Data file exists.");
        } else{
            File f = new File("data.txt");
            try (FileWriter fw = new FileWriter(f)) {
                System.out.println("Insert the path where the wallpapers will be downloaded: ");
                Scanner sc = new Scanner(System.in);
                String path = sc.nextLine();
                System.out.print("Path: " + path);
                fw.write(path);
            }
        }
        
        //Getting the path from the data file
        File f = new File("data.txt");
        FileReader fr = new FileReader(f);
        Scanner sc = new Scanner(f);
        String s = sc.nextLine();
        
        


        //Getting the document and then downloading and setting the wallpaper
        getDocument();
        getImageandSetWallpaper(s);
        
        

        
    }
    
        public static void getDocument() throws MalformedURLException, IOException{
            try{
                URL int_file = new URL("https://www.reddit.com/r/wallpaper/top/.json?t=day&limit=1");
                ReadableByteChannel rbc = Channels.newChannel(int_file.openStream());
                FileOutputStream fos = new FileOutputStream("file.json");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                
            }catch(Exception o){
                o.printStackTrace();
                getDocument();
            }
        }
        
        public static void getImageandSetWallpaper(String path) throws FileNotFoundException, IOException, org.json.simple.parser.ParseException{
        //Parsing the document
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("file.json"));
        
        //Taking the data from the document
        JSONObject json = (JSONObject) obj;
        JSONObject data = (JSONObject) json.get("data");
        Object children = (JSONArray) data.get("children");
        JSONArray c = (JSONArray) children;
        JSONObject cdata =  (JSONObject) c.get(0);
        JSONObject cdatadata = (JSONObject) cdata.get("data");
        String url =  cdatadata.get("url").toString();
	String id = cdatadata.get("id").toString();
        
        //Adding a date to identify the images of x days
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM");
        Date date = new Date();
        int h = date.toInstant().atZone(ZoneId.systemDefault()).getHour();


        
        


        //Printing information 
        System.out.println("Url: " + url+ "\n");
        System.out.println("Date: " + date + "\n");
        

        //Downloading the image:
        
        String pathPart = path+"\\DailyWallpaper\\";
        
        if(!new File(pathPart).exists()){
            Path pathF = Paths.get(pathPart);
            Files.createDirectory(pathF);
        }
        
        String image_path = pathPart + formatter.format(date) + "h" + h + id + ".jpg";
        System.out.println("Wallpaper path: " + image_path);
        try(InputStream in = new URL(url).openStream()){
            Files.copy(in, Paths.get(image_path));
            System.out.println("Image downloaded. \n");
        }catch(Exception i){
            System.out.println("\nCouldn't download the image or the image already exists. \n");
            i.printStackTrace();
        }
        
        //Setting the wallpaper
        
        User32.INSTANCE.SystemParametersInfo(0x0014, 0, image_path, 1);
        System.out.println("Wallpaper set! \n");
        
        
    }

}
