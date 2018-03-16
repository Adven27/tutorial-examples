package javaeetutorial.batch.messagesender;

import javaeetutorial.batch.messagesender.items.Client;
import javaeetutorial.batch.messagesender.items.Message;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.List;

public class MessageWriter implements ItemWriter {
    private String messageId;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private String sql;

    public MessageWriter() {
    }

    @Override
    public void write(List clients) {
        Message msg = findMessage();
        msg.setText(msg.getText() + prepareText(clients));
        jdbcTemplate.update(sql, new BeanPropertySqlParameterSource(msg));
    }

    private Message findMessage() {
        String query = "SELECT * FROM MESSAGE WHERE ID=:messageId";
        SqlParameterSource params = new MapSqlParameterSource("messageId", Integer.valueOf(messageId));
        Message msg = jdbcTemplate.queryForObject(query, params, new BeanPropertyRowMapper<>(Message.class));

        if (msg == null) {
            throw new IllegalStateException("Message should exist but not found by " + messageId);
        }
        return msg;
    }

    private String prepareText(List<Object> clients) {
        StringBuilder result = new StringBuilder();
        for (Object obj : clients) {
            result.append(((Client) obj).getName());
        }
        return result.toString();
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}