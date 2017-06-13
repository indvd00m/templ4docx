package pl.jsolve.templ4docx.tests.variable.image;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.Test;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.ImageVariable;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestImageTemplate extends AbstractImageTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "variable/image/image-template";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);
        is.close();
        docx.setVariablePattern(new VariablePattern("${", "}"));
        docx.setProcessMetaInformation(true);

        String imgPath640 = getClass().getClassLoader().getResource("variable/image/world-1138035_640.jpg").getPath();
        @SuppressWarnings("unused")
        String imgPath1280 = getClass().getClassLoader().getResource("variable/image/world-1138035_1280.jpg").getPath();

        Variables var = new Variables();
        var.addImageVariable(new ImageVariable("${var01}", imgPath640, 640, 480));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        String text = docx.readTextContent();
        String expected = "";
        expected += "This is test simple template with image variable: .";

        int imagesSize = 0;

        for (XWPFParagraph paragraph : docx.getXWPFDocument().getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                imagesSize += run.getEmbeddedPictures().size();
            }
        }

        assertEquals(expected, text.trim());
        assertTrue(imagesSize > 0);
    }

}
