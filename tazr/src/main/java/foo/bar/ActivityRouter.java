package foo.bar;

import foo.bar.model.Activity;
import foo.bar.repo.ActivityRepo;
import net.plsar.RouteAttributes;
import net.plsar.annotations.*;
import net.plsar.annotations.network.Get;
import net.plsar.annotations.network.Post;
import net.plsar.model.BeforeResult;
import net.plsar.model.FlashMessage;
import net.plsar.model.NetworkRequest;
import net.plsar.model.ViewCache;

import java.util.List;
import java.util.Map;

@NetworkRouter
public class ActivityRouter {

    @Bind
    ActivityRepo activityRepo;

    @Before({ActivityBefore.class})
    @Design("/pages/default.ux")
    @Get("/")
    public String index(NetworkRequest req, ViewCache cache, FlashMessage message, BeforeResult beforeResult){
        System.out.println("index");
        List<Activity> activities = activityRepo.all();
        cache.set("activities", activities);

        //a property from my tazr.properties file
        RouteAttributes routeAttributes = req.getRouteAttributes();
        String property = routeAttributes.get("property");
        System.out.println(property);
        System.out.println("value passed from before: " + beforeResult.get("key"));

        return "pages/index.ux";
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
        int id = activityRepo.save(activity);
        System.out.println("id: " + id);
        message.set("success.");
        return "redirect:/";
    }

}
