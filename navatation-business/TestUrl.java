import java.net.URL;
public class TestUrl {
    public static void main(String[] args) {
        try {
            URL url = new URL("file:../data/sys_data/bg_img/");
            System.out.println("Success: " + url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
