package com.dataart.edu.modulardesignbasics.repository;

import com.dataart.edu.modulardesignbasics.model.Result;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

@AllArgsConstructor
@Repository
public class ResultRepository {

    private final JdbcTemplate jdbcTemplate;

    public void deleteAll() {
        jdbcTemplate.update("delete from result");
    }

    public int[] saveAll(Collection<Result> results) {
        return this.jdbcTemplate.batchUpdate(
                "insert into result (dir_id, file_name, words) values(?,?,?)",
                new BatchPreparedStatementSetter() {
                    Iterator<Result> iterator = results.iterator();

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Result next = iterator.next();
                        ps.setLong(1, next.getSourceId());
                        ps.setString(2, next.getFileName());
                        ps.setArray(3, toSqlArray(ps, next.getWords().toArray()));
                    }

                    public int getBatchSize() {
                        return results.size();
                    }

                });
    }

    public void deleteBySourceIdAndFileName(Long sourceId, String fileName) {
        jdbcTemplate.update("delete from result where dir_id = ? and file_name = ?", sourceId, fileName);
    }

    public int save(Result result) {
        return jdbcTemplate.update(
                "insert into result (dir_id, file_name, words) values(?,?,?)",
                ps -> {
                    ps.setLong(1, result.getSourceId());
                    ps.setString(2, result.getFileName());
                    ps.setArray(3, toSqlArray(ps, result.getWords().toArray()));
                });
    }

    private Array toSqlArray(PreparedStatement ps, Object[] words) throws SQLException {
        return ps.getConnection().createArrayOf("varchar", words);
    }
}
