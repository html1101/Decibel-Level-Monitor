// Source: https://spring.io/guides/gs/serving-web-content/

// This does the heavy lifting of the volume detecting communicating with the web client.

package com.decibel.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
public class WebControl {
	// Initialize volume control lib
	Meter new_meter = new Meter();
	Thread t1 = new Thread(new_meter.new RunIt());

	// Initialize empty profile
	Profile me = new Profile("Yo", "Some random teacher", "Some random description");

	@GetMapping("/class")
	public String greeting(@RequestParam(name="className", required=true) String name, Model model) {
		model.addAttribute("className", name);
		return "class";
	}

	@GetMapping("/home")
	public ModelAndView goHome() {
		ModelAndView mav = new ModelAndView("home");
		mav.addObject("classList", me.getClasses());
		return mav;
	}

	@GetMapping("/startMonitoring")
	@ResponseBody
	public String start() {
		// Before starting, make sure thread isn't already started
		if(!t1.isAlive()) {
			t1.start();
			return "Started monitoring";
		} else return "Thread already running";
	}


	@GetMapping("/getResp")
	@ResponseBody
	public ArrayList<Float> arr() {
		return new_meter.getRMSList();
	}
}