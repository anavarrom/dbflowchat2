package dbflow.chat.service.dto;

import java.time.LocalDate;
import java.io.Serializable;

/**
 * A DTO for the {@link dbflow.chat.domain.Message} entity.
 */
public class MessageDTO implements Serializable {
    
    private Long id;

    private String from;

    private String to;

    private String body;

    private Long order;

    private LocalDate emittedDate;

    private LocalDate receivedDate;

    private LocalDate readDate;

    private Double latitude;

    private Double longitud;


    private Long chatId;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getOrder() {
        return order;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public LocalDate getEmittedDate() {
        return emittedDate;
    }

    public void setEmittedDate(LocalDate emittedDate) {
        this.emittedDate = emittedDate;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public LocalDate getReadDate() {
        return readDate;
    }

    public void setReadDate(LocalDate readDate) {
        this.readDate = readDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MessageDTO)) {
            return false;
        }

        return id != null && id.equals(((MessageDTO) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MessageDTO{" +
            "id=" + getId() +
            ", from='" + getFrom() + "'" +
            ", to='" + getTo() + "'" +
            ", body='" + getBody() + "'" +
            ", order=" + getOrder() +
            ", emittedDate='" + getEmittedDate() + "'" +
            ", receivedDate='" + getReceivedDate() + "'" +
            ", readDate='" + getReadDate() + "'" +
            ", latitude=" + getLatitude() +
            ", longitud=" + getLongitud() +
            ", chatId=" + getChatId() +
            "}";
    }
}
