package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.EmailCampaign;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 * Created by lu on 2017/3/13.
 */
public interface EmailCampaignDao {
    @Mapper(EmailCampaignDaoMapper.class)
    @SqlQuery("SELECT * FROM email_campaign WHERE uuid =:uuid")
    EmailCampaign getByUUID(@Bind("uuid") String uuid);

    @Mapper(EmailCampaignDaoMapper.class)
    @SqlQuery("SELECT * FROM email_campaign WHERE email =:email")
    EmailCampaign getByEmail(@Bind("email") String email);
}
