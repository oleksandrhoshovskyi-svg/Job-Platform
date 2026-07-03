package jobplatform.model;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class PermanentOffer extends JobOffer {

    private int noticePeriodDays;
    private boolean benefitsIncluded;

    protected PermanentOffer() {
    }

    public PermanentOffer(Employer createdBy, String title, String description, String location, String workMode, String salaryRange, List<String> requirements, int noticePeriodDays, boolean benefitsIncluded) {
        this(createdBy, title, description, location, workMode, salaryRange, requirements, createDefaultRecruitmentProcess(title), noticePeriodDays, benefitsIncluded);
    }

    public PermanentOffer(Employer createdBy, String title, String description, String location, String workMode, String salaryRange, List<String> requirements, RecruitmentProcess recruitmentProcess, int noticePeriodDays, boolean benefitsIncluded) {
        super(createdBy, title, description, location, workMode, salaryRange, requirements, recruitmentProcess);

        if (noticePeriodDays < 0) throw new IllegalArgumentException("Notice period cannot be negative.");

        this.noticePeriodDays = noticePeriodDays;
        this.benefitsIncluded = benefitsIncluded;
    }

    public int getNoticePeriodDays() {
        return noticePeriodDays;
    }

    public boolean isBenefitsIncluded() {
        return benefitsIncluded;
    }

    @Override
    public String toString() {
        return "[Permanent] " + getTitle() + " (" + getOfferStatus() + ")";
    }
}