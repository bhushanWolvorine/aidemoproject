package com.aidemoproject.validators.journeyspecfic.upi;

import java.util.ArrayList;
import java.util.List;

import com.aidemoproject.basevalidators.compliance.ComplianceReport;
import com.aidemoproject.basevalidators.compliance.ComplianceValidator;
import com.aidemoproject.basevalidators.hallucination.HallucinationValidator;
import com.aidemoproject.basevalidators.orchestration.OrchestrationIssue;
import com.aidemoproject.basevalidators.orchestration.OrchestrationValidator;
import com.aidemoproject.basevalidators.retrieval.RetrievalValidator;
import com.aidemoproject.common.AgentResponse;
import com.aidemoproject.common.AgentResponseValidator;
import com.aidemoproject.common.validationreport.ValidationReport;

public class UpiPaymentValidator implements AgentResponseValidator {


    private OrchestrationValidator orchestrationValidator = new UpiOrchestrationValidator();
    private HallucinationValidator hallucinationValidator = new UpiHallucinationValidator();
    private RetrievalValidator retrievalValidator = new UpiRetrievalValidator();
    private ComplianceValidator complianceValidator = new RbiComplianceValidator();


    private UpiPaymentValidator() {}


    public static UpiPaymentValidator create() {
        return new UpiPaymentValidator();
    }


    public UpiPaymentValidator withOrchestrationValidator(OrchestrationValidator validator) {
        this.orchestrationValidator = validator;
        return this;
    }

    public UpiPaymentValidator withHallucinationValidator(HallucinationValidator validator) {
        this.hallucinationValidator = validator;
        return this;
    }

    public UpiPaymentValidator withRetrievalValidator(RetrievalValidator validator) {
        this.retrievalValidator = validator;
        return this;
    }

    public UpiPaymentValidator withComplianceValidator(ComplianceValidator validator) {
        this.complianceValidator = validator;
        return this;
    }

    @Override
    public ValidationReport verify(AgentResponse resp) {
        List<String> failures = new ArrayList<>();

        //  Orchestration
        OrchestrationIssue orchIssue = orchestrationValidator.getIssues(resp.getConversationLog());
        if (orchIssue.hasAnyIssue()) {
            failures.add("ORCHESTRATION FAILURE: " + orchIssue);
        }

        //  Hallucination
        boolean hasHallucination = hallucinationValidator.hasHallucination(
            resp.getConversationLog(), resp.getSessionId()
        );
        if (hasHallucination) {
            failures.add("HALLUCINATION DETECTED");
        }

        //  Retrieval
        double retrievalScore = retrievalValidator.getRetrievalScore(
            resp.getBody(), resp.getConversationLog(), resp.getJourneyType()
        );
        if (retrievalScore < 0.95) {
            failures.add("RAG DRIFT");
        }

        //  Compliance
        ComplianceReport compReport = complianceValidator.validate(
            resp.getBody(), resp.getConversationLog(), resp.getJourneyType()
        );
        if (!compReport.isCompliant()) {
            failures.addAll(compReport.getViolations());
        }

        boolean passed = failures.isEmpty();

        return new ValidationReport(
            passed,
            failures,
            "UPI Payment",
            hasHallucination ? 1.0 : 0.0,
            orchIssue.hasCriticalIssue() ? 0.0 : 1.0,
            retrievalScore,
            compReport.isCompliant() ? 1.0 : 0.0
        );
    }

    @Override
    public String getJourneyName() {
        return "UPI Payment";
    }
}