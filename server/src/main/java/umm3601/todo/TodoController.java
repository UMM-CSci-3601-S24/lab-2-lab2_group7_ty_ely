package umm3601.todo;

import java.io.IOException;

import io.javalin.Javalin;
import io.javalin.http.Context;
import umm3601.Controller;

public class TodoController implements Controller {

  private TodoDatabase todoDatabase;

  public TodoController(TodoDatabase todoDatabase) {
    this.todoDatabase = todoDatabase;
  }

  public static TodoController buildTodoController(String todoDataFile) throws IOException {
    TodoController todoController = null;

    TodoDatabase todoDatabase = new TodoDatabase(todoDataFile);
    todoController = new TodoController(todoDatabase);

    return todoController;
  }

  public void getTodos(Context ctx) {
    Todo[] todos = todoDatabase.listTodos(ctx.queryParamMap());
    ctx.json(todos);
  }

  public void addRoutes(Javalin server) {
    server.get("/api/todos/{id}", this::getTodos);
    server.get("/api/todos", this::getTodos);
  }
}
