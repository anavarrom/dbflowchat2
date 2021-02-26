package dbflow.chat.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * A Message.
 */
@Entity
@Table(name = "message")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "message")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "jhi_from")
    private String from;

    @Column(name = "jhi_to")
    private String to;

    @Column(name = "body")
    private String body;

    @Column(name = "jhi_order")
    private Long order;

    @Column(name = "emitted_date")
    private LocalDate emittedDate;

    @Column(name = "received_date")
    private LocalDate receivedDate;

    @Column(name = "read_date")
    private LocalDate readDate;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitud")
    private Double longitud;

    @ManyToOne
    @JsonIgnoreProperties(value = "messages", allowSetters = true)
    private Chat chat;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public Message from(String from) {
        this.from = from;
        return this;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public Message to(String to) {
        this.to = to;
        return this;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public Message body(String body) {
        this.body = body;
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getOrder() {
        return order;
    }

    public Message order(Long order) {
        this.order = order;
        return this;
    }

    public void setOrder(Long order) {
        this.order = order;
    }

    public LocalDate getEmittedDate() {
        return emittedDate;
    }

    public Message emittedDate(LocalDate emittedDate) {
        this.emittedDate = emittedDate;
        return this;
    }

    public void setEmittedDate(LocalDate emittedDate) {
        this.emittedDate = emittedDate;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public Message receivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
        return this;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public LocalDate getReadDate() {
        return readDate;
    }

    public Message readDate(LocalDate readDate) {
        this.readDate = readDate;
        return this;
    }

    public void setReadDate(LocalDate readDate) {
        this.readDate = readDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Message latitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitud() {
        return longitud;
    }

    public Message longitud(Double longitud) {
        this.longitud = longitud;
        return this;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Chat getChat() {
        return chat;
    }

    public Message chat(Chat chat) {
        this.chat = chat;
        return this;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        return id != null && id.equals(((Message) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Message{" +
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
            "}";
    }
}
