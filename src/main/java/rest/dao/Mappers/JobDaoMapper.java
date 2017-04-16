package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Job;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/1/10.
 */
public class JobDaoMapper implements ResultSetMapper<Job> {
    public Job map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Job(rs.getLong("id"), rs.getString("position"), rs.getString("city"), rs.getString("subject"),
                rs.getString("grade"), rs.getString("jobType"), rs.getInt("classSize"), rs.getInt("contractLen"),
                rs.getInt("salaryMin"),rs.getInt("salaryMax"),rs.getString("salaryMessage"),rs.getInt("experience"), rs.getLong("recruiter_id"), rs.getTimestamp("postDate"), rs.getTimestamp("startDate"),
                rs.getString("schoolName"),rs.getBoolean("eligibleCandidates"),rs.getBoolean("nativeSpeaker"),
                rs.getString("certificates"),rs.getString("education"),rs.getString("additional"),rs.getString("accommodation"),
                rs.getString("airfare"),rs.getString("weeklyRestDay"),rs.getString("vacation"),rs.getString("annualLeave"),
                rs.getString("bonus"),rs.getBoolean("lunch"),rs.getBoolean("chinese"),rs.getString("status"),
                rs.getString("aboutSchool"), rs.getString("schoolType"),rs.getTimestamp("lastEditedDate"),
                rs.getString("notes"));

    }
}
