package jobplatform.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class RecruitmentStage extends BaseEntity {

    private static final List<RecruitmentStage> extent = new ArrayList<>();

    private String name;
    private int orderNumber;
    private String stageType;
    private int deadlineDays;

    @ManyToOne(fetch = FetchType.EAGER)
    private RecruitmentProcess recruitmentProcess;

    protected RecruitmentStage() {
    }

    public RecruitmentStage(
            RecruitmentProcess recruitmentProcess,
            String name,
            int orderNumber,
            String stageType,
            int deadlineDays
    ) {
        if (recruitmentProcess == null) throw new IllegalArgumentException("Recruitment process cannot be null.");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Stage name cannot be empty.");
        if (orderNumber <= 0) throw new IllegalArgumentException("Order number must be positive.");
        if (deadlineDays < 0) throw new IllegalArgumentException("Deadline days cannot be negative.");

        this.recruitmentProcess = recruitmentProcess;
        this.name = name;
        this.orderNumber = orderNumber;
        this.stageType = stageType;
        this.deadlineDays = deadlineDays;

        recruitmentProcess.addStage(this);

        extent.add(this);
    }

    public static List<RecruitmentStage> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<RecruitmentStage> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public RecruitmentProcess getRecruitmentProcess() {
        return recruitmentProcess;
    }

    public String getName() {
        return name;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public String getStageType() {
        return stageType;
    }

    public int getDeadlineDays() {
        return deadlineDays;
    }

    @Override
    public String toString() {
        return orderNumber + ". " + name;
    }
}