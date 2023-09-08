package com.ipsator.MagicLinkAuthentication_System.ServiceImplementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImplementation {
	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private TemplateEngine templateEngine;

	public void sendEmailWithUrl(String to, String subject, String url) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		// Create a Thymeleaf context and set variables
		Context context = new Context();
		context.setVariable("url", url);

		// Process the HTML email template
		String htmlContent = templateEngine.process("email-template", context);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);

		// Send the email
		emailSender.send(message);
	}
}
