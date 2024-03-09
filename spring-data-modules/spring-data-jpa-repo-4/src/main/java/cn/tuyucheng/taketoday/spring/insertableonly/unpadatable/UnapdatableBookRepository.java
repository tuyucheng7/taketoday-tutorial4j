package cn.tuyucheng.taketoday.spring.insertableonly.unpadatable;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UnapdatableBookRepository extends JpaRepository<UnapdatableBook, Long> {
}