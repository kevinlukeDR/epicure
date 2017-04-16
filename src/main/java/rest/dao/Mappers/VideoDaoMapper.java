package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Video;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/2/24.
 */
public class VideoDaoMapper implements ResultSetMapper<Video> {
    public Video map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
        return new Video(rs.getLong("id"), rs.getString("path"), rs.getBoolean("nudity"),rs.getTimestamp("uploadDate"),
                rs.getInt("status"), rs.getString("answeredQuestion"), rs.getLong("profile_id"));
    }
}
