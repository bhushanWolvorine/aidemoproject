package com.aidemoproject.validator.prompt;

import java.util.List;

/**
 * Validates that the actual prompt sent to LLM hasn't regressed Catches:
 * temperature drift, bad few-shot, formatting loss, instruction removal
 */
public interface PromptValidator {

	/**
	 * Full validation — returns report
	 */
	PromptValidationReport validate(String actualPrompt, String journeyContext);

	/**
	 * Quick check — true if perfect
	 */
	boolean isValid(String actualPrompt);

	/**
	 * Returns name of the prompt template this validator protects
	 */
	String getTemplateName();
}