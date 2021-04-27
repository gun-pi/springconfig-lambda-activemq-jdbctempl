package com.example.demoaws;

import com.example.demoaws.db.DocumentDao;
import org.springframework.jms.core.JmsTemplate;

public class LambdaHolder {

    public static JmsTemplate jmsTemplate;

    public static DocumentDao documentDao;

    public static JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public static void setJmsTemplate(final JmsTemplate jmsTemplate) {
        LambdaHolder.jmsTemplate = jmsTemplate;
    }

    public static DocumentDao getDocumentDao() {
        return documentDao;
    }

    public static void setDocumentDao(final DocumentDao documentDao) {
        LambdaHolder.documentDao = documentDao;
    }
}
