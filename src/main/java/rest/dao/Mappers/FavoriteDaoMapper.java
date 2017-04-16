package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Favorite;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/3/21.
 */
public class FavoriteDaoMapper implements ResultSetMapper<Favorite> {
    public Favorite map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Favorite(rs.getLong("account_id"), rs.getLong("job_id"), rs.getTimestamp("favoredDate"));
    }
}