package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Reference;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import resources.*;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@XmlRootElement
public class Grade {
	@Id
	@Indexed
	private int id;
	private float value;

	public void setDate(Date date) {
		this.date = date;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd", timezone="CET")
	private Date date;
	@Reference
	private Course course;

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	@InjectLinks({
			@InjectLink(resource = StudentGradesId.class, value="/students/${resource.index}/grades/${resource.id}", rel="self"),
			@InjectLink(resource = StudentGrades.class, rel = "parent")
	})
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	List<Link> links;

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Grade(){};

	public Grade(float value, Date date, Course course) throws ParseException {
		this.id = -1;
		this.value = value;
		this.date = date;
		this.course = course;
	}

	public Grade(int id, float value, Date date, Course course) throws ParseException {
		this.id = id;
		this.value = value;
		this.date = date;
		this.course = course;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
