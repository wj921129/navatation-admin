public class TestUri { public static void main(String[] args) { System.out.println(java.nio.file.Paths.get("..\data\sys_data\bg_img").toAbsolutePath().normalize().toUri().toString()); } }
