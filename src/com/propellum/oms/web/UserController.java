/**
 * 
 */

package com.propellum.oms.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.propellum.oms.entities.User;
import com.propellum.oms.entities.UserType;
import com.propellum.oms.services.AuthenticationService;

/**
 * @author viral.sejpal
 *
 */
@EnableWebMvc
@Controller
public class UserController
{
	SessionManager sessionManager = new SessionManager();

	@RequestMapping("logout.htm")
	public String logout(HttpServletRequest request, ModelMap map)
	{
		request.getSession().invalidate();
		map.addAttribute(WebConstants.NOTIFICATION_KEY, "Logout successful.!");

		return "redirect:listOrders.htm";
	}

	@RequestMapping("changePassword.htm")
	public String changePassword(HttpServletRequest request, ModelMap map)
	{
		String loginid = request.getParameter("loginId");
		String password = request.getParameter("pass");
		String cpassword = request.getParameter("cpass");

		if(password.trim().length() < 6)
		{
			map.addAttribute(WebConstants.NOTIFICATION_KEY, "Passwords' minimum length should be 6. ===ERROR");
		}
		else
		{

			if(password.equals(cpassword))
			{
				boolean response = AuthenticationService.getInstance().updatePassword(loginid, password);
				if(response)
					map.addAttribute(WebConstants.NOTIFICATION_KEY, "Password changed successfully. ===SUCCESS");
				else
					map.addAttribute(WebConstants.NOTIFICATION_KEY, "Some error occurred. ===ERROR");
			}
			else
			{
				map.addAttribute(WebConstants.NOTIFICATION_KEY, "Passwords do not match. ===ERROR");
			}
		}

		User user = AuthenticationService.getInstance().getUserByLoginId(loginid);

		map.addAttribute("user", user);
		return "userProfile";
	}

	@RequestMapping("viewAllUsers.htm")
	public String viewAllUsers(HttpServletRequest request, ModelMap map)
	{
		User sessionUser = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		if(sessionUser.getUserType().equals(UserType.ADMIN))
		{
			List<User> userList = AuthenticationService.getInstance().getAllUsers();
			map.addAttribute("userList", userList);
			return "userList";
		}
		else
		{
			return "";
		}
	}

	@RequestMapping("viewUser.htm")
	public String viewUser(HttpServletRequest request, ModelMap map)
	{
		int rid = Integer.parseInt(request.getParameter("rid"));
		User user = AuthenticationService.getInstance().getUserByRegistrationId(rid);
		map.addAttribute("user", user);

		return "userProfile";

	}

	@RequestMapping("viewProfile.htm")
	public String viewProfile(HttpServletRequest request, ModelMap map)
	{
		User sessionUser = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);
		map.addAttribute("user", sessionUser);
		return "userProfile";

	}

	@RequestMapping("createUserForm.htm")
	public String createUserForm(HttpServletRequest request, ModelMap map)
	{
		map.addAttribute("user", new User());
		return "userProfile";

	}

	@RequestMapping("storeUser.htm")
	public String storeUser(HttpServletRequest request, @ModelAttribute("user") User user, ModelMap map)
	{
		//		AuthenticationService.getInstance().createUser(userToBeCreated, password);

		User sessionUser = (User) request.getSession().getAttribute(WebConstants.SESSION_USER_KEY);

		if(user.getRegistrationId() > 0)
		{
			boolean response = AuthenticationService.getInstance().updateUser(user);
			if(response)
				map.addAttribute(WebConstants.NOTIFICATION_KEY, "Details updated Successfully.! ===SUCCESS");
			else
				map.addAttribute(WebConstants.NOTIFICATION_KEY, "Some error occurred.! ===ERROR");
		}
		else if(sessionUser.getUserType().equals(UserType.ADMIN))
		{
			int response = AuthenticationService.getInstance().createUser(user, "oms@123");
			if(response > 0)
				map.addAttribute(WebConstants.NOTIFICATION_KEY, "User Created Successfully.! ===SUCCESS");
			else
				map.addAttribute(WebConstants.NOTIFICATION_KEY, "Some error occurred.! ===ERROR");
		}
		return "userProfile";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/login", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> login(HttpServletRequest request, @RequestParam String username, @RequestParam String password, HttpSession session)
	{
		Map<String, Object> result = new HashMap<String, Object>();
		try
		{
			AuthenticationService userService = AuthenticationService.getInstance();
			User user = userService.authorize(username, password);
			String token;
			if(user != null)
			{
				token = sessionManager.addSession(user, request.getRemoteAddr());
				result.put("Status", "Success");
				result.put("token", token);
			}
			else
			{
				result.put("Status", "401");
				result.put("message", "Token Cannot be generated check the credentials");
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;

	}

}
