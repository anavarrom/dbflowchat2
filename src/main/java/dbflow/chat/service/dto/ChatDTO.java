package dbflow.chat.service.dto;

import java.time.LocalDate;
import java.io.Serializable;
import dbflow.chat.domain.enumeration.ChatType;

/**
 * A DTO for the {@link dbflow.chat.domain.Chat} entity.
 */
public class ChatDTO implements Serializable {
    
    private Long id;

    private String owner;

    private String to;

    private String subject;

    private LocalDate createdDate;

    private LocalDate lastMessageDate;

    private ChatType type;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDate getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(LocalDate lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChatDTO)) {
            return false;
        }

        return id != null && id.equals(((ChatDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ChatDTO{" +
            "id=" + getId() +
            ", owner='" + getOwner() + "'" +
            ", to='" + getTo() + "'" +
            ", subject='" + getSubject() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastMessageDate='" + getLastMessageDate() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
