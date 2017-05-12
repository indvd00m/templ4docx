package pl.jsolve.templ4docx.tests.variable.bulletlist;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.Test;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.variable.BulletListVariable;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestBulletListTemplate extends AbstractBulletListTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "variable/bulletlist/bulletlist-template";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);
        is.close();
        docx.setVariablePattern(new VariablePattern("${", "}"));

        Variables var = new Variables();
        var.addBulletListVariable(
                new BulletListVariable("${var01}", Arrays.asList(new TextVariable("${var01}", "value01_1"))));
        var.addBulletListVariable(new BulletListVariable("${var02}",
                Arrays.asList(new TextVariable("${var02}", "value02_1"), new TextVariable("${var02}", "value02_2"))));
        var.addBulletListVariable(
                new BulletListVariable("${var03}", Arrays.asList(new TextVariable("${var03}", "value03_1"),
                        new TextVariable("${var03}", "value03_2"), new TextVariable("${var03}", "value03_3"))));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        String text = docx.readTextContent();
        String expected = "";
        expected += "This is test simple template with one list variable: value01_1. \n";
        expected += "\n";
        expected += "And second list variable: value02_1. \n";
        expected += "And second list variable: value02_2. \n";
        expected += "\n";
        expected += "And third: value03_1.\n";
        expected += "And third: value03_2.\n";
        expected += "And third: value03_3.\n";
        expected += "\n";
        expected += "Same variables with new lines:\n";
        expected += "value01_1\n";
        expected += "value02_1\n";
        expected += "value02_2\n";
        expected += "value03_1\n";
        expected += "value03_2\n";
        expected += "value03_3\n";
        expected += "\n";
        expected += "\n";
        expected += "Same variables with numeration:\n";
        expected += "value01_1\n";
        expected += "\n";
        expected += "Same variables with numeration:\n";
        expected += "value02_1\n";
        expected += "value02_2\n";
        expected += "\n";
        expected += "Same variables with numeration:\n";
        expected += "value03_1\n";
        expected += "value03_2\n";
        expected += "value03_3";

        assertEquals(expected, text.trim());
    }

}
