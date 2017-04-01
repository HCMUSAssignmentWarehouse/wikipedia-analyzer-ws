/**
 * Created by Genius Doan on 3/30/2017.
 */
public class WordNode {
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
