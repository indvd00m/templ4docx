package pl.jsolve.templ4docx.variable.object;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class DefaultConverter implements IConverter {

    @Override
    public String convert(Object value) {
        if (value != null) {
            return value.toString();
        }
        return null;
    }

}
