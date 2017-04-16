package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Tag;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/3/10.
 */
public class TagDaoMapper implements ResultSetMapper<Tag> {
    public Tag map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Tag(rs.getInt("id"),rs.getString("name"));
    }
}