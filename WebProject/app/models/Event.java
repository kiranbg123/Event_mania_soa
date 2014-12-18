package models;

import play.*;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class Event extends Model {
	public String title;
	//public long id;
	@Required
	public String type;
	
	@Required
	public int maxseats;
	public int seatsleft;
	
	@Required
	public String eventdate;
	
	public String startTime;
	public String endTime;
	
	public Event(String title, String type)
	{
		this.title = title;
		this.type = type;
		//this.id = id;
	}
	
    
}
