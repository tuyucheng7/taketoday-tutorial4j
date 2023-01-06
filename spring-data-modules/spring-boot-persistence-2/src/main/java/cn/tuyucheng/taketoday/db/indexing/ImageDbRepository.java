package cn.tuyucheng.taketoday.db.indexing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ImageDbRepository extends JpaRepository<Image, Long> {

}