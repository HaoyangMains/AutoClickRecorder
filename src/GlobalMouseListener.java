import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
 
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import java.util.Timer;
import java.util.TimerTask;
/**
 * https://github.com/kwhat/jnativehook
 */
public class GlobalMouseListener extends JFrame implements NativeMouseInputListener, NativeKeyListener {
 
    private final JLabel info;
    int flag=0;
    LocalTime last;
    String path="";
    private static final Logger logger = Logger.getLogger( GlobalScreen.class.getPackage().getName());
    public GlobalMouseListener() {

        String dir="D:\\screemshot\\";
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");  
        String name=date.format(formatter);
        path+=(dir+name+"\\");
        File file =new File(path);    
        //create if not exist    
        if  (!file .exists()  && !file .isDirectory())      
        {       
            file.mkdir();    
        }
        info = new JLabel("start record");

        last = LocalTime.now();
        setTitle("AutoClickRecorder");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(info);

        //regist before use it!
        try {

            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeMouseListener(this);
            GlobalScreen.addNativeKeyListener(this);

        } catch (NativeHookException e) {
        }

        setAlwaysOnTop(true);
        setResizable(false);
        setVisible(true);
        pack();//use pack to fit the size of components
    }


    //post-invoke
    /**
     * @see NativeMouseListener#nativeMouseClicked(NativeMouseEvent)
     */

    //if interval more than 300ms
    public void nativeMouseClicked(NativeMouseEvent e) {
        LocalTime cur=LocalTime.now();
        Duration dur= Duration.between(last,cur);
        if(dur.toMillis()>300){
            last=cur;
            screentshot();
        }
        
    }
    

    //pre-invoke

    // /**
    //  * @see NativeMouseListener#nativeMousePressed(NativeMouseEvent)
    //  */
    // public void nativeMousePressed(NativeMouseEvent e) {
    //     appendDisplay(e.paramString());
    // }
 
    //record space as start and stop
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) 
    {
        int key = e.getKeyCode();
        
        if(key==NativeKeyEvent.VC_SPACE){
            //press space to start the record!

            flag=(flag+1)%2;
            if(flag==1){
                info.setText("off record");
            }
            else{
                info.setText("start record");
            }
        }
        if(key==NativeKeyEvent.VC_ENTER){
            //press enter also record.
            screentshot();
        }

    }

    private void screentshot() {

        if(flag==0){
                   //get current localTime
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm-ss"); 
        String name=time.format(formatter);

        //after 1000ms delay then start the task!
        Timer timer = new Timer();
	    timer.schedule(new TimerTask() {
	      public void run() {
	       //do this
           try {
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Rectangle screenRectangle = new Rectangle(screenSize);
                Robot robot = new Robot();
                BufferedImage image = robot.createScreenCapture(screenRectangle);
                String fileName = name + "." + "jpg";
                ImageIO.write(image, "jpg", new File(path + fileName));
           } catch (Exception e) {
            // TODO: handle exception
           }
	      }
	    }, 1000); 
        }
    }
   
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GlobalMouseListener::new);
    }
}
