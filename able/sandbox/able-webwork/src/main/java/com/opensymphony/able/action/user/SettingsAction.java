package com.opensymphony.able.action.user;

import com.opensymphony.able.action.AbleActionSupport;
import com.opensymphony.able.webwork.Redirect;
import com.opensymphony.able.webwork.Result;

@Result(type = Redirect.class, location = "../home")
public class SettingsAction extends AbleActionSupport {
    public String execute() throws Exception {
        userService.save(getCurrentUser());

        return SUCCESS;
    }
}
