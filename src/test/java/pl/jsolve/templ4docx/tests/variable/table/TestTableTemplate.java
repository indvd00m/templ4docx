package pl.jsolve.templ4docx.tests.variable.table;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.tests.variable.table.model.Student;
import pl.jsolve.templ4docx.variable.BulletListVariable;
import pl.jsolve.templ4docx.variable.ImageVariable;
import pl.jsolve.templ4docx.variable.TableVariable;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variable;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 * @see https://jsolve.github.io/java/templ4docx-2-0-0-table-variables/
 *
 */
public class TestTableTemplate extends AbstractTableVariableTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "variable/table/table-template";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);
        is.close();
        docx.setVariablePattern(new VariablePattern("${", "}"));

        Variables var = new Variables();

        TableVariable tableVariable = new TableVariable();

        List<Variable> nameColumnVariables = new ArrayList<Variable>();
        List<Variable> ageColumnVariables = new ArrayList<Variable>();
        List<Variable> logoColumnVariables = new ArrayList<Variable>();
        List<Variable> languagesColumnVariables = new ArrayList<Variable>();

        for (Student student : getStudents()) {
            nameColumnVariables.add(new TextVariable("${name}", student.getName()));
            ageColumnVariables.add(new TextVariable("${age}", student.getAge().toString()));
            logoColumnVariables.add(new ImageVariable("${logo}", student.getLogoPath(), 40, 40));

            List<Variable> languages = new ArrayList<Variable>();
            for (String language : student.getLanguages()) {
                languages.add(new TextVariable("${languages}", language));
            }
            languagesColumnVariables.add(new BulletListVariable("${languages}", languages));
        }

        tableVariable.addVariable(nameColumnVariables);
        tableVariable.addVariable(ageColumnVariables);
        tableVariable.addVariable(logoColumnVariables);
        tableVariable.addVariable(languagesColumnVariables);

        var.addTableVariable(tableVariable);

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        String text = docx.readTextContent();
        String expected = "";
        expected += "This is test simple template with table variables.\n";
        expected += "Name	Age	Image	Languages\n";
        expected += "Lukasz	28		Polish	English\n";
        expected += "Tomek	24		Polish	English	French";

        assertEquals(expected, text.trim());
    }

    List<Student> getStudents() {
        String imgPath = getClass().getClassLoader().getResource("variable/table/smile01.png").getPath();
        List<Student> students = new ArrayList<Student>();
        students.add(new Student("Lukasz", 28, imgPath, Arrays.asList("Polish", "English")));
        students.add(new Student("Tomek", 24, imgPath, Arrays.asList("Polish", "English", "French")));
        return students;
    }

}
