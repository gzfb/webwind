package org.expressme.webwind;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.expressme.webwind.container.ContainerFactory;
import org.expressme.webwind.converter.ConverterFactory;
import org.expressme.webwind.renderer.JavaScriptRenderer;
import org.expressme.webwind.renderer.Renderer;
import org.expressme.webwind.renderer.TextRenderer;
import org.expressme.webwind.template.JspTemplateFactory;
import org.expressme.webwind.template.TemplateFactory;

/**
 * Dispatcher handles ALL requests from clients, and dispatches to appropriate 
 * handler to handle each request.
 * 
 * @author Michael Liao (askxuefeng@gmail.com)
 */
class Dispatcher {

    private final Log log = LogFactory.getLog(getClass());

    private ServletContext servletContext;
    private ContainerFactory containerFactory;
    private boolean multipartSupport = false;
    private long maxFileSize = 10L * 1024L * 1024L; // default to 10M.
    private UrlMatcher[] urlMatchers = null;
    private Map<UrlMatcher, Action> urlMap = new HashMap<UrlMatcher, Action>();
    private ConverterFactory converterFactory = new ConverterFactory();
    private Interceptor[] interceptors = null;
    private ExceptionHandler exceptionHandler = null;

    public void init(Config config) throws ServletException {
        log.info("Init Dispatcher...");
        this.servletContext = config.getServletContext();
        try {
            initAll(config);
        }
        catch (ServletException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServletException("Dispatcher init failed.", e);
        }
    }

    void initAll(Config config) throws Exception {
        // detect multipart support:
        try {
            Class.forName("org.apache.commons.fileupload.servlet.ServletFileUpload");
            this.multipartSupport = true;
            log.info("Using CommonsFileUpload to handle multipart http request.");
            String maxFileSize = config.getInitParameter("maxFileSize");
            if (maxFileSize!=null) {
                try {
                    long n = Long.parseLong(maxFileSize);
                    if (n<=0)
                        throw new NumberFormatException();
                    this.maxFileSize = n;
                }
                catch (NumberFormatException e) {
                    log.warn("Invalid parameter <maxFileSize> value '" + maxFileSize + "', using default.");
                }
            }
        }
        catch (ClassNotFoundException e) {
            log.info("CommonsFileUpload not found. Multipart http request can not be handled.");
        }

        // init IoC container:
        String containerName = config.getInitParameter("container");
        if (containerName==null)
            throw new ConfigException("Missing init parameter <container>.");
        this.containerFactory = Utils.createContainerFactory(containerName);
        this.containerFactory.init(config);
        List<Object> beans = this.containerFactory.findAllBeans();
        initComponents(beans);

        // init template engine:
        initTemplateFactory(config);
    }

    void initTemplateFactory(Config config) {
        String name = config.getInitParameter("template");
        if (name==null) {
            name = JspTemplateFactory.class.getName();
            log.info("No template factory specified. Default to '" + name + "'.");
        }
        TemplateFactory tf = Utils.createTemplateFactory(name);
        tf.init(config);
        log.info("Template factory '" + tf.getClass().getName() + "' init ok.");
        TemplateFactory.setTemplateFactory(tf);
    }

    void initComponents(List<Object> beans) {
        List<Interceptor> intList = new ArrayList<Interceptor>();
        for (Object bean : beans) {
            if (bean instanceof Interceptor)
                intList.add((Interceptor)bean);
            if (this.exceptionHandler==null && bean instanceof ExceptionHandler)
                this.exceptionHandler = (ExceptionHandler) bean;
            addActions(bean);
        }
        if (this.exceptionHandler==null)
            this.exceptionHandler = new DefaultExceptionHandler();
        this.interceptors = intList.toArray(new Interceptor[intList.size()]);
        // sort interceptors by its annotation of 'InterceptorOrder':
        Arrays.sort(
                this.interceptors,
                new Comparator<Interceptor>() {
                    public int compare(Interceptor i1, Interceptor i2) {
                        InterceptorOrder o1 = i1.getClass().getAnnotation(InterceptorOrder.class);
                        InterceptorOrder o2 = i2.getClass().getAnnotation(InterceptorOrder.class);
                        int n1 = o1==null ? Integer.MAX_VALUE : o1.value();
                        int n2 = o2==null ? Integer.MAX_VALUE : o2.value();
                        if (n1==n2)
                            return i1.getClass().getName().compareTo(i2.getClass().getName());
                        return n1<n2 ? (-1) : 1;
                    }
                }
        );
        this.urlMatchers = urlMap.keySet().toArray(new UrlMatcher[urlMap.size()]);
        // sort url matchers by its url:
        Arrays.sort(
                this.urlMatchers,
                new Comparator<UrlMatcher>() {
                    public int compare(UrlMatcher o1, UrlMatcher o2) {
                        String u1 = o1.url;
                        String u2 = o2.url;
                        int n = u1.compareTo(u2);
                        if (n==0)
                            throw new ConfigException("Cannot mapping one url '" + u1 + "' to more than one action method.");
                        return n;
                    }
                }
        );
    }

    // find all action methods and add them into urlMap:
    void addActions(Object bean) {
        Class<?> clazz = bean.getClass();
        Method[] ms = clazz.getMethods();
        for (Method m : ms) {
            if (isActionMethod(m)) {
                Mapping mapping = m.getAnnotation(Mapping.class);
                String url = mapping.value();
                UrlMatcher matcher = new UrlMatcher(url);
                if (matcher.getArgumentCount()!=m.getParameterTypes().length) {
                    warnInvalidActionMethod(m, "Arguments in URL '" + url + "' does not match the arguments of method.");
                    continue;
                }
                log.info("Mapping url '" + url + "' to method '" + m.toGenericString() + "'.");
                urlMap.put(matcher, new Action(bean, m));
            }
        }
    }

    // check if the specified method is a vaild action method:
    boolean isActionMethod(Method m) {
        Mapping mapping = m.getAnnotation(Mapping.class);
        if (mapping==null)
            return false;
        if (mapping.value().length()==0) {
            warnInvalidActionMethod(m, "Url mapping cannot be empty.");
            return false;
        }
        if (Modifier.isStatic(m.getModifiers())) {
            warnInvalidActionMethod(m, "method is static.");
            return false;
        }
        Class<?>[] argTypes = m.getParameterTypes();
        for (Class<?> argType : argTypes) {
            if (!converterFactory.canConvert(argType)) {
                warnInvalidActionMethod(m, "unsupported parameter '" + argType.getName() + "'.");
                return false;
            }
        }
        Class<?> retType = m.getReturnType();
        if (retType.equals(void.class)
                || retType.equals(String.class)
                || Renderer.class.isAssignableFrom(retType)
        )
            return true;
        warnInvalidActionMethod(m, "unsupported return type '" + retType.getName() + "'.");
        return false;
    }

    // log warning message of invalid action method:
    void warnInvalidActionMethod(Method m, String string) {
        log.warn("Invalid Action method '" + m.toGenericString() + "': " + string);
    }

    public boolean service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String url = req.getRequestURI();
        String path = req.getContextPath();
        if (path.length()>0)
            url = url.substring(path.length());
        // set default character encoding to "utf-8" if encoding is not set:
        if (req.getCharacterEncoding()==null)
            req.setCharacterEncoding("UTF-8");
        if (log.isDebugEnabled())
            log.debug("Handle for URL: " + url);
        Execution execution = null;
        for (UrlMatcher matcher : this.urlMatchers) {
            String[] args = matcher.getMatchedParameters(url);
            if (args!=null) {
                Action action = urlMap.get(matcher);
                Object[] arguments = new Object[args.length];
                for (int i=0; i<args.length; i++) {
                    Class<?> type = action.arguments[i];
                    if (type.equals(String.class))
                        arguments[i] = args[i];
                    else
                        arguments[i] = converterFactory.convert(type, args[i]);
                }
                execution = new Execution(req, resp, action, arguments);
                break;
            }
        }
        if (execution!=null) {
            handleExecution(execution, req, resp);
        }
        return execution!=null;
    }

    void handleExecution(Execution execution, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (this.multipartSupport) {
            if (MultipartHttpServletRequest.isMultipartRequest(request)) {
                request = new MultipartHttpServletRequest(request, maxFileSize);
            }
        }
        ActionContext.setActionContext(servletContext, request, response);
        try {
            InterceptorChainImpl chains = new InterceptorChainImpl(interceptors);
            chains.doInterceptor(execution);
            handleResult(request, response, chains.getResult());
        }
        catch (Exception e) {
            handleException(request, response, e);
        }
        finally {
            ActionContext.removeActionContext();
        }
    }

    void handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) throws ServletException, IOException {
        try {
            exceptionHandler.handle(request, response, ex);
        }
        catch (ServletException e) {
            throw e;
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServletException(e);
        }
    }

    void handleResult(HttpServletRequest request, HttpServletResponse response, Object result) throws Exception {
        if (result==null)
            return;
        if (result instanceof Renderer) {
            Renderer r = (Renderer) result;
            r.render(this.servletContext, request, response);
            return;
        }
        if (result instanceof String) {
            String s = (String) result;
            if (s.startsWith("redirect:")) {
                response.sendRedirect(s.substring("redirect:".length()));
                return;
            }
            if (s.startsWith("script:")) {
                String script = s.substring("script:".length());
                new JavaScriptRenderer(script).render(servletContext, request, response);
                return;
            }
            new TextRenderer(s).render(servletContext, request, response);
            return;
        }
        throw new ServletException("Cannot handle result with type '" + result.getClass().getName() + "'.");
    }

    public void destroy() {
        log.info("Destroy Dispatcher...");
        this.containerFactory.destroy();
    }

}
