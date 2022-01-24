
package com.propellum.oms.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.propellum.oms.entities.User;
import com.propellum.oms.services.AuthenticationService;

/**
 * Servlet Filter implementation class AuthenticationFilter
 */
@WebFilter("/AuthenticationFilter")
public class AuthenticationFilter implements Filter
{

	/**
	 * Default constructor.
	 */
	public AuthenticationFilter()
	{
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy()
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		String erpg = "index.jsp";
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		HttpSession session = httpRequest.getSession();

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		if(session.getAttribute(WebConstants.SESSION_USER_KEY) == null)
		{
			if(username != null && password != null)
			{
				User sessionUser = AuthenticationService.getInstance().authorize(request.getParameter("username"), request.getParameter("password"));
				if(sessionUser != null)
				{
					session.setAttribute(WebConstants.SESSION_USER_KEY, sessionUser);
				}
				else
				{
					/*out.write("<html><body><div id='servletResponse' style='text-align: center;'>");
					out.print("<p id='errMsg' style='color: red; font-size: larger;'>Username Or Password Is Invalid. Please Try Again ....!</p>");*/
					request.setAttribute("errMsg", "Username Or Password Is Invalid. Please Try Again...!");
				}

			}

		}

		if(session.getAttribute(WebConstants.SESSION_USER_KEY) == null)
		{
			erpg = "error.jsp";
			if(request.getAttribute("errMsg") == null || request.getAttribute("errMsg").equals(""))
			{
				request.setAttribute("errMsg", "Logged Out Successfully!");
			}

			RequestDispatcher rd = request.getRequestDispatcher(erpg);
			rd.include(request, response);

			return;

		}
		else
		{
			// pass the request along the filter chain
			chain.doFilter(request, response);
		}
		/*out.write("</div></body></html>");
		out.close();*/
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException
	{
		// TODO Auto-generated method stub
	}

}
