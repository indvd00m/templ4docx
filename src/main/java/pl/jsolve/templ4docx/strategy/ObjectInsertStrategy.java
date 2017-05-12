package pl.jsolve.templ4docx.strategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.insert.ObjectInsert;
import pl.jsolve.templ4docx.variable.ObjectVariable;
import pl.jsolve.templ4docx.variable.Variable;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class ObjectInsertStrategy implements InsertStrategy {

    @Override
    public void insert(Insert insert, Variable variable) {
        if (!(insert instanceof ObjectInsert)) {
            return;
        }
        if (!(variable instanceof ObjectVariable)) {
            return;
        }

        ObjectInsert objectInsert = (ObjectInsert) insert;
        ObjectVariable objectVariable = (ObjectVariable) variable;
        XWPFParagraph paragraph = objectInsert.getParagraph();
        for (int i = 0; i < paragraph.getRuns().size(); i++) {
            XWPFRun run = paragraph.getRuns().get(i);
            String text = run.text();
            if (StringUtils.contains(text, objectInsert.getKey().getKey())) {
                text = StringUtils.replace(text, objectVariable.getKey(), objectVariable.getStringValue());
                replaceRun(paragraph, i, text);
            }
        }
    }

    protected XWPFRun replaceRun(XWPFParagraph paragraph, XWPFRun run, String text) {
        int index = paragraph.getRuns().indexOf(run);
        return replaceRun(paragraph, index, text);
    }

    protected XWPFRun replaceRun(XWPFParagraph paragraph, int index, String text) {
        XWPFRun run = paragraph.getRuns().get(index);
        XWPFRun newRun = paragraph.insertNewRun(index);
        applyStyle(run, newRun);
        newRun.setText(text);
        paragraph.removeRun(index + 1);
        return newRun;
    }

    protected void applyStyle(XWPFRun source, XWPFRun target) {
        applyRPr(target, source.getCTR().getRPr());
    }

    protected void applyRPr(XWPFRun run, CTRPr rPr) {
        CTRPr sourceRPr = run.getCTR().isSetRPr() ? run.getCTR().getRPr() : run.getCTR().addNewRPr();
        sourceRPr.set(rPr);
    }
}
