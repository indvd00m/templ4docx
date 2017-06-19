package pl.jsolve.templ4docx.tests.variable.object;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.tests.variable.object.model.DateContainer;
import pl.jsolve.templ4docx.tests.variable.object.model.Obj01;
import pl.jsolve.templ4docx.variable.ObjectVariable;
import pl.jsolve.templ4docx.variable.Variables;
import pl.jsolve.templ4docx.variable.object.IConverter;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestDateConverterTemplate extends AbstractVariableObjectTest {

    @Test
    public void test() throws IOException, ParseException {
        String templateFileName = "variable/object/date-converter-template";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);
        is.close();
        docx.setVariablePattern(new VariablePattern("${", "}"));

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse("2017-06-19");
        IConverter converter = new IConverter() {

            @Override
            public String convert(Object value) {
                if (value == null) {
                    return null;
                }
                if (value instanceof Date) {
                    Date dateValue = (Date) value;
                    return sdf.format(dateValue);
                }
                return value.toString();
            }
        };

        Variables var = new Variables();
        var.addObjectVariable(new ObjectVariable("${var01}", new Obj01(), docx.getVariablePattern()));
        var.addObjectVariable(
                new ObjectVariable("${var02}", new DateContainer(date), docx.getVariablePattern(), converter));

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        String text = docx.readTextContent();
        assertEquals(
                "This is test simple template with three variables: value01, date=2017-06-19. This is nested values of variables: field1Value, 2017-06-19.",
                text.trim());
    }

}
