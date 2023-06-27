package com.contact.main.helper;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

@Component
public class sessionHelper {
	
	public void removeMessage() {
		try {
			System.out.println("message removed from session");
			HttpSession session = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest().getSession();
			session.removeAttribute("message");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
