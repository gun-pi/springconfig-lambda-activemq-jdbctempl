package com.example.demoaws.db;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.util.Random;

public class DocumentDao {

    private static final Random random = new Random();

    private static final String QUERY = "INSERT INTO documents (content, published_on, id) VALUES (?, ?, ?)";

    private JdbcTemplate jdbcTemplate;

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
