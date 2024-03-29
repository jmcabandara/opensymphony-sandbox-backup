package com.opensymphony.able.demo.action;

import com.opensymphony.able.action.DefaultActionBean;
import com.opensymphony.able.demo.action.util.AbleActionBeanContext;
import com.opensymphony.able.demo.model.User;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.validation.LocalizableError;

public abstract class AbstractActionBean extends DefaultActionBean implements ActionBean {
    protected AbleActionBeanContext ctx;

    public void setContext(ActionBeanContext ctx) {
        super.setContext(ctx);
        this.ctx = (AbleActionBeanContext) ctx;
    }

    public ActionBeanContext getContext() {
        return ctx;
    }

    protected void addFieldError(String field, String key, Object... params) {
        getContext().getValidationErrors().add(field, new LocalizableError(key, params));
    }

    protected void addGlobalError(String key) {
        ctx.getValidationErrors().addGlobalError(new LocalizableError(key));
    }

    public boolean isUserLoggedIn() {
        return ctx.isUserLoggedIn();
    }

    public User getCurrentUser() {
        return ctx.getUser();
    }

    public long getUserId() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return -1;
        } else {
            return currentUser.getId();
        }
    }

}
