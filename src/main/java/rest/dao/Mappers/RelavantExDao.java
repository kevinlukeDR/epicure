package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.RelavantEx;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.LongMapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by lu on 2017/1/13.
 */
public interface RelavantExDao {
    @Mapper(RelavantExDaoMapper.class)
    @SqlQuery("SELECT * FROM related_experience WHERE profile_id = :profile_id")
    List<RelavantEx> findById(@Bind("profile_id") long profile_id);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO related_experience (role, company, fromDate, toDate, responsibility, profile_id) " +
            "VALUES (:role, :company, :fromDate, :toDate, :responsibility, :profile_id)")
    long addRelevant(@Bind("role") String role, @Bind("company") String company, @Bind("fromDate") Timestamp fromDate,
                     @Bind("toDate") Timestamp toDate, @Bind("responsibility") String responsibility,
                     @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE related_experience SET role=:role, company=:company, fromDate=:fromDate, " +
            "toDate=:toDate, responsibility=:responsibility " +
            "WHERE (id=:id)")
    int updateRelevant(@Bind("role") String role, @Bind("company") String company, @Bind("fromDate") Timestamp fromDate,
                       @Bind("toDate") Timestamp toDate, @Bind("responsibility") String responsibility,
                       @Bind("id") long id);

    @Mapper(LongMapper.class)
    @SqlQuery("SELECT id FROM related_experience WHERE profile_id = :profile_id")
    List<Long> getAllIdbyProfileId(@Bind("profile_id") long profile_id);

    @SqlUpdate("DELETE FROM related_experience "+
            "WHERE id =:id")
    int deleteExperience(@Bind("id") long id);
}
