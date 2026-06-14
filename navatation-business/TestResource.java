import org.springframework.core.io.*;
public class TestResource {
    public static void main(String[] args) throws Exception {
        Resource r1 = new DefaultResourceLoader().getResource("file:../data/sys_data/bg_img/");
        System.out.println("file:../data... exists: " + r1.exists());
        try {
            System.out.println("URL: " + r1.getURL());
            System.out.println("File: " + r1.getFile().getAbsolutePath());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
