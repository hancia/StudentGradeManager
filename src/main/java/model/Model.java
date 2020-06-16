package model;

import com.mongodb.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;
import dev.morphia.query.UpdateOperations;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@XmlRootElement
public class Model {
	private static Model instance = new Model();
	private final MongoClient client;
	private final Morphia morphia;
	private static Datastore database;
//	public List<Course> courses = new ArrayList<>();
//	public List<Student> students = new ArrayList<>();

	public static Model getInstance(){
		return instance;
	}

	public static Datastore getDatabase() {
		return database;
	}

	static {
		try {
			fillDatabase(instance);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private Model() {
		this.client = new MongoClient("localhost", 8004);
		this.morphia = new Morphia();
		this.morphia.mapPackage("model");
		database = this.morphia.createDatastore(client, "students");
		this.morphia.createDatastore(client, "courses");
		database.ensureIndexes();
		database.enableDocumentValidation();
	}

	public static void fillDatabase(Model model) throws ParseException {
		Datastore db = model.getDatabase();
		if (db.createQuery(Student.class).count() > 0
				|| db.createQuery(Course.class).count() > 0
				|| db.createQuery(GradeSequence.class).count() > 0
				|| db.createQuery(IndexSequence.class).count() > 0) {
			return;
		}
		Course c1 = new Course("Kurs 1", "Jan Kowalski");
		Course c2 = new Course("Kurs 2", "Adam Nowak");
		Course c3 = new Course("Kurs 3", "Anna Kowalska");

		database.save(c1);
		database.save(c2);
		database.save(c3);

		long i = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date date = sdf.parse("05/05/2020");
		Student s1 = new Student(i, "Jan", "Wiśniewski", date, new ArrayList<>());
		Student s2 = new Student(i + 1, "Filip", "Kowalczyk", date, new ArrayList<>());
		Student s3 = new Student(i + 2, "Katarzyna", "Wójcik", date, new ArrayList<>());

		Grade g1 = new Grade(2.0f, date, c1);
		Grade g2 = new Grade(4.0f, date, c2);
		Grade g3 = new Grade(3.0f, date, c3);
		s1.getGrades().add(g1);
		s1.getGrades().add(g2);
		s1.getGrades().add(g3);

		Grade g4 = new Grade(5.0f, date, c1);
		Grade g5 = new Grade(5.0f, date,  c2);
		Grade g6 = new Grade(5.0f, date, c3);
		s2.getGrades().add(g4);
		s2.getGrades().add(g5);
		s2.getGrades().add(g6);

		Grade g7 = new Grade(4.0f, date, c1);
		Grade g8 = new Grade(5.0f, date, c2);
		Grade g9 = new Grade(2.0f, date, c3);
		s3.getGrades().add(g7);
		s3.getGrades().add(g8);
		s3.getGrades().add(g9);

		model.saveStudent(s1);
		model.saveStudent(s2);
		model.saveStudent(s3);
	}

	public void saveStudent(Student s) {
		if (s.get_id() == null) {
			Long id = generateStudentId("students");
			s.setIndex(id);
		}
		if(s.getGrades() == null){
			s.setGrades(new ArrayList<>());
		}
		for(var gr : s.getGrades()){
			if(gr.getId()==-1) {
				gr.setId(generateGradeId(s.getIndex().toString()));
			}
		}
		database.save(s);
	}

	private Long generateStudentId(String name) {
		String collName = name;
		Query<IndexSequence> q = database.createQuery(IndexSequence.class).field("_id").equal(collName);
		UpdateOperations<IndexSequence> update = database.createUpdateOperations(IndexSequence.class).inc("counter", 1);
		IndexSequence counter = database.findAndModify(q, update);
		if (counter == null) {
			counter = new IndexSequence(collName);
			database.save(counter);
		}
		return counter.getCounter();
	}

	public synchronized int generateGradeId(String name) {
		String collName = name;
		Query<GradeSequence> q = database.createQuery(GradeSequence.class).field("_id").equal(collName);
		UpdateOperations<GradeSequence> update = database.createUpdateOperations(GradeSequence.class).inc("counter", 1);
		GradeSequence counter = database.findAndModify(q, update);
		if (counter == null) {
			counter = new GradeSequence(collName);
			database.save(counter);
		}
		return counter.getCounter();
	}
}
