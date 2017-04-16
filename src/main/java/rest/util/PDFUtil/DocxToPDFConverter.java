package rest.util.PDFUtil;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by lu on 2017/3/23.
 */
public class DocxToPDFConverter extends Converter {


    public DocxToPDFConverter(InputStream inStream, OutputStream outStream, boolean showMessages, boolean closeStreamsWhenComplete) {
        super(inStream, outStream, showMessages, closeStreamsWhenComplete);
    }

    @Override
    public void convert() throws Exception {
        loading();


        XWPFDocument document = new XWPFDocument(inStream);

        PdfOptions options = PdfOptions.create();


        processing();
        PdfConverter.getInstance().convert(document, outStream, options);

        finished();

    }

}