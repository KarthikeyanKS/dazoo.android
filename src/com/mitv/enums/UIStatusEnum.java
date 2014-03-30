
package com.mitv.enums;



public enum UIStatusEnum 
{
	LOADING(1, "Loading"),
	SUCCESS_WITH_CONTENT(2, "Success with content"),
	SUCCESS_WITH_NO_CONTENT(2, "Success with no content"),
	NO_CONNECTION_AVAILABLE(3, "No connection available"),
	FAILED_VALIDATION(4, "Failed validation"),
	FAILED(5, "Failed"),
	API_VERSION_TOO_OLD(6, "API Version too old"),
	USER_TOKEN_EXPIRED(7, "User token has expired");

	
	private final int id;
	private final String description; /* Used for logs */

	UIStatusEnum(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.id);
		sb.append(": ");
		sb.append(this.description);

		return sb.toString();
	}

	public int getId() {
		return id;
	}
}