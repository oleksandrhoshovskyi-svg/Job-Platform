package jobplatform.model;

import javax.persistence.Entity;
import java.util.List;

@Entity
public class ContractOffer extends JobOffer {

    private int contractDurationMonths;
    private boolean extendable;

    protected ContractOffer() {
    }

    public ContractOffer(
            Employer createdBy,
            String title,
            String description,
            String location,
            String workMode,
            String salaryRange,
            List<String> requirements,
            int contractDurationMonths,
            boolean extendable
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
                contractDurationMonths,
                extendable
        );
    }

    public ContractOffer(
            Employer createdBy,
            String title,
            String description,
            String location,
            String workMode,
            String salaryRange,
            List<String> requirements,
            RecruitmentProcess recruitmentProcess,
            int contractDurationMonths,
            boolean extendable
    ) {
        super(createdBy, title, description, location, workMode, salaryRange, requirements, recruitmentProcess);

        if (contractDurationMonths <= 0) {
            throw new IllegalArgumentException("Contract duration must be positive.");
        }

        this.contractDurationMonths = contractDurationMonths;
        this.extendable = extendable;
    }

    public int getContractDurationMonths() {
        return contractDurationMonths;
    }

    public boolean isExtendable() {
        return extendable;
    }

    @Override
    public String toString() {
        return "[Contract] " + getTitle() + " (" + getOfferStatus() + ")";
    }
}