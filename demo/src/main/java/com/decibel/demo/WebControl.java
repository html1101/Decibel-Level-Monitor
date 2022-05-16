// Source: https://spring.io/guides/gs/serving-web-content/

// This does the heavy lifting of the volume detecting communicating with the web client.

package com.decibel.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import java.util.HashMap;

import java.util.ArrayList;
import java.util.Scanner;

import javax.persistence.Entity;

@Controller
public class WebControl {
	// Initialize volume control lib
	Meter new_meter = new Meter();
	Thread t1 = new Thread(new_meter.new RunIt());
	Profile me;

	public WebControl() {
		// Starting monitoring...
		t1.start();
		
		// Initialize profile
		Scanner init = new Scanner(System.in);
		System.out.print("Enter user's name: ");
		String name = init.nextLine();
		System.out.print("Enter a description about the user: ");
		String desc = init.nextLine();

		me = new Profile(name, "./person.jpg", desc);
		init.close();
	}

	@GetMapping("/class")
	public String greeting(@RequestParam(name="className", required=true) String name, Model model) {
		model.addAttribute("className", name);

		// Get this class, then put some info about it in our addObject
		Profile.classStruct findClass = me.getThisClass(name);
		if(findClass != null) {
			// We found this class, send some info about it
			System.out.println("Found Class!");
			model.addAttribute("RMSInfo", new_meter.getRMSList(findClass.getStart()));
			model.addAttribute("startTime", findClass.getStart());
		}
		return "class";
	}

	@GetMapping("/")
	public ModelAndView goHome() {
		ModelAndView mav = new ModelAndView("home");
		mav.addObject("classList", me.getClasses());
		mav.addObject("userName", me.getName());
		mav.addObject("userProfilePath", me.getProfilePath());
		mav.addObject("description", me.getDesc());
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
	public HashMap<String,ArrayList<Float>> arr(@RequestParam(name="time", required=true) String time) {
		return new_meter.getRMSList(time);
	}

	@PostMapping("/addClass")
	public ResponseEntity<HttpStatus> addClass(@RequestBody Profile.classStruct classN) {
		// Add this class

		System.out.println("Adding class...");
		System.out.println(classN);
		me.addClass(classN.getClassName(), classN.getStart(), classN.getEnd());

		return ResponseEntity.ok(HttpStatus.OK);
	}

	@Entity
	public static class classInf {
		private String className;
		public String getClassName() {
			return className;
		}
	}
	@PostMapping("getClassInfo")
	public String information(@RequestBody classInf c) {
		// Get class + return information
		Profile.classStruct findClass = me.getThisClass(c.getClassName());
		if(findClass != null) {
			return "CLASS=" + c.getClassName() + "\n" + "STARTS=" + findClass.getStart() + "\nENDS=" + findClass.getEnd();
		} else return "";
	}
}