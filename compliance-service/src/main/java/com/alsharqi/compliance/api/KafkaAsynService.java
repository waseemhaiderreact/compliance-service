package com.alsharqi.compliance.api;


import com.alsharqi.compliance.compliancerequest.ComplianceRequest;
import com.alsharqi.compliance.events.compliance.ComplianceModel;
import com.alsharqi.compliance.events.compliance.ComplianceSourceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class KafkaAsynService {

    @Autowired
    private ComplianceSourceBean complianceSourceBean;

    @Async
    void sendCompliance(ComplianceRequest complianceRequest){
        try {
            ComplianceModel complianceModel = new ComplianceModel("CREATE", complianceRequest);
            complianceSourceBean.publishCompliance(complianceModel);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
