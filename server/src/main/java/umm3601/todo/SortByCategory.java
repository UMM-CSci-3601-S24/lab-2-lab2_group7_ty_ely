package umm3601.todo;

import java.util.Comparator;

public class SortByCategory implements Comparator<Todo> {
  public int compare(Todo a, Todo b) {
    return a.category.compareTo(b.category);
  }
}
