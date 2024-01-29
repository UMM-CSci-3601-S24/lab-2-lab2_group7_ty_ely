package umm3601.todo;

import java.util.Comparator;

public class SortByStatus implements Comparator<Todo> {
  public int compare(Todo a, Todo b) {
    if (a.status == b.status) {
      return 0;
    } else if (a.status) {
      return 1;
    } else {
      return -1;
    }
  }
}
