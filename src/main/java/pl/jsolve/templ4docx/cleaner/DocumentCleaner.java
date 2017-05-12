package pl.jsolve.templ4docx.cleaner;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;

import pl.jsolve.sweetener.text.Strings;
import pl.jsolve.templ4docx.core.Docx;
import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.extractor.KeyExtractor;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.variable.ObjectVariable;
import pl.jsolve.templ4docx.variable.Variables;

/**
 * One variable may be shared by many XWPFRun. This class moves split variable to one XWPFRun
 * @author Lukasz Stypka
 */
public class DocumentCleaner {

    private KeyExtractor keyExtractor;

    public DocumentCleaner() {
        this.keyExtractor = new KeyExtractor();
    }

    /**
     * Main method for cleaning XWPFRun in whole document. This method moves split variable to one XWPFRun
     * @param docx Docx
     * @param variables variables
     * @param variablePattern variablePattern
     */
    public void clean(Docx docx, Variables variables, VariablePattern variablePattern) {
        List<Key> keys = keyExtractor.extractKeys(variables);
        for (XWPFParagraph paragraph : docx.getXWPFDocument().getParagraphs()) {
            clean(paragraph, keys, variablePattern);
        }

        cleanTables(docx.getXWPFDocument().getTables(), keys, variablePattern);
    }

    /**
     * Clean content of tables. This method is invoked recursively for each table
     * @param tables
     * @param keys
     * @param variablePattern
     */
    private void cleanTables(List<XWPFTable> tables, List<Key> keys, VariablePattern variablePattern) {
        for (XWPFTable table : tables) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        if (!cell.getTables().isEmpty()) {
                            cleanTables(cell.getTables(), keys, variablePattern);
                        }
                        clean(paragraph, keys, variablePattern);
                    }
                }
            }
        }
    }

    /**
     * Clean list of XWPFRun. If one variable is split between many XWPFRun, this method will move this variable to run
     * where variable begins. The text from other XWPFRuns which contain parts of found variable is cleaned.
     * @param runs
     * @param keys
     * @param variablePattern
     */
    private void clean(XWPFParagraph paragraph, List<Key> keys, VariablePattern variablePattern) {
        if (paragraph.getRuns() == null || paragraph.getRuns().isEmpty() || paragraph.getRuns().size() == 1) {
            return;
        } else {
            // validate whether xwpfRun contains any variable pattern which are not recognized
            String notRecognizedVariable = "";
            String notRecognizedPrefix = "";
            int notRecognizedVariableStartIndex = -1;
            for (int i = 0; i < paragraph.getRuns().size(); i++) {
                String text = paragraph.getRuns().get(i).text();
                if (text != null) {
                    // check whether variable is started but not ended

                    List<Integer> suffixIndexesOf = Strings.indexesOf(text, variablePattern.getSuffix());
                    if (notRecognizedVariableStartIndex != -1) {
                        if (!suffixIndexesOf.isEmpty()) {
                            notRecognizedVariable += text.substring(0, suffixIndexesOf.get(0) + 1);
                            XWPFRun startRun = paragraph.getRuns().get(notRecognizedVariableStartIndex);
                            String fixedNotRecognizedVariable = ObjectVariable
                                    .fixInvalidFieldName(notRecognizedVariable);
                            boolean executeResult = containsKey(keys, notRecognizedVariable)
                                    || containsKey(keys, fixedNotRecognizedVariable);
                            if (executeResult) {
                                // Set found variable to start run
                                String textFromStartRun = startRun.text();
                                String prefix = getFirstChar(variablePattern.getPrefix());
                                List<Integer> prefixIndexesOf = Strings.indexesOf(textFromStartRun, prefix);
                                int lastIndexOfPrefix = prefixIndexesOf.get(prefixIndexesOf.size() - 1);
                                textFromStartRun = textFromStartRun.substring(0, lastIndexOfPrefix)
                                        + notRecognizedVariable;
                                startRun = replaceRun(paragraph, notRecognizedVariableStartIndex, textFromStartRun);

                                // clean runs between start and end variable pattern
                                for (int j = notRecognizedVariableStartIndex + 1; j < i; j++) {
                                    replaceRun(paragraph, j, "");
                                }
                                text = paragraph.getRuns().get(i).text();
                                Integer suffixIndex = suffixIndexesOf.get(0);
                                replaceRun(paragraph, i, text.substring(suffixIndex + 1));
                                i = notRecognizedVariableStartIndex;
                            }

                            notRecognizedVariableStartIndex = -1;
                            notRecognizedVariable = "";
                            continue;
                        }
                        notRecognizedVariable += text;
                    }

                    String prefix = getFirstChar(variablePattern.getPrefix());
                    List<Integer> prefixIndexesOf = Strings.indexesOf(text, prefix);
                    if (!prefixIndexesOf.isEmpty() && prefixIndexesOf.size() > suffixIndexesOf.size()) {
                        notRecognizedVariableStartIndex = i;
                        notRecognizedPrefix = text.substring(prefixIndexesOf.get(prefixIndexesOf.size() - 1));
                        notRecognizedVariable = notRecognizedPrefix;
                    }
                }
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

    /**
     * @param keys
     * @param textContent
     * @return boolean which will indicate, if given string contains any key from list
     */
    private boolean containsKey(List<Key> keys, String textContent) {
        for (Key key : keys) {
            if (StringUtils.contains(textContent, key.getKey())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param prefix
     * @return Escaped first char. If string starts with \, the second char will be also included in returned string
     */
    private String getFirstChar(String prefix) {
        if (prefix.length() == 1) {
            return prefix;
        } else if (prefix.startsWith("\\") && prefix.length() > 1) {
            return prefix.substring(0, 2);
        }
        return prefix.substring(0, 1);
    }

}
