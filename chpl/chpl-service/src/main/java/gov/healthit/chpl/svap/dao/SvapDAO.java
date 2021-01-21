package gov.healthit.chpl.svap.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import gov.healthit.chpl.dao.impl.BaseDAOImpl;
import gov.healthit.chpl.exception.EntityRetrievalException;
import gov.healthit.chpl.svap.domain.Svap;
import gov.healthit.chpl.svap.domain.SvapCriteriaMap;
import gov.healthit.chpl.svap.entity.SvapCriteriaMapEntity;
import gov.healthit.chpl.svap.entity.SvapEntity;

@Repository
public class SvapDAO extends BaseDAOImpl {

    public Svap getById(Long id) throws EntityRetrievalException {
        SvapEntity entity = getSvapEntityById(id);
        if (entity != null) {
            return new Svap(entity);
        }
        return null;
    }

    public List<SvapCriteriaMap> getAllSvapCriteriaMap() throws EntityRetrievalException {
        return getAllSvapCriteriaMapEntities().stream()
                .map(e -> new SvapCriteriaMap(e))
                .collect(Collectors.toList());
    }

    private SvapEntity getSvapEntityById(Long id) throws EntityRetrievalException {
        List<SvapEntity> result = entityManager.createQuery("SELECT s "
                        + "FROM SvapEntity s "
                        + "WHERE (NOT s.deleted = true) "
                        + "AND (s.id = :entityid) ",
                        SvapEntity.class)
                .setParameter("entityid", id)
                .getResultList();

        if (result.size() > 1) {
            throw new EntityRetrievalException("Data error. Duplicate svap id in database.");
        }

        return result.get(0);
    }

    private List<SvapCriteriaMapEntity> getAllSvapCriteriaMapEntities() throws EntityRetrievalException {
        return entityManager.createQuery("SELECT scm "
                        + "FROM SvapCriteriaMapEntity scm "
                        + "JOIN FETCH scm.criteria c "
                        + "JOIN FETCH c.certificationEdition "
                        + "JOIN FETCH scm.svap "
                        + "WHERE scm.deleted <> true ",
                        SvapCriteriaMapEntity.class)
                .getResultList();
    }

}