package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Favorite;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.util.IntegerMapper;

import java.util.List;

/**
 * Created by lu on 2017/3/21.
 */
public interface FavoriteDao {
    @SqlUpdate("INSERT INTO account_favorite_job SET account_id=:account_id, job_id=:job_id")
    int insertFavorite(@Bind("account_id") long account_id, @Bind("job_id") long job_id);

    @Mapper(IntegerMapper.class)
    @SqlQuery("SELECT count(*) FROM account_favorite_job WHERE job_id=:job_id")
    int numberOfFavorite(@Bind("job_id") long job_id);

    @Mapper(FavoriteDaoMapper.class)
    @SqlQuery("SELECT * FROM account_favorite_job WHERE account_id=:account_id")
    List<Favorite> getAllJobs(@Bind("account_id") long account_id);

    @SqlUpdate("DELETE FROM account_favorite_job WHERE account_id=:account_id AND job_id =:job_id")
    int deleteFavorite(@Bind("account_id") long account_id, @Bind("job_id") long job_id);
}
