package pl.jsolve.templ4docx.tests.variable.table.model;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class IncompleteRequiredType {

    String name;
    String description;
    int count;

    public IncompleteRequiredType(String name) {
        super();
        this.name = name;
    }

    public IncompleteRequiredType(String name, String description, int count) {
        super();
        this.name = name;
        this.description = description;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCount() {
        return count;
    }

}
