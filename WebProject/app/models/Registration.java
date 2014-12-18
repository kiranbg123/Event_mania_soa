package models;

import play.*;
import play.data.validation.Match;
import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
public class Registration extends Model {
	
	@Required
    @MaxSize(15)
    @MinSize(4)
    @Match(value="^\\w*$", message="Not a valid username")
    public String studentId;
	
	@Required
	public String eventTitle;
	
	public String eventDate;
	
	public String startTime;
	public String endTime;
	
	@Required
	public long eventId;
	
	public String hasRegistered;
	
	public Registration(long eventId, String eventTitle, String eventDate, String startTime, String endTime, String studentId, String hasRegistered)
	{
		this.eventId = eventId;
		this.studentId = studentId;
		this.hasRegistered = hasRegistered;
		this.eventTitle = eventTitle;
		this.eventDate = eventDate;
		this.startTime = startTime;
		this.endTime = endTime;
		
	}
    
}
