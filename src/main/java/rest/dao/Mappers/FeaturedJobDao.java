package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.FeaturedJob;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

/**
 * Created by lu on 2017/2/13.
 */
public interface FeaturedJobDao {

    @Mapper(FeaturedJobDaoMapper.class)
    @SqlQuery("SELECT * FROM featured_job")
    List<FeaturedJob> find();
}
