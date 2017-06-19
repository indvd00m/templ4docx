package pl.jsolve.templ4docx.variable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ClassUtils;

import pl.jsolve.templ4docx.core.VariablePattern;
import pl.jsolve.templ4docx.utils.ReflectionHelper;
import pl.jsolve.templ4docx.variable.object.DefaultConverter;
import pl.jsolve.templ4docx.variable.object.IConverter;

/**
 * @author indvd00m (gotoindvdum[at]gmail[dot]com)
 *
 */
public class ObjectVariable implements Variable {

    private final ObjectVariable parentObjectVariable;
    private final VariablePattern variablePattern;
    private final int maxNestedLevel;
    private final String key;
    private final Object value;
    private static final Pattern invalidFieldPattern = Pattern.compile("(?<=\\.)(\\w)");
    private IConverter converter = new DefaultConverter();

    private ObjectVariable(ObjectVariable parentObjectVariable, String key, Object value,
            VariablePattern variablePattern, int maxNestedLevel) {
        this.parentObjectVariable = parentObjectVariable;
        this.key = key;
        this.value = value;
        this.variablePattern = variablePattern;
        this.maxNestedLevel = maxNestedLevel;
    }

    private ObjectVariable(ObjectVariable parentObjectVariable, String key, Object value,
            VariablePattern variablePattern, IConverter converter, int maxNestedLevel) {
        this.parentObjectVariable = parentObjectVariable;
        this.key = key;
        this.value = value;
        this.variablePattern = variablePattern;
        this.converter = converter;
        this.maxNestedLevel = maxNestedLevel;
    }

    public ObjectVariable(String key, Object value, VariablePattern variablePattern) {
        this(null, key, value, variablePattern, 0);
    }

    public ObjectVariable(String key, Object value, VariablePattern variablePattern, IConverter converter) {
        this(null, key, value, variablePattern, converter, 0);
    }

    public ObjectVariable(String key, Object value, VariablePattern variablePattern, int maxNestedLevel) {
        this(null, key, value, variablePattern, maxNestedLevel);
    }

    public ObjectVariable(String key, Object value, VariablePattern variablePattern, IConverter converter,
            int maxNestedLevel) {
        this(null, key, value, variablePattern, converter, maxNestedLevel);
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public String getStringValue() {
        return converter.convert(value);
    }

    public List<ObjectVariable> getFieldVariables() {
        List<ObjectVariable> fieldVariables = new ArrayList<ObjectVariable>();

        if (value == null)
            return fieldVariables;

        if (maxNestedLevel > 0) {
            int level = getLevel();
            if (level >= maxNestedLevel)
                return fieldVariables;
        }

        Class<? extends Object> clazz = value.getClass();
        if (clazz.isArray())
            return fieldVariables;
        if (clazz.isPrimitive())
            return fieldVariables;
        if (clazz.isSynthetic())
            return fieldVariables;
        if (clazz.isAnnotation())
            return fieldVariables;
        if (clazz.isAnonymousClass())
            return fieldVariables;
        if (ClassUtils.isPrimitiveOrWrapper(clazz))
            return fieldVariables;
        if (clazz.equals(String.class))
            return fieldVariables;

        Collection<Field> fields = ReflectionHelper.getFields(value);
        for (Field field : fields) {
            String fieldKey = calculateTreeKey(field);
            Object fieldValue = ReflectionHelper.getFieldValue(value, field);
            ObjectVariable fieldVariable = new ObjectVariable(this, fieldKey, fieldValue, variablePattern, converter,
                    maxNestedLevel);
            fieldVariables.add(fieldVariable);
        }

        return fieldVariables;
    }

    public List<ObjectVariable> getFieldVariablesTree() {
        List<ObjectVariable> tree = new ArrayList<ObjectVariable>();

        List<ObjectVariable> fieldVariables = getFieldVariables();
        for (ObjectVariable var : fieldVariables) {
            tree.add(var);
            tree.addAll(var.getFieldVariablesTree());
        }

        return tree;
    }

    /**
     * @return List of path from root to current object
     */
    protected List<ObjectVariable> getPath() {
        List<ObjectVariable> path = new ArrayList<ObjectVariable>();
        path.add(this);

        ObjectVariable parent = this.parentObjectVariable;
        while (parent != null) {
            path.add(parent);
            parent = parent.parentObjectVariable;
        }

        Collections.reverse(path);
        return path;
    }

    protected int getLevel() {
        int level = 0;
        ObjectVariable parent = this.parentObjectVariable;
        while (parent != null) {
            level++;
            parent = parent.parentObjectVariable;
        }
        return level;
    }

    protected String calculateTreeKey(Field field) {
        StringBuilder sb = new StringBuilder();
        sb.append(variablePattern.getOriginalPrefix());
        sb.append(getKeyNameWithoutPattern());
        sb.append('.');
        sb.append(field.getName());
        sb.append(variablePattern.getOriginalSuffix());
        return sb.toString();
    }

    protected String getKeyNameWithoutPattern() {
        String keyName = key;
        String prefix = variablePattern.getOriginalPrefix();
        String suffix = variablePattern.getOriginalSuffix();
        if (key.startsWith(prefix) && key.endsWith(suffix)) {
            keyName = key.substring(prefix.length(), key.length() - suffix.length());
        }
        return keyName;
    }

    @Override
    public String toString() {
        return key + "=" + getStringValue();
    }

    public static String fixInvalidFieldName(String varName) {
        StringBuilder sb = new StringBuilder(varName);
        Matcher matcher = invalidFieldPattern.matcher(sb);
        int index = 0;
        while (matcher.find(index)) {
            int start = matcher.start();
            int end = matcher.end();
            index = end;

            String fieldPrefix = matcher.group(1);
            String fixedFieldPrefix = fieldPrefix.toLowerCase();
            if (!fieldPrefix.equals(fixedFieldPrefix)) {
                sb.replace(start, end, fixedFieldPrefix);
                index += fixedFieldPrefix.length() - fieldPrefix.length();
            }
        }
        String fixedVarName = sb.toString();
        return fixedVarName;
    }

    public IConverter getConverter() {
        return converter;
    }

}
