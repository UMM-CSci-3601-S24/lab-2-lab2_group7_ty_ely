package umm3601.todo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;
import umm3601.Main;

/**
 * Tests the logic of the TodoController
 *
 * @throws IOException
 */

@SuppressWarnings({"MagicNumber"})
public class TodoControllerSpec {
  private TodoController todoController;
  private static TodoDatabase db;

  @Mock
  private Context ctx;

  @Captor
  private ArgumentCaptor<Todo[]> todoArrayCaptor;

   /**
   * Setup the "database" with some example todos and
   * create a TodoController to exercise in the tests.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */

  @BeforeEach
  public void setUp() throws IOException {
    MockitoAnnotations.openMocks(this);
    db = new TodoDatabase(Main.TODO_DATA_FILE);
    todoController = new TodoController(db);
  }

  @Test
  public void canBuildController() throws IOException {
    // Call the `TodoController.buildTodoController` method
    // to construct a controller instance "by hand".
    TodoController controller = TodoController.buildTodoController(Main.TODO_DATA_FILE);
    Javalin mockServer = Mockito.mock(Javalin.class);
    controller.addRoutes(mockServer);

    // Verify that calling `addRoutes()` above caused `get()` to be called
    // on the server at least twice. We use `any()` to say we don't care about
    // the arguments that were passed to `.get()`.
    verify(mockServer, Mockito.atLeast(2)).get(any(), any());
  }

  @Test
  public void buildControllerFailsWithIllegalDbFile() {
    Assertions.assertThrows(IOException.class, () -> {
      TodoController.buildTodoController("this is not a legal file name");
    });
  }

   /**
   * Confirm that we can get all the todos
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canGetAllUsers() throws IOException {
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    assertEquals(db.size(), todoArrayCaptor.getValue().length);
  }

  /**
   * Confirm that we can get all the todos with a body that contains sit.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canGetUsersWithBodyContainsSit() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("contains", Arrays.asList(new String[] {"sit"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);

    // Confirm that all the todos passed to `json` contain sit in body.
    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertTrue(todo.body.indexOf("sit") != -1);
    }
  }

   /**
   * Confirm that we can get limited number of todos
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canGetLimitedUsers() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] {"20"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    assertEquals(20, todoArrayCaptor.getValue().length);
  }

  /**
  * Test that if the user sends a request with an illegal value in
  * the limit field (i.e., something that can't be parsed to a number)
  * we get a reasonable error code back.
  */
  @Test
  public void respondsAppropriatelyToIllegalAge() {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("limit", Arrays.asList(new String[] {"abc"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    Throwable exception = Assertions.assertThrows(BadRequestResponse.class, () -> {
      todoController.getTodos(ctx);
    });
    assertEquals("Specified limit '" + "abc" + "' can't be parsed to an integer", exception.getMessage());
  }

   /**
   * Confirm that we can get all the todos with status complete.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canGetTodosByCompleteStatus() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] {"complete"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertTrue(todo.status);
    }
  }

   /**
   * Confirm that we can get all the todos with status incomplete.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canGetTodosByIncompleteStatus() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("status", Arrays.asList(new String[] {"incomplete"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);
    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertFalse(todo.status);
    }
  }

   /**
   * Confirm that we get a todo when using a valid user ID.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canGetUserWithSpecifiedId() throws IOException {
    // A specific todo ID known to be in the "database".
    String id = "58895985c1849992336c219b";
    // Get the todo associated with that ID.
    Todo todo = db.getTodo(id);

    when(ctx.pathParam("id")).thenReturn(id);

    todoController.getTodo(ctx);

    verify(ctx).json(todo);
    verify(ctx).status(HttpStatus.OK);
  }

   /**
   * Confirm that we get a 404 Not Found response when
   * we request a todo ID that doesn't exist.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void respondsAppropriatelyToRequestForNonexistentId() throws IOException {
    when(ctx.pathParam("id")).thenReturn(null);
    Throwable exception = Assertions.assertThrows(NotFoundResponse.class, () -> {
      todoController.getTodo(ctx);
    });
    assertEquals("No todo with id " + null + " was found.", exception.getMessage());
  }

   /**
   * Confirm that we can get all the todos with owner Fry.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canGetUsersWithOwner() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("owner", Arrays.asList(new String[] {"Fry"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);

    // Confirm that all the users passed to `json` work for OHMNET.
    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertEquals("Fry", todo.owner);
    }
  }

   /**
   * Confirm that we can get all the todos with category video games.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canGetUsersWithCategory() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("category", Arrays.asList(new String[] {"video games"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);

    // Confirm that all the users passed to `json` work for OHMNET.
    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertEquals("video games", todo.category);
    }
  }

   /**
   * Confirm that we can get all the todos ordered by owner.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canOrderOutputOrderOfTodosByOwner() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] {"owner"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    for (int i = 0; i < todoArrayCaptor.getValue().length - 1; i++) {
      assertTrue(todoArrayCaptor.getValue()[i].owner.compareTo(todoArrayCaptor.getValue()[i + 1].owner) <= 0);
    }
  }

   /**
   * Confirm that we can get all the todos ordered by category.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canOrderOutputOrderOfTodosByCategory() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] {"category"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    for (int i = 0; i < todoArrayCaptor.getValue().length - 1; i++) {
      assertTrue(todoArrayCaptor.getValue()[i].category.compareTo(todoArrayCaptor.getValue()[i + 1].category) <= 0);
    }
  }

   /**
   * Confirm that we can get all the todos ordered by body.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canOrderOutputOrderOfTodosByBody() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] {"body"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    for (int i = 0; i < todoArrayCaptor.getValue().length - 1; i++) {
      assertTrue(todoArrayCaptor.getValue()[i].body.compareTo(todoArrayCaptor.getValue()[i + 1].body) <= 0);
    }
  }

   /**
   * Confirm that we can get all the todos ordered by status.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canOrderOutputOrderOfTodosByStatus() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("orderBy", Arrays.asList(new String[] {"status"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);
    verify(ctx).json(todoArrayCaptor.capture());
    int i = 0;
    while (!todoArrayCaptor.getValue()[i].status) {
      i++;
    }
    while (i < todoArrayCaptor.getValue().length) {
      assertTrue(todoArrayCaptor.getValue()[i++].status);
    }
  }

  /**
   * Confirm that we can get all the users with owner Fry, category video games, contains sit, and status true.
   * This is a "combination" test that tests the interaction of the
   * `owner`, `category`, 'contains' and 'status' query parameters.
   *
   * @throws IOException if there are problems reading from the "database" file.
   */
  @Test
  public void canGetUsersWithGivenOwnerAndCategoryAndStatus() throws IOException {
    Map<String, List<String>> queryParams = new HashMap<>();
    queryParams.put("category", Arrays.asList(new String[] {"video games"}));
    queryParams.put("owner", Arrays.asList(new String[] {"Fry"}));
    queryParams.put("status", Arrays.asList(new String[] {"complete"}));
    queryParams.put("contains", Arrays.asList(new String[] {"sit"}));
    when(ctx.queryParamMap()).thenReturn(queryParams);

    todoController.getTodos(ctx);

    // Confirm that all the users passed to `json` have owner Fry, category video games, contains sit, and status true
    verify(ctx).json(todoArrayCaptor.capture());
    for (Todo todo : todoArrayCaptor.getValue()) {
      assertEquals("Fry", todo.owner);
      assertEquals("video games", todo.category);
      assertEquals(true, todo.status);
      assertTrue(todo.body.indexOf("sit") != -1);
    }
  }


}
