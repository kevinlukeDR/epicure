package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.ProfileStrength;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

/**
 * Created by lu on 2017/3/22.
 */
public interface ProfileStrengthDao {
    @Mapper(ProfileStrengthDaoMapper.class)
    @SqlQuery("SELECT * FROM profile_strength WHERE profile_id=:profile_id")
    ProfileStrength getStrengthById(@Bind("profile_id") long profile_id);

    @SqlUpdate("INSERT INTO profile_strength SET profile_id = :profile_id")
    int insert(@Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET avatar=:or WHERE profile_id = :profile_id")
    int avatar(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET resume=:or WHERE profile_id = :profile_id")
    int resume(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET video=:or WHERE profile_id = :profile_id")
    int video(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET gender=:or WHERE profile_id = :profile_id")
    int gender(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET birth=:or WHERE profile_id = :profile_id")
    int birth(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET availableDate=:or WHERE profile_id = :profile_id")
    int availability(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET nationality=:or WHERE profile_id = :profile_id")
    int nationality(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET education=:or WHERE profile_id = :profile_id")
    int education(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET certification=:or WHERE profile_id = :profile_id")
    int certification(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET experience=:or WHERE profile_id = :profile_id")
    int experience(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET social=:or WHERE profile_id = :profile_id")
    int social(@Bind("or") int or, @Bind("profile_id") long profile_id);

    @SqlUpdate("UPDATE profile_strength SET preference=:or WHERE profile_id = :profile_id")
    int preference(@Bind("or") int or, @Bind("profile_id") long profile_id);

}
