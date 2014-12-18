package controllers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import compressor.Compress;

import models.Event;
import models.Registration;
import play.Logger;
import play.mvc.*;
import play.cache.Cache;;

@With(Compress.class)
public class Events extends Controller {

    public static void index() {
    	//Impliment Memcache through Play
    	//List<Event> events = Cache.get("Events", List.class);
    	//if(events == null)
    	//{
    	//Logger.info("Events not found in Cache");
    	List<Event> events= Event.findAll();
    	
    	
    	//Cache.set("Events", events);
    	//}
    	//else
    	//{
    	//	Logger.info("Events found in Cache");
    	//}
    	//Logger.info("The event list is " + events);
        render(events);
    }
    
    public static void logout(){
    	session.put("user", null);
    	//renderTemplate("/Application/index.html");
    	Application app  = new Application();
    	app.index();
    }
    public static void list(List<Event> events){
    render(events);
    }
    public static void displayevent(String search){
    	List<Event> events = Event.find("lower(title) like ?1 OR lower(type) like ?2", "%"+search+"%", "%"+search+"%").fetch();

    	Logger.info("The category searched for  " + search);
    	//Logger.info("The event list is " + events.toString());
    	
        renderJSON(events);
    }
   /* public static void displayEvent(String category){
    	List<Event> events = Event.find("type", category).fetch();
    	Logger.info("The category is as  " + category);
    	Logger.info("The event list is " + events.toString());
        renderJSON(events);
    }*/
    
    public static void reg_event(long id)
    {
    	Logger.info("Id is " + id);
    	Event event = Event.find("id",id).first();
    	int flagEvent;
    	
    	event =	Event.find("id",id).first();
    	String studentId = session.get("user");
    	int seats_left = event.seatsleft;
    	if(studentId == null)
    	{
    		//Display the user that he/she can not register without Student Id
    		flagEvent = 0;
    		renderJSON(flagEvent);
    	}
    	if(seats_left <= 0)
    	{
    		//flash.error("Sorry, There are no seats available for this Event Right now!!!");
    		flagEvent = 1;
			renderJSON(flagEvent);
    	}
    	else
    		//Register for the event only if Student is not registered.
    	{
    		Registration registration_details = Registration.find("byEventIdAndStudentId", event.id, studentId).first();
    		
    		if(registration_details != null && registration_details.hasRegistered.equals("Registered"))
    		{
    			
    			//flash.error("You have already Registered for this Event");
    			flagEvent = 2;
    			renderJSON(flagEvent);
    		}
    		else
    		{
    			String hasRegistered = "Registered";
    			
    			if (registration_details == null)
    			{
    				Registration registration = new Registration(event.id, event.title, event.eventdate, event.startTime,event.endTime, studentId, hasRegistered );
    				registration.create();
    				
    			}
    			else
    			{
    				registration_details.hasRegistered = hasRegistered;
    				registration_details.save();
    			}
    			event.seatsleft = event.seatsleft -1;
    			event.save();
    			//Update the Cache
    			List<Event> events = Event.findAll();
		    	Cache.set("Events", events);
    			//Update Account history Cache
		    	
		    	List<Registration> registrations = Registration.find("studentId", studentId).fetch();
	        	Cache.set("registrationsById" + studentId, registrations);
	        	Logger.info("Cache has been updated");
    			//flash.success(studentId +",You have Sucessfully Registered for the event with id "+ id);
	        	flagEvent = 3;
    			renderJSON(flagEvent);
    			//index();
    		}
    	}
    }
    
   /* public static void accountHistory_ajax()
    {
    	String studentId = session.get("user");
    	
    	List<Registration> account_history = Cache.get("registrationsById" + studentId, List.class);
    	if(account_history == null)
    	{
    	account_history = Registration.find("studentId", studentId).fetch();
    	Cache.set("registrationsById" + studentId, account_history);
    	Logger.info(studentId);
    	}
    	//flash.success("Hey " + studentId + "! Here are the events which you have Registered/Cancelled recently");
    	renderJSON(account_history);
    }*/
    
    public static void accountHistory()
    {
    	String studentId = session.get("user");
    	
    	Logger.info("session user: " +studentId);
    	
    	List<Registration> account_history = Cache.get("registrationsById" + studentId, List.class);
    	if(account_history == null)
    	{
    	account_history = Registration.find("studentId", studentId).fetch();
    	Cache.set("registrationsById" + studentId, account_history);
    	Logger.info("Its MISS in the Cache");
    	}
    	else
    	{
    		Logger.info("Its HIT in the cache");
    	}
    	//flash.success("Hey " + studentId + "! Here are the events which you have Registered/Cancelled recently");
    	render(account_history);
    }
    
    public static void cancel_Registration(long id)
    {	
    	String studentId = session.get("user");
    	List<Registration> account_history = null;
    	Registration registration_detail = Registration.find("byEventIdAndStudentId", id, studentId ).first();
    	//Logger.info("Registration Details" + registration_detail.eventId + " " + registration_detail.hasRegistered ) ;
    	if(registration_detail != null && !registration_detail.hasRegistered.equals("Cancelled"))
    	{
    		
    		Event event = Event.find("id", id).first();
    		
    		//Update the seats left in Events table
    		event.seatsleft = event.seatsleft + 1;
    		event.save();
    		//Update the Cache
    		List<Event> events = Event.findAll();
	    	Cache.set("Events", events);
	    	Logger.info("Canche is updated");
    		//Update the hasRegistered field to false
    		registration_detail.hasRegistered = "Cancelled";
    		registration_detail.save();
    		
    		//Update Cache 
    		List<Registration> registrations = Registration.find("studentId", studentId).fetch();
        	Cache.set("registrationsById" + studentId, registrations);
    		//flash.success(studentId + " You have Successfully cancelled the registration");
    		//accountHistory();
    		
    		account_history = Cache.get("registrationsById" + studentId, List.class);
        	if(account_history == null)
        	{
        	account_history = Registration.find("studentId", studentId).fetch();
        	Cache.set("registrationsById" + studentId, account_history);
        	
        	Logger.info("Its MISS in the Cache");
        	}
        	else
        	{
        		Logger.info("Its HIT in the Cache");
        	}
        	//flash.success("Hey " + studentId + "! Here are the events which you have Registered/Cancelled recently");
        	renderJSON(account_history);
      
    	}
    	
    	else
    	{
    		//flash.error(studentId + "  You have not registered for this event/Event has been cancelled by the Admin");
    		account_history = null;
    		renderJSON(account_history);
    	}
    	
    	
    }
}
