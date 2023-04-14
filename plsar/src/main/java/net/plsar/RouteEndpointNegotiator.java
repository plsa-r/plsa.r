package net.plsar;

import net.plsar.annotations.*;
import net.plsar.implement.RouteEndpointBefore;
import net.plsar.model.*;
import net.plsar.schemes.RenderingScheme;
import net.plsar.resources.ComponentsHolder;
import net.plsar.resources.MimeResolver;
import net.plsar.security.SecurityAttributes;
import net.plsar.security.SecurityManager;
import net.plsar.resources.StargzrResources;

import java.io.*;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RouteEndpointNegotiator {

    RouteAttributes routeAttributes;
    ComponentsHolder componentsHolder;
    SecurityAttributes securityAttributes;

    public RouteResult performNetworkRequest(String RENDERER, String resourcesDirectory, FlashMessage flashMessage, ViewCache viewCache, ViewConfig viewConfig, NetworkRequest networkRequest, NetworkResponse networkResponse, SecurityAttributes securityAttributes, SecurityManager securityManager, List<Class<?>> viewRenderers, ConcurrentMap<String, byte[]> viewBytesMap){

        String completePageRendered = "";
        String errorMessage = "";

        try {

            String routeEndpointPath = networkRequest.getRequestPath();
            String routeEndpointAction = networkRequest.getRequestAction().toLowerCase();

            if(routeEndpointPath.equals("/launcher.status")){
                RouteResult routeResult = new RouteResult();
                routeResult.setContentType("text/plain");
                routeResult.setResponseBytes("200 Ok".getBytes());
                routeResult.setResponseCode("200 Ok");
                return routeResult;
            }

            StargzrResources stargzrResources = new StargzrResources();
            UserExperienceResolver userExperienceResolver = new UserExperienceResolver();

            RouteAttributes routeAttributes = networkRequest.getRouteAttributes();
            RouteEndpointHolder routeEndpointHolder = routeAttributes.getRouteEndpointHolder();

            if(routeEndpointPath.startsWith("/" + resourcesDirectory + "/")) {

                MimeResolver mimeResolver = new MimeResolver(routeEndpointPath);

                if (RENDERER.equals(RenderingScheme.CACHE_REQUESTS)) {

                    ByteArrayOutputStream outputStream = stargzrResources.getViewFileCopy(routeEndpointPath, viewBytesMap);
                    if (outputStream == null) {
                        RouteResult routeResult = new RouteResult();
                        routeResult.setContentType("text/plain");
                        routeResult.setResponseBytes("404 Not Found!".getBytes());
                        routeResult.setResponseCode("404");
                        return routeResult;
                    }

                    RouteResult routeResult = new RouteResult();
                    routeResult.setContentType(mimeResolver.resolve());
                    routeResult.setResponseBytes(outputStream.toByteArray());
                    routeResult.setResponseCode("200 Ok");
                    return routeResult;

                }else{

                    String assetsPath = Paths.get("src", "main", viewConfig.getViewsPath()).toString();
                    String filePath = assetsPath.concat(routeEndpointPath);
                    File staticResourcefile = new File(filePath);
                    InputStream fileInputStream = new FileInputStream(staticResourcefile);

                    if (fileInputStream != null && routeEndpointAction.equals("get")) {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        byte[] bytes = new byte[(int) staticResourcefile.length()];
                        int bytesRead;
                        try {
                            while ((bytesRead = fileInputStream.read(bytes, 0, bytes.length)) != -1) {
                                outputStream.write(bytes, 0, bytesRead);
                            }
                            fileInputStream.close();
                            outputStream.flush();
                            outputStream.close();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        RouteResult routeResult = new RouteResult();
                        routeResult.setContentType(mimeResolver.resolve());
                        routeResult.setResponseBytes(outputStream.toByteArray());
                        routeResult.setResponseCode("200 Ok");
                        return routeResult;
                    }
                }

            }

            RouteEndpoint routeEndpoint = null;
            routeEndpointPath = routeEndpointPath.toLowerCase().trim();

            if(routeEndpointPath.equals("")){
                routeEndpointPath = "/";
                String routeKey = routeEndpointAction.toLowerCase() + routeEndpointPath.toLowerCase();
                routeEndpoint = routeEndpointHolder.getRouteEndpoints().get(routeKey);
            }

            if(routeEndpoint == null) {
                if (routeEndpointPath.length() > 1 && routeEndpointPath.endsWith("/")) {
                    int endIndex = routeEndpointPath.indexOf("/", routeEndpointPath.length() - 1);
                    routeEndpointPath = routeEndpointPath.substring(0, endIndex);
                }
                if (routeEndpointHolder.getRouteEndpoints().containsKey(routeEndpointAction + ":" + routeEndpointPath)) {
                    routeEndpoint = routeEndpointHolder.getRouteEndpoints().get(routeEndpointAction + ":" + routeEndpointPath);
                }
            }

            if(routeEndpoint == null) {
                for (Map.Entry<String, RouteEndpoint> routeEndpointEntry : routeEndpointHolder.getRouteEndpoints().entrySet()) {
                    RouteEndpoint activeRouteEndpoint = routeEndpointEntry.getValue();
                    Matcher routeEndpointMatcher = Pattern.compile(activeRouteEndpoint.getRegexRoutePath()).matcher(routeEndpointPath);
                    if (routeEndpointMatcher.matches() &&
                            getRouteVariablesMatch(routeEndpointPath, activeRouteEndpoint) &&
                            activeRouteEndpoint.isRegex()) {
                        routeEndpoint = activeRouteEndpoint;
                    }
                }
            }

            if(routeEndpoint == null){
                RouteResult routeResult = new RouteResult();
                routeResult.setContentType("text/plain");
                routeResult.setResponseBytes("404 Not Found!".getBytes());
                routeResult.setResponseCode("404");
                return routeResult;
            }

            MethodComponents methodComponents = getMethodAttributesComponents(routeEndpointPath, new BeforeResult(), viewCache, flashMessage, networkRequest, networkResponse, securityManager, routeEndpoint);
            Method routeEndpointInstanceMethod = routeEndpoint.getRouteMethod();

            String title = null, keywords = null, description = null;
            if(routeEndpointInstanceMethod.isAnnotationPresent(Meta.class)){
                Meta metaAnnotation = routeEndpointInstanceMethod.getAnnotation(Meta.class);
                title = metaAnnotation.title();
                keywords = metaAnnotation.keywords();
                description = metaAnnotation.description();
            }

            routeEndpointInstanceMethod.setAccessible(true);
            Object routeInstance = routeEndpoint.getKlass().getConstructor().newInstance();

            RouteDependencyResolver routeDependencyResolver = new RouteDependencyResolver();
            routeDependencyResolver.setRouteInstance(routeInstance);
            routeDependencyResolver.setComponentsHolder(componentsHolder);
            routeDependencyResolver.setRouteAttributes(routeAttributes);
            Map<String, Object> routeEndpointInstances = routeDependencyResolver.resolve();

            BeforeRouteResolver beforeRouteResolver = new BeforeRouteResolver();
            beforeRouteResolver.setViewCache(viewCache);
            beforeRouteResolver.setRouteInstance(routeInstance);
            beforeRouteResolver.setNetworkRequest(networkRequest);
            beforeRouteResolver.setNetworkResponse(networkResponse);
            beforeRouteResolver.setSecurityManager(securityManager);
            beforeRouteResolver.setMethodComponents(methodComponents);
            beforeRouteResolver.setRouteEndpointInstanceMethod(routeEndpointInstanceMethod);
            beforeRouteResolver.setRouteEndpointInstances(routeEndpointInstances);
            BeforeResult beforeResult = beforeRouteResolver.resolve();

            if(beforeResult != null &&
                    !beforeResult.getRedirectUri().equals("")){
                RouteResult routeResult = new RouteResult();
                routeResult.setContentType("text/plain");
                routeResult.setResponseBytes("303".getBytes());
                routeResult.setResponseCode("303");
                return routeResult;
            }
            System.out.println(beforeResult);

            ArrayList methodParametersArr = addOrderBeforeResult(beforeResult, methodComponents);
            Object[] methodParametersLst = methodParametersArr.toArray();
            Object routeResponseObject = routeEndpointInstanceMethod.invoke(routeInstance, methodParametersLst);
            String methodResponse = String.valueOf(routeResponseObject);
            if(methodResponse == null){
                RouteResult routeResult = new RouteResult();
                routeResult.setContentType("text/plain");
                routeResult.setResponseBytes("404 Not Found!".getBytes());
                routeResult.setResponseCode("404");
                return routeResult;
            }

            if(methodResponse.startsWith("redirect:")) {
                RedirectInfo redirectInfo = new RedirectInfo();
                redirectInfo.setMethodName(routeEndpointInstanceMethod.getName());
                redirectInfo.setKlassName(routeInstance.getClass().getName());
                String redirectRouteUri = stargzrResources.getRedirect(methodResponse);
                networkRequest.setRedirect(true);
                networkRequest.setRedirectLocation(redirectRouteUri);

                RouteResult routeResult = new RouteResult();
                routeResult.setContentType("text/html");
                routeResult.setResponseBytes("303".getBytes());
                routeResult.setResponseCode("303");
                return routeResult;
            }

            if(routeEndpointInstanceMethod.isAnnotationPresent(JsonOutput.class)){
                RouteResult routeResult = new RouteResult();
                routeResult.setContentType("application/json");
                routeResult.setResponseBytes(methodResponse.getBytes());
                routeResult.setResponseCode("200 Ok");
                return routeResult;
            }

            if(routeEndpointInstanceMethod.isAnnotationPresent(Text.class)){
                RouteResult routeResult = new RouteResult();
                routeResult.setContentType("text/html");
                routeResult.setResponseBytes(methodResponse.getBytes());
                routeResult.setResponseCode("200 Ok");
                return routeResult;
            }

            if(RENDERER.equals("cache-request")) {

                ByteArrayOutputStream unebaos = stargzrResources.getViewFileCopy(methodResponse, viewBytesMap);
                if(unebaos == null){
                    return new RouteResult("404".getBytes(), "404", "text/html");
                }
                completePageRendered = unebaos.toString();

            }else{

                Path webPath = Paths.get("src", "main", viewConfig.getViewsPath());
                if (methodResponse.startsWith("/")) {
                    methodResponse = methodResponse.replaceFirst("/", "");
                }

                String htmlPath = webPath.toFile().getAbsolutePath().concat(File.separator + methodResponse);
                File viewFile = new File(htmlPath);
                ByteArrayOutputStream unebaos = new ByteArrayOutputStream();

                InputStream pageInputStream = new FileInputStream(viewFile);
                byte[] bytes = new byte[(int) viewFile.length()];
                int pageBytesLength;
                while ((pageBytesLength = pageInputStream.read(bytes)) != -1) {
                    unebaos.write(bytes, 0, pageBytesLength);
                    if(pageInputStream.available() == 0)break;
                }
                completePageRendered = unebaos.toString();//todo? ugly
            }


            viewCache.set("message", flashMessage.getMessage());

            String designUri = null;
            if(routeEndpointInstanceMethod.isAnnotationPresent(Design.class)){
                Design annotation = routeEndpointInstanceMethod.getAnnotation(Design.class);
                designUri = annotation.value();
            }
            if(designUri != null) {
                String designContent;
                if(RENDERER.equals("cache-request")) {

                    ByteArrayOutputStream baos = stargzrResources.getViewFileCopy(designUri, viewBytesMap);
                    designContent = baos.toString();

                }else{

                    Path designPath = Paths.get("src", "main", "webapp", designUri);
                    File designFile = new File(designPath.toString());
                    InputStream designInputStream = new FileInputStream(designFile);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    byte[] bytes = new byte[(int) designFile.length()];
                    int length;
                    while ((length = designInputStream.read(bytes)) != -1) {
                        baos.write(bytes, 0, length);
                        if(designInputStream.available() == 0)break;
                    }
                    designContent = baos.toString();

                }

                if(designContent == null){
                    RouteResult routeResult = new RouteResult();
                    routeResult.setContentType("text/plain");
                    routeResult.setResponseBytes("404 Design Not Found!".getBytes());
                    routeResult.setResponseCode("200 Ok");
                    return routeResult;
                }

                if(!designContent.contains("<a:render/>")){
                    RouteResult routeResult = new RouteResult();
                    routeResult.setContentType("text/plain");
                    routeResult.setResponseBytes("Design file is missing <a:render/>".getBytes());
                    routeResult.setResponseCode("200 Ok");
                    return routeResult;
                }

                String[] bits = designContent.split("<a:render/>");
                String header = bits[0];
                String bottom = "";
                if(bits.length > 1) bottom = bits[1];

                header = header + completePageRendered;
                completePageRendered = header + bottom;

                if(title != null) {
                    completePageRendered = completePageRendered.replace("${title}", title);
                }
                if(keywords != null) {
                    completePageRendered = completePageRendered.replace("${keywords}", keywords);
                }
                if(description != null){
                    completePageRendered = completePageRendered.replace("${description}", description);
                }

                completePageRendered = userExperienceResolver.resolve(completePageRendered, viewCache, networkRequest, securityAttributes, viewRenderers);

                RouteResult routeResult = new RouteResult();
                routeResult.setContentType("text/html");
                routeResult.setResponseBytes(completePageRendered.getBytes());
                routeResult.setResponseCode("200 Ok");
                return routeResult;

            }else{
                completePageRendered = userExperienceResolver.resolve(completePageRendered, viewCache, networkRequest, securityAttributes, viewRenderers);

                RouteResult routeResult = new RouteResult();
                routeResult.setContentType("text/html");
                routeResult.setResponseBytes(completePageRendered.getBytes());
                routeResult.setResponseCode("200 Ok");
                return routeResult;
            }

        }catch (IllegalAccessException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (NoSuchFieldException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (NoSuchMethodException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (InstantiationException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (IOException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        } catch (PlsarException ex) {
            errorMessage = "<p style=\"border:solid 1px #ff0000; color:#ff0000;\">" + ex.getMessage() + "</p>";
            ex.printStackTrace();
        }
        String erroredPageRendered = errorMessage + completePageRendered;
        RouteResult routeResult = new RouteResult();
        routeResult.setContentType("text/plain");
        routeResult.setResponseBytes(erroredPageRendered.getBytes());
        routeResult.setResponseCode("500");
        return routeResult;
    }

    ArrayList addOrderBeforeResult(BeforeResult beforeResult, MethodComponents methodComponents){
        ArrayList methodComponentsLst = new ArrayList();
        for(Map.Entry<String, MethodAttribute> entry: methodComponents.getRouteMethodAttributes().entrySet()){
            if(entry.getKey().equals("beforeresult")){
                methodComponentsLst.add(beforeResult);
            }else{
                methodComponentsLst.add(entry.getValue().getAttribute());
            }
        }
        return methodComponentsLst;
    }



    MethodComponents getMethodAttributesComponents(String routeEndpointPath, BeforeResult beforeResult, ViewCache viewCache, FlashMessage flashMessage, NetworkRequest networkRequest, NetworkResponse networkResponse, SecurityManager securityManager, RouteEndpoint routeEndpoint) {
        MethodComponents methodComponents = new MethodComponents();
        Parameter[] endpointMethodAttributes = routeEndpoint.getRouteMethod().getParameters();

        Integer pathVariableIndex = 0;
        String routeEndpointPathClean = routeEndpointPath.replaceFirst("/", "");
        String[] routePathUriAttributes = routeEndpointPathClean.split("/");
        for(Parameter endpointMethodAttribute:  endpointMethodAttributes){
            String methodAttributeKey = endpointMethodAttribute.getName().toLowerCase();
            String description = endpointMethodAttribute.getDeclaringExecutable().getName().toLowerCase();

            RouteAttribute routeAttribute = routeEndpoint.getRouteAttributes().get(methodAttributeKey);
            MethodAttribute methodAttribute = new MethodAttribute();
            methodAttribute.setDescription(description);

            pathVariableIndex = routeAttribute.getRoutePosition() != null ? routeAttribute.getRoutePosition() : 0;
            if(endpointMethodAttribute.getType().getTypeName().equals("net.plsar.security.SecurityManager")){
                methodAttribute.setDescription("securitymanager");
                methodAttribute.setAttribute(securityManager);
                methodComponents.getRouteMethodAttributes().put("securitymanager", methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(securityManager);
            }
            if(endpointMethodAttribute.getType().getTypeName().equals("net.plsar.model.NetworkRequest")){
                methodAttribute.setDescription("networkrequest");
                methodAttribute.setAttribute(networkRequest);
                methodComponents.getRouteMethodAttributes().put("networkrequest", methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(networkRequest);
            }
            if(endpointMethodAttribute.getType().getTypeName().equals("net.plsar.model.NetworkResponse")){
                methodAttribute.setDescription("networkresponse");
                methodAttribute.setAttribute(networkResponse);
                methodComponents.getRouteMethodAttributes().put("networkresponse", methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(networkResponse);
            }
            if(endpointMethodAttribute.getType().getTypeName().equals("net.plsar.model.FlashMessage")){
                methodAttribute.setDescription("flashmessage");
                methodAttribute.setAttribute(flashMessage);
                methodComponents.getRouteMethodAttributes().put("flashmessage", methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(flashMessage);
            }
            if(endpointMethodAttribute.getType().getTypeName().equals("net.plsar.model.BeforeResult")){
                methodAttribute.setDescription("beforeresult");
                methodAttribute.setAttribute(beforeResult);
                methodComponents.getRouteMethodAttributes().put("beforeresult", methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(beforeResult);
            }
            if(endpointMethodAttribute.getType().getTypeName().equals("net.plsar.model.ViewCache")){
                methodAttribute.setDescription("viewcache");
                methodAttribute.setAttribute(viewCache);
                methodComponents.getRouteMethodAttributes().put("viewcache", methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(viewCache);
            }
            if(endpointMethodAttribute.getType().getTypeName().equals("java.lang.Integer")){
                Integer attributeValue = Integer.valueOf(routePathUriAttributes[pathVariableIndex]);
                methodAttribute.setAttribute(attributeValue);
                methodComponents.getRouteMethodAttributes().put(methodAttribute.getDescription().toLowerCase(), methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(attributeValue);
                methodComponents.getRouteMethodAttributeVariablesList().add(attributeValue);
            }
            if(endpointMethodAttribute.getType().getTypeName().equals("java.lang.Long")){
                Long attributeValue = Long.valueOf(routePathUriAttributes[pathVariableIndex]);
                methodAttribute.setAttribute(attributeValue);
                methodComponents.getRouteMethodAttributes().put(methodAttribute.getDescription().toLowerCase(), methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(attributeValue);
                methodComponents.getRouteMethodAttributeVariablesList().add(attributeValue);
            }
            if(endpointMethodAttribute.getType().getTypeName().equals("java.math.BigDecimal")){
                BigDecimal attributeValue = new BigDecimal(routePathUriAttributes[pathVariableIndex]);
                methodAttribute.setAttribute(attributeValue);
                methodComponents.getRouteMethodAttributes().put(methodAttribute.getDescription().toLowerCase(), methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(attributeValue);
                methodComponents.getRouteMethodAttributeVariablesList().add(attributeValue);
            }
            if(endpointMethodAttribute.getType().getTypeName().equals("java.lang.Boolean")){
                Boolean attributeValue = Boolean.valueOf(routePathUriAttributes[pathVariableIndex]);
                methodAttribute.setAttribute(attributeValue);
                methodComponents.getRouteMethodAttributes().put(methodAttribute.getDescription().toLowerCase(), methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(attributeValue);
                methodComponents.getRouteMethodAttributeVariablesList().add(attributeValue);
            }
            if(endpointMethodAttribute.getType().getTypeName().equals("java.lang.String")){
                String attributeValue = String.valueOf(routePathUriAttributes[pathVariableIndex]);
                methodAttribute.setAttribute(attributeValue);
                methodComponents.getRouteMethodAttributes().put(methodAttribute.getDescription().toLowerCase(), methodAttribute);
                methodComponents.getRouteMethodAttributesList().add(attributeValue);
                methodComponents.getRouteMethodAttributeVariablesList().add(attributeValue);
            }
        }
        return methodComponents;
    }

    boolean getRouteVariablesMatch(String routeEndpointPath, RouteEndpoint routeEndpoint) {
        String[] routeUriParts = routeEndpointPath.split("/");
        String[] routeEndpointParts = routeEndpoint.getRoutePath().split("/");
        if(routeUriParts.length != routeEndpointParts.length)return false;
        return true;
    }

    public SecurityAttributes getSecurityAttributes() {
        return securityAttributes;
    }

    public void setSecurityAttributes(SecurityAttributes securityAttributes) {
        this.securityAttributes = securityAttributes;
    }

    public RouteAttributes getRouteAttributes() {
        return routeAttributes;
    }

    public void setRouteAttributes(RouteAttributes routeAttributes) {
        this.routeAttributes = routeAttributes;
    }

    public ComponentsHolder getComponentsHolder() {
        return componentsHolder;
    }

    public void setComponentsHolder(ComponentsHolder componentsHolder) {
        this.componentsHolder = componentsHolder;
    }
}
