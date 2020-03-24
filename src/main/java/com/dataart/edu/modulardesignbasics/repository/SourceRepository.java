package com.dataart.edu.modulardesignbasics.repository;

import com.dataart.edu.modulardesignbasics.model.Source;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@AllArgsConstructor
@Repository
public class SourceRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Source> findAll() {
        return jdbcTemplate.query(
                "select * from source",
                (rs, owNum) -> new Source(
                        rs.getLong("id"),
                        rs.getString("dir"),
                        rs.getTimestamp("last_scanned") == null ? null :
                                rs.getTimestamp("last_scanned").toLocalDateTime()
                )
        );
    }

    public int[] updateAll(Collection<Source> sources) {
        return jdbcTemplate.batchUpdate(
                "update source set last_scanned = ? where id = ?",
                new BatchPreparedStatementSetter() {
                    Iterator<Source> iterator = sources.iterator();

                    public void setValues(PreparedStatement ps, int i)
                            throws SQLException {
                        Source next = iterator.next();
                        ps.setTimestamp(1, Timestamp.valueOf(next.getLastScanned()));
                        ps.setLong(2, next.getId());
                    }

                    public int getBatchSize() {
                        return sources.size();
                    }
                });
    }

    public int update(Source source) {
        return jdbcTemplate.update(
                "update source set last_scanned = ? where id = ?",
                source.getLastScanned(), source.getId()
        );
    }
}
