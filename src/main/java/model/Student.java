package model;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.morphia.annotations.*;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import resources.Students;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity("students")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Student {
	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	@Id
	@XmlTransient
	private ObjectId _id;
	@Indexed(options = @IndexOptions(unique = true))
	private Long index;
	private String firstName;
	private String lastName;
	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd", timezone="CET")
	private Date birthday;

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Link> getLinks() {
		return links;
	}

	@InjectLinks({
			@InjectLink(value = "/students/${instance.index}", rel = "self"),
			@InjectLink(resource = Students.class, rel = "parent")})
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	List<Link> links;

	@Embedded
	@XmlTransient
	private List<Grade> grades = new ArrayList<>();

	public Student() {
	}

	public Student(Long index, String firstName, String lastName, Date birthday, List<Grade> grades) {
		this.index = index;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthday = birthday;
		this.grades = grades;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public List<Grade> getGrades() {
		return grades;
	}

	public void setGrades(List<Grade> grades) {
		this.grades = grades;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}
}
