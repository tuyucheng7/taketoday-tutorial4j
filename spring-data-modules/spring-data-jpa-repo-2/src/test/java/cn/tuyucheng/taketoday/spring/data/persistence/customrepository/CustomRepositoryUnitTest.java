package cn.tuyucheng.taketoday.spring.data.persistence.customrepository;

import cn.tuyucheng.taketoday.spring.data.persistence.customrepository.model.User;
import cn.tuyucheng.taketoday.spring.data.persistence.customrepository.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = CustomRepositoryApplication.class)
class CustomRepositoryUnitTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void givenCustomRepository_whenInvokeCustomFindMethod_thenEntityIsFound() {
        User user = new User();
        user.setEmail("foo@gmail.com");
        user.setName("userName");

        User persistedUser = userRepository.save(user);

        assertEquals(persistedUser, userRepository.customFindMethod(user.getId()));
    }
}