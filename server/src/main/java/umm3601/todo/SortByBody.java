package umm3601.todo;

import java.util.Comparator;

public class SortByBody implements Comparator<Todo> {
  public int compare(Todo a, Todo b) {
    return a.body.compareTo(b.body);
  }
}
