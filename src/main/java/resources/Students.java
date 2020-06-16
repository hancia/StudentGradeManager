package resources;

import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import model.Model;
import model.Student;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Path("/students")
public class Students {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<Student> getAllStudents(@QueryParam("index") Long index,
										@QueryParam("firstName") String firstName,
										@QueryParam("lastName") String lastName,
										@QueryParam("birthday") Date birthday,
										@DefaultValue("0") @QueryParam("birthdayCompare") int birthdayCompare) {
		UpdateOperations<Student> updateOperations = Model.getDatabase().createUpdateOperations(Student.class)
				.set("links", new ArrayList<>());
		Model.getDatabase().update(Model.getDatabase().createQuery(Student.class), updateOperations);

		Query<Student> query = Model.getDatabase().createQuery(Student.class);
		if(index != null){
			query = query.field("index").equal(index);
		}
		if (firstName != null)
			query = query.field("firstName").containsIgnoreCase(firstName);

		if (lastName != null)
			query = query.field("lastName").containsIgnoreCase(lastName);

		if (birthday != null) {
			if (birthdayCompare < 0)
				query = query.field("birthday").lessThanOrEq(birthday);
			else if (birthdayCompare == 0)
				query = query.field("birthday").equal(birthday);
			else
				query = query.field("birthday").greaterThanOrEq(birthday);
		}
		return query.find().toList();
	}

	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public synchronized Response addStudent(Student student) throws URISyntaxException {
		if (student == null) {
			return Response.status(400).build();
		}

		if (student.getFirstName() == null) {
			return Response.status(400).build();
		}

		if (student.getLastName() == null) {
			return Response.status(400).build();
		}

		if (student.getBirthday() == null) {
			return Response.status(400).build();
		}

		if (student.getFirstName().equals("") || student.getLastName().equals("") || student.getBirthday().toString().equals("")) {
			return Response.status(400).build();
		}
		student.setLinks(new ArrayList<>());
		student.setGrades(new ArrayList<>());
		Model.getInstance().saveStudent(student);
		Response response = Response.created(new URI("students/" + student.getIndex())).build();
		return response;
	}

}
