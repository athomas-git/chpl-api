package gov.healthit.chpl.dao.statistics;

import gov.healthit.chpl.dao.impl.BaseDAOImpl;
import gov.healthit.chpl.dto.NonconformityTypeStatisticsDTO;
import gov.healthit.chpl.entity.ParticipantExperienceStatisticsEntity;
import gov.healthit.chpl.entity.surveillance.NonconformityTypeStatisticsEntity;
import gov.healthit.chpl.exception.EntityRetrievalException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository("nonconformityTypeStatisticsDAO")
public class NonconformityTypeStatisticsDAOImpl extends BaseDAOImpl implements NonconformityTypeStatisticsDAO {

    @Override
    public List<NonconformityTypeStatisticsDTO> getAllNonconformityStatistics() {
        String hql = "FROM NonconformityTypeStatisticsEntity WHERE deleted = false";
        Query query = entityManager.createQuery(hql);

        List<NonconformityTypeStatisticsEntity> entities = query.getResultList();

        List<NonconformityTypeStatisticsDTO> dtos = new ArrayList<NonconformityTypeStatisticsDTO>();
        for (NonconformityTypeStatisticsEntity entity : entities) {
            NonconformityTypeStatisticsDTO dto = new NonconformityTypeStatisticsDTO(entity);
            dtos.add(dto);
        }

        return dtos;
    }

    public void create(NonconformityTypeStatisticsDTO dto) {
        NonconformityTypeStatisticsEntity entity = new NonconformityTypeStatisticsEntity();
        entity.setNonconformityCount(dto.getNonconformityCount());
        entity.setNonconformityType(dto.getNonconformityType());
        if (dto.getLastModifiedDate() == null) {
            entity.setLastModifiedDate(new Date());
        } else {
            entity.setLastModifiedDate(dto.getLastModifiedDate());
        }

        if (dto.getLastModifiedUser() == null) {
            entity.setLastModifiedUser(-2L);
        } else {
            entity.setLastModifiedUser(dto.getLastModifiedUser());
        }

        if (dto.getDeleted() == null) {
            entity.setDeleted(false);
        } else {
            entity.setDeleted(dto.getDeleted());
        }
        entityManager.persist(entity);
        entityManager.flush();
    }
    
    @Override
    public void delete(final Long id) throws EntityRetrievalException {
        NonconformityTypeStatisticsEntity toDelete = getEntityById(id);

        if (toDelete != null) {
            toDelete.setDeleted(true);
            toDelete.setLastModifiedUser(getUserId());
            entityManager.merge(toDelete);
        }
    }
    
    private NonconformityTypeStatisticsEntity getEntityById(final Long id) throws EntityRetrievalException {
        NonconformityTypeStatisticsEntity entity = null;

        Query query = entityManager.createQuery(
                "from NonconformityTypeStatisticsEntity a where (NOT deleted = true) AND (id = :entityid) ",
                NonconformityTypeStatisticsEntity.class);
        query.setParameter("entityid", id);
        List<NonconformityTypeStatisticsEntity> result = query.getResultList();

        if (result.size() > 1) {
            throw new EntityRetrievalException("Data error. Duplicate id in database.");
        } else if (result.size() == 1) {
            entity = result.get(0);
        }

        return entity;
    }
}
