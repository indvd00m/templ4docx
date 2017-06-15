package pl.jsolve.templ4docx.util;

import java.util.ArrayList;
import java.util.List;

public class Key {

    private String key;
    private VariableType variableType;
    private List<Key> subKeys;

    public Key(String key, VariableType variableType) {
        this.key = key;
        this.variableType = variableType;
        this.subKeys = new ArrayList<Key>();
    }

    public String getKey() {
        return key;
    }

    public VariableType getVariableType() {
        return variableType;
    }

    public void addSubKey(Key subkey) {
        this.subKeys.add(subkey);
    }

    public Key getFirstSubKey() {
        return subKeys.get(0);
    }

    public boolean containsSubKey() {
        return !subKeys.isEmpty();
    }

    public List<Key> getSubKeys() {
        return subKeys;
    }

    @Override
    public String toString() {
        return "Key [key=" + key + ", variableType=" + variableType + ", subKeys=" + subKeys + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Key other = (Key) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

}
