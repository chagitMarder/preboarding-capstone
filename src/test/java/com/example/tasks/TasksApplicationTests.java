package com.example.tasks;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.core.ParameterizedTypeReference;
import java.util.Set;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class HttpRequestTest {

	@LocalServerPort
	private int port;

	@Autowired
	private RestTestClient restTestClient;

	@Autowired
	private TaskController taskController;

	@Test
	void tasksShouldReturnATasksSet() {
		restTestClient.get()
				.uri("http://localhost:%d/tasks".formatted(port))
				.exchange()
				.expectBody(new ParameterizedTypeReference<Set<Task>>() {});
	}

	
	@Test
	void createTaskShouldReturnCreatedTask() {

    	restTestClient.post()
            .uri("http://localhost:%d/tasks?title=a&done=false".formatted(port))
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.title").isEqualTo("a")
            .jsonPath("$.done").isEqualTo(false);
	}	

	
	@Test
	void createTaskShouldFail() {

    	restTestClient.post()
            .uri("http://localhost:%d/tasks".formatted(port))
            .exchange()
            .expectStatus().isBadRequest();
	}

	@Test
		void getTaskByIdShouldReturnTask() {
		taskController.tasks.add(new Task(1, "a", false));
    	restTestClient.get()
        	.uri("http://localhost:%d/tasks/1".formatted(port))
        	.exchange()
        	.expectStatus().isOk()
       		.expectBody()
        	.jsonPath("$.id").isEqualTo(1)
        	.jsonPath("$.title").isEqualTo("a")
        	.jsonPath("$.done").isEqualTo(false);
	}	

	@Test
		void getTaskByIdFail() {
    	restTestClient.get()
        	.uri("http://localhost:%d/tasks/-1".formatted(port))
        	.exchange()
        	.expectStatus().isNotFound();
	}	

	@Test
		void deleteTaskByIdShouldReturnNoContent() {
    	restTestClient.delete()
        	.uri("http://localhost:%d/tasks/1".formatted(port))
        	.exchange()
        	.expectStatus().isNoContent();
	}
}
