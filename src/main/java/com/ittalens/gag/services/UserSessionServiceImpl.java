package com.ittalens.gag.services;

import com.ittalens.gag.model.exceptions.UnauthorizedException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
@AllArgsConstructor
public class UserSessionServiceImpl implements UserSessionService {

    @Autowired
    private final HttpSession httpSession;
    public static final String USER_ID = "USER_ID";
    public static final String LOGGED = "LOGGED";

    @Override
    public void isLogged() {
        if (httpSession.getAttribute(LOGGED) != null) {
            return;
        }
        throw new UnauthorizedException("Must to be logged");
    }

    @Override
    public Long currentUserId(){
        Long userId = Long.parseLong(httpSession.getAttribute(USER_ID).toString());
        return userId;
    }

}
