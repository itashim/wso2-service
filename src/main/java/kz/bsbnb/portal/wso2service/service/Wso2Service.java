package kz.bsbnb.portal.wso2service.service;

import kz.bsbnb.portal.wso2service.dto.UserRegisterDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import org.wso2.carbon.um.ws.service.*;
import org.wso2.carbon.um.ws.service.dao.xsd.ClaimDTO;
import org.wso2.carbon.user.mgt.common.xsd.ClaimValue;

import java.util.List;

@Service
@Slf4j
public class Wso2Service {
    private final Environment environment;
    private final WebServiceTemplate webServiceTemplate;

    public Wso2Service(Environment env, WebServiceTemplate webServiceTemplate) {
        this.environment = env;
        this.webServiceTemplate = webServiceTemplate;
    }

    @SneakyThrows
    public void createUserWso2(UserRegisterDto userDto) {
        long stime =  System.currentTimeMillis();
        try {
            ObjectFactory objectFactory = new ObjectFactory();
            org.wso2.carbon.user.mgt.common.xsd.ObjectFactory objectFactoryClaim = new org.wso2.carbon.user.mgt.common.xsd.ObjectFactory();

            AddUser addUser = objectFactory.createAddUser();
            ClaimValue claimValue;
            if (userDto.getUsername() != null) {
                claimValue = new ClaimValue();
                claimValue.setClaimURI(objectFactoryClaim.createClaimValueClaimURI("http://wso2.org/claims/username"));
                claimValue.setValue(objectFactoryClaim.createClaimValueValue(userDto.getUsername()));
                addUser.getClaims().add(claimValue);
            }
            if (userDto.getEmail() != null) {
                claimValue = new ClaimValue();
                claimValue.setClaimURI(objectFactoryClaim.createClaimValueClaimURI("http://wso2.org/claims/emailaddress"));
                claimValue.setValue(objectFactoryClaim.createClaimValueValue(userDto.getEmail()));
                addUser.getClaims().add(claimValue);
            }
            if (userDto.getFirstName() != null) {
                claimValue = new ClaimValue();
                claimValue.setClaimURI(objectFactoryClaim.createClaimValueClaimURI("http://wso2.org/claims/givenname"));
                claimValue.setValue(objectFactoryClaim.createClaimValueValue(userDto.getFirstName()));
                addUser.getClaims().add(claimValue);
            }
            if (userDto.getMiddleName() != null) {
                claimValue = new ClaimValue();
                claimValue.setClaimURI(objectFactoryClaim.createClaimValueClaimURI("http://wso2.org/claims/middleName"));
                claimValue.setValue(objectFactoryClaim.createClaimValueValue(userDto.getMiddleName()));
                addUser.getClaims().add(claimValue);
            }
            if (userDto.getLastName() != null) {
                claimValue = new ClaimValue();
                claimValue.setClaimURI(objectFactoryClaim.createClaimValueClaimURI("http://wso2.org/claims/lastname"));
                claimValue.setValue(objectFactoryClaim.createClaimValueValue(userDto.getLastName()));
                addUser.getClaims().add(claimValue);
            }
            /*if (userDto.getBirthdayDay() != null && userDto.getBirthdayMonth() != null && userDto.getBirthdayYear() != null) {
                claimValue = new ClaimValue();
                claimValue.setClaimURI(objectFactoryClaim.createClaimValueClaimURI("http://wso2.org/claims/dob"));
                claimValue.setValue(objectFactoryClaim.createClaimValueValue(userDto.getBirthdayYear() + "/" + userDto.getBirthdayMonth() + "/" + userDto.getBirthdayDay()));
                addUser.getClaims().add(claimValue);
            }
            /*if (userDto.getMale() != null) {
                claimValue = new ClaimValue();
                claimValue.setClaimURI(objectFactoryClaim.createClaimValueClaimURI("http://wso2.org/claims/gender"));
                claimValue.setValue(objectFactoryClaim.createClaimValueValue(userDto.getMale() ? "male" : "female"));
                addUser.getClaims().add(claimValue);

                claimValue = new ClaimValue(); // Mr. Ms

            claimValue.setClaimURI(objectFactoryClaim.createClaimValueClaimURI("http://wso2.org/claims/title"));
            claimValue.setValue(objectFactoryClaim.createClaimValueValue(userDto.getMale() ? "Mr." : "Ms"));
            addUser.getClaims().add(claimValue);
            }*/

            addUser.setUserName(objectFactory.createAddUserUserName(userDto.getUsername()));
            addUser.setCredential(objectFactory.createAddUserCredential(userDto.getPassword1()));
            addUser.setRequirePasswordChange(false);
            addUser.getRoleList().add("Application/frsi");
            addUser.getRoleList().add("Internal/everyone");
            //addUser.getRoleList().add("Application/liferay");
            addUser.getRoleList().add("Application/sso.bsbnb.rk");
            webServiceTemplate.marshalSendAndReceive(addUser);
            log.info("WSO2 addUser took {}msec", (System.currentTimeMillis() - stime));
        } catch (Exception exp) {
            log.error("WSO2 addUser userDto="+userDto.toStringForShow(), exp);
            throw exp;
        }
    }

    @SneakyThrows
    public Boolean isExistingUser(String username) {
        long stime = System.currentTimeMillis();
        ObjectFactory objectFactory = new ObjectFactory();
        try {
            IsExistingUser isExistingUser = new IsExistingUser();
            isExistingUser.setUserName(objectFactory.createIsExistingUserUserName(username));
            IsExistingUserResponse response = (IsExistingUserResponse) webServiceTemplate.marshalSendAndReceive(isExistingUser);
            log.info("WSO2 isExistingUser username={} took {}msec", username, (System.currentTimeMillis() - stime));
            return response.isReturn();
        } catch (Exception exp) {
            log.error("WSO2 isExistingUser username="+username, exp);
            throw exp;
        }
    }

    @SneakyThrows
    public Integer getUserId(String username) {
        ObjectFactory objectFactory = new ObjectFactory();
        try {
            GetUserId getUserId = new GetUserId();
            getUserId.setUsername(objectFactory.createGetUserIdUsername(username));
            GetUserIdResponse response = (GetUserIdResponse) webServiceTemplate.marshalSendAndReceive(getUserId);
            return response.getReturn();
        } catch (Exception exp) {
            log.error("WSO2 getUserId username="+username, exp);
            throw exp;
        }
    }

    @SneakyThrows
    public List<ClaimDTO> getUserClaimValues(String username, String profileName) {
        ObjectFactory objectFactory = new ObjectFactory();
        try {
            GetUserClaimValues claimValues = new GetUserClaimValues();
            claimValues.setUserName(objectFactory.createGetUserClaimValuesUserName(username));
            claimValues.setProfileName(objectFactory.createGetUserClaimValuesProfileName(profileName));
            GetUserClaimValuesResponse response = (GetUserClaimValuesResponse) webServiceTemplate.marshalSendAndReceive(claimValues);
            return response.getReturn();
        } catch (Exception exp) {
            log.error("WSO2 GetUserClaimValues username="+username, exp);
            throw exp;
        }
    }

    @SneakyThrows
    public void setUserClaimValues(String username, String profileName, List<ClaimValue> list) {
        ObjectFactory objectFactory = new ObjectFactory();
        try {
            SetUserClaimValues claimValues = new SetUserClaimValues();
            claimValues.setUserName(objectFactory.createGetUserClaimValuesUserName(username));
            claimValues.setProfileName(objectFactory.createGetUserClaimValuesProfileName(profileName));
            claimValues.getClaims().addAll(list);
        } catch (Exception exp) {
            log.error("WSO2 GetUserClaimValues username="+username, exp);
            throw exp;
        }
    }

    @SneakyThrows
    public void deleteUser(String username) {
        long stime = System.currentTimeMillis();
        ObjectFactory objectFactory = new ObjectFactory();
        try {
            DeleteUser deleteUser = objectFactory.createDeleteUser();
            deleteUser.setUserName(objectFactory.createDeleteUserUserName(username));
            webServiceTemplate.marshalSendAndReceive(deleteUser);
            log.info("WSO2 deleteUser username={} took {}msec", username, (System.currentTimeMillis() - stime));
        } catch (Exception exp) {
            String error = exp.getMessage() != null ? exp.getMessage() : exp.getCause().toString();
            if (exp instanceof SoapFaultClientException && error != null && error.contains("30007 - UserNotFound")) {
                log.info("UserNotFound in WSO2, continue creating user in wso2");
            }   else {
                log.error("WSO2 deleteUser username="+username, exp);
                throw exp;
            }
        }
    }

    @SneakyThrows
    public void updateCredentialByAdmin(String username, String credential) {
        ObjectFactory objectFactory = new ObjectFactory();
        try {
            UpdateCredentialByAdmin updateCredential = new UpdateCredentialByAdmin();
            updateCredential.setUserName(objectFactory.createUpdateCredentialByAdminUserName(username));
            updateCredential.setNewCredential(objectFactory.createUpdateCredentialByAdminNewCredential(credential));
            webServiceTemplate.marshalSendAndReceive(updateCredential);
        } catch (Exception exp) {
            log.error("WSO2 updateCredentialByAdmin username="+username, exp);
        }
    }
}
