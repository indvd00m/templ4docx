package pl.jsolve.templ4docx.tests.variable.table.model;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class ObjectContainer {

    Object value;

    public ObjectContainer(Object value) {
        super();
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ObjectContainer [value=" + value + "]";
    }

}
