package pl.jsolve.templ4docx.tests.variable.table.model;

import java.util.List;

/**
 * @see https://jsolve.github.io/java/templ4docx-2-0-0-table-variables/
 *
 */
public class Student {
    private String name;
    private Integer age;
    private String logoPath;
    private List<String> languages;

    public Student(String name, Integer age, String logoPath, List<String> languages) {
        this.name = name;
        this.age = age;
        this.logoPath = logoPath;
        this.languages = languages;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public List<String> getLanguages() {
        return languages;
    }
}
