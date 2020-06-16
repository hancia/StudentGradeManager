package resources;

import dev.morphia.query.UpdateOperations;
import model.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Path("/students/{index}/grades/{id}")
public class StudentGradesId {
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getStudentGrade(@PathParam("index") Long id, @PathParam("id") int gradeid){
		List<Student> st = Model.getDatabase().createQuery(Student.class).field("index").equal(id).asList();
		if(st.size() == 0) {
			return Response.status(404).build();
		}
		else{
			List<Grade> grades = st.get(0).getGrades();
			for(var gr : grades){
				if(gr.getId()==gradeid){
					return Response.ok(gr).build();
				}
			}
			return Response.status(404).build();
		}
	}

	@PUT
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public synchronized Response modifyGrade(@PathParam("index") Long id, @PathParam("id") int gradeid, Grade grade){
		if(!Arrays.asList(2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 4.5f, 5.0f).contains(grade.getValue()) || grade.getCourse() == null){
			return Response.status(400).build();
		}
		List<Course> cr = Model.getDatabase().createQuery(Course.class).field("id").equal(grade.getCourse().getId()).asList();
		if (cr.size() == 0) {
			return Response.status(404).build();
		} else {
			List<Student> st = Model.getDatabase().createQuery(Student.class).field("index").equal(id).asList();
			if(st.size() == 0) {
				return Response.status(404).build();
			}
			else{
				List<Grade> grades = st.get(0).getGrades();
				Grade ret = null;
				for(var gr : grades){
					if(gr.getId()==(gradeid)){
						gr.setCourse(grade.getCourse());
						gr.setValue(grade.getValue());
						gr.setDate(grade.getDate());
						ret = gr;
					}
				}
				updateStudentGradesList(id, st.get(0));
				Response.ResponseBuilder response = Response.status(204).entity(ret);
				return response.build();
			}
		}
	}

	@DELETE
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public synchronized Response deleteGrade(@PathParam("index") Long id, @PathParam("id") int gradeid){
		List<Student> st = Model.getDatabase().createQuery(Student.class).field("index").equal(id).asList();
		if(st.size() == 0) {
			return Response.status(404).build();
		}
		else{
			if(st.get(0).getGrades().removeIf(s -> s.getId()==gradeid)){
				updateStudentGradesList(id, st.get(0));
				Response.ResponseBuilder response = Response.status(204);
				return response.build();
			}
			return Response.status(404).build();
		}
	}

	private synchronized void updateStudentGradesList(Long id, Student student){
		UpdateOperations<Student> updateOperations = Model.getDatabase().createUpdateOperations(Student.class)
				.set("grades", student.getGrades());
		Model.getDatabase().update(Model.getDatabase().createQuery(Student.class).field("index").equal(id), updateOperations);

	}
}
