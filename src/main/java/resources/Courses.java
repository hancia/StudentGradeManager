package resources;

import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import model.Course;
import model.Model;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


@Path("/courses")
public class Courses {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Course> getAllCourses(@QueryParam("id") ObjectId id,
									  @QueryParam("name") String name,
									  @QueryParam("lecturer") String lecturer) {
		UpdateOperations<Course> updateOperations = Model.getDatabase().createUpdateOperations(Course.class)
				.set("links", new ArrayList<>());
		Model.getDatabase().update(Model.getDatabase().createQuery(Course.class), updateOperations);
		Query<Course> query = Model.getDatabase().createQuery(Course.class);
		if(id != null){
			query = query.field("id").equal(id);
		}

		if (name != null)
			query = query.field("name").containsIgnoreCase(name);

		if (lecturer != null)
			query = query.field("lecturer").containsIgnoreCase(lecturer);
		return query.find().toList();
	}

	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public synchronized Response addCourse(Course course) throws URISyntaxException {
		if (course == null) {
			return Response.status(400).build();
		}

		if (course.getName() == null || course.getName().equals("")) {
			return Response.status(400).build();
		}

		if (course.getLecturer() == null || course.getLecturer().equals("")) {
			return Response.status(400).build();
		}

		course.setLinks(new ArrayList<>());
		course.setId((ObjectId) Model.getDatabase().save(course).getId());
		return Response.created(new URI("courses/" + course.getId())).build();
	}
}
