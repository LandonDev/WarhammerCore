package landon.jurassiccore.cooldown;

public class Cooldown {
  private CooldownType type;
  
  private int time;
  
  public Cooldown(CooldownType type) {
    this.type = type;
  }
  
  public CooldownType getType() {
    return this.type;
  }
  
  public int getTime() {
    return this.time;
  }
  
  public void setTime(int time) {
    this.time = time;
  }
}
