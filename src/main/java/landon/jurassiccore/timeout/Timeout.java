package landon.jurassiccore.timeout;

public class Timeout {
  private TimeoutType type;
  
  private long time;
  
  public Timeout(TimeoutType type) {
    this.type = type;
  }
  
  public TimeoutType getType() {
    return this.type;
  }
  
  public long getTime() {
    return this.time;
  }
  
  public void setTime(long time) {
    this.time = time;
  }
}
