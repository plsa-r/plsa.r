package net.plsar.implement;

import net.plsar.model.NetworkRequest;
import net.plsar.security.SecurityAttributes;

public interface ViewRenderer {

    /**
     * net.plsar.example would be sec:auth
     * within the view &lt;sec:auth&gt;Hi!&lt;/sec:auth&gt;
     *
     * @return key:value pair
     */
    public String getKey();

    /**
     * @return true if conditional snipit, false if content is rendered.
     */
    public Boolean isEval();

    public boolean truthy(NetworkRequest networkRequest, SecurityAttributes securityAttributes);

    public String render(NetworkRequest networkRequest, SecurityAttributes securityAttributes);

}
