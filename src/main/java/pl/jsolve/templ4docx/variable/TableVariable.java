package pl.jsolve.templ4docx.variable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.jsolve.templ4docx.exception.IncorrectNumberOfRowsException;
import pl.jsolve.templ4docx.util.Key;
import pl.jsolve.templ4docx.util.VariableType;

public class TableVariable implements Variable {

    private List<List<? extends Variable>> variables;
    private int numberOfRows = 0;

    public TableVariable() {
        this.variables = new ArrayList<List<? extends Variable>>();
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public List<List<? extends Variable>> getVariables() {
        return variables;
    }

    public void addVariable(List<? extends Variable> variable) {
        if (variable.isEmpty()) {
            return;
        }
        if (numberOfRows == 0) {
            numberOfRows = variable.size();
        } else if (numberOfRows != variable.size()) {
            throw new IncorrectNumberOfRowsException("Incorrect number of rows. Expected " + numberOfRows + " but was "
                    + variable.size());
        }
        this.variables.add(variable);
        // object variables
        Variable firstVariable = variable.get(0);
        if (firstVariable instanceof ObjectVariable) {
            ObjectVariable firstObjVar = (ObjectVariable) firstVariable;
            List<ObjectVariable> fieldVarsTree = firstObjVar.getFieldVariablesTree();
            if (!fieldVarsTree.isEmpty()) {
                for (int i = 0; i < fieldVarsTree.size(); i++) {
                    List<ObjectVariable> fieldVarColumn = new ArrayList<ObjectVariable>(variable.size());
                    this.variables.add(fieldVarColumn);
                }
                Map<String, Integer> keysIndexByName = new HashMap<String, Integer>();
                // first variable now used as sample for all next variables
                keysIndexByName.put(firstObjVar.getKey(), 0);
                for (int i = 0; i < fieldVarsTree.size(); i++) {
                    ObjectVariable var = fieldVarsTree.get(i);
                    keysIndexByName.put(var.getKey(), i + 1);
                }
                for (Variable var : variable) {
                    if (var instanceof ObjectVariable == false) {
                        throw new IllegalArgumentException(
                                String.format("Expected type of variable: %s, but actual is: %s",
                                        ObjectVariable.class.getName(), var.getClass().getName()));
                    }
                    ObjectVariable rowFieldVar = (ObjectVariable) var;
                    if (!rowFieldVar.getKey().equals(firstObjVar.getKey())) {
                        throw new IllegalArgumentException(String.format(
                                "Expected name of variable: %s, but actual is: %s", firstObjVar, rowFieldVar.getKey()));
                    }
                    List<ObjectVariable> columnFieldVars = rowFieldVar.getFieldVariablesTree();
                    for (int i = 0; i < columnFieldVars.size(); i++) {
                        ObjectVariable columnFieldVar = columnFieldVars.get(i);
                        Integer columnIndex = keysIndexByName.get(columnFieldVar.getKey());
                        if (columnIndex == null)
                            continue;
                        @SuppressWarnings("unchecked")
                        List<ObjectVariable> columnList = (List<ObjectVariable>) this.variables.get(columnIndex);
                        columnList.add(columnFieldVar);
                    }
                }
            }
        }
    }

    public Set<Key> getKeys() {
        return extract(variables);
    }

    private Set<Key> extract(List<List<? extends Variable>> variables) {
        Set<Key> keys = new HashSet<Key>();

        for (List<? extends Variable> variable : variables) {
            if (variable.isEmpty()) {
                break;
            }
            Variable firstVariable = variable.get(0);
            if (firstVariable instanceof TextVariable) {
                keys.add(new Key(((TextVariable) firstVariable).getKey(), VariableType.TEXT));
            } else if (firstVariable instanceof ImageVariable) {
                keys.add(new Key(((ImageVariable) firstVariable).getKey(), VariableType.IMAGE));
            } else if (firstVariable instanceof BulletListVariable) {
                keys.add(new Key(((BulletListVariable) firstVariable).getKey(), VariableType.BULLET_LIST));
            } else if (firstVariable instanceof ObjectVariable) {
                keys.add(new Key(((ObjectVariable) firstVariable).getKey(), VariableType.OBJECT));
            } else if (firstVariable instanceof TableVariable) {
                keys.addAll(extract(((TableVariable) firstVariable).getVariables()));
            }
        }

        return keys;
    }

    public boolean containsKey(String key) {
        return containsKey(variables, key);
    }

    private boolean containsKey(List<List<? extends Variable>> variables, String key) {

        for (List<? extends Variable> variable : variables) {
            if (variable.isEmpty()) {
                break;
            }
            Variable firstVariable = variable.get(0);
            if (firstVariable instanceof TextVariable) {
                if (key.equals(((TextVariable) firstVariable).getKey())) {
                    return true;
                }
            } else if (firstVariable instanceof ImageVariable) {
                if (key.equals(((ImageVariable) firstVariable).getKey())) {
                    return true;
                }
            } else if (firstVariable instanceof BulletListVariable) {
                if (key.equals(((BulletListVariable) firstVariable).getKey())) {
                    return true;
                }
            } else if (firstVariable instanceof ObjectVariable) {
                if (key.equals(((ObjectVariable) firstVariable).getKey())) {
                    return true;
                }
            } else if (firstVariable instanceof TableVariable) {
                boolean containsKey = containsKey(((TableVariable) firstVariable).getVariables(), key);
                if (containsKey) {
                    return true;
                }
            }
        }
        return false;
    }

    public Variable getVariable(Key key, int index) {
        return getVariable(variables, key, index);
    }

    private Variable getVariable(List<List<? extends Variable>> variables, Key key, int index) {
        for (List<? extends Variable> variable : variables) {
            if (variable.isEmpty() || variable.size() <= index) {
                break;
            }
            Variable firstVariable = variable.get(0);
            if (firstVariable instanceof TextVariable) {
                if (key.getKey().equals(((TextVariable) firstVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstVariable instanceof ImageVariable) {
                if (key.getKey().equals(((ImageVariable) firstVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstVariable instanceof BulletListVariable) {
                if (key.getKey().equals(((BulletListVariable) firstVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstVariable instanceof ObjectVariable) {
                if (key.getKey().equals(((ObjectVariable) firstVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstVariable instanceof TableVariable) {
                Variable foundVariable = getVariable(((TableVariable) firstVariable).getVariables(), key, index);
                if (foundVariable != null) {
                    return foundVariable;
                }
            }
        }
        return null;
    }
}
