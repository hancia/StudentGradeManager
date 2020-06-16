package resources;

import dev.morphia.query.UpdateOperations;
import model.Model;
import model.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@Path("/students/{index}")
public class StudentId {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getStudentById(@PathParam("index") Long id){
		List<Student> st = Model.getDatabase().createQuery(Student.class).field("index").equal(id).asList();
		if(st.size() == 0) {
			return Response.status(404).build();
		}
		else{
			Response.ResponseBuilder response = Response.status(200).entity(st.get(0));
			return response.build();
		}
	}

	@DELETE
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public synchronized Response deleteStudent(@PathParam("index") Long id){
		List<Student> st = Model.getDatabase().createQuery(Student.class).field("index").equal(id).asList();
		if(st.size() == 0) {
			return Response.status(404).build();
		}
		else{
			Model.getDatabase().delete(st.get(0));
			Response.ResponseBuilder response = Response.status(204);
			return response.build();
		}
	}

	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public synchronized Response modifyStudent(@PathParam("index") Long id, Student student) {
		if (student.getFirstName() == null || student.getLastName() == null || student.getBirthday() == null) {
			return Response.status(400).build();
		}
		if (student.getFirstName().equals("") || student.getLastName().equals("")) {
			return Response.status(400).build();
		}
		UpdateOperations<Student> updateOperations = Model.getDatabase().createUpdateOperations(Student.class)
				.set("firstName", student.getFirstName())
				.set("lastName", student.getLastName())
				.set("birthday", student.getBirthday());

		Model.getDatabase().update(Model.getDatabase().createQuery(Student.class).field("index").equal(id), updateOperations);
		return Response.status(204).build();
	}
}
