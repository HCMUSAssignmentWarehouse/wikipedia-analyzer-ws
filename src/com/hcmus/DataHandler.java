import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Genius Doan on 4/2/2017.
 */
public class DataHandler extends DefaultHandler {
    boolean isTitle = false;
    boolean isText = false;
    int pageNumber = 0;
    StringBuffer buffer;
    OnDataLoadedListener mListener;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //super.startElement(uri, localName, qName, attributes);
        //System.out.println("Start Element :" + qName);

        if (qName.equalsIgnoreCase("page")) {
            pageNumber++;
        } else if (qName.equalsIgnoreCase("title")) {
            isTitle = true;
        } else if (qName.equalsIgnoreCase("text")) {
            isText = true;
            buffer = new StringBuffer();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // super.endElement(uri, localName, qName);
        if (qName.equalsIgnoreCase("text")) {
            if (buffer != null && mListener != null) {
                mListener.onTextLoaded(buffer.toString());
            }
            isText = false;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        //super.characters(ch, start, length);\

        if (isTitle) {
            String title = new String(ch, start, length);
            if (!title.isEmpty() && mListener != null)
                mListener.onTitleLoaded(title);
            isTitle = false;
        } else if (isText) {
            buffer.append(ch, start, length);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        if (mListener != null)
            mListener.onPageNumberCounted(pageNumber);
    }

    public void setOnDataLoadedListener(OnDataLoadedListener listener) {
        mListener = listener;
    }

    public interface OnDataLoadedListener {
        public void onPageNumberCounted(int pageNumber);

        public void onTitleLoaded(String title);

        public void onTextLoaded(String text);
    }
}
