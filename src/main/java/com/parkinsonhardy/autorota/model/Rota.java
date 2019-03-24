package com.parkinsonhardy.autorota.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Rota {

    private @Id @GeneratedValue Long id;
    private String name;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private int timeoutSeconds;
    private String status; // todo enum
    @Lob
    @Column
    private String stringRepresentation;

    private Rota() {}

    public Rota(String name, LocalDateTime startDateTime, int timeoutSeconds, String status, String stringRepresentation) {
        this.name = name;
        this.startDateTime = startDateTime;
        this.timeoutSeconds = timeoutSeconds;
        this.status = status;
        this.stringRepresentation = stringRepresentation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStringRepresentation() {
        return stringRepresentation;
    }

    public void setStringRepresentation(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rota that = (Rota) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
