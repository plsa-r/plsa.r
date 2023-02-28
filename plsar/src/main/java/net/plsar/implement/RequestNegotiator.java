package net.plsar.implement;

import net.plsar.model.NetworkRequest;
import net.plsar.model.NetworkResponse;

public interface RequestNegotiator {
    void intercept(NetworkRequest request, NetworkResponse response);
}
