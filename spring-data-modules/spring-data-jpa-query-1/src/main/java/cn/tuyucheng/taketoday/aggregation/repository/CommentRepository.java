package cn.tuyucheng.taketoday.aggregation.repository;

import cn.tuyucheng.taketoday.aggregation.model.Comment;
import cn.tuyucheng.taketoday.aggregation.model.custom.CommentCount;
import cn.tuyucheng.taketoday.aggregation.model.custom.ICommentCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

	@Query("SELECT c.year, COUNT(c.year) FROM Comment AS c GROUP BY c.year ORDER BY c.year DESC")
	List<Object[]> countTotalCommentsByYear();

	@Query("SELECT new cn.tuyucheng.taketoday.aggregation.model.custom.CommentCount(c.year, COUNT(c.year)) FROM Comment AS c GROUP BY c.year ORDER BY c.year DESC")
	List<CommentCount> countTotalCommentsByYearClass();

	@Query("SELECT c.year AS yearComment, COUNT(c.year) AS totalComment FROM Comment AS c GROUP BY c.year ORDER BY c.year DESC")
	List<ICommentCount> countTotalCommentsByYearInterface();

	@Query(value = "SELECT c.\"year\" AS yearComment, COUNT(c.*) AS totalComment FROM \"comment\" AS c GROUP BY c.\"year\" ORDER BY c.\"year\" DESC", nativeQuery = true)
	List<ICommentCount> countTotalCommentsByYearNative();
}