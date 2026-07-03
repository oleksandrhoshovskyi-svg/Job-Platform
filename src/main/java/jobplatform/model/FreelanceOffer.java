package jobplatform.model;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class FreelanceOffer extends JobOffer {

    private Double estimatedBudget;
    private boolean milestoneBased;

    protected FreelanceOffer() {
    }

    public FreelanceOffer(
            Employer createdBy,
            String title,
            String description,
            String location,
            String workMode,
            String salaryRange,
            List<String> requirements,
            Double estimatedBudget,
            boolean milestoneBased
    ) {
        this(
                createdBy,
                title,
                description,
                location,
                workMode,
                salaryRange,
                requirements,
                createDefaultRecruitmentProcess(title),
                estimatedBudget,
                milestoneBased
        );
    }

    public FreelanceOffer(
            Employer createdBy,
            String title,
            String description,
            String location,
            String workMode,
            String salaryRange,
            List<String> requirements,
            RecruitmentProcess recruitmentProcess,
            Double estimatedBudget,
            boolean milestoneBased
    ) {
        super(createdBy, title, description, location, workMode, salaryRange, requirements, recruitmentProcess);

        if (estimatedBudget != null && estimatedBudget < 0) {
            throw new IllegalArgumentException("Estimated budget cannot be negative.");
        }

        this.estimatedBudget = estimatedBudget == null ? 0.0 : estimatedBudget;
        this.milestoneBased = milestoneBased;
    }

    public Double getEstimatedBudget() {
        return estimatedBudget;
    }

    public boolean isMilestoneBased() {
        return milestoneBased;
    }

    @Override
    public String toString() {
        return "[Freelance] " + getTitle() + " (" + getOfferStatus() + ")";
    }
}