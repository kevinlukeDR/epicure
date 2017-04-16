package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Candidate.Candidate;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

/**
 * Created by lu on 2017/1/9.
 */
public interface CandidateDao {
    @Mapper(CandidateDaoMapper.class)
    @SqlQuery("SELECT * FROM emailCampain WHERE id = :id")
    Candidate findById(@Bind("id") int id);

    @Mapper(CandidateDaoMapper.class)
    @SqlQuery("SELECT * FROM emailCampain WHERE fname = :fname")
    Candidate findByFname(@Bind("fname") String fname);

    @Mapper(CandidateDaoMapper.class)
    @SqlQuery("SELECT * FROM emailCampain")
    List<Candidate> getAllCandidate();

    @Mapper(CandidateDaoMapper.class)
    @SqlQuery("SELECT * FROM emailCampain WHERE email = :email")
    Candidate findByEmail(@Bind("email") String email);

    @SqlUpdate("UPDATE emailCampain SET status = 1 where email = :email")
    void sendEmail(@Bind("email") String email);

//
//    @SqlUpdate("UPDATE emailCampain SET active = 0 where account_id = :account_id")
//    void inactiveCandidateByAccountId(@Bind("account_id") int account_id);

//    @SqlUpdate("DELETE FROM emailCampain WHERE id = :id")
//    void deleteCandidate(@Bind("id") int id);
//
//    @SqlUpdate("UPDATE emailCampain SET account_id = :account_id, first_name = :first_name, last_name = :last_name, sex = :sex, phone_one = :phone_one," +
//            " phone_two = :phone_two, address = :address, business_type = :business_type, active = :active " +
//            "last_update_time = CURRENT_TIMESTAMP  where id = :id")
//    void updateCandidate(@Bind("id") int id,
//                       @Bind("account_id") int account_id,
//                       @Bind("first_name") String first_name,
//                       @Bind("last_name") String last_name,
//                       @Bind("sex") int sex,
//                       @Bind("phone_one") String phone_one,
//                       @Bind("phone_two") String phone_two,
//                       @Bind("address") String address,
//                       @Bind("business_type") String business_type,
//                       @Bind("active") boolean active);
//
//
//    @GetGeneratedKeys
//    @SqlUpdate("INSERT INTO emailCampain (id, account_id, first_name, last_name, sex, " +
//            "phone_one, phone_two, address, business_type, active, created_time, last_update_time)" +
//            "values (NULL, :account_id, :first_name, :last_name, :sex, :phone_one," +
//            " :phone_two , :address, :business_type, :active , NULL, NULL)")
//    int createCandidate(
//            @Bind("account_id") int account_id,
//            @Bind("first_name") String password,
//            @Bind("last_name") String last_name,
//            @Bind("sex") int sex,
//            @Bind("phone_one") String phone_one,
//            @Bind("phone_two") String phone_two,
//            @Bind("address") String address,
//            @Bind("business_type") String business_type,
//            @Bind("active") boolean active
//    );
}
