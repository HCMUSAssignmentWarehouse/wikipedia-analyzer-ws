import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new Analyzer("viwiki-20170301-pages-articles.xml");
        analyzer.writeToFile("1412363_1412477.txt");
    }
}

