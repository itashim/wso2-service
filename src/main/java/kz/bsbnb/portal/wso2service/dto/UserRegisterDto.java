package kz.bsbnb.portal.wso2service.dto;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import java.io.Serializable;

@Data
public class UserRegisterDto implements Serializable {
    private String username;
    private String password1;
    private String password2;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String iin;
    private String bin;
    private String orgName;
    private String fio;
    private Boolean isEmailSent;
    private String currentPosition;
    private Integer birthdayMonth;
    private Integer birthdayDay;
    private Integer birthdayYear;
    private String phone;
    private KsmrNotResident ksmrNotResident;

    @Data
    @ToString
    public static class KsmrNotResident {
        private String vatCode;
        private String npNrUid;
        private String orgCountry;
        private String natCountry;
    }

    public UserRegisterDto() {
    }

    public String toStringForShow() {
        UserRegisterDto obj = new UserRegisterDto();
        obj.setUsername(this.username);
        obj.setBin(this.bin);
        obj.setBirthdayDay(this.birthdayDay);
        obj.setBirthdayMonth(this.birthdayMonth);
        obj.setBirthdayYear(this.birthdayYear);
        obj.setCurrentPosition(this.currentPosition);
        obj.setEmail(this.email);
        obj.setFio(this.fio);
        obj.setFirstName(this.firstName);
        obj.setLastName(this.lastName);
        obj.setMiddleName(this.middleName);
        obj.setIin(this.iin);
        obj.setOrgName(this.orgName);
        obj.setPhone(this.phone);
        return ReflectionToStringBuilder.toString(obj);
    }

    @Override
    public String toString() {
        return toStringForShow();
    }
}


