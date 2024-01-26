package umm3601.todo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

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
  //   if (queryParams.containsKey("body")) {
  //     String targetBody = queryParams.get("body").get(0);
  //     filteredTodos = filteredTodosByBody(filteredTodos, targetBody);
  //   }
  //   if (queryParams.containsKey("category")) {
  //     String targetCategory = queryParams.get("category").get(0);
  //     filteredTodos = filteredTodosByCategory(filteredTodos, targetCategory);
  // }
  // if (queryParams.containsKey("status")) {
  //       String targetStatus = queryParams.get("status").get(0);
  //       filteredTodos = filteredTodosByStatus(filteredTodos, targetStatus);
  //   }
    return filteredTodos;
  }

  // private Todo[] filteredTodosByCategory(Todo[] todos, String targetCategory) {
  //   return Arrays.stream(todos).filter(x -> x.category.equals(targetCategory)).toArray(Todo[]::new);
  // }

  // private Todo[] filteredTodosByStatus(Todo[] todos, String targetStatus) {
  //   return Arrays.stream(todos).filter(x -> x.category.equals(targetStatus)).toArray(Todo[]::new);
  // }

  // private Todo[] filteredTodosByBody(Todo[] todos, String targetBody) {
  //   return Arrays.stream(todos).filter(x -> x.category.equals(targetBody)).toArray(Todo[]::new);
  // }

  // private Todo[] filteredTodosByOwner(Todo[] todos, String targetOwner) {
  //   return Arrays.stream(todos).filter(x -> x.owner.equals(targetOwner)).toArray(Todo[]::new);
  // }

}
