package foo.bar;

import net.plsar.implement.RouteEndpointBefore;
import net.plsar.model.*;
import net.plsar.security.SecurityManager;

public class ActivityBefore implements RouteEndpointBefore {
    @Override
    public BeforeResult before(FlashMessage flashMessage, ViewCache viewCache, NetworkRequest req, NetworkResponse resp, SecurityManager securityManager, BeforeAttributes beforeAttributes) {
        //performed before ActivityRouter.index() gets called.
        //all path variables are passed in BeforeAttributes
        //to retrieve call the variable by its path name which should
        //be the same as the variables name itself
        System.out.println("before activityrouter.index");
        BeforeResult beforeResult = new BeforeResult();
        beforeResult.set("key", "017.3");
        return beforeResult;
    }
}
