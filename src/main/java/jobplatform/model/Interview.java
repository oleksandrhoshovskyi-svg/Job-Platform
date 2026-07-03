package jobplatform.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Interview extends BaseEntity {

    private static final List<Interview> extent = new ArrayList<>();

    private LocalDateTime scheduledAt;
    private String interviewFormat;
    private String interviewStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    private RecruitmentStage recruitmentStage;

    protected Interview() {
    }

    public Interview(LocalDateTime scheduledAt, String interviewFormat) {
        this(scheduledAt, interviewFormat, null);
    }

    public Interview(LocalDateTime scheduledAt, String interviewFormat, RecruitmentStage recruitmentStage) {
        if (scheduledAt == null) throw new IllegalArgumentException("Interview date cannot be null.");
        if (interviewFormat == null || interviewFormat.isBlank()) {
            throw new IllegalArgumentException("Interview format cannot be empty.");
        }

        this.scheduledAt = scheduledAt;
        this.interviewFormat = interviewFormat;
        this.recruitmentStage = recruitmentStage;
        this.interviewStatus = "SCHEDULED";

        extent.add(this);
    }

    public void cancel() {
        interviewStatus = "CANCELLED";
    }

    public void complete() {
        interviewStatus = "COMPLETED";
    }

    public static List<Interview> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<Interview> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public String getInterviewFormat() {
        return interviewFormat;
    }

    public String getInterviewStatus() {
        return interviewStatus;
    }

    public RecruitmentStage getRecruitmentStage() {
        return recruitmentStage;
    }

    @Override
    public String toString() {
        return interviewFormat + " interview at " + scheduledAt;
    }
}