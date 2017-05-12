package pl.jsolve.templ4docx.strategy;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.insert.TextInsert;
import pl.jsolve.templ4docx.variable.TextVariable;
import pl.jsolve.templ4docx.variable.Variable;

public class TextInsertStrategy implements InsertStrategy {

    @Override
    public void insert(Insert insert, Variable variable) {
        if (!(insert instanceof TextInsert)) {
            return;
        }
        if (!(variable instanceof TextVariable)) {
            return;
        }

        TextInsert textInsert = (TextInsert) insert;
        TextVariable textVariable = (TextVariable) variable;
        XWPFParagraph paragraph = textInsert.getParagraph();
        for (int i = 0; i < paragraph.getRuns().size(); i++) {
            XWPFRun run = paragraph.getRuns().get(i);
            String text = run.text();
            if (StringUtils.contains(text, textInsert.getKey().getKey())) {
                text = StringUtils.replace(text, textVariable.getKey(), textVariable.getValue());
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
