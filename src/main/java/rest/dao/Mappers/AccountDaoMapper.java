package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Account;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/1/13.
 */
public class AccountDaoMapper implements ResultSetMapper<Account> {
    public Account map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Account(rs.getInt("id"), rs.getString("email"), rs.getString("password"), rs.getInt("profile_id"),
                rs.getInt("status"),rs.getTimestamp("createDate"), rs.getTimestamp("activeDate"),rs.getTimestamp("disableDate"),
                rs.getTimestamp("lastLoginDate"), rs.getInt("loginTimes"));
    }
}
