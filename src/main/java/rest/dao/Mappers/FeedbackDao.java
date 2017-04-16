package rest.dao.Mappers;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by lu on 2017/2/23.
 */
public interface FeedbackDao {
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO feedback (reason) VALUES (:reason)")
    long addReason(@Bind("reason") String reason);
}
