package net.plsar.security.negotiator;

import net.plsar.implement.RequestNegotiator;
import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;

//////// Thank you Apache Shiro! ////////
public class AuthNegotiator implements RequestNegotiator {
    @Override
    public void intercept(NetworkRequest request, NetworkResponse response) {
        ThreadLocal<NetworkRequest> requestStorage = new InheritableThreadLocal<>();
        requestStorage.set(request);

        ThreadLocal<NetworkResponse> responseStorage = new InheritableThreadLocal<>();
        responseStorage.set(response);
    }
}
