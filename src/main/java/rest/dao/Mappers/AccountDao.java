package rest.dao.Mappers;

import com.teachoversea.rest.dao.Objects.Job.Account;
import com.teachoversea.rest.util.LogSqlFactory;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by lu on 2017/1/13.
 */

@LogSqlFactory
public interface AccountDao {

    //TODO check Active or not
    @SqlQuery("SELECT profile_id FROM account WHERE email = :email and password = :password")
    int findProfileId(@Bind("email") String email, @Bind("password") String password);

    @Mapper(AccountDaoMapper.class)
    @SqlQuery("SELECT * FROM account WHERE email = :email")
    Account getAccountByEmail(@Bind("email") String email);

    @Mapper(AccountDaoMapper.class)
    @SqlQuery("SELECT * FROM account WHERE email = :email AND status != :status")
    Account testEmail(@Bind("email") String email, @Bind("status") int status);

    @Mapper(AccountDaoMapper.class)
    @SqlQuery("SELECT * FROM account WHERE id = :id")
    Account getAccountById(@Bind("id") long id);

    @Mapper(AccountDaoMapper.class)
    @SqlQuery("SELECT * FROM account WHERE profile_id = :id")
    Account getAccountByProfileId(@Bind("id") long id);


    @SqlQuery("SELECT count(*) FROM account")
    int getTotalNumber();

    @Mapper(AccountDaoMapper.class)
    @SqlQuery("SELECT * FROM account")
    List<Account> getTotalAccount();

    @SqlQuery("SELECT profile_id FROM account WHERE id = :id")
    long findProfileIdById(@Bind("id") long id);

    @SqlUpdate("UPDATE account SET email=:newemail WHERE email=:oldemail")
    int updateEmail(@Bind("newemail") String newemail, @Bind("oldemail") String oldemail);

    @SqlUpdate("UPDATE account SET email=:email WHERE id=:id")
    int updateEmailById(@Bind("email") String email, @Bind("id") long id);

    @SqlUpdate("UPDATE account SET password=:newpassword WHERE password=:oldpassword")
    int updatePassword(@Bind("newpassword") String newpassword, @Bind("oldpassword") String oldpassword);

    @SqlUpdate("UPDATE account SET password=:newpassword WHERE id=:id")
    int forgetPassword(@Bind("newpassword") String newpassword, @Bind("id") long id);

    @SqlUpdate("UPDATE account SET status =:status, activeDate = :activeDate WHERE id=:id")
    int updateStatus(@Bind("status") int status, @Bind("activeDate") Timestamp activeDate, @Bind("id") long id);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO account (email, password, profile_id, status) " +
            "VALUES ( :email, :password, :profile_id, :status)")
    int addAccount(@Bind("email") String email, @Bind("password") String password,
                   @Bind("profile_id") long profile_id, @Bind("status") int status);

    @SqlUpdate("UPDATE account SET lastLoginDate=:lastLoginDate, loginTimes=loginTimes+1 WHERE id=:id")
    int loginTime(@Bind("lastLoginDate") Timestamp lastLoginDate, @Bind("id") long id);
}
