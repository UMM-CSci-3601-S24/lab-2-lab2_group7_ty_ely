package umm3601.todo;

import java.util.Comparator;

public class SortByOwner implements Comparator<Todo> {
  public int compare(Todo a, Todo b) {
    return a.owner.compareTo(b.owner);
  }
}
