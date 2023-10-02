//package com.ipsator.MagicLinkAuthentication_System.Controller;
//
//import org.springframework.cloud.sleuth.Tracer;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
////@RestController
//public class HelloController {
//	private final Tracer tracer;
//
//	public HelloController(Tracer tracer) {
//		this.tracer = tracer;
//	}
//
//	@GetMapping("/hello")
//	public String hello() {
//		String traceId = tracer.currentSpan().context().traceId();
//		log.info("Request trace ID: {}", traceId);
//		return "Hello!";
//	}
//}
