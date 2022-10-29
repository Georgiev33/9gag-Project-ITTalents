package com.ittalens.gag.model.dao;

import com.ittalens.gag.model.dto.comments.CommentResponseDTO;
import com.ittalens.gag.model.repository.CommentReactionsRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class CommentDAO {
    @Autowired
    private  JdbcTemplate jdbcTemplate;

    @Autowired
    private CommentReactionsRepository reactionsRepository;

    public List<CommentResponseDTO> getAllCommentsForPostSortedByReactionCount(int offset, int pageSize, long postId){
        String sql =
                "SELECT c.id, c.text, c.created_at, c.resource_path, c.replied_comment_id,\n" +
                " c.post_id, c.created_by, c.replied_comment_id ,c.created_by , count(ucr.comment_id) AS number_of_reactions \n" +
                "FROM comments c\n" +
                "LEFT JOIN users_comments_reactions ucr ON c.id = ucr.comment_id\n" +
                "GROUP BY c.id HAVING post_id = ? ORDER BY number_of_reactions DESC LIMIT ?,?";
        return jdbcTemplate.query(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setLong(1,postId);
                ps.setInt(2,offset * pageSize);
                ps.setInt(3, pageSize);
            }
        }, (rs, rowNum) -> new CommentResponseDTO(
                rs.getLong("id"),
                postId,
                (rs.getLong("replied_comment_id")),
                rs.getString("text"),
                "localhost:8080/comment/download/" + rs.getLong("id"),
                (rs.getLong("created_by")),
                rs.getDate("created_at").toLocalDate().atStartOfDay()
                ));
    }
}
