package jobplatform.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
public class Notification extends BaseEntity {

    private static final List<Notification> extent = new ArrayList<>();

    @Column(length = 2000)
    private String message;

    private LocalDateTime sentAt;
    private boolean readStatus;
    private String notificationType;

    @ManyToOne(fetch = FetchType.EAGER)
    private PlatformUser recipient;

    protected Notification() {
    }

    public Notification(String message, String notificationType, PlatformUser recipient) {
        if (message == null || message.isBlank()) throw new IllegalArgumentException("Notification message cannot be empty.");
        if (recipient == null) throw new IllegalArgumentException("Notification recipient cannot be null.");

        this.message = message;
        this.notificationType = notificationType;
        this.recipient = recipient;
        this.sentAt = LocalDateTime.now();
        this.readStatus = false;

        recipient.addNotification(this);

        extent.add(this);
    }

    public void markAsRead() {
        readStatus = true;
    }

    public static List<Notification> getExtent() {
        return Collections.unmodifiableList(extent);
    }

    public static void setExtent(List<Notification> loaded) {
        extent.clear();
        extent.addAll(loaded);
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public PlatformUser getRecipient() {
        return recipient;
    }

    @Override
    public String toString() {
        return "[" + notificationType + "] " + message;
    }
}