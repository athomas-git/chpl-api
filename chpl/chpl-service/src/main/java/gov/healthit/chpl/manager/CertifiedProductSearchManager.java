package gov.healthit.chpl.manager;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gov.healthit.chpl.caching.CacheNames;
import gov.healthit.chpl.dao.CertifiedProductSearchDAO;
import gov.healthit.chpl.domain.compliance.DirectReview;
import gov.healthit.chpl.domain.compliance.DirectReviewNonConformity;
import gov.healthit.chpl.domain.search.CertifiedProductBasicSearchResult;
import gov.healthit.chpl.domain.search.CertifiedProductFlatSearchResult;
import gov.healthit.chpl.domain.search.CertifiedProductFlatSearchResultLegacy;
import gov.healthit.chpl.domain.search.SearchRequest;
import gov.healthit.chpl.domain.search.SearchResponse;
import gov.healthit.chpl.service.DirectReviewSearchService;

@Service
public class CertifiedProductSearchManager {
    private CertifiedProductSearchDAO searchDao;
    private DirectReviewSearchService drService;

    @Autowired
    public CertifiedProductSearchManager(CertifiedProductSearchDAO searchDao, DirectReviewSearchService drService) {
        this.searchDao = searchDao;
        this.drService = drService;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.COLLECTIONS_LISTINGS, key = "'listings'")
    public List<CertifiedProductFlatSearchResult> search() {
        List<CertifiedProductFlatSearchResult> results = searchDao.getAllCertifiedProducts();
        results.stream()
            .forEach(searchResult -> populateDirectReviewFields(searchResult));
        return results;
    }

    private void populateDirectReviewFields(CertifiedProductFlatSearchResult searchResult) {
        List<DirectReview> listingDrs = drService.getDirectReviewsRelatedToListing(searchResult.getId(), searchResult.getDeveloperId());
        searchResult.setDirectReviewCount(listingDrs.size());
        searchResult.setOpenDirectReviewNonConformityCount(
                calculateNonConformitiesWithStatusForListing(listingDrs, searchResult.getId(), DirectReviewNonConformity.STATUS_OPEN));
        searchResult.setClosedDirectReviewNonConformityCount(
                calculateNonConformitiesWithStatusForListing(listingDrs, searchResult.getId(), DirectReviewNonConformity.STATUS_CLOSED));
    }

    private int calculateNonConformitiesWithStatusForListing(List<DirectReview> listingDrs, Long listingId,
            String nonConformityStatus) {
        return (int) listingDrs.stream()
        .filter(dr -> dr.getNonConformities() != null && dr.getNonConformities().size() > 0)
        .flatMap(dr -> dr.getNonConformities().stream())
        .filter(nc -> isNonConformityRelatedToListing(nc, listingId))
        .filter(nc -> nc.getNonConformityStatus() != null
                && nc.getNonConformityStatus().equalsIgnoreCase(nonConformityStatus))
        .count();
    }

    private boolean isNonConformityRelatedToListing(DirectReviewNonConformity nonConformity, Long listingId) {
        if (nonConformity.getDeveloperAssociatedListings() == null
                || nonConformity.getDeveloperAssociatedListings().size() == 0) {
            return true;
        }
        return nonConformity.getDeveloperAssociatedListings().stream()
                .filter(dal -> dal.getId().equals(listingId))
                .findAny().isPresent();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.COLLECTIONS_LISTINGS, key = "'legacyListings'")
    @Deprecated
    public List<CertifiedProductFlatSearchResultLegacy> searchLegacy() {
        List<CertifiedProductFlatSearchResultLegacy> results = searchDao.getAllCertifiedProductsLegacy();
        return results;
    }

    @Transactional
    public SearchResponse search(SearchRequest searchRequest) {

        Collection<CertifiedProductBasicSearchResult> searchResults = searchDao.search(searchRequest);
        int totalCountSearchResults = searchDao.getTotalResultCount(searchRequest);

        SearchResponse response = new SearchResponse(Integer.valueOf(totalCountSearchResults),
                searchResults, searchRequest.getPageSize(), searchRequest.getPageNumber());
        return response;
    }
}
