package net.plsar;

import net.plsar.schemes.RenderingScheme;

public class ViewConfig {

    public ViewConfig(){
        this.viewsPath = "webapp";
        this.resourcesPath = "resources";
        this.viewExtension = ".jsp";
        this.renderingScheme = RenderingScheme.RELOAD_EACH_REQUEST;
    }

    String viewsPath;
    String resourcesPath;
    String viewExtension;
    String renderingScheme;

    public String getViewsPath() {
        return viewsPath;
    }

    public void setViewsPath(String viewsPath) {
        this.viewsPath = viewsPath;
    }

    public String getResourcesPath() {
        return resourcesPath;
    }

    public void setResourcesPath(String resourcesPath) {
        this.resourcesPath = resourcesPath;
    }

    public String getViewExtension() {
        return viewExtension;
    }

    public void setViewExtension(String viewExtension) {
        this.viewExtension = viewExtension;
    }

    public String getRenderingScheme() {
        return renderingScheme;
    }

    public void setRenderingScheme(String renderingScheme) {
        this.renderingScheme = renderingScheme;
    }
}
