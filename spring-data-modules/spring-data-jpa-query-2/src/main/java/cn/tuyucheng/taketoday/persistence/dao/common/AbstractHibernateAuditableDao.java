package cn.tuyucheng.taketoday.persistence.dao.common;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditQuery;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unchecked")
public class AbstractHibernateAuditableDao<T extends Serializable> extends AbstractHibernateDao<T> implements IAuditOperations<T> {

    @Override
    public List<T> getEntitiesAtRevision(final Number revision) {
        final AuditReader auditReader = AuditReaderFactory.get(getCurrentSession());
        final AuditQuery query = auditReader.createQuery().forEntitiesAtRevision(clazz, revision);
        return (List<T>) query.getResultList();
    }

    @Override
    public List<T> getEntitiesModifiedAtRevision(final Number revision) {
        final AuditReader auditReader = AuditReaderFactory.get(getCurrentSession());
        final AuditQuery query = auditReader.createQuery().forEntitiesModifiedAtRevision(clazz, revision);
        return (List<T>) query.getResultList();
    }

    @Override
    public List<T> getRevisions() {
        final AuditReader auditReader = AuditReaderFactory.get(getCurrentSession());
        final AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(clazz, true, true);
        return (List<T>) query.getResultList();
    }
}