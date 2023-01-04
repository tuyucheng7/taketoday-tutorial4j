package cn.tuyucheng.taketoday.persistence.dao;

import cn.tuyucheng.taketoday.persistence.model.MyUser;
import cn.tuyucheng.taketoday.persistence.model.QMyUser;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.SingleValueBinding;

public interface MyUserRepository extends JpaRepository<MyUser, Long>, QuerydslPredicateExecutor<MyUser>, QuerydslBinderCustomizer<QMyUser> {
    @Override
    default public void customize(final QuerydslBindings bindings, final QMyUser root) {
        bindings.bind(String.class)
              .first((SingleValueBinding<StringPath, String>) StringExpression::containsIgnoreCase);
        bindings.excluding(root.email);
    }
}