package com.ittalens.gag.model.dao;

import com.ittalens.gag.model.dto.posts.PostRespDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class PostDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SORTED_BY_REACT =
            "SELECT p.id, p.title, p.resource_path, p.created_at, p.created_by, p.category_id,\n" +
            "COUNT(r.post_id) AS number_of_reactions,\n" +
            "COUNT(CASE WHEN r.status = 1 THEN r.status END) AS likes,\n" +
            "COUNT(CASE WHEN r.status = 0 THEN r.status END) AS dislikes\n" +
            "FROM posts p LEFT JOIN users_posts_reactions r ON (p.id = r.post_id)\n" +
            "GROUP BY p.id HAVING p.created_at > DATE_ADD(CURDATE(), INTERVAL -7 DAY)\n" +
            "ORDER BY number_of_reactions DESC LIMIT ?,?";

    private static final String SORTED_BY_CATEGORY_AND_REACT =
            "SELECT p.id, p.title, p.resource_path, p.created_at, p.created_by, p.category_id,\n" +
            "COUNT(r.post_id) AS number_of_reactions,\n" +
            "COUNT(CASE WHEN r.status = 1 THEN r.status END) AS likes,\n" +
            "COUNT(CASE WHEN r.status = 0 THEN r.status END) AS dislikes\n" +
            "FROM posts p LEFT JOIN users_posts_reactions r ON (p.id = r.post_id)\n" +
            "GROUP BY p.id HAVING p.created_at > DATE_ADD(CURDATE(), INTERVAL -7 DAY) AND p.category_id = ?\n" +
            "ORDER BY number_of_reactions DESC LIMIT ?,?";

    private static final String SORTED_BY_CATEGORY_AND_DATE =
            "SELECT p.id, p.title, p.resource_path, p.created_at, p.created_by, p.category_id, \n" +
            "COUNT(CASE WHEN upr.status = 1 THEN upr.status END) AS likes,\n" +
            "COUNT(CASE WHEN upr.status = 0 THEN upr.status END) AS dislikes\n" +
            "FROM posts p \n" +
            "LEFT JOIN users_posts_reactions upr ON (p.id = upr.post_id )\n" +
            "GROUP BY p.id HAVING p.created_at > DATE_ADD(CURDATE(), INTERVAL -7 DAY) AND p.category_id = ? \n" +
            "ORDER BY p.created_at DESC LIMIT ?,?";

    private static final String SORTED_BY_TAG_AND_REACT =
            "SELECT p.id, p.title, p.resource_path, p.created_at, p.created_by, p.category_id, \n" +
            "COUNT(CASE WHEN r.status = 1 THEN r.status END) AS likes, \n" +
            "COUNT(CASE WHEN r.status = 0 THEN r.status END) AS dislikes,\n" +
            "COUNT(r.post_id) AS number_of_reactions, pwt.tag_id AS tag\n" +
            "FROM posts p LEFT JOIN users_posts_reactions r ON (p.id = r.post_id)\n" +
            "JOIN posts_with_tags pwt ON (p.id = pwt.post_id AND pwt.tag_id = ?)\n" +
            "GROUP BY p.id HAVING p.created_at > DATE_ADD(CURDATE(), INTERVAL -7 DAY)\n" +
            "ORDER BY number_of_reactions DESC LIMIT ?,?";

    private static final String SORTED_BY_TAG_AND_DATE =
            "SELECT p.id, p.title, p.resource_path, p.created_at, p.created_by, p.category_id, pwt.tag_id AS tag,\n" +
            "COUNT(CASE WHEN upr.status = 1 THEN upr.status END) AS likes,\n" +
            "COUNT(CASE WHEN upr.status = 0 THEN upr.status END) AS dislikes\n" +
            "FROM posts p\n" +
            "JOIN posts_with_tags pwt ON (p.id = pwt.post_id AND pwt.tag_id = ?)\n" +
            "LEFT JOIN users_posts_reactions upr ON(p.id = upr.post_id)\n" +
            "GROUP BY p.id HAVING p.created_at > DATE_ADD(CURDATE(), INTERVAL -7 DAY)\n" +
            "ORDER BY created_at DESC LIMIT ?,?";

    public List<PostRespDTO> getAllRecentPostsSortedByReactionCount(int offset, int pageSize) {
        return withThreeParam(SORTED_BY_REACT, offset, pageSize);
    }

    public List<PostRespDTO> getAllRecentPostsByCategorySortedByReactionCount(int offset, int pageSize, long categoryId) {
        return withFourParam(SORTED_BY_CATEGORY_AND_REACT, offset, pageSize, categoryId);
    }

    public List<PostRespDTO> getAllRecentPostsByCategoryId(int offset, int pageSize, long categoryId) {
        return withFourParam(SORTED_BY_CATEGORY_AND_DATE, offset, pageSize, categoryId);
    }

    public List<PostRespDTO> getAllRecentPostsByTagIdSortedByReactionCount(int offset, int pageSize, long tagId) {
        return withFourParam(SORTED_BY_TAG_AND_REACT, offset, pageSize, tagId);
    }

    public List<PostRespDTO> getAlLRecentPostsByTagId(int offset, int pageSize, long tagId) {
        return withFourParam(SORTED_BY_TAG_AND_DATE, offset, pageSize, tagId);

    }

    private List<PostRespDTO> withThreeParam(String sql, int offset, int pageSize) {

        List<PostRespDTO> respDTOS = jdbcTemplate.query(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setInt(1, offset * pageSize);
                ps.setInt(2, pageSize);
            }
        }, (rs, rowNum) -> new PostRespDTO(
                rs.getLong("id"),
                rs.getString("title"),
                "http://localhost:8080/posts/download/" + rs.getLong("id"),
                LocalDateTime.of(rs.getDate("created_at").toLocalDate(), rs.getTime("created_at").toLocalTime()),
                rs.getLong("created_by"),
                rs.getLong("category_id"),
                rs.getInt("likes"),
                rs.getInt("dislikes")));
        return respDTOS;
    }

    private List<PostRespDTO> withFourParam(String sql, int offset, int pageSize, long id){

        List<PostRespDTO> respDTOS = jdbcTemplate.query(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1, id);
                ps.setInt(2, offset * pageSize);
                ps.setInt(3, pageSize);
            }
        }, (rs, rowNum) -> new PostRespDTO(
                rs.getLong("id"),
                rs.getString("title"),
                "http://localhost:8080/posts/download/" + rs.getLong("id"),
                LocalDateTime.of(rs.getDate("created_at").toLocalDate(), rs.getTime("created_at").toLocalTime()),
                rs.getLong("created_by"),
                rs.getLong("category_id"),
                rs.getInt("likes"),
                rs.getInt("dislikes")));
        return respDTOS;

    }
}
