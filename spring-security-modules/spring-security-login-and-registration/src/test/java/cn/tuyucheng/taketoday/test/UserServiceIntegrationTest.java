package cn.tuyucheng.taketoday.test;


import cn.tuyucheng.taketoday.persistence.dao.RoleRepository;
import cn.tuyucheng.taketoday.persistence.dao.UserRepository;
import cn.tuyucheng.taketoday.persistence.dao.VerificationTokenRepository;
import cn.tuyucheng.taketoday.persistence.model.Role;
import cn.tuyucheng.taketoday.persistence.model.User;
import cn.tuyucheng.taketoday.persistence.model.VerificationToken;
import cn.tuyucheng.taketoday.service.IUserService;
import cn.tuyucheng.taketoday.service.UserService;
import cn.tuyucheng.taketoday.spring.LoginNotificationConfig;
import cn.tuyucheng.taketoday.spring.ServiceConfig;
import cn.tuyucheng.taketoday.spring.TestDbConfig;
import cn.tuyucheng.taketoday.spring.TestIntegrationConfig;
import cn.tuyucheng.taketoday.validation.EmailExistsException;
import cn.tuyucheng.taketoday.web.dto.UserDto;
import cn.tuyucheng.taketoday.web.error.UserAlreadyExistException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestDbConfig.class, ServiceConfig.class, TestIntegrationConfig.class, LoginNotificationConfig.class})
public class UserServiceIntegrationTest {

	@Autowired
	private IUserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private VerificationTokenRepository tokenRepository;

	@Test
	public void givenNewUser_whenRegistered_thenCorrect() throws EmailExistsException {
		final String userEmail = UUID.randomUUID().toString();
		final UserDto userDto = createUserDto(userEmail);

		final User user = userService.registerNewUserAccount(userDto);

		assertNotNull(user);
		assertNotNull(user.getEmail());
		assertEquals(userEmail, user.getEmail());
		assertNotNull(user.getId());
	}

	@Test
	public void givenDetachedUser_whenAccessingEntityAssociations_thenCorrect() {
		Role role = roleRepository.findByName("ROLE_USER");
		if (role == null) {
			roleRepository.saveAndFlush(new Role("ROLE_USER"));
		}

		// detached entity
		final User user = registerUser();

		// only roles are eagerly fetched
		assertNotNull(user.getRoles());

		List<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
		assertEquals(1, roles.size());
		assertEquals("ROLE_USER", roles.iterator().next());
	}

	@Test
	public void givenDetachedUser_whenServiceLoadById_thenCorrect() throws EmailExistsException {
		final User user = registerUser();
		final User user2 = userService.getUserByID(user.getId()).get();
		assertEquals(user, user2);
	}

	@Test
	public void givenDetachedUser_whenServiceLoadByEmail_thenCorrect() throws EmailExistsException {
		final User user = registerUser();
		final User user2 = userService.findUserByEmail(user.getEmail());
		assertEquals(user, user2);
	}

	@Test
	public void givenUserRegistered_whenDuplicatedRegister_thenCorrect() {
		assertThrows(UserAlreadyExistException.class, () -> {

			final String email = UUID.randomUUID().toString();
			final UserDto userDto = createUserDto(email);
			userService.registerNewUserAccount(userDto);
			userService.registerNewUserAccount(userDto);
		});

	}

	@Transactional
	public void givenUserRegistered_whenDtoRoleAdmin_thenUserNotAdmin() {
		assertNotNull(roleRepository);
		final UserDto userDto = new UserDto();
		userDto.setEmail(UUID.randomUUID().toString());
		userDto.setPassword("SecretPassword");
		userDto.setMatchingPassword("SecretPassword");
		userDto.setFirstName("First");
		userDto.setLastName("Last");
		assertNotNull(roleRepository.findByName("ROLE_ADMIN"));
		final Long adminRoleId = roleRepository.findByName("ROLE_ADMIN").getId();
		assertNotNull(adminRoleId);
		userDto.setRole(adminRoleId.intValue());
		final User user = userService.registerNewUserAccount(userDto);
		assertFalse(user.getRoles().stream().map(Role::getId).anyMatch(ur -> ur.equals(adminRoleId)));
	}

	@Test
	public void givenUserRegistered_whenCreateToken_thenCorrect() {
		final User user = registerUser();
		final String token = UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);
	}

	@Test
	public void givenUserRegistered_whenCreateTokenCreateDuplicate_thenCorrect() {
		final User user = registerUser();
		final String token = UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);
		userService.createVerificationTokenForUser(user, token);
	}

	@Test
	public void givenUserAndToken_whenLoadToken_thenCorrect() {
		final User user = registerUser();
		final String token = UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);
		final VerificationToken verificationToken = userService.getVerificationToken(token);
		assertNotNull(verificationToken);
		assertNotNull(verificationToken.getId());
		assertNotNull(verificationToken.getUser());
		assertEquals(user, verificationToken.getUser());
		assertEquals(user.getId(), verificationToken.getUser().getId());
		assertEquals(token, verificationToken.getToken());
		assertTrue(verificationToken.getExpiryDate().toInstant().isAfter(Instant.now()));
	}

	@Test
	public void givenUserAndToken_whenRemovingUser_thenCorrect() {
		final User user = registerUser();
		final String token = UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);
		userService.deleteUser(user);
	}

	@Test
	public void givenUserAndToken_whenRemovingUserByDao_thenFKViolation() {
		assertThrows(DataIntegrityViolationException.class, () -> {
			final User user = registerUser();
			final String token = UUID.randomUUID().toString();
			userService.createVerificationTokenForUser(user, token);
			final long userId = user.getId();
			userService.getVerificationToken(token).getId();
			userRepository.deleteById(userId);
		});
	}

	@Test
	public void givenUserAndToken_whenRemovingTokenThenUser_thenCorrect() {
		final User user = registerUser();
		final String token = UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);
		final long userId = user.getId();
		final long tokenId = userService.getVerificationToken(token).getId();
		tokenRepository.deleteById(tokenId);
		userRepository.deleteById(userId);
	}

	@Test
	public void givenUserAndToken_whenRemovingToken_thenCorrect() {
		final User user = registerUser();
		final String token = UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);
		final long tokenId = userService.getVerificationToken(token).getId();
		tokenRepository.deleteById(tokenId);
	}

	@Test
	public void givenUserAndToken_whenNewTokenRequest_thenCorrect() {
		final User user = registerUser();
		final String token = UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);
		final VerificationToken origToken = userService.getVerificationToken(token);
		final VerificationToken newToken = userService.generateNewVerificationToken(token);
		assertNotEquals(newToken.getToken(), origToken.getToken());
		assertNotEquals(newToken.getExpiryDate(), origToken.getExpiryDate());
		assertNotEquals(newToken, origToken);
	}

	@Test
	public void givenTokenValidation_whenValid_thenUserEnabled_andTokenRemoved() {
		User user = registerUser();
		final String token = UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);
		final long userId = user.getId();
		final String token_status = userService.validateVerificationToken(token);
		assertEquals(token_status, UserService.TOKEN_VALID);
		user = userService.getUserByID(userId).get();
		assertTrue(user.isEnabled());
	}

	@Test
	public void givenTokenValidation_whenInvalid_thenCorrect() {
		final User user = registerUser();
		final String token = UUID.randomUUID().toString();
		final String invalid_token = "INVALID_" + UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);
		userService.getVerificationToken(token).getId();
		final String token_status = userService.validateVerificationToken(invalid_token);
		token_status.equals(UserService.TOKEN_INVALID);
	}

	@Test
	public void givenTokenValidation_whenExpired_thenCorrect() {
		final User user = registerUser();
		final String token = UUID.randomUUID().toString();
		userService.createVerificationTokenForUser(user, token);
		user.getId();
		final VerificationToken verificationToken = userService.getVerificationToken(token);
		verificationToken.setExpiryDate(Date.from(verificationToken.getExpiryDate().toInstant().minus(2, ChronoUnit.DAYS)));
		tokenRepository.saveAndFlush(verificationToken);
		final String token_status = userService.validateVerificationToken(token);
		assertNotNull(token_status);
		token_status.equals(UserService.TOKEN_EXPIRED);
	}

	//

	private UserDto createUserDto(final String email) {
		final UserDto userDto = new UserDto();
		userDto.setEmail(email);
		userDto.setPassword("SecretPassword");
		userDto.setMatchingPassword("SecretPassword");
		userDto.setFirstName("First");
		userDto.setLastName("Last");
		userDto.setRole(0);
		return userDto;
	}

	private User registerUser() {
		final String email = UUID.randomUUID().toString();
		final UserDto userDto = createUserDto(email);
		final User user = userService.registerNewUserAccount(userDto);
		assertNotNull(user);
		assertNotNull(user.getId());
		assertEquals(email, user.getEmail());
		return user;
	}

}
