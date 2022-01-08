package landon.jurassiccore.balance;

import java.util.UUID;

public class Balance {
  private UUID uuid;
  
  private String name;
  
  private double balance;
  
  public Balance(UUID uuid, String name, double balance) {
    this.uuid = uuid;
    this.name = name;
    this.balance = balance;
  }
  
  public UUID getUUID() {
    return this.uuid;
  }
  
  public String getName() {
    return this.name;
  }
  
  public double getBalance() {
    return this.balance;
  }
}
