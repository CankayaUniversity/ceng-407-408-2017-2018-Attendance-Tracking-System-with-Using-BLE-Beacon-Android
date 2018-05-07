package seniorproject.attendancetrackingsystem.utils;

import java.util.Iterator;
import java.util.LinkedList;

public class Queue<T> implements Iterable<T> {
  private LinkedList<T> elements = new LinkedList<>();

  public void enqueue(T element){
    elements.add(element);
  }

  public T dequeue(){
    return elements.removeFirst();
  }

  public T peek(){
    return elements.getFirst();
  }

  public void clear(){
    elements.clear();
  }

  public int size(){
    return elements.size();
  }

  public boolean isEmpty(){
    return elements.isEmpty();
  }

  public void enqueueDistinct(T element){
    if(!elements.contains(element)) elements.add(element);
  }

  @Override
  public Iterator<T> iterator(){
    return elements.iterator();
  }
}
