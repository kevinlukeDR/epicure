package rest.dao.Mappers;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import rest.dao.Objects.Job.Account;

import java.util.List;

/**
 * Created by Yiyu Jia on 1/6/16.
 */


//@RegisterMapper(DeviceStatusMapper.class)
public interface AccountDao {

    /*
    @SqlUpdate("create table if not exists <table> (" +
            "startTime TimeStamp not null," +
            "stopTime TimeStamp not null," +
            "uuid varchar(255)" +
            ")")
    void createAccountTable(@Define("table") String table);
    */

    @Mapper(AccountDaoMapper.class)
    @SqlQuery("SELECT * FROM account WHERE id = :id")
    Account findById(@Bind("id") int id);

    @SqlQuery("SELECT * FROM account")
    List<Account> getAllAccount();

    @SqlUpdate("UPDATE account SET active = 0 where id = :id")
    void inactiveAccount(@Bind("id") int id);


    @SqlUpdate("DELETE FROM Account WHERE id = :id")
    void deleteAccount(@Bind("id") int id);

    @SqlUpdate("UPDATE account SET ip = :ip,name = :name, password = :password, active = :active where id = :id")
    void updateAccount(@Bind("id") int id,
                       @Bind("name") String name,
                       @Bind("password") String password,
                       @Bind("active") boolean active);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO account (id, name, password, active, email, created_time) " +
            "values (NULL, :name, :password, 0, :email, NULL)")
    int createAccount(
            @Bind("name") String name,
            @Bind("password") String password,
            @Bind("email") String email);


    @Mapper(AccountDaoMapper.class)
    @SqlQuery("SELECT * FROM account WHERE name = :name AND password = :password")
    Account findByNamePassword(@Bind("name") String name, @Bind("password") String password);
}
