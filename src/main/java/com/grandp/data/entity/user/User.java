package com.grandp.data.entity.user;

import com.grandp.data.entity.student_data.StudentData;
import com.grandp.data.hasher.PasswordHasher;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.grandp.data.entity.authority.SimpleAuthority;

import lombok.Builder;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Builder
@Entity
@Table
@AllArgsConstructor
@Setter
public class User implements SimpleUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "first_name")
	@Pattern(regexp = UserHelper.REGEX_NAME, message = "First name must start with capital letter containing only letters and/or special characters - comma, dot, hyphen, space or apostrophe.")
	private String firstName;

	@Column(name = "last_name")
	@Pattern(regexp = UserHelper.REGEX_NAME, message = "Last name must start with capital letter containing only letters and/or special characters - comma, dot, hyphen, space or apostrophe.")
	private String lastName;

	@Column(name = "email")
//	@Pattern(regexp = UserHelper.REGEX_EMAIL, message = "Email address must be a valid mail address - must start with a non-special character, containing a '@' and a '.'")
	private String email;

	@Column(name = "personal_id")
	@Pattern(regexp = UserHelper.REGEX_PERSONAL_ID, message = "Personal ID must contain only digits and must have length 8-12.")
	private String personalId; // Uniform Civil Number

	@Column(name = "password")
//	@Pattern(regexp = UserHelper.REGEX_PASSWORD, message = "Password must be min 8 and max 32 length containing at least 1 uppercase, 1 lowercase, 1 special character and 1 digit.")
	private String password;

	// ============================================================================================
	private boolean isExpired;

	private boolean isActive;

	private boolean isLocked;

	@OneToOne
	private StudentData userData; //FIXME this should be SimpleData

	@ManyToMany(cascade = CascadeType.ALL ,fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_authorities",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "authority_id"))
	private Set<SimpleAuthority> authorities = new HashSet<>();


	public User() {
		
	}

	@JsonCreator
	public User(@JsonProperty("firstName") String firstName,
				@JsonProperty("lastName") String lastName,
				@JsonProperty("email") String email,
				@JsonProperty("personalId") String personalId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.personalId = checkNumericField(personalId, UserUtils.PERSONAL_ID);
		String pwd = firstName + personalId + ".";
		this.password = PasswordHasher.getHasher().encode(pwd); // first password is the personalId
		
		isActive = true;
		isExpired = false;
		isLocked = false;
	}

	private String checkField(String input, String fieldName) {
		assertNotNull(input, fieldName);

		return input;
	}

	private String checkNumericField(String personalId, String fieldName) {
		assertNotNull(personalId, fieldName);
		assertOnlyDigits(personalId, fieldName);

		return personalId;
	}

	private void assertNotNull(String input, String fieldName) {
		if (input == null) {
			throw new IllegalArgumentException(fieldName + " cannot be null.");
		}
	}

	private void assertOnlyDigits(String input, String fieldName) {
		if (!input.matches(UserUtils.REGEX_DIGITS_ONLY)) {
			throw new IllegalArgumentException(fieldName + " can contain only digits.");
		}
	}

	public long getId() {
		return this.id;
	}

	@Override
	public Set<SimpleAuthority> getAuthorities() {
		return authorities;
	}
	
	public boolean hasAuthority(String authority) {
		return authorities.stream().anyMatch(auth -> auth.getAuthority().equalsIgnoreCase(authority));
	}

	public void removeAuthority(SimpleAuthority authority) {
		this.authorities.remove(authority);
	}

	public void addAuthority(SimpleAuthority authority) {
		this.authorities.add(authority);
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getEmail() {
		return this.email;
	}

	public String getPersonalId() {
		return this.personalId;
	}

	public StudentData getUserData() {
		return this.userData;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !this.isExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !this.isLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return this.isActive;
	}

	public boolean isAdministrator() {
		return this.authorities.contains(SimpleAuthority.ADMINISTRATOR);
	}

	public boolean isTeacher() {
		return this.authorities.contains(SimpleAuthority.TEACHER);
	}

	public boolean isStudent(){
		if (! this.authorities.contains(SimpleAuthority.STUDENT)) {
//			throw new Exception("User with personalID '" + personalId + "' does not have Role 'STUDENT'.");
			return false;
		}

		if (! (this.userData instanceof StudentData)) {
//			throw new Exception("User with personalID '" + personalId + "' does not have assigned Student Data."); //FIXME trace this
			return false;
		}

		return true;
	}

	public boolean isGuest() {
		return this.authorities.contains(SimpleAuthority.GUEST);
	}

	public void setUserData(StudentData userData) {
		this.userData = userData;
	}

	@Override
	public String toString() {
		return "User [" +
				"id=" + id + ", " +
				"firstName=" + firstName +
				", lastName=" + lastName +
				", email=" + email +
				", personalId=" + personalId +
				", isExpired=" + isExpired +
				", isActive=" + isActive +
				", isLocked=" + isLocked +
				", authorities=" + String.join(", ", authorities.stream().map(SimpleAuthority::getAuthority).toArray(String[]::new)) + "]";
	}

	
	
	
}