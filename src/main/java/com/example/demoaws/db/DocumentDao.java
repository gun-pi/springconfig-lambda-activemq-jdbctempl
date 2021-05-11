package com.example.demoaws.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;

public class DocumentDao {

    private static final String QUERY = "INSERT INTO documents (content, published_on) VALUES (?, ?) RETURNING id";

    private static final KeyHolder keyHolder = new GeneratedKeyHolder();

    private JdbcTemplate jdbcTemplate;

    public DocumentDao(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Long save(DocumentEntity documentEntity) {
        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(QUERY, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, documentEntity.getContent());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(documentEntity.getPublishedOn()));
            return preparedStatement;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
