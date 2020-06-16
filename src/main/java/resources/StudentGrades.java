package resources;

import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;
import model.Course;
import model.Grade;
import model.Model;
import model.Student;
import org.bson.types.ObjectId;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Path("/students/{index}/grades")
public class StudentGrades {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public synchronized List<Grade> getStudentGrades(@PathParam("index") Long id,
													 @DefaultValue("0")@QueryParam("value") float value,
													 @DefaultValue("0") @QueryParam("valueCompare") int valueCompare,
													 @DefaultValue("-1") @QueryParam("id") int grade_id,
													 @QueryParam("course")String course_id,
													 @QueryParam("date") Date date,
													 @DefaultValue("0") @QueryParam("dateCompare") int dateCompare){
		List<Student> st = Model.getDatabase().createQuery(Student.class).field("index").equal(id).asList();
		List<Grade> grades = st.get(0).getGrades();
		if(value != 0){
			if(valueCompare > 0){
				grades.removeIf(g -> g.getValue() < value);
			}
			else if(valueCompare == 0){
				grades.removeIf(g -> g.getValue() != value);
			}
			else{
				grades.removeIf(g -> g.getValue() > value);
			}
		}

		if(grade_id != -1){
			grades.removeIf(g -> g.getId() != grade_id);
		}

		if(course_id != null){
			try {
				ObjectId objectId = new ObjectId(course_id);
				grades.removeIf(g -> !g.getCourse().getId().equals(objectId));
			} catch(Exception e){
				return new ArrayList<Grade>();
			}
		}

		if(date != null){

			if(dateCompare > 0){
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				grades.removeIf(g -> sdf.format(g.getDate()).compareTo(sdf.format(date)) < 0);
			}
			else if(dateCompare == 0){
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				grades.removeIf(g -> !sdf.format(g.getDate()).equals(sdf.format(date)));
			}
			else{
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				grades.removeIf(g -> sdf.format(g.getDate()).compareTo(sdf.format(date)) > 0);
			}
		}
		return grades;
	}

	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public synchronized Response addStudentGrade(@PathParam("index") Long id, Grade grade) throws URISyntaxException {
		if(!Arrays.asList(2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f).contains(grade.getValue()) || grade.getCourse() == null){
			return Response.status(400).build();
		}
		List<Course> cr = Model.getDatabase().createQuery(Course.class).field("id").equal(grade.getCourse().getId()).asList();
		if (cr.size() == 0) {
			return Response.status(400).build();
		} else {
			List<Student> st = Model.getDatabase().createQuery(Student.class).field("index").equal(id).asList();
			if(st.size() == 0) {
				return Response.status(404).build();
			}
			else{
				if(st.get(0).getGrades().size() != 0) {
					UpdateOperations<Student> updateoperationdelete = Model.getDatabase().createUpdateOperations(Student.class)
							.removeAll("grades", st.get(0).getGrades());

					Model.getDatabase().update(Model.getDatabase().createQuery(Student.class).field("index").equal(id), updateoperationdelete);
				}
				grade.setLinks(new ArrayList<>());
				int new_id = Model.getInstance().generateGradeId(id.toString());
				grade.setId(new_id);
				st.get(0).getGrades().add(grade);
				UpdateOperations<Student> updateOperations = Model.getDatabase().createUpdateOperations(Student.class)
						.push("grades", st.get(0).getGrades());
				Model.getDatabase().update(Model.getDatabase().createQuery(Student.class).field("index").equal(id), updateOperations);
				return Response.created(new URI("students/"+id+"/grades/"+new_id)).build();
			}
		}
	}
}
