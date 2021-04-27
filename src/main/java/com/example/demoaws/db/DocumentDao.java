package com.example.demoaws.db;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.Random;

public class DocumentDao {

    private JdbcTemplate jdbcTemplate;

    private final Random random = new Random();

    private final String QUERY = "INSERT INTO documents (content, published_on, id) VALUES (?, ?, ?)";

    public DocumentDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(DocumentEntity documentEntity) {
        Long id = random.nextLong();
        jdbcTemplate.update(QUERY,
                documentEntity.getContent(),
                Timestamp.valueOf(documentEntity.getPublishedOn()),
                id);
        return id;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}