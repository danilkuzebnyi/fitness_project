package org.danylo.db;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;

@Component
public class DatabaseMigration {
    private final DataSource dataSource;

    @Autowired
    public DatabaseMigration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void applyDatabaseMigrations() {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
    }
}
