package jobplatform.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class CandidateSkill extends BaseEntity {

    private static final List<CandidateSkill> extent = new ArrayList<>();

    private String proficiencyLevel;
    private int yearsOfExperience;

    @ManyToOne(fetch = FetchType.EAGER)
    private CandidateProfile candidateProfile;

    @ManyToOne(fetch = FetchType.EAGER)
    private Skill skill;

    protected CandidateSkill() {
    }

    public CandidateSkill(CandidateProfile candidateProfile, Skill skill, String proficiencyLevel, int yearsOfExperience) {
        if (candidateProfile == null) throw new IllegalArgumentException("Candidate profile cannot be null.");
        if (skill == null) throw new IllegalArgumentException("Skill cannot be null.");
        if (yearsOfExperience < 0) throw new IllegalArgumentException("Years of experience cannot be negative.");

        for (CandidateSkill candidateSkill : candidateProfile.getCandidateSkills()) {
            if (candidateSkill.getSkill().getName().equalsIgnoreCase(skill.getName())) {
                throw new IllegalArgumentException("This skill is already assigned to the candidate profile.");
            }
        }

        this.candidateProfile = candidateProfile;
        this.skill = skill;
        this.proficiencyLevel = proficiencyLevel;
        this.yearsOfExperience = yearsOfExperience;

        candidateProfile.addCandidateSkill(this);

        extent.add(this);
    }

    public static List<CandidateSkill> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<CandidateSkill> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public CandidateProfile getCandidateProfile() {
        return candidateProfile;
    }

    public Skill getSkill() {
        return skill;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public String getSkillName() {
        return skill != null ? skill.getName() : "";
    }

    @Override
    public String toString() {
        return skill.getName() + " - " + proficiencyLevel
                + ", " + yearsOfExperience + " years";
    }
}