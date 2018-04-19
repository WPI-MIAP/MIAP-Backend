package org.maras.framework;

import java.util.List;

/**
 * Function representing the status of the system
 * This class is used to simplify the JSON management
 * @author Brian
 *
 */
public class MarasStatus {
	@Override
	public String toString() {
		return "Status [status=" + status + ", updated=" + updated + ", sources=" + sources + "]";
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public List<String> getSources() {
		return sources;
	}

	public void setSources(List<String> sources) {
		this.sources = sources;
	}
	
	public void addSource(String source) {
		this.sources.add(source);
	}

	private String status;
	private String updated;
	private List<String> sources;
	private String message;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public MarasStatus(String status, String updated, List<String> sources, String message) {
		this.status = status;
		this.updated = updated;
		this.sources = sources;
		this.message = message;
	}
}
