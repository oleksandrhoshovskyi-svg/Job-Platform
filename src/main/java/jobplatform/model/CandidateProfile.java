package jobplatform.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class CandidateProfile extends BaseEntity {

    private static final List<CandidateProfile> extent = new ArrayList<>();

    private String headline;
    @Column(length = 2000)
    private String summary;
    private String profileStatus;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private JobSeeker owner;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<CV> cvs = new ArrayList<>();

    @OneToMany(mappedBy = "candidateProfile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<CandidateSkill> candidateSkills = new ArrayList<>();

    protected CandidateProfile() {
    }

    CandidateProfile(JobSeeker owner) {
        if (owner == null) throw new IllegalArgumentException("Owner cannot be null.");

        this.owner = owner;
        this.profileStatus = "INCOMPLETE";

        extent.add(this);
    }

    public int computeCompletenessLevel() {
        int completed = 0;
        int total = 5;

        if (headline != null && !headline.isBlank()) completed++;
        if (summary != null && !summary.isBlank()) completed++;
        if (owner.getPreferredPosition() != null && !owner.getPreferredPosition().isBlank()) completed++;
        if (owner.getLocation() != null && !owner.getLocation().isBlank()) completed++;
        if (!cvs.isEmpty()) completed++;

        return completed * 100 / total;
    }

    public boolean isComplete() {
        return getMissingRequiredData().isEmpty();
    }

    public List<String> getMissingRequiredData() {
        List<String> missing = new ArrayList<>();

        if (headline == null || headline.isBlank()) missing.add("headline");
        if (summary == null || summary.isBlank()) missing.add("summary");
        if (owner.getPreferredPosition() == null || owner.getPreferredPosition().isBlank()) {
            missing.add("preferred position");
        }
        if (owner.getLocation() == null || owner.getLocation().isBlank()) {
            missing.add("location");
        }
        if (cvs.isEmpty()) missing.add("CV");

        return missing;
    }

    public void completeMissingData(String headline, String summary, String preferredPosition, String location) {
        setHeadline(headline);
        setSummary(summary);
        owner.setPreferredPosition(preferredPosition);
        owner.setLocation(location);
        profileStatus = isComplete() ? "COMPLETE" : "INCOMPLETE";
    }

    void addCV(CV cv) {
        if (!cvs.contains(cv)) {
            cvs.add(cv);
        }
    }

    void addCandidateSkill(CandidateSkill candidateSkill) {
        if (!candidateSkills.contains(candidateSkill)) {
            candidateSkills.add(candidateSkill);
        }
    }

    public boolean hasCV(CV cv) {
        return cvs.contains(cv);
    }

    public static List<CandidateProfile> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<CandidateProfile> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public JobSeeker getOwner() {
        return owner;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline == null ? "" : headline.trim();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? "" : summary.trim();
    }

    public String getProfileStatus() {
        return profileStatus;
    }

    public List<CV> getCVs() {
        return Collections.unmodifiableList(cvs);
    }

    public List<CandidateSkill> getCandidateSkills() {
        return Collections.unmodifiableList(candidateSkills);
    }

    @Override
    public String toString() {
        return "CandidateProfile of " + owner.getPlatformUser().getFullName()
                + " (" + profileStatus + ")";
    }
}