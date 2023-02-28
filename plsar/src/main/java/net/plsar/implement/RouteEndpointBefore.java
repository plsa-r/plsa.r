package net.plsar.implement;

import net.plsar.model.*;
import net.plsar.security.SecurityManager;

public interface RouteEndpointBefore {
    BeforeResult before(FlashMessage flashMessage, ViewCache viewCache, NetworkRequest req, NetworkResponse resp, SecurityManager securityManager, BeforeAttributes beforeAttributes);
}
