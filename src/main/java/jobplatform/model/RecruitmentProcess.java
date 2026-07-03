package jobplatform.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class RecruitmentProcess extends BaseEntity {

    private static final List<RecruitmentProcess> extent = new ArrayList<>();

    private String name;
    private String processStatus;
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "recruitmentProcess", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<RecruitmentStage> stages = new ArrayList<>();

    protected RecruitmentProcess() {
    }

    public RecruitmentProcess(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Recruitment process name cannot be empty.");

        this.name = name;
        this.processStatus = "PLANNED";

        extent.add(this);
    }

    public void startProcess() {
        processStatus = "ACTIVE";
        startDate = LocalDate.now();
    }

    public void finishProcess() {
        processStatus = "FINISHED";
        endDate = LocalDate.now();
    }

    void addStage(RecruitmentStage stage) {
        if (!stages.contains(stage)) {
            stages.add(stage);
        }
    }

    public static List<RecruitmentProcess> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<RecruitmentProcess> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public String getName() {
        return name;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<RecruitmentStage> getStages() {
        return Collections.unmodifiableList(stages);
    }

    @Override
    public String toString() {
        return name + " (" + processStatus + ")";
    }
}