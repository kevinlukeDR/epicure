package rest.dao.Mappers;


import com.teachoversea.rest.dao.Objects.Job.Certification;
import com.teachoversea.rest.util.LogSqlFactory;
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
@LogSqlFactory
public interface CertificationDao {
    @Mapper(CertificationDaoMapper.class)
    @SqlQuery("SELECT * FROM certification WHERE profile_id = :profile_id")
    List<Certification> findById(@Bind("profile_id") long profile_id);

    @Mapper(LongMapper.class)
    @SqlQuery("SELECT id FROM certification WHERE profile_id = :profile_id")
    List<Long> getAllIdbyProfileId(@Bind("profile_id") long profile_id);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO certification (type, issueDate, issuingBody, profile_id) VALUES (" +
            ":type, :issueDate, :issuingBody, :profile_id)")
    long addCertification(@Bind("type") String type, @Bind("issueDate") Timestamp issueDate, @Bind("issuingBody") String issuingBody,
                          @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE certification SET type =:type, issueDate=:issueDate, issuingBody=:issuingBody " +
            "WHERE id =:id")
    int updateCertification(@Bind("type") String type, @Bind("issueDate") Timestamp issueDate, @Bind("issuingBody") String issuingBody,
                            @Bind("id") long id);

    @SqlUpdate("DELETE FROM certification "+
            "WHERE id =:id")
    int deleteCertification(@Bind("id") long id);
}
