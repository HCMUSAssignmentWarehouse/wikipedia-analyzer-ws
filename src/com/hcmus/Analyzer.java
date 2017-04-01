import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Genius Doan on 3/31/2017.
 */
public class Analyzer {
    public Analyzer()
    {
        //Default
    }

    public Analyzer(String filePath)
    {
        this.filePath = filePath;
        initData();
    }

    private List<WordNode> nodeList = new ArrayList<>();
    private static final Pattern PATTERN_HTTP_LINK = Pattern.compile("http[s]*://(\\w+\\.)*(\\w+)");
    private int pageNumber = 0;
    private String filePath;

    private void initData()
    {
        try {
            File fXmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("page");
            pageNumber = nList.getLength();
            for (int i = 0; i < nList.getLength(); i++) {

                org.w3c.dom.Node nNode = nList.item(i);
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                    List<String> titleList = new ArrayList<String>(Arrays.asList(title.split(" ")));
                    countWord(titleList);
                    String text = eElement.getElementsByTagName("text").item(0).getTextContent();
                    String temp = text;
                    Matcher matcher = PATTERN_HTTP_LINK.matcher(temp);
                    while (matcher.find()) {
                        String w = matcher.group();
                        text = text.replace(w,"");
                    }
                    text = text.replaceAll("__NOTOC__","");
                    text = text.replaceAll("__NOEDITSECTION__","");
                    text = text.replaceAll("[0-9]","");
                    text = text.replaceAll(",<>|'!%@#^&?.…\\-;:\\\"()=\\/*^","");
                    text = text.replaceAll("#đổi","");
                    text = text.replaceAll("&amp","");
                    text = text.replaceAll(">","");
                    text = text.replaceAll("<","");
                    text = text.replaceAll("#;\"","");
                    text = text.replaceAll("==.*==","");
                    text = text.replaceAll("-","");
                    //text = text.replaceAll("{.*}","");
                    text = text.replaceAll("\\[.*\\]\\]","");
                    List<String> textList = new ArrayList<String>(Arrays.asList(text.split(" ")));
                    countWord(textList);

                    //System.out.println("title: "+ title + "\n" + "content: " + text);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void countWord(List<String> input){
        for (int i = 0; i < input.size();i++){
            boolean check = false;
            for (int j = 0; j < nodeList.size(); j++){
                if (input.get(i).equalsIgnoreCase(String.valueOf(nodeList.get(j).getWord()))){
                    nodeList.get(j).addFrequency();
                    check = true;
                    break;
                }
            }

            if (!check){
                WordNode temp = new WordNode();
                temp.setWord(input.get(i));
                temp.setFrequency(1);
                nodeList.add(temp);
            }
        }
    }

    public void writeToFile(){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("1412363_1412477.txt");
            writer.append("Tổng số bài viết: "+ pageNumber +"\n");
            writer.append("Tần suất:\n");
            for (int i = 0; i < nodeList.size(); i++){
                writer.append(nodeList.get(i).getWord() + "\t" + nodeList.get(i).getFrequency() + "\n");
                System.out.println(nodeList.get(i).getWord() + "\t" + nodeList.get(i).getFrequency());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        writer.flush();
        writer.close();
    }
}
