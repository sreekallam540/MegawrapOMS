
package com.propellum.oms.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.propellum.oms.entities.User;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer
{

	@Autowired
	SessionManager	sessionManager;

	List<String>	unsecuredUrls	= null;
	List<String>	securedUrls		= null;

	public WebMvcConfig()
	{
		unsecuredUrls = new ArrayList<String>();
		unsecuredUrls.add("/login");
		unsecuredUrls.add("/error");

		securedUrls = new ArrayList<String>();
		securedUrls.add("/order");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry)
	{
		registry.addInterceptor(getSecurityInterceptor());
	}

	private HandlerInterceptor getSecurityInterceptor()
	{
		return new HandlerInterceptor()
		{
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
			{
				if(unsecuredUrls.contains(request.getServletPath()))
					return true;
				for(String url : securedUrls)
				{
					if(request.getServletPath().contains(url))
					{
						String token = request.getHeader("X_AUTH_TOKEN");
						if(token != null && token.trim().length() > 0)
						{
							User user = sessionManager.getSession(token, request.getRemoteAddr());
							if(user != null)
							{
								request.setAttribute("user", user);
								return true;
							}
							request.getRequestDispatcher("/error/401").forward(request, response);
							return false;
						}
						request.getRequestDispatcher("/error/403").forward(request, response);
						return false;
					}
				}

				return true;
			}

			@Override
			public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception
			{
				// TODO Auto-generated method stub

			}
		};
	}

	@Override
	public void addFormatters(FormatterRegistry registry)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Validator getValidator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry)
	{
		registry.addResourceHandler("/pdfs/**").addResourceLocations("/WEB-INF/pdfs/");

		registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css/");
		//registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/css123/");
		registry.addResourceHandler("/font/**").addResourceLocations("/WEB-INF/font/");
		//registry.addResourceHandler("/css/**").addResourceLocations("/WEB-INF/tablecss/");
		registry.addResourceHandler("/img/**").addResourceLocations("/WEB-INF/img/");
		registry.addResourceHandler("/style/**").addResourceLocations("/WEB-INF/styles/");
		registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js/");
		registry.addResourceHandler("/js/**").addResourceLocations("/WEB-INF/js123/");
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer)
	{
		// TODO Auto-generated method stub

	}

}
