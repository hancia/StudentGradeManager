package model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import org.bson.types.ObjectId;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLinks;
import resources.Courses;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

@Entity("courses")
@XmlRootElement
public class Course {
	@XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	@Id
	private ObjectId id;
	private String name;
	private String lecturer;

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	@InjectLinks({
			@InjectLink(value = "/courses/${instance.id}", rel = "self"),
			@InjectLink(resource = Courses.class, rel = "parent")})
	@XmlElement(name = "link")
	@XmlElementWrapper(name = "links")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	List<Link> links;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLecturer() {
		return lecturer;
	}

	public void setLecturer(String lecturer) {
		this.lecturer = lecturer;
	}

	public Course(){};

	public Course(String name, String lecturer){
		this.name = name;
		this.lecturer = lecturer;
	}
}
