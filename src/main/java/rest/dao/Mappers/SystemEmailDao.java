package rest.dao.Mappers;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.sql.Timestamp;

/**
 * Created by lu on 2017/2/21.
 */
public interface SystemEmailDao {
    @SqlUpdate("UPDATE system_email SET status = :status, inactiveDate = :inactiveDate WHERE email_campaign_id=:email_campaign_id")
    int unsubscribe(@Bind("status") int status, @Bind("inactiveDate") Timestamp inactiveDate, @Bind("email_campaign_id") long email_campaign_id);

    @SqlQuery("SELECT email FROM system_email WHERE uuid =:uuid")
    String getEmailByUUID(@Bind("uuid") String uuid);
}
