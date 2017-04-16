package rest.util;

/**
 * Created by lu on 2017/2/15.
 */
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.sqlobject.SqlStatementCustomizer;
import org.skife.jdbi.v2.sqlobject.SqlStatementCustomizerFactory;
import org.skife.jdbi.v2.sqlobject.SqlStatementCustomizingAnnotation;
import org.skife.jdbi.v2.tweak.StatementCustomizer;

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SqlStatementCustomizingAnnotation(LogSqlFactory.Factory.class)
public @interface LogSqlFactory {

    class Factory implements SqlStatementCustomizerFactory {

        @Override
        public SqlStatementCustomizer createForMethod(Annotation annotation, Class sqlObjectType, Method method) {
            return null;
        }

        @Override
        public SqlStatementCustomizer createForType(Annotation annotation, Class sqlObjectType) {
            return q -> q.addStatementCustomizer(new StatementCustomizer() {
                @Override
                public void beforeExecution(PreparedStatement stmt, StatementContext ctx) throws SQLException {
                    System.out.println(stmt.toString());
                }

                @Override
                public void afterExecution(PreparedStatement stmt, StatementContext ctx) throws SQLException { }

                @Override
                public void cleanup(StatementContext ctx) throws SQLException { }
            });
        }

        @Override
        public SqlStatementCustomizer createForParameter(Annotation annotation, Class sqlObjectType, Method method, Object arg) {
            return null;
        }
    }

}