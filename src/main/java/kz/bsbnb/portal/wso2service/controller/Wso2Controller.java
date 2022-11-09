package kz.bsbnb.portal.wso2service.controller;

import kz.bsbnb.portal.wso2service.dto.UserRegisterDto;
import kz.bsbnb.portal.wso2service.service.Wso2Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.wso2.carbon.um.ws.service.dao.xsd.ClaimDTO;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/wso2")
public class Wso2Controller {
    private final Wso2Service wso2Service;

    public Wso2Controller(Wso2Service wso2Service) {
        this.wso2Service = wso2Service;
    }

    @PutMapping("/delete-user")
    @ResponseStatus(value = HttpStatus.OK)
    public void deleteUser(@RequestParam("username") String username) {
        wso2Service.deleteUser(username);
    }

    @GetMapping("/is-user-exist")
    public Boolean isExistingUser(@RequestParam("username") String username) {
        return wso2Service.isExistingUser(username);
    }

    @PostMapping("/create-user")
    @ResponseStatus(value = HttpStatus.OK)
    public void createUserWso2(@RequestBody UserRegisterDto userRegisterDto) {
        wso2Service.createUserWso2(userRegisterDto);
    }

    @GetMapping("/get-user")
    @ResponseStatus(value = HttpStatus.OK)
    public Integer getUserId(@RequestParam("username") String username) {
        return wso2Service.getUserId(username);
    }

    @GetMapping("/user-claims")
    public @ResponseBody List<ClaimDTO> getUserClaimValues(@RequestParam("username") String username,
                                                           @RequestParam("profileName") String profileName) {
        return wso2Service.getUserClaimValues(username, profileName);
    }
}
