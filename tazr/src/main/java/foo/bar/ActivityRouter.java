package foo.bar;

import foo.bar.model.Activity;
import foo.bar.repo.ActivityRepo;
import net.plsar.RouteAttributes;
import net.plsar.annotations.Bind;
import net.plsar.annotations.Component;
import net.plsar.annotations.Design;
import net.plsar.annotations.NetworkRouter;
import net.plsar.annotations.network.Get;
import net.plsar.annotations.network.Post;
import net.plsar.model.FlashMessage;
import net.plsar.model.NetworkRequest;
import net.plsar.model.ViewCache;

import java.util.List;
import java.util.Map;

@NetworkRouter
public class ActivityRouter {

    @Bind
    ActivityRepo activityRepo;

    @Design("/pages/default.svi")
    @Get("/")
    public String index(NetworkRequest req, ViewCache cache, FlashMessage message){
        List<Activity> activities = activityRepo.all();
        cache.set("activities", activities);

        //a property from my tazr.properties file
        RouteAttributes routeAttributes = req.getRouteAttributes();
        String property = routeAttributes.get("property");
        message.set(property);

        return "pages/index.svi";
    }

    @Post("/save")
    public String save(NetworkRequest req, FlashMessage message){
        Activity activity = req.get(Activity.class);
        if(activity.getDescription().equals("")){
            message.set("description required.");
            return "redirect:/";
        }
        /*
            or
            Activity activity = new Activity();
            activity.setDescription(req.getValue("description"));
        */
        activityRepo.save(activity);
        message.set("success.");
        return "redirect:/";
    }

}
