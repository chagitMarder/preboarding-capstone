package com.example.tasks;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
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

	@Test
	void updateTaskShouldReturnUpdatedTask() {
		taskController.tasks.add(new Task(2, "old", false));
		restTestClient.put()
				.uri("http://localhost:%d/tasks/2".formatted(port))
				.contentType(MediaType.APPLICATION_JSON)
				.body(new TaskUpdateRequest("new title", true))
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isEqualTo(2)
				.jsonPath("$.title").isEqualTo("new title")
				.jsonPath("$.done").isEqualTo(true);
	}

	@Test
	void updateTaskOnlyTitleShouldKeepOldDone() {
		taskController.tasks.add(new Task(3, "old title", true));
		restTestClient.put()
				.uri("http://localhost:%d/tasks/3".formatted(port))
				.contentType(MediaType.APPLICATION_JSON)
				.body(new TaskUpdateRequest("updated title only", null))
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isEqualTo(3)
				.jsonPath("$.title").isEqualTo("updated title only")
				.jsonPath("$.done").isEqualTo(true);
	}

	@Test
	void updateTaskOnlyDoneShouldKeepOldTitle() {
		taskController.tasks.add(new Task(4, "keep this title", false));
		restTestClient.put()
				.uri("http://localhost:%d/tasks/4".formatted(port))
				.contentType(MediaType.APPLICATION_JSON)
				.body(new TaskUpdateRequest(null, true))
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.id").isEqualTo(4)
				.jsonPath("$.title").isEqualTo("keep this title")
				.jsonPath("$.done").isEqualTo(true);
	}

	@Test
	void updateTaskByIdFail() {
		restTestClient.put()
				.uri("http://localhost:%d/tasks/-1".formatted(port))
				.contentType(MediaType.APPLICATION_JSON)
				.body(new TaskUpdateRequest("a", false))
				.exchange()
				.expectStatus().isBadRequest();
	}

	@Test
	void filterTasksByDoneTrueShouldReturnOnlyCompletedTasks() {
		taskController.tasks.add(new Task(100, "completed task", true));
		taskController.tasks.add(new Task(101, "pending task", false));

		restTestClient.get()
				.uri("http://localhost:%d/tasks?done=true".formatted(port))
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$[?(@.id == 100)].done").isEqualTo(true)
				.jsonPath("$[?(@.id == 101)]").doesNotExist();
	}

	@Test
	void filterTasksByDoneShouldReturnEmptyWhenNoMatch() {
		taskController.tasks.removeIf(t -> Boolean.TRUE.equals(t.done()));

		restTestClient.get()
				.uri("http://localhost:%d/tasks?done=true".formatted(port))
				.exchange()
				.expectStatus().isOk()
				.expectBody()
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	void filterTasksByDoneInvalidParamShouldFail() {
		restTestClient.get()
				.uri("http://localhost:%d/tasks?done=notABoolean".formatted(port))
				.exchange()
				.expectStatus().isBadRequest();
	}
}
