package pl.jsolve.templ4docx.tests.meta;

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
public class TestTableObjectTemplate extends AbstractMetaTest {

    @Test
    public void test() throws IOException {
        String templateFileName = "meta/table-object-template";
        InputStream is = getClass().getClassLoader().getResourceAsStream(templateFileName + ".docx");

        Docx docx = new Docx(is);
        is.close();
        docx.setProcessMetaInformation(true);
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

        // regenerate from meta information
        docx = new Docx(processedPath);
        docx.setProcessMetaInformation(true);
        docx.setVariablePattern(new VariablePattern("$P{", "}"));

        var = new Variables();
        var.addTextVariable(new TextVariable("$P{CUSTOMER}", "Smit John"));
        var.addTextVariable(new TextVariable("$P{CURRENT_DATE}", "2017-05-06"));
        var.addTextVariable(new TextVariable("$P{REQCREATEDATE}", "Some text in variable 2"));

        tableVariable1 = new TableVariable();
        {
            List<Variable> columnVariables = new ArrayList<Variable>();
            columnVariables.add(new TextVariable("$P{textRowVar1}", "textRowValue11"));
            columnVariables.add(new TextVariable("$P{textRowVar1}", "textRowValue22"));
            columnVariables.add(new TextVariable("$P{textRowVar1}", "textRowValue33"));
            tableVariable1.addVariable(columnVariables);
        }
        var.addTableVariable(tableVariable1);

        tableVariable2 = new TableVariable();
        {
            List<Variable> columnVariables = new ArrayList<Variable>();
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_TYPES}",
                    new IncompleteRequiredType("type11"), docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_TYPES}",
                    new IncompleteRequiredType("type22"), docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_TYPES}",
                    new IncompleteRequiredType("type33"), docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_TYPES}",
                    new IncompleteRequiredType("type44"), docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_TYPES}",
                    new IncompleteRequiredType("type55"), docx.getVariablePattern()));
            tableVariable2.addVariable(columnVariables);
        }
        var.addTableVariable(tableVariable2);

        tableVariable3 = new TableVariable();
        {
            List<Variable> columnVariables = new ArrayList<Variable>();
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_PROPS}",
                    new IncompleteRequiredProp("prop11"), docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_PROPS}",
                    new IncompleteRequiredProp("prop22"), docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_PROPS}",
                    new IncompleteRequiredProp("prop33"), docx.getVariablePattern()));
            columnVariables.add(new ObjectVariable("$P{INCOMPLETE_REQUIRED_PROPS}",
                    new IncompleteRequiredProp("prop44"), docx.getVariablePattern()));
            tableVariable3.addVariable(columnVariables);
        }
        var.addTableVariable(tableVariable3);

        docx.fillTemplate(var);

        String processedPath2 = String.format("%s%s%s", tmpPath, File.separator,
                templateFileName + "-processed2" + ".docx");

        docx.save(processedPath2);

        text = docx.readTextContent();
        expected = "";
        expected += "Test text\n";
        expected += "\n";
        expected += "Customer: Smit John\n";
        expected += "\n";
        expected += "NOTIFICATION\n";
        expected += "this is test template file\n";
        expected += "Kazan city		2017-05-06\n";
        expected += "\n";
        expected += "\n";
        expected += "Text text text Some text in variable 2:\n";
        expected += "\n";
        expected += "№		Name\n";
        expected += "	textRowValue11\n";
        expected += "	textRowValue22\n";
        expected += "	textRowValue33\n";
        expected += "	type11\n";
        expected += "	type22\n";
        expected += "	type33\n";
        expected += "	type44\n";
        expected += "	type55\n";
        expected += "	prop11\n";
        expected += "	prop22\n";
        expected += "	prop33\n";
        expected += "	prop44\n";
        expected += "\n";
        expected += "\n";
        expected += "Text text text.\n";
        expected += "\n";
        expected += "Phone: 8-800-2000-878\n";
        expected += "\n";
        expected += "\n";
        expected += "Notification delivered:\n";
        expected += "Smit John	/_________________/\n";
        expected += "(sign)";

        assertEquals(expected, text.trim());
    }

}
