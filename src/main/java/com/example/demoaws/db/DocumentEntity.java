package com.example.demoaws.db;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class DocumentEntity {

    private Long id;

    private String creator;

    private String content;

    private LocalDateTime publishedOn;

    private byte[] file;

    public DocumentEntity() {
    }

    public DocumentEntity(final String string) {
        this.content = string;
        this.publishedOn = ZonedDateTime.now(ZoneId.of("Europe/Moscow")).toLocalDateTime();
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(final LocalDateTime publishedOn) {
        this.publishedOn = publishedOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(final String creator) {
        this.creator = creator;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(final byte[] file) {
        this.file = file;
    }
}
