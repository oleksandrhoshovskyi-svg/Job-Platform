package jobplatform.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class SavedOffer extends BaseEntity {

    private static final List<SavedOffer> extent = new ArrayList<>();

    private LocalDateTime savedAt;

    @Column(length = 1000)
    private String note;

    @ManyToOne(fetch = FetchType.EAGER)
    private JobSeeker jobSeeker;

    @ManyToOne(fetch = FetchType.EAGER)
    private JobOffer jobOffer;

    protected SavedOffer() {
    }

    public SavedOffer(JobSeeker jobSeeker, JobOffer jobOffer, String note) {
        if (jobSeeker == null) throw new IllegalArgumentException("JobSeeker cannot be null.");
        if (jobOffer == null) throw new IllegalArgumentException("JobOffer cannot be null.");

        this.jobSeeker = jobSeeker;
        this.jobOffer = jobOffer;
        this.note = note == null ? "" : note.trim();
        this.savedAt = LocalDateTime.now();

        jobSeeker.addSavedOffer(this);

        extent.add(this);
    }

    public static void removeFromExtent(SavedOffer savedOffer) {
        extent.remove(savedOffer);
    }

    public static List<SavedOffer> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<SavedOffer> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? "" : note.trim();
    }

    public JobSeeker getJobSeeker() {
        return jobSeeker;
    }

    public JobOffer getJobOffer() {
        return jobOffer;
    }

    @Override
    public String toString() {
        return jobOffer.getTitle() + " saved by " + jobSeeker.getPlatformUser().getFullName();
    }
}