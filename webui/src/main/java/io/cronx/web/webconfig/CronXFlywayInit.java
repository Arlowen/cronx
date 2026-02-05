package io.cronx.web.webconfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import io.cronx.toolkit.utils.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Configuration
@Slf4j
public class CronXFlywayInit {

    @Value("${spring.flyway.cronx.enabled:true}")
    private Boolean              flywayEnabled;

    @Value("${spring.flyway.cronx.baseline-on-migrate:true}")
    private Boolean              baselineOnMigrate;

    @Value("${spring.flyway.cronx.baseline-description:<< CronX >>}")
    private String               baselineDescription;

    @Value("${spring.flyway.cronx.sql-migration-prefix:V}")
    private String               sqlMigrationPrefix;

    @Value("${spring.flyway.cronx.sql-migration-separator:__}")
    private String               sqlMigrationSeparator;

    @Value("${spring.flyway.cronx.sql-migration-suffixes:.sql}")
    private String               sqlMigrationSuffixes;

    @Value("${spring.flyway.cronx.locations}")
    private String[]             locations;

    @Value("${spring.flyway.cronx.table}")
    private String               table;

    @Resource
    private DataSourceProperties dsPropertiesCronX;

    public void doUpgrade() {
        if (flywayEnabled) {
            log.info("CronX DB Upgrade Starting...");

            try {
                String schemaName = "cronx";
                String url = dsPropertiesCronX.getUrl();

                String regex = "(jdbc:mysql://[^/]+/)([^?]+)(\\?.*)?";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(url);

                if (matcher.find()) {
                    schemaName = matcher.group(2);
                    url = matcher.group(1) + matcher.group(3);
                }

                Flyway flyway = Flyway.configure()
                    .dataSource(url, dsPropertiesCronX.getUsername(), dsPropertiesCronX.getPassword())
                    .locations(locations)
                    .baselineOnMigrate(baselineOnMigrate)
                    .baselineDescription(baselineDescription)
                    .sqlMigrationPrefix(sqlMigrationPrefix)
                    .sqlMigrationSeparator(sqlMigrationSeparator)
                    .sqlMigrationSuffixes(sqlMigrationSuffixes)
                    .table(table)
                    .schemas(schemaName)
                    .outOfOrder(false)
                    .load();

                flyway.migrate();
            } catch (Exception e) {
                String msg = "CronX DB Upgrade failed, msg: " + ExceptionUtils.getRootCauseMessage(e);
                log.error(msg);
                throw new RuntimeException(e);
            }

            log.info("CronX DB Upgrade Done...");
        }
    }
}
