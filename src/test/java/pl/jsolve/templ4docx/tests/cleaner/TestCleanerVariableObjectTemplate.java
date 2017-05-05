package pl.jsolve.templ4docx.tests.cleaner;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.tests.variable.object.model.Obj01;
import pl.jsolve.templ4docx.tests.variable.object.model.Obj02;
import pl.jsolve.templ4docx.tests.variable.object.model.Obj03;
import pl.jsolve.templ4docx.variable.ObjectVariable;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestCleanerVariableObjectTemplate extends AbstractCleanerTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "cleaner/cleaner-variable-object-template";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);
        is.close();
        docx.setVariablePattern(new VariablePattern("${", "}"));

        Variables var = new Variables();
        var.addObjectVariable(new ObjectVariable("${var01}", new Obj01(), docx.getVariablePattern()));
        var.addObjectVariable(new ObjectVariable("${var02}", new Obj02(), docx.getVariablePattern()));
        var.addObjectVariable(new ObjectVariable("${var03}", new Obj03(), docx.getVariablePattern()));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        String text = docx.readTextContent();
        String expected = "";
        expected += "This is test simple template with three variables: value01, value02, value03. \n";
        expected += "This is nested values of variables: field1Value, field2Value, field3Value. \n";
        expected += "And more: field11Value. \n";
        expected += "And more: ${var01.Field1.Field11}.";
        assertEquals(expected, text.trim());
    }

}
