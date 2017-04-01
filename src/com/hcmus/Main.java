import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new Analyzer("test.xml");
        analyzer.writeToFile();
    }
}

