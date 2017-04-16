package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Picture;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by lu on 2017/2/3.
 */
public class PictureDaoMapper implements ResultSetMapper<Picture> {
    public Picture map(int i, ResultSet rs, StatementContext statementContext) throws SQLException {
            return new Picture(rs.getLong("id"), rs.getLong("recruiter_id"), rs.getString("path"),
                    rs.getInt("status"));
            }
}
