import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Genius Doan on 3/31/2017.
 */
public class Analyzer {
    private static final Pattern PATTERN_HTTP_LINK = Pattern.compile("http[s]*://(\\w+\\.)*(\\w+)");
    Map<String, Integer> frequencyMap;
    private int pageNumber = 0;
    private String filePath;
    public Analyzer() {
        //Default
    }
    public Analyzer(String filePath) {
        this.filePath = filePath;
        frequencyMap = new HashMap<>();
        initData();
    }

    private void initData() {
        try {
            File inputFile = new File(filePath);
            if (inputFile.exists()) {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();

                DataHandler dataHandler = new DataHandler();
                dataHandler.setOnDataLoadedListener(new DataHandler.OnDataLoadedListener() {
                    @Override
                    public void onPageNumberCounted(int pageNumber) {
                        Analyzer.this.pageNumber = pageNumber;
                    }

                    @Override
                    public void onTitleLoaded(String title) {
                        List<String> titleList = new ArrayList<String>(Arrays.asList(title.split(" ")));
                        countWord(titleList);
                    }

                    @Override
                    public void onTextLoaded(String text) {
                        String temp = text;
                        Matcher matcher = PATTERN_HTTP_LINK.matcher(temp);
                        while (matcher.find()) {
                            String w = matcher.group();
                            text = text.replace(w, ""); //Remove old data
                        }

                        //Normalize data before count frequency
                        text = normalizeOutputData(text);
                        List<String> textList = new ArrayList<String>(Arrays.asList(text.split(" ")));
                        countWord(textList);
                    }
                });
                saxParser.parse(inputFile, dataHandler);
            } else {
                System.err.println("Invalid input file! Please double check your file path");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String normalizeOutputData(String text) {
        text = text.replaceAll("__NOTOC__", "");
        text = text.replaceAll("__NOEDITSECTION__", "");
        text = text.replaceAll("[0-9]", "");
        text = text.replaceAll("!", "");
        text = text.replaceAll("\\\\", " ");
        text = text.replaceAll("/", " ");
        text = text.replaceAll("\t", " ");
        text = text.replaceAll("\n", " ");
        text = text.replaceAll("[-_:=|,;#!?@$%()+]", " ");
        text = text.replaceAll("\\.", " ");
        text = text.replaceAll("<.*?>", " "); //Remove html tags
        text = text.replaceAll("[^\\p{L}\\p{Nd}\\s]+", "");

        /*
        text = text.replaceAll(",<>|'!%@#^&?.…\\-;:\\\"()=\\/*^","");
        text = text.replaceAll("#đổi","");
        text = text.replaceAll("&amp","");
        text = text.replaceAll(">","");
        text = text.replaceAll("<","");
        text = text.replaceAll("#;\"","");
        text = text.replaceAll("==.*==","");
        //text = text.replaceAll("{.*}","");
        text = text.replaceAll("\\[.*\\]\\]","");
        */

        return text;
    }

    public void countWord(List<String> input) {
        String word;
        int count;
        for (int i = 0; i < input.size(); i++) {
            word = input.get(i);

            if (word != null && !word.isEmpty()) {
                count = 1;
                word = word.toLowerCase();
                if (frequencyMap.containsKey(word)) {
                    //This word already in the list
                    count = frequencyMap.get(word) + 1; //Increase the frequency
                }
                frequencyMap.put(word, count);
            }
        }
    }

    public void writeToFile() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("1412363_1412477.txt");
            writer.append("Tổng số bài viết: " + pageNumber + "\n");
            writer.append("Tần suất:\n");

            for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
                String toWrite = entry.getKey() + "\t" + entry.getValue() + "\n";
                writer.append(toWrite);
                System.out.print(toWrite);
            }

            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
