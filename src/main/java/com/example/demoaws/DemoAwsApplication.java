package com.example.demoaws;

import com.example.demoaws.db.DocumentDao;
import com.example.demoaws.db.DocumentEntity;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.function.context.FunctionRegistration;
import org.springframework.cloud.function.context.FunctionType;
import org.springframework.cloud.function.context.FunctionalSpringApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.sql.DataSource;
import java.util.function.Function;

@SpringBootConfiguration
public class DemoAwsApplication implements ApplicationContextInitializer<GenericApplicationContext> {

    private final String brokerUrl = "ssl://b-d304a064-c674-4e8f-86de-be9139fdc19e-1.mq.eu-central-1.amazonaws.com:61617";

    private final String brokerUser = "activemq";

    private final String brokerPassword = "exampleexample";

    public static void main(String[] args) {
        FunctionalSpringApplication.run(DemoAwsApplication.class, args);
    }

    public Function<String, String> function() {
        return input -> {
            JmsTemplate jmsTemplate = LambdaHolder.getJmsTemplate();
            jmsTemplate.convertAndSend("queue", input);
            String message = (String) jmsTemplate.receiveAndConvert("queue");
            final DocumentEntity documentEntity = new DocumentEntity(message);
            final Long id = LambdaHolder.getDocumentDao().save(documentEntity);
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
                    brokerUser,
                    brokerPassword,
                    brokerUrl);
            activeMQConnectionFactory.setTrustAllPackages(true);
            return activeMQConnectionFactory;
        });

        context.registerBean(CachingConnectionFactory.class,
                () -> new CachingConnectionFactory(context.getBean(ActiveMQConnectionFactory.class)));

        context.registerBean(JmsTemplate.class, () -> new JmsTemplate(context.getBean(CachingConnectionFactory.class)));

        context.registerBean(DefaultJmsListenerContainerFactory.class, () -> {
            DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
            factory.setConnectionFactory(context.getBean(ActiveMQConnectionFactory.class));
            return factory;
        });

        context.registerBean(DataSource.class, () -> {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://database-1.cx96u0a6s3vd.eu-central-1.rds.amazonaws.com:5432/postgres");
            dataSource.setUsername("postgres");
            dataSource.setPassword("exampleexample");
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
