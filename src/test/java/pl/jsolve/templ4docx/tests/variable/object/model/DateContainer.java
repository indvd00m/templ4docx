package pl.jsolve.templ4docx.tests.variable.object.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class DateContainer {

    Date value;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public DateContainer() {

    }

    public DateContainer(Date value) {
        super();
        this.value = value;
    }

    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "date=" + sdf.format(value);
    }

}
