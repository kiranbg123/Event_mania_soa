package models;

import play.db.jpa.*;
import play.data.validation.*;

import javax.persistence.*;

@Entity
@Table(name="Student")
public class User extends Model {
    
    @Required
    @MaxSize(15)
    @MinSize(4)
    @Match(value="^\\w*$", message="Not a valid username")
    public String username;
    
    @Required
    @MaxSize(15)
    @MinSize(5)
    public String password;
   
    
    
    @Required
    @MaxSize(100)
    public String name;
   
    public User(String name, String password, String username) {
        this.name = name;
        this.password = password;
        this.username = username;
       // this.access_token = null;
    }

    public String toString()  {
        return "User(" + username + ")";
    }
    
    //Following are used for Facebook login
 // public long uid;
  //  public String access_token;
  //  public String name;
    

    /*
    public User(long uid) {
        this.uid = uid;
    } */

    public static User get(long id) {
        return find("id", id).first();
    }
    
     public User(String name)
     {
    	 this.name = name;
    	 this.password = null;
    	 this.username = null;
     }
    public static User createNew(String name) {
        //long uid = (long)Math.floor(Math.random() * 10000);
       User user = new User(name);
        user.create();
        return user;
    } 

    
    //End of Facebook Login
    
    
}