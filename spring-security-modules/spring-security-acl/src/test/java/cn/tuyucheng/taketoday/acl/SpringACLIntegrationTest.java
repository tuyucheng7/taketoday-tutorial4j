package cn.tuyucheng.taketoday.acl;

import cn.tuyucheng.taketoday.acl.persistence.dao.NoticeMessageRepository;
import cn.tuyucheng.taketoday.acl.persistence.entity.NoticeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.ServletTestExecutionListener;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@TestExecutionListeners(listeners = {ServletTestExecutionListener.class,
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      TransactionalTestExecutionListener.class,
      WithSecurityContextTestExecutionListener.class})
class SpringACLIntegrationTest extends AbstractJUnit4SpringContextTests {

    private static Integer FIRST_MESSAGE_ID = 1;
    private static Integer SECOND_MESSAGE_ID = 2;
    private static Integer THIRD_MESSAGE_ID = 3;
    private static String EDITTED_CONTENT = "EDITED";

    @Configuration
    @ComponentScan("cn.tuyucheng.taketoday.acl.*")
    public static class SpringConfig {

    }

    @Autowired
    NoticeMessageRepository repo;

    @Test
    @WithMockUser(username = "manager")
    void givenUserManager_whenFindAllMessage_thenReturnFirstMessage() {
        List<NoticeMessage> details = repo.findAll();
        assertNotNull(details);
        assertEquals(1, details.size());
        assertEquals(FIRST_MESSAGE_ID, details.get(0).getId());
    }

    @Test
    @WithMockUser(username = "manager")
    void givenUserManager_whenFind1stMessageByIdAndUpdateItsContent_thenOK() {
        NoticeMessage firstMessage = repo.findById(FIRST_MESSAGE_ID);
        assertNotNull(firstMessage);
        assertEquals(FIRST_MESSAGE_ID, firstMessage.getId());

        firstMessage.setContent(EDITTED_CONTENT);
        repo.save(firstMessage);

        NoticeMessage editedFirstMessage = repo.findById(FIRST_MESSAGE_ID);
        assertNotNull(editedFirstMessage);
        assertEquals(FIRST_MESSAGE_ID, editedFirstMessage.getId());
        assertEquals(EDITTED_CONTENT, editedFirstMessage.getContent());
    }

    @Test
    @WithMockUser(username = "hr")
    void givenUsernameHr_whenFindMessageById2_thenOK() {
        NoticeMessage secondMessage = repo.findById(SECOND_MESSAGE_ID);
        assertNotNull(secondMessage);
        assertEquals(SECOND_MESSAGE_ID, secondMessage.getId());
    }

    @Test
    @WithMockUser(username = "hr")
    void givenUsernameHr_whenUpdateMessageWithId2_thenFail() {
        NoticeMessage secondMessage = new NoticeMessage();
        secondMessage.setId(SECOND_MESSAGE_ID);
        secondMessage.setContent(EDITTED_CONTENT);

        assertThrows(AccessDeniedException.class, () -> repo.save(secondMessage));
    }

    @Test
    @WithMockUser(roles = {"EDITOR"})
    void givenRoleEditor_whenFindAllMessage_thenReturn3Message() {
        List<NoticeMessage> details = repo.findAll();
        assertNotNull(details);
        assertEquals(3, details.size());
    }

    @Test
    @WithMockUser(roles = {"EDITOR"})
    void givenRoleEditor_whenUpdateThirdMessage_thenOK() {
        NoticeMessage thirdMessage = new NoticeMessage();
        thirdMessage.setId(THIRD_MESSAGE_ID);
        thirdMessage.setContent(EDITTED_CONTENT);
        repo.save(thirdMessage);
    }

    @Test
    @WithMockUser(roles = {"EDITOR"})
    void givenRoleEditor_whenFind1stMessageByIdAndUpdateContent_thenFail() {
        NoticeMessage firstMessage = repo.findById(FIRST_MESSAGE_ID);
        assertNotNull(firstMessage);
        assertEquals(FIRST_MESSAGE_ID, firstMessage.getId());
        firstMessage.setContent(EDITTED_CONTENT);
        assertThrows(AccessDeniedException.class, () -> repo.save(firstMessage));
    }
}