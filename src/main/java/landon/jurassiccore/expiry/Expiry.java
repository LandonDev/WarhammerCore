package landon.jurassiccore.expiry;

public class Expiry {
  private ExpiryType type;
  
  private long time;
  
  public Expiry(ExpiryType type) {
    this.type = type;
  }
  
  public ExpiryType getType() {
    return this.type;
  }
  
  public long getTime() {
    return this.time;
  }
  
  public void setTime(long time) {
    this.time = time;
  }
}
