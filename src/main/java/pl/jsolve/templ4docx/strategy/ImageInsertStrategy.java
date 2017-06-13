package pl.jsolve.templ4docx.strategy;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

import pl.jsolve.templ4docx.insert.ImageInsert;
import pl.jsolve.templ4docx.insert.Insert;
import pl.jsolve.templ4docx.variable.ImageVariable;
import pl.jsolve.templ4docx.variable.Variable;

public class ImageInsertStrategy implements InsertStrategy {

    @Override
    public void insert(Insert insert, Variable variable) {
        if (!(insert instanceof ImageInsert)) {
            return;
        }
        if (!(variable instanceof ImageVariable)) {
            return;
        }

        ImageInsert imageInsert = (ImageInsert) insert;
        ImageVariable imageVariable = (ImageVariable) variable;
        XWPFParagraph paragraph = imageInsert.getParagraph();
        for (int i = 0; i < paragraph.getRuns().size(); i++) {
            XWPFRun run = paragraph.getRuns().get(i);
            String text = run.text();
            if (StringUtils.contains(text, imageInsert.getKey().getKey())) {
                text = StringUtils.replace(text, imageInsert.getKey().getKey(), "");
                XWPFRun newrun = replaceRun(paragraph, i, text);
                insertPicture(newrun, imageVariable);
            }
        }
    }

    private void insertPicture(XWPFRun r, ImageVariable imageVariable) {
        try {
            r.addPicture(imageVariable.getImageStream(),
                    imageVariable.getImageType().getImageType(), imageVariable.getKey(),
                    Units.toEMU(imageVariable.getWidth()), Units.toEMU(imageVariable.getHeight()));
            imageVariable.getImageStream().reset();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
