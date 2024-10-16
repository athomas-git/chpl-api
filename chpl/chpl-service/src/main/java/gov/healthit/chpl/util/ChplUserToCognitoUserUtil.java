package gov.healthit.chpl.util;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.healthit.chpl.dao.auth.UserDAO;
import gov.healthit.chpl.domain.auth.User;
import gov.healthit.chpl.exception.UserRetrievalException;
import gov.healthit.chpl.user.cognito.CognitoApiWrapper;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class ChplUserToCognitoUserUtil {

    private UserDAO userDAO;
    private CognitoApiWrapper cognitoApiWrapper;

    @Autowired
    public ChplUserToCognitoUserUtil(UserDAO userDAO, CognitoApiWrapper cognitoApiWrapper) {
        this.cognitoApiWrapper = cognitoApiWrapper;
        this.userDAO = userDAO;
    }

    public User getUser(Long chplUserId, UUID cognitoUserId) {
        User currentUser = null;
        if (chplUserId != null) {
            try {
                currentUser = userDAO.getById(chplUserId, true).toDomain();
            } catch (UserRetrievalException e) {
                LOGGER.error("Could not retreive user with ID: {}", chplUserId, e);
            }
        } else if (cognitoUserId != null) {
            try {
                currentUser = cognitoApiWrapper.getUserInfo(cognitoUserId);
            } catch (UserRetrievalException e) {
                LOGGER.error("Could not retreive user with ID: {}", cognitoUserId, e);
            }
        }
        return currentUser;
    }

}
