package com.example.demoaws;

import com.example.demoaws.db.DocumentDao;
import com.example.demoaws.db.DocumentEntity;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jms.core.JmsTemplate;

import javax.sql.DataSource;
import java.util.function.Function;

@SpringBootConfiguration
public class DemoAwsApplication implements ApplicationContextInitializer<GenericApplicationContext> {

    private static final String BROKER_URL =
            "ssl://b-82646c3d-9ad4-4a2a-be59-a244daa6e033-1.mq.eu-central-1.amazonaws.com:61617";
    private static final String BROKER_USER = "activemq";
    private static final String BROKER_PASSWORD = "exampleexample";
    private static final String BROKER_QUEUE = "queue";

    private static final String DATABASE_URL =
            "jdbc:postgresql://postgresdatabase.cx96u0a6s3vd.eu-central-1.rds.amazonaws.com:5432/postgres";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "exampleexample";
    private static final String DATABASE_DRIVER = "org.postgresql.Driver";

    private static final Logger LOG = LoggerFactory.getLogger(DemoAwsApplication.class);

    public static void main(String[] args) {
        FunctionalSpringApplication.run(DemoAwsApplication.class, args);
    }

    public Function<String, String> function() {
        return input -> {
            LOG.info("Function is starting with input: {}", input);

            String message;
            try {
                JmsTemplate jmsTemplate = LambdaHolder.getJmsTemplate();
                jmsTemplate.convertAndSend(BROKER_QUEUE, input);
                message = (String) jmsTemplate.receiveAndConvert(BROKER_QUEUE);
            } catch (Exception e) {
                LOG.error("Exception occurred during JMS interaction ", e);
                throw new RuntimeException(e);
            }

            Long id;
            try {
                final DocumentEntity documentEntity = new DocumentEntity(message);
                id = LambdaHolder.getDocumentDao().save(documentEntity);
            } catch (Exception e) {
                LOG.error("Exception occurred during database interaction ", e);
                throw new RuntimeException(e);
            }

            LOG.info("Function is finishing with message {} and id {}", input, id);
            return id.toString();
        };
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        context.registerBean("function", FunctionRegistration.class,
                () -> new FunctionRegistration<>(function())
                        .type(FunctionType.from(String.class).to(String.class)));

        context.registerBean(ActiveMQConnectionFactory.class, () -> {
            final ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(
                    BROKER_USER,
                    BROKER_PASSWORD,
                    BROKER_URL);
            return activeMQConnectionFactory;
        });

        context.registerBean(JmsTemplate.class, () ->
                new JmsTemplate(context.getBean(ActiveMQConnectionFactory.class)));

        context.registerBean(DataSource.class, () -> {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl(DATABASE_URL);
            dataSource.setUsername(DATABASE_USER);
            dataSource.setPassword(DATABASE_PASSWORD);
            dataSource.setDriverClassName(DATABASE_DRIVER);
            return dataSource;
        });

        context.registerBean(JdbcTemplate.class, () -> new JdbcTemplate(context.getBean(DataSource.class)));

        context.registerBean(DocumentDao.class, () -> new DocumentDao(context.getBean(JdbcTemplate.class)));

        context.registerBean(LambdaHolder.class, () -> {
            LambdaHolder lambdaHolder = new LambdaHolder();
            lambdaHolder.setDocumentDao(context.getBean(DocumentDao.class));
            lambdaHolder.setJmsTemplate(context.getBean(JmsTemplate.class));
            return lambdaHolder;
        });
    }
}
