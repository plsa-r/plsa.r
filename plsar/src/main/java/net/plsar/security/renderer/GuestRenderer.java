package net.plsar.security.renderer;

import net.plsar.implement.ViewRenderer;
import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityAttributes;
import net.plsar.security.SecurityManager;
import net.plsar.security.SecurityManagerHelper;

public class GuestRenderer implements ViewRenderer {

    public boolean truthy(NetworkRequest networkRequest, SecurityAttributes securityAttributes){
        SecurityManagerHelper securityManagerHelper = new SecurityManagerHelper();
        SecurityManager security = securityManagerHelper.getSecurityManager(networkRequest, securityAttributes);
        return !security.isAuthenticated(networkRequest);
    }

    public String render(NetworkRequest networkRequest, SecurityAttributes securityAttributes){
        return "";
    }

    public String getKey() { return "a:guest"; }

    public Boolean isEval() {
        return true;
    }
}
