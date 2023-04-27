package net.plsar;

import net.plsar.annotations.NetworkRouter;
import net.plsar.annotations.Text;
import net.plsar.annotations.network.Get;

@NetworkRouter
public class TestRouter {

    @Text
    @Get("/")
    public String index(){ return "index."; }

}
