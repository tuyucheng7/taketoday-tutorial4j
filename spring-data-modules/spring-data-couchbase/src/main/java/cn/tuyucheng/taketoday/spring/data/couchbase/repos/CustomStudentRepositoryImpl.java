package cn.tuyucheng.taketoday.spring.data.couchbase.repos;

import cn.tuyucheng.taketoday.spring.data.couchbase.model.Student;
import com.couchbase.client.java.view.Stale;
import com.couchbase.client.java.view.ViewQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;

import java.util.List;

public class CustomStudentRepositoryImpl implements CustomStudentRepository {

    private static final String DESIGN_DOC = "student";

    @Autowired
    private CouchbaseTemplate template;

    public List<Student> findByFirstNameStartsWith(String s) {
        return template.findByView(ViewQuery.from(DESIGN_DOC, "byFirstName").startKey(s).stale(Stale.FALSE), Student.class);
    }
}