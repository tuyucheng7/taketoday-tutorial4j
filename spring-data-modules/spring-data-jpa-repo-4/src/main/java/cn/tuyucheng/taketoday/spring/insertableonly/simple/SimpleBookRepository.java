package cn.tuyucheng.taketoday.spring.insertableonly.simple;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SimpleBookRepository extends JpaRepository<SimpleBook, Long> {
}