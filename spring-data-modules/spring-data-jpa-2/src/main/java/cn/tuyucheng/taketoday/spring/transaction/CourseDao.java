package cn.tuyucheng.taketoday.spring.transaction;

import cn.tuyucheng.taketoday.spring.hibernate.AbstractHibernateDao;
import org.springframework.stereotype.Repository;

@Repository
public class CourseDao extends AbstractHibernateDao<Course> {
   public CourseDao() {
      super();
      setClazz(Course.class);
   }
}