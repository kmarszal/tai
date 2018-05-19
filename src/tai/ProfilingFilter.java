package tai;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.util.logging.Logger;

public class ProfilingFilter implements Filter{
    private static final Logger LOGGER = Logger.getLogger(ProfilingFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            LOGGER.info("Processing request to " + ((HttpServletRequest) servletRequest).getRequestURI());
        }
    }

    @Override
    public void destroy() {

    }
}