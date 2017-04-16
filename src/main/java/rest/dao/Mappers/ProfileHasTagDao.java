package rest.dao.Mappers;

import com.teachoversea.rest.util.LogSqlFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.IntegerMapper;

import java.util.List;

/**
 * Created by lu on 2017/3/10.
 */
@LogSqlFactory
public interface ProfileHasTagDao {
    @SqlUpdate("INSERT INTO profile_has_tag SET profile_id=:profile_id, tag_id=:tag_id")
    int insertProfileTag(@Bind("profile_id") long profile_id, @Bind("tag_id") int tag_id);

    @SqlUpdate("DELETE FROM profile_has_tag WHERE profile_id = :profile_id")
    int deleteTag(@Bind("profile_id") long profile_id);

    @Mapper(IntegerMapper.class)
    @SqlQuery("SELECT tag_id FROM profile_has_tag WHERE profile_id =:profile_id")
    List<Integer> getTagId(@Bind("profile_id") long profile_id);
}
