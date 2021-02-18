package com.alsharqi.compliance.api;


import com.alsharqi.compliance.compliance.Compliance;
import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.events.compliance.ComplianceModel;
import com.alsharqi.compliance.events.compliance.ComplianceSourceBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class KafkaAsynService {

    private static final Logger LOGGER = LogManager.getLogger(KafkaAsynService.class);

    @Autowired
    private ComplianceSourceBean complianceSourceBean;

    @Async
    void sendCompliance(Compliance compliance){
        try {
            ComplianceModel complianceModel = new ComplianceModel("CREATE", compliance);
            complianceSourceBean.publishCompliance(complianceModel);
        }
        catch(Exception e){
            LOGGER.error("Error while sending Compliance created event to search service",e);
            e.printStackTrace();
        }
    }
}
