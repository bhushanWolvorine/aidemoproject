package com.aidemoproject.validator.orchestration;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable result of orchestration validation Pure class — no records, no
 * preview features
 */
public class OrchestrationIssue {

	private final List<String> missingTools;
	private final List<String> unexpectedTools;
	private final List<String> wrongOrder;

	public OrchestrationIssue(List<String> missingTools, List<String> unexpectedTools, List<String> wrongOrder) {
		// Defensive copy — immutable
		this.missingTools = missingTools != null ? Collections.unmodifiableList(new ArrayList<>(missingTools))
				: Collections.emptyList();
		this.unexpectedTools = unexpectedTools != null ? Collections.unmodifiableList(new ArrayList<>(unexpectedTools))
				: Collections.emptyList();
		this.wrongOrder = wrongOrder != null ? Collections.unmodifiableList(new ArrayList<>(wrongOrder))
				: Collections.emptyList();
	}

	public List<String> getMissingTools() {
		return missingTools;
	}

	public List<String> getUnexpectedTools() {
		return unexpectedTools;
	}

	public List<String> getWrongOrder() {
		return wrongOrder;
	}

	public boolean hasCriticalIssue() {
		return !missingTools.isEmpty() || !wrongOrder.isEmpty();
	}

	public boolean hasAnyIssue() {
		return !missingTools.isEmpty() || !unexpectedTools.isEmpty() || !wrongOrder.isEmpty();
	}

	@Override
	public String toString() {
		return "OrchestrationIssue{" + "missing=" + missingTools + ", unexpected=" + unexpectedTools + ", wrongOrder="
				+ wrongOrder + '}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof OrchestrationIssue))
			return false;
		OrchestrationIssue that = (OrchestrationIssue) o;
		return Objects.equals(missingTools, that.missingTools) && Objects.equals(unexpectedTools, that.unexpectedTools)
				&& Objects.equals(wrongOrder, that.wrongOrder);
	}

	@Override
	public int hashCode() {
		return Objects.hash(missingTools, unexpectedTools, wrongOrder);
	}
}