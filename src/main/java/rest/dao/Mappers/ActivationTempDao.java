package rest.dao.Mappers;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

/**
 * Created by lu on 2017/2/5.
 */
public interface ActivationTempDao {

    @SqlQuery("SELECT account_id FROM email_temp WHERE token = :token")
    long findByToken(@Bind("token") String token);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO email_temp (account_id, token) VALUES (:account_id, :token)")
    long insert(@Bind("account_id") long account_id, @Bind("token") String token);
}
