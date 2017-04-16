package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Session;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.sql.Timestamp;

public interface SessionDao {
    @SqlUpdate("insert into session (uuid, account_id, type) values (:uuid, " +
            ":account_id, :type)")
    int insert(
            @Bind("uuid") String uuid,
            @Bind("account_id") long account_id,
            @Bind("type") int type
    );

    @Mapper(SessionDaoMapper.class)
    @SqlQuery("SELECT * FROM session WHERE uuid = :uuid")
    Session getSession(@Bind("uuid") String uuid);

    @SqlUpdate("UPDATE session SET refreshedDate = :refreshedDate WHERE uuid = :uuid")
    int update(@Bind("refreshedDate") Timestamp refreshedDate, @Bind("uuid") String uuid);
}
