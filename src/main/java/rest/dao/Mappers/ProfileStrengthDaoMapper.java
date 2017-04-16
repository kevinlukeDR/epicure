package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.ProfileStrength;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/3/22.
 */

public class ProfileStrengthDaoMapper implements ResultSetMapper<ProfileStrength> {
    public ProfileStrength map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new ProfileStrength(rs.getLong("profile_id"), rs.getInt("avatar"), rs.getInt("resume"),
                rs.getInt("video"), rs.getInt("gender"), rs.getInt("birth"), rs.getInt("availableDate"),
                rs.getInt("nationality"), rs.getInt("education"), rs.getInt("certification"),
                rs.getInt("experience"), rs.getInt("social"), rs.getInt("preference"));
    }
}
