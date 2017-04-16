package rest.dao.Mappers;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

import java.sql.Timestamp;

/**
 * Created by lu on 2017/3/13.
 */
public interface EmailCampaignDetailsDao {

    @SqlUpdate("UPDATE email_campaign_details SET respond_date=:respond_date " +
            "WHERE email_campaign_id=:email_campaign_id")
    int updateRecord(@Bind("respond_date") Timestamp respond_date, @Bind("email_campaign_id") long email_campaign_id);
}
