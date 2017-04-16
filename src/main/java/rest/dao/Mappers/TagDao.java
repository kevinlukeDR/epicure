package rest.dao.Mappers;

import com.teachoversea.rest.util.LogSqlFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.LongMapper;
import org.skife.jdbi.v2.util.StringMapper;

/**
 * Created by lu on 2017/3/10.
 */
@LogSqlFactory
public interface TagDao {
    @Mapper(LongMapper.class)
    @SqlQuery("SELECT id FROM tag WHERE name=:name")
    int getIdByTagName(@Bind("name") String name);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO tag SET name=:name")
    int insertTag(@Bind("name") String name);

    @Mapper(StringMapper.class)
    @SqlQuery("SELECT name FROM tag WHERE id=:id")
    String getNameById(@Bind("id") int id);
}
