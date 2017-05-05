package pl.jsolve.templ4docx.tests.variable.table;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.tests.variable.table.model.IncompleteRequiredProp;
import pl.jsolve.templ4docx.tests.variable.table.model.IncompleteRequiredType;
import pl.jsolve.templ4docx.variable.ObjectVariable;
import pl.jsolve.templ4docx.variable.TableVariable;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variable;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class TestTableObjectTemplate04 extends AbstractTableVariableTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "variable/table/table-object-template-04";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);
        is.close();
        docx.setVariablePattern(new VariablePattern("$P{", "}"));

        Variables var = new Variables();
        var.addTextVariable(new TextVariable("$P{CUSTOMER}", "John Smit"));
        var.addTextVariable(new TextVariable("$P{CURRENT_DATE}", "2017-05-05"));
        var.addTextVariable(new TextVariable("$P{REQCREATEDATE}", "Some text in variable"));

        TableVariable tableVariable1 = new TableVariable();
        {
            List<Variable> columnVariables = new ArrayList<Variable>();
            columnVariables.add(new TextVariable("$P{textRowVar1}", "textRowValue1"));
            columnVariables.add(new TextVariable("$P{textRowVar1}", "textRowValue2"));
            columnVariables.add(new TextVariable("$P{textRowVar1}", "textRowValue3"));
            tableVariable1.addVariable(columnVariables);
        }
        var.addTableVariable(tableVariable1);

        TableVariable tableVariable2 = new TableVariable();
        {
            List<Variable> columnVariables = new ArrayList<Variable>();
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_TYPES}", new IncompleteRequiredType("type1"),
                    docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_TYPES}", new IncompleteRequiredType("type2"),
                    docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_TYPES}", new IncompleteRequiredType("type3"),
                    docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_TYPES}", new IncompleteRequiredType("type4"),
                    docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_TYPES}", new IncompleteRequiredType("type5"),
                    docx.getVariablePattern()));
            tableVariable2.addVariable(columnVariables);
        }
        var.addTableVariable(tableVariable2);

        TableVariable tableVariable3 = new TableVariable();
        {
            List<Variable> columnVariables = new ArrayList<Variable>();
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_PROPS}", new IncompleteRequiredProp("prop1"),
                    docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_PROPS}", new IncompleteRequiredProp("prop2"),
                    docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_PROPS}", new IncompleteRequiredProp("prop3"),
                    docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_PROPS}", new IncompleteRequiredProp("prop4"),
                    docx.getVariablePattern()));
            tableVariable3.addVariable(columnVariables);
        }
        var.addTableVariable(tableVariable3);

        docx.fillTemplate(var);

        String tmpPath = System.getProperty("java.io.tmpdir");
        String processedPath = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed" + ".docx");

        docx.save(processedPath);

        String text = docx.readTextContent();
        String expected = "";
        expected += "Test text\n";
        expected += "\n";
        expected += "Customer: John Smit\n";
        expected += "\n";
        expected += "NOTIFICATION\n";
        expected += "this is test template file\n";
        expected += "Kazan city		2017-05-05\n";
        expected += "\n";
        expected += "\n";
        expected += "Text text text Some text in variable:\n";
        expected += "\n";
        expected += "№		Name\n";
        expected += "	textRowValue1\n";
        expected += "	textRowValue2\n";
        expected += "	textRowValue3\n";
        expected += "	type1\n";
        expected += "	type2\n";
        expected += "	type3\n";
        expected += "	type4\n";
        expected += "	type5\n";
        expected += "	prop1\n";
        expected += "	prop2\n";
        expected += "	prop3\n";
        expected += "	prop4\n";
        expected += "\n";
        expected += "\n";
        expected += "Text text text.\n";
        expected += "\n";
        expected += "Phone: 8-800-2000-878\n";
        expected += "\n";
        expected += "\n";
        expected += "Notification delivered:\n";
        expected += "John Smit	/_________________/\n";
        expected += "(sign)";

        assertEquals(expected, text.trim());
    }

}
