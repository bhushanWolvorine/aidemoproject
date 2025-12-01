package com.aidemoproject.validator.compliance;






import java.util.List;

/**
* Validates regulatory & safety compliance of agent replies
* One implementation per domain (Banking/RBI, Insurance/IRDAI, etc.)
*/
public interface ComplianceValidator {

 /**
  * Full compliance check
  */
 ComplianceReport validate(String agentReply, List<String> conversationLog, String journeyContext);

 /**
  * Quick pass/fail
  */
 boolean isCompliant(String agentReply);

 /**
  * Returns the compliance domain this validator enforces
  */
 String getComplianceDomain();
}