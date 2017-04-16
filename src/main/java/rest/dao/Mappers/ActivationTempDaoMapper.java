package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.ActivationTemp;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/2/5.
 */
public class ActivationTempDaoMapper implements ResultSetMapper<ActivationTemp> {
    public ActivationTemp map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new ActivationTemp(rs.getLong("id"),rs.getLong("account_id"), rs.getString("token"));
    }
}
