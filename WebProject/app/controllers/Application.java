package controllers;

import play.*;
import play.data.validation.Valid;
import play.db.jpa.GenericModel.JPAQuery;
import play.libs.OAuth2;
import play.libs.WS;
import play.mvc.*;

import java.util.*;

import com.google.gson.JsonObject;
import compressor.Compress;

import models.*;
import play.Logger;
import controllers.Events;

@With(Compress.class)
public class Application extends Controller {

    public static void index() {
    	//List events = Event.find("order by id Desc").fetch();
        render();
    	
    }
    
    public static void bootstrap() {
    	//List events = Event.find("order by id Desc").fetch();
        render();
    }
    
    public static void createEvent(String title){
    	//Event event = new Event(title).save();
    	//renderJSON(event);
    	
    }
    
    public static void login(String username, String password) {
    	Events event = new Events();
        User user = User.find("byUsernameAndPassword", username, password).first();
    	//User user = User.find("select * from student where username=? and password=?",username,password).first();
        //Logger.info();
        if(user != null) {
            session.put("user", user.username);
           flash.success("Welcome, " + user.name);
            event.index();
                
        }
        else{
        // Oops
        flash.put("username", username);
        flash.error("login Failed");
        Logger.info(user +" login failed");
        index();
        }
    }
    
    public static void register() {
        render();
    }
    
    public static void saveUser(@Valid User user, String verifyPassword) {
        validation.required(verifyPassword);
        validation.equals(verifyPassword, user.password).message("Your password doesn't match");
        if(validation.hasErrors()) {
           render("@register", user, verifyPassword);
        	//flash.error("Password doesn't match");
        	//register();
        }	
        User user_exist = User.find("byUsername", user.username).first();
        if(user_exist != null)
        {
        	flash.error("Username already Exists");
        	register();
        }
        else
        {
        	user.create();
        //No need to put the username in the session as we are going to login again anyway
        //session.put("user", user.username);
        	flash.success("Welcome " + user.name + ", Please login with your Credentials");
        	index();
        }
    }
    
    //The following lines of Code is for Facebook OAuth implimentation
    
    public static OAuth2 FACEBOOK = new OAuth2(
            "https://graph.facebook.com/oauth/authorize",
            "https://graph.facebook.com/oauth/access_token",
            "731421200284377",
            "24bfda2abc2fbdcc143532bba4ee00d2"
    );
    public static void facebookLogin(String access_token) {
       // User u = connected();
        JsonObject me = null;
        Logger.info("access_token is " + access_token);
        //removing if condition as we are no more writing facebook info into database
        //if (u != null && u.access_token != null) {
            me = WS.url("https://graph.facebook.com/me?access_token=%s", WS.encode(access_token)).get().getJson().getAsJsonObject();
          Logger.info("The Json Object is " + me.get("name"));
            //  String name = me.get("name").toString();
          Logger.info("me.get(name) " + me.get("name") );
              setuser(me.get("name").toString());
      //  }
        //Logger.info("The Json Object is " + me.get("name"));
      //  String name = me.get("name").toString();
       // setuser(me.get("name").toString());
        render(me);
    }

    public static void auth() {
        if (OAuth2.isCodeResponse()) {
           // User u = connected();
            OAuth2.Response response = FACEBOOK.retrieveAccessToken(authURL());
           String access_token = response.accessToken;
            Logger.info("Facebook Response: " + response);
          //  u.save();
            facebookLogin(access_token);
        }
        FACEBOOK.retrieveVerificationCode(authURL());
    }

    @Before
    static void setuser(String name) {
        User user = null;
        
        //No ID for facebook login
        /*
        if (session.contains("id")) {
           // Logger.info("existing user: " + session.get("id"));
            user = User.get(Long.parseLong(session.get("id")));
        }
        */
        if (user == null) {
        	//Not writing to database anymore for FB users
            //user = User.createNew(name);
            //session.put("id", user.id);
        }
        renderArgs.put("user", name);
    }

    static String authURL() {
        return play.mvc.Router.getFullUrl("Application.auth");
    }

    static User connected() {
        return (User)renderArgs.get("user");
    }
    
   public static void continueToEvents()
    {
    	Events event = new Events();
    	event.index();
    }
   public static void admin_login()
   {
   	
	   flash.error("");
   	render("/Application/admin_login.html");
   }
   public static void validate_admin(String username, String password)
   {
 	   if(username.equals("Admin") && password.equals("Password")) {
           session.put("user", username);
          // flash.success("Welcome, " + username);
        flash.error("");
           renderTemplate("Crud/index.html");
             
       }
       else{
       // Oops
       flash.put("username", username);
       flash.error("Invalid Admin User name or Password");
       Logger.info(username +"login failed");
       render("/Application/admin_login.html");
       }
   }



}