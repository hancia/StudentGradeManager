package resources;

import dev.morphia.query.UpdateOperations;
import model.Course;
import model.Model;
import model.Student;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/courses/{id}")
public class CoursesId {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getCourseById(@PathParam("id") ObjectId id) {
		List<Course> cr = Model.getDatabase().createQuery(Course.class).field("_id").equal(id).asList();
		if (cr.size() == 0) {
			return Response.status(404).build();
		} else {
			Response.ResponseBuilder response = Response.status(200).entity(cr.get(0));
			return response.build();
		}
	}

	@DELETE
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public synchronized Response deleteCourse(@PathParam("id") ObjectId id) {
		List<Course> cr = Model.getDatabase().createQuery(Course.class).field("id").equal(id).asList();
		if (cr.size() == 0) {
			return Response.status(404).build();
		} else {
				for(var st: Model.getDatabase().createQuery(Student.class)){
					if(st.getGrades().removeIf(s -> s.getCourse().getId().equals(id))) {
						UpdateOperations<Student> updateOperations = Model.getDatabase().createUpdateOperations(Student.class)
								.set("grades", st.getGrades());
						Model.getDatabase().update(Model.getDatabase().createQuery(Student.class).field("index").equal(st.getIndex()), updateOperations);
					}
				}
				Model.getDatabase().delete(cr.get(0));
				Response.ResponseBuilder response = Response.status(204);
				return response.build();
			}
		}

	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public synchronized Response modifyCourse(@PathParam("id") ObjectId id, Course course) {
		if (course.getName() == null || course.getName().equals("")|| course.getLecturer() == null || course.getLecturer().equals("")) {
			return Response.status(400).build();
		}

		UpdateOperations<Course> updateOperations = Model.getDatabase().createUpdateOperations(Course.class)
				.set("name", course.getName())
				.set("lecturer", course.getLecturer());
		Model.getDatabase().update(Model.getDatabase().createQuery(Course.class).field("id").equal(id), updateOperations);
		Response.ResponseBuilder response = Response.status(204).entity(course);
		return response.build();
	}
}
