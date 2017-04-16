package rest.dao.Mappers;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import rest.dao.Objects.Job.Account;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Yiyu Jia on 1/6/16.
 */
public class AccountDaoMapper implements ResultSetMapper<Account> {
    public Account map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Account(rs.getInt("id"), rs.getString("name"), rs.getString("password"), rs.getString("email"), rs.getBoolean("active"));
    }
}
