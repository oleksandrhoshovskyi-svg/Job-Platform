package jobplatform.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Company extends BaseEntity {

    private static final List<Company> extent = new ArrayList<>();

    private String name;
    private String description;
    private String website;
    private String location;
    private String verificationStatus;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<Employer> employers = new ArrayList<>();

    protected Company() {
    }

    public Company(String name, String description, String website, String location) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Company name cannot be empty.");

        this.name = name;
        this.description = description;
        this.website = website;
        this.location = location;
        this.verificationStatus = "PENDING";

        extent.add(this);
    }

    void addEmployer(Employer employer) {
        if (!employers.contains(employer)) {
            employers.add(employer);
        }
    }

    public static List<Company> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<Company> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public String getLocation() {
        return location;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public List<Employer> getEmployers() {
        return Collections.unmodifiableList(employers);
    }

    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
}