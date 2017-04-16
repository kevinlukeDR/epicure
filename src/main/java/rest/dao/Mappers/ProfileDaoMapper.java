package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Profile;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/1/13.
 */
public class ProfileDaoMapper implements ResultSetMapper<Profile> {
public Profile map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Profile(rs.getLong("id"),rs.getString("fname"),
                rs.getString("lname"), rs.getTimestamp("availableDate"), rs.getString("birth"),
                rs.getString("nationality"),rs.getTimestamp("lastUpdate"), rs.getTimestamp("lastUpdate2"),
                rs.getTimestamp("lastUpdate3"),rs.getString("gender"), rs.getString("personalWebsite"), rs.getString("linkedin"),
                rs.getString("facebook"), rs.getString("instagram"), rs.getString("twitter"),
                rs.getString("wechat"));
        }
}
