package cn.tuyucheng.taketoday.relationships;

import cn.tuyucheng.taketoday.relationships.models.AppUser;
import cn.tuyucheng.taketoday.relationships.models.Tweet;
import cn.tuyucheng.taketoday.relationships.repositories.TweetRepository;
import cn.tuyucheng.taketoday.relationships.repositories.UserRepository;
import cn.tuyucheng.taketoday.relationships.security.AppUserPrincipal;
import cn.tuyucheng.taketoday.relationships.util.DummyContentUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.util.Assert.isTrue;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration
@DirtiesContext
class SpringDataWithSecurityIntegrationTest {
    private static UserRepository userRepository;
    private static TweetRepository tweetRepository;

    @Autowired
    private ServletContext servletContext;

    AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();

    @BeforeEach
    void testInit() {
        ctx.register(AppConfig.class);
        ctx.setServletContext(servletContext);
        ctx.refresh();
        userRepository = ctx.getBean(UserRepository.class);
        tweetRepository = ctx.getBean(TweetRepository.class);
        List<AppUser> appUsers = (List<AppUser>) userRepository.saveAll(DummyContentUtil.generateDummyUsers());
        tweetRepository.saveAll(DummyContentUtil.generateDummyTweets(appUsers));
    }

    /**
     * This is to ensure the tables are dropped in proper order.
     * After the Spring Boot 2.2.2 upgrade, DDL statements generated automatically try to drop Tweet table first.
     * As a result we get org.h2.jdbc.JdbcSQLSyntaxErrorException because Tweet_Likes table depends on Tweet.
     *
     * @see <a href="https://stackoverflow.com/questions/59364212/integrationtest-isolation-fails-in-springboot-2-2-2-release-error-dopping-table">
     * StackOverflow#59364212
     * </a>
     * @see <a href="https://stackoverflow.com/questions/59561551/hibernate-h2-specify-drop-table-order">
     * StackOverflow#59561551
     * </a>
     */
    @AfterEach
    void tearDown() {
        JdbcTemplate jdbcTemplate = ctx.getBean(JdbcTemplate.class);
        JdbcTestUtils.dropTables(jdbcTemplate, "Tweet_Likes", "Tweet");
    }

    @Test
    void givenAppUser_whenLoginSuccessful_shouldUpdateLastLogin() {
        AppUser appUser = userRepository.findByUsername("lionel@messi.com");
        Authentication auth = new UsernamePasswordAuthenticationToken(new AppUserPrincipal(appUser), null, DummyContentUtil.getAuthorities());
        SecurityContextHolder.getContext()
              .setAuthentication(auth);
        userRepository.updateLastLogin(new Date());
    }

    @Test
    void givenNoAppUserInSecurityContext_whenUpdateLastLoginAttempted_shouldFail() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> userRepository.updateLastLogin(new Date()));
    }

    @Test
    void givenAppUser_whenLoginSuccessful_shouldReadMyPagedTweets() {
        AppUser appUser = userRepository.findByUsername("lionel@messi.com");
        Authentication auth = new UsernamePasswordAuthenticationToken(new AppUserPrincipal(appUser), null, DummyContentUtil.getAuthorities());
        SecurityContextHolder.getContext()
              .setAuthentication(auth);
        Page<Tweet> page = null;
        do {
            page = tweetRepository.getMyTweetsAndTheOnesILiked(PageRequest.of(page != null ? page.getNumber() + 1 : 0, 5));
            for (Tweet twt : page.getContent()) {
                isTrue((Objects.equals(twt.getOwner(), appUser.getUsername())) || (twt.getLikes()
                      .contains(appUser.getUsername())), "I do not have any Tweets");
            }
        } while (page.hasNext());
    }

    @Test
    void givenNoAppUser_whenPaginatedResultsRetrievalAttempted_shouldFail() {
        Page<Tweet> page = null;
        assertThrows(InvalidDataAccessApiUsageException.class, () -> tweetRepository.getMyTweetsAndTheOnesILiked(PageRequest.of(0, 5)));
    }
}