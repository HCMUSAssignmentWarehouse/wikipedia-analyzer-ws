import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Genius Doan on 4/2/2017.
 */
public class DataHandler extends DefaultHandler {
    private boolean foundTitle = false;
    private boolean foundText = false;
    private boolean foundNS = false;
    private boolean isChosenNamespace = true;
    private int pageNumber = 0;
    private String chosenNS = "0";
    private StringBuffer buffer;
    private OnDataLoadedListener mListener;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //super.startElement(uri, localName, qName, attributes);
        //This called when we starting to read an element
        if (qName.equalsIgnoreCase("title")) {
            foundTitle = true;
        } else if (qName.equalsIgnoreCase("text")) {
            foundText = true;
            buffer = new StringBuffer();
        } else if (qName.equalsIgnoreCase("ns")) {
            //Get namespace to check
            foundNS = true;
            isChosenNamespace = true;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // super.endElement(uri, localName, qName);
        //This called in the end of an element
        if (qName.equalsIgnoreCase("text")) {
            //Combine all text
            if (buffer != null && mListener != null && isChosenNamespace) {
                //Notify Analyzer to get the text data back
                mListener.onTextLoaded(buffer.toString());
            }
            foundText = false;
        } else if (qName.equalsIgnoreCase("page") && isChosenNamespace) {
            isChosenNamespace = false;
            pageNumber++;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //super.characters(ch, start, length);\

        if (foundTitle) {
            String title = new String(ch, start, length);
            if (!title.isEmpty() && mListener != null && isChosenNamespace)
                mListener.onTitleLoaded(title);
            foundTitle = false;
        } else if (foundNS) {
            String pageNS = new String(ch, start, length);
            isChosenNamespace = pageNS.equals(chosenNS);
            foundNS = false;
        } else if (foundText) {
            buffer.append(ch, start, length);
        }

    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        if (mListener != null)
            mListener.onPageNumberCounted(pageNumber);
    }

    public void setNamespaceString(String mainNS) {
        this.chosenNS = mainNS;
    }

    public void setOnDataLoadedListener(OnDataLoadedListener listener) {
        mListener = listener;
    }

    public interface OnDataLoadedListener {
        void onPageNumberCounted(int pageNumber);

        void onTitleLoaded(String title);

        void onTextLoaded(String text);
    }
}
