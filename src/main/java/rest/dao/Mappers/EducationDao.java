package rest.dao.Mappers;


import com.teachoversea.rest.dao.Objects.Job.Education;
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
public interface EducationDao {
    @Mapper(EducationDaoMapper.class)
    @SqlQuery("SELECT * FROM education_experience WHERE profile_id = :profile_id")
    List<Education> findById(@Bind("profile_id") long profile_id);

    @Mapper(LongMapper.class)
    @SqlQuery("SELECT id FROM education_experience WHERE profile_id = :profile_id")
    List<Long> getAllIdbyProfileId(@Bind("profile_id") long profile_id);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO education_experience (degree, field, fromDate, toDate, school, country, city, profile_id)" +
            "VALUES (:degree, :field, :fromDate,:toDate, :school,:country, :city,  :profile_id)")
    long addEducation(@Bind("degree") String degree, @Bind("field") String field, @Bind("fromDate") Timestamp fromDate,
                      @Bind("toDate") Timestamp toDate, @Bind("school") String school, @Bind("country") String country,
                      @Bind("city") String city,
                      @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE education_experience SET degree=:degree, field=:field, fromDate=:fromDate, toDate=:toDate, " +
            "school=:school, country=:country, city=:city " +
            "WHERE id=:id")
    int updateEducation(@Bind("degree") String degree, @Bind("field") String field, @Bind("fromDate") Timestamp fromDate,
                        @Bind("toDate") Timestamp toDate, @Bind("school") String school, @Bind("country") String country, @Bind("city") String city,
                        @Bind("id") long id);

    @SqlUpdate("DELETE FROM education_experience "+
            "WHERE id =:id")
    int deleteEducation(@Bind("id") long id);
}
