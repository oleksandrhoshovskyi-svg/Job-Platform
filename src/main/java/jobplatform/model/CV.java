package jobplatform.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class CV extends BaseEntity {

    private static final List<CV> extent = new ArrayList<>();

    private String fileName;
    private LocalDateTime uploadedAt;
    private boolean defaultCV;

    @ManyToOne(fetch = FetchType.EAGER)
    private CandidateProfile profile;

    protected CV() {
    }

    public CV(String fileName, CandidateProfile profile) {
        this(profile, fileName, false);
    }

    public CV(String fileName, CandidateProfile profile, boolean defaultCV) {
        this(profile, fileName, defaultCV);
    }

    public CV(CandidateProfile profile, String fileName, boolean defaultCV) {
        if (profile == null) throw new IllegalArgumentException("Candidate profile cannot be null.");
        if (fileName == null || fileName.isBlank()) throw new IllegalArgumentException("File name cannot be empty.");

        this.profile = profile;
        this.fileName = fileName;
        this.defaultCV = defaultCV;
        this.uploadedAt = LocalDateTime.now();

        profile.addCV(this);

        extent.add(this);
    }

    public void markAsDefault() {
        for (CV cv : profile.getCVs()) {
            cv.defaultCV = false;
        }
        defaultCV = true;
    }

    public static List<CV> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<CV> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public CandidateProfile getProfile() {
        return profile;
    }

    public String getFileName() {
        return fileName;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public boolean isDefaultCV() {
        return defaultCV;
    }

    public boolean isDefault() {
        return defaultCV;
    }

    @Override
    public String toString() {
        return fileName + (defaultCV ? " (default)" : "");
    }
}