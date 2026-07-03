package jobplatform.model;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Skill extends BaseEntity {

    private static final List<Skill> extent = new ArrayList<>();

    private String name;
    private String category;

    protected Skill() {
    }

    public Skill(String name, String category) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Skill name cannot be empty.");

        this.name = name;
        this.category = category;

        extent.add(this);
    }

    public static List<Skill> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<Skill> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return name;
    }
}