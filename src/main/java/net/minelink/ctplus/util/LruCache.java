package net.minelink.ctplus.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> extends LinkedHashMap<K, V> {
  private int capacity;
  
  public LruCache(int capacity) {
    super(capacity, 0.75F, true);
    this.capacity = capacity;
  }
  
  public int getCapacity() {
    return this.capacity;
  }
  
  public void setCapacity(int capacity) {
    this.capacity = capacity;
  }
  
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return (size() > this.capacity);
  }
}
