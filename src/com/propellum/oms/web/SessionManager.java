
package com.propellum.oms.web;

import java.util.UUID;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.stereotype.Component;

import com.propellum.oms.entities.User;

@Component
public final class SessionManager
{

	private static PassiveExpiringMap<String, User> store = null;

	public SessionManager()
	{
		store = new PassiveExpiringMap<String, User>(1000 * 60 * 30);
	}

	public String addSession(User user, String ip)
	{
		String token = null;
		if(user != null)
		{
			token = UUID.randomUUID().toString();
			store.put(token + ip, user);

		}
		return token;
	}

	public User getSession(String token, String ip)
	{
		User user = store.get(token + ip);
		if(user != null)
		{
			store.put(token + ip, user);
		}
		return user;
	}

}
