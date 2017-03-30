package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static List<Word> WordAndFrequencyList = new ArrayList<>();
    public static String regex = "http[s]*://(\\w+\\.)*(\\w+)";
    static int fageNumber = 0;
    public static void main(String[] args) throws IOException {
        List<String> list;
        try {
            list = new ArrayList<>();
            File fXmlFile = new File("test.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("page");
            fageNumber = nList.getLength();
            for (int i = 0; i < nList.getLength(); i++) {

                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String title = eElement.getElementsByTagName("title").item(0).getTextContent();
                    List<String> titleList = new ArrayList<String>(Arrays.asList(title.split(" ")));
                    CountWord(titleList);
                    String text = eElement.getElementsByTagName("text").item(0).getTextContent();
                    String temp = text;
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(temp);
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
                    CountWord(textList);

                    //System.out.println("title: "+ title + "\n" + "content: " + text);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        WriteToFile();
    }

    static void CountWord(List<String> input){
        for (int i = 0; i < input.size();i++){
            boolean check = false;
            for (int j= 0; j < WordAndFrequencyList.size(); j++){
                if (input.get(i).equalsIgnoreCase(String.valueOf(WordAndFrequencyList.get(j).getWord()))){
                    WordAndFrequencyList.get(j).addFrequency();
                    check = true;
                    break;
                }
            }

            if (!check){
                Word temp = new Word();
                temp.setWord(input.get(i));
                temp.setFrequency(1);
                WordAndFrequencyList.add(temp);
            }
        }
    }

    static void WriteToFile(){
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("1412363_1412477.txt");
            writer.append("Tổng số bài viết: "+fageNumber +"\n");
            writer.append("Tần suất:\n");
            for (int i = 0; i < WordAndFrequencyList.size();i++){
                writer.append(WordAndFrequencyList.get(i).getWord() + "\t" + WordAndFrequencyList.get(i).getFrequency() + "\n");
                System.out.println(WordAndFrequencyList.get(i).getWord() + "\t" + WordAndFrequencyList.get(i).getFrequency());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        writer.flush();
        writer.close();
    }


  

    static class Word {
        String word;
        Integer frequency;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public Integer getFrequency() {
            return frequency;
        }

        public void addFrequency(){
            frequency ++;
        }

        public void setFrequency(Integer frequency) {
            this.frequency = frequency;
        }
    }


}

