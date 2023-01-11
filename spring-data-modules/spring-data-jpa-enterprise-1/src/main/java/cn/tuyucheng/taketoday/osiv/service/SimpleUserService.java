package cn.tuyucheng.taketoday.osiv.service;

import cn.tuyucheng.taketoday.osiv.model.BasicUser;
import cn.tuyucheng.taketoday.osiv.repository.BasicUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SimpleUserService implements UserService {

    private final BasicUserRepository userRepository;

    public SimpleUserService(BasicUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BasicUser> findOne(String username) {
        return userRepository.findDetailedByUsername(username);
    }
}