package umm3601.todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.javalin.http.BadRequestResponse;

public class TodoDatabase {
  private Todo[] allTodos;

  public TodoDatabase(String todoDataFile) throws IOException {
    InputStream resourceAsStream = getClass().getResourceAsStream(todoDataFile);
    if (resourceAsStream == null) {
      throw new IOException("Could not find " + todoDataFile);
    }
    InputStreamReader reader = new InputStreamReader(resourceAsStream);
    ObjectMapper objectMapper = new ObjectMapper();
    allTodos = objectMapper.readValue(reader, Todo[].class);
  }

  public int size() {
    return allTodos.length;
  }

  public Todo getTodo(String id) {
    return Arrays.stream(allTodos).filter(x -> x._id.equals(id)).findFirst().orElse(null);
  }

  public Todo[] listTodos(Map<String, List<String>> queryParams) {
    Todo[] filteredTodos = allTodos;
  //   if (queryParams.containsKey("owner")) {
  //     String targetOwner = queryParams.get("owner").get(0);
  //     filteredTodos = filteredTodosByOwner(filteredTodos, targetOwner);
  //   }
      if (queryParams.containsKey("contains")) {
        String targetBody = queryParams.get("contains").get(0);
        filteredTodos = filteredTodosByBody(filteredTodos, targetBody);
    }
  //  if (queryParams.containsKey("category")) {
  //     String targetCategory = queryParams.get("category").get(0);
  //     filteredTodos = filteredTodosByCategory(filteredTodos, targetCategory);
  // }
    if (queryParams.containsKey("status")) {
      boolean b;
        String targetStatus = queryParams.get("status").get(0);
        if (targetStatus.equals("complete")) {
          b = true;
        } else {
          b = false;
        }
        filteredTodos = filteredTodosByStatus(filteredTodos, b);
    }
    if (queryParams.containsKey("limit")) {
      int targetStatus = Integer.parseInt(queryParams.get("limit").get(0));
      if (filteredTodos.length > targetStatus) {
        filteredTodos = Arrays.copyOfRange(filteredTodos, 0, targetStatus);
      }
    }
    return filteredTodos;
  }

  // private Todo[] filteredTodosByCategory(Todo[] todos, String targetCategory) {
  //   return Arrays.stream(todos).filter(x -> x.category.equals(targetCategory)).toArray(Todo[]::new);
  // }

  private Todo[] filteredTodosByStatus(Todo[] todos, boolean targetStatus) {
    return Arrays.stream(todos).filter(x -> x.status == targetStatus).toArray(Todo[]::new);
  }

  private Todo[] filteredTodosByBody(Todo[] todos, String targetBody) {
    int n = 0;
    String trimTargetBody = targetBody.trim();
    if (trimTargetBody == "") {
      throw new BadRequestResponse("Specified String '" + targetBody + "' is not a valid input");
    }
    for (int i = 0; i < todos.length; i++) {
      if (todos[i].body.indexOf(trimTargetBody) != -1) {
        todos[n++] = todos[i];
      }
    }
    Todo[] filteredTodos = Arrays.copyOfRange(todos, 0, n);
    return filteredTodos;
  }

  // private Todo[] filteredTodosByOwner(Todo[] todos, String targetOwner) {
  //   return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
  // }

}
