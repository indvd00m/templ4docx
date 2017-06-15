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
            throw new IncorrectNumberOfRowsException(
                    "Incorrect number of rows. Expected " + numberOfRows + " but was " + variable.size());
        }
        this.variables.add(variable);
        // object variables
        ObjectVariable templateObjVar = null;
        for (Variable var : variable) {
            if (var instanceof ObjectVariable) {
                ObjectVariable objVar = (ObjectVariable) var;
                Object value = objVar.getValue();
                if (value != null) {
                    templateObjVar = objVar;
                    break;
                }
            }
        }
        if (templateObjVar != null) {
            // first not null variable now used as template for all next variables
            List<ObjectVariable> fieldVarsTree = templateObjVar.getFieldVariablesTree();
            if (!fieldVarsTree.isEmpty()) {
                for (int i = 0; i < fieldVarsTree.size(); i++) {
                    List<ObjectVariable> fieldVarColumn = new ArrayList<ObjectVariable>(variable.size());
                    for (int j = 0; j < variable.size(); j++) {
                        fieldVarColumn.add(null);
                    }
                    this.variables.add(fieldVarColumn);
                }
                Map<String, Integer> keysIndexByName = new HashMap<String, Integer>();
                keysIndexByName.put(templateObjVar.getKey(), 0);
                for (int i = 0; i < fieldVarsTree.size(); i++) {
                    ObjectVariable var = fieldVarsTree.get(i);
                    keysIndexByName.put(var.getKey(), i + 1);
                }
                for (int varIndex = 0; varIndex < variable.size(); varIndex++) {
                    Variable var = variable.get(varIndex);
                    if (var instanceof ObjectVariable == false) {
                        throw new IllegalArgumentException(
                                String.format("Expected type of variable: %s, but actual is: %s",
                                        ObjectVariable.class.getName(), var.getClass().getName()));
                    }
                    ObjectVariable rowFieldVar = (ObjectVariable) var;
                    if (!rowFieldVar.getKey().equals(templateObjVar.getKey())) {
                        throw new IllegalArgumentException(
                                String.format("Expected name of variable: %s, but actual is: %s", templateObjVar,
                                        rowFieldVar.getKey()));
                    }
                    List<ObjectVariable> columnFieldVars = rowFieldVar.getFieldVariablesTree();
                    for (ObjectVariable columnFieldVar : columnFieldVars) {
                        Integer columnIndex = keysIndexByName.get(columnFieldVar.getKey());
                        if (columnIndex == null)
                            continue;
                        @SuppressWarnings("unchecked")
                        List<ObjectVariable> columnList = (List<ObjectVariable>) this.variables.get(columnIndex);
                        columnList.set(varIndex, columnFieldVar);
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
                continue;
            }
            Variable firstNotNullVariable = getFirstNotNull(variable);
            if (firstNotNullVariable instanceof TextVariable) {
                keys.add(new Key(((TextVariable) firstNotNullVariable).getKey(), VariableType.TEXT));
            } else if (firstNotNullVariable instanceof ImageVariable) {
                keys.add(new Key(((ImageVariable) firstNotNullVariable).getKey(), VariableType.IMAGE));
            } else if (firstNotNullVariable instanceof BulletListVariable) {
                keys.add(new Key(((BulletListVariable) firstNotNullVariable).getKey(), VariableType.BULLET_LIST));
            } else if (firstNotNullVariable instanceof ObjectVariable) {
                keys.add(new Key(((ObjectVariable) firstNotNullVariable).getKey(), VariableType.OBJECT));
            } else if (firstNotNullVariable instanceof TableVariable) {
                keys.addAll(extract(((TableVariable) firstNotNullVariable).getVariables()));
            }
        }

        return keys;
    }

    private Variable getFirstNotNull(List<? extends Variable> variable) {
        for (Variable var : variable) {
            if (var != null)
                return var;
        }
        return null;
    }

    public boolean containsKey(String key) {
        return containsKey(variables, key);
    }

    private boolean containsKey(List<List<? extends Variable>> variables, String key) {

        for (List<? extends Variable> variable : variables) {
            if (variable.isEmpty()) {
                continue;
            }
            Variable firstNotNullVariable = getFirstNotNull(variable);
            if (firstNotNullVariable instanceof TextVariable) {
                if (key.equals(((TextVariable) firstNotNullVariable).getKey())) {
                    return true;
                }
            } else if (firstNotNullVariable instanceof ImageVariable) {
                if (key.equals(((ImageVariable) firstNotNullVariable).getKey())) {
                    return true;
                }
            } else if (firstNotNullVariable instanceof BulletListVariable) {
                if (key.equals(((BulletListVariable) firstNotNullVariable).getKey())) {
                    return true;
                }
            } else if (firstNotNullVariable instanceof ObjectVariable) {
                if (key.equals(((ObjectVariable) firstNotNullVariable).getKey())) {
                    return true;
                }
            } else if (firstNotNullVariable instanceof TableVariable) {
                boolean containsKey = containsKey(((TableVariable) firstNotNullVariable).getVariables(), key);
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
                continue;
            }
            Variable firstNotNullVariable = getFirstNotNull(variable);
            if (firstNotNullVariable instanceof TextVariable) {
                if (key.getKey().equals(((TextVariable) firstNotNullVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstNotNullVariable instanceof ImageVariable) {
                if (key.getKey().equals(((ImageVariable) firstNotNullVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstNotNullVariable instanceof BulletListVariable) {
                if (key.getKey().equals(((BulletListVariable) firstNotNullVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstNotNullVariable instanceof ObjectVariable) {
                if (key.getKey().equals(((ObjectVariable) firstNotNullVariable).getKey())) {
                    return variable.get(index);
                }
            } else if (firstNotNullVariable instanceof TableVariable) {
                Variable foundVariable = getVariable(((TableVariable) firstNotNullVariable).getVariables(), key, index);
                if (foundVariable != null) {
                    return foundVariable;
                }
            }
        }
        return null;
    }
}
