package gov.nyc.doitt.jobstatemanager.security;

import java.security.InvalidParameterException;

/**
 * User account details as returned from NYC.ID OAuth Service
 */
public class UserAccount {

	private String id;
	private String userType;
	private String email;
	private String firstName;
	private String lastName;
	private boolean validated;
	private boolean termsOfUse;
	private boolean active;
	private boolean nycEmployee;
	private boolean hasNYCAccount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public boolean isTermsOfUse() {
		return termsOfUse;
	}

	public void setTermsOfUse(boolean termsOfUse) {
		this.termsOfUse = termsOfUse;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isNycEmployee() {
		return nycEmployee;
	}

	public void setNycEmployee(boolean nycEmployee) {
		this.nycEmployee = nycEmployee;
	}

	public boolean isHasNYCAccount() {
		return hasNYCAccount;
	}

	public void setHasNYCAccount(boolean hasNYCAccount) {
		this.hasNYCAccount = hasNYCAccount;
	}

	public boolean hasAuth() {
		return email != null;
	}

	public String getAuthDomain() {

		String[] authIdParts = getAuthIdParts();
		return authIdParts == null ? null : authIdParts[1];
	}

	public String getAuthUser() {

		return email == null ? null : email;
	}

	private String[] getAuthIdParts() {
		if (email == null) {
			return null;
		}
		String[] parts = email.split("@");
		if (parts.length != 2) {
			throw new InvalidParameterException(String.format("email '%s'is malformed", email));
		}
		return parts;
	}
}
