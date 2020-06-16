import model.DateParamConverterProvider;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import resources.*;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class Main {
	public static void main(String[] args) throws IOException {
		URI baseUri = UriBuilder.fromUri("http://localhost/").port(8000).build();
		ResourceConfig config = new ResourceConfig(Students.class, Courses.class, StudentId.class, StudentGrades.class,
				CoursesId.class, StudentGradesId.class, DeclarativeLinkingFeature.class, DateParamConverterProvider.class,
				CustomHeaders.class);
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
		server.start();
	}
}