package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
@Entity
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private LocalDateTime scheduledTime;
    private String recipient;
    private boolean isSent;


    public NotificationTask(Long id, String message, LocalDateTime scheduledTime, String recipient, boolean isSent) {
        this.id = id;
        this.message = message;
        this.scheduledTime = scheduledTime;
        this.recipient = recipient;
        this.isSent = isSent;
    }

    public NotificationTask() {

    }

    // Getters and Setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", recipient='" + recipient + '\'' +
                ", isSent=" + isSent +
                '}';
    }
}
