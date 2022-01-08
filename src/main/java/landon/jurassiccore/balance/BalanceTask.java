package landon.jurassiccore.balance;

import org.bukkit.scheduler.BukkitRunnable;

public class BalanceTask extends BukkitRunnable {
  private BalanceManager balanceManager;
  
  public BalanceTask(BalanceManager balanceManager) {
    this.balanceManager = balanceManager;
  }
  
  public void run() {
    this.balanceManager.clearBalances();
    this.balanceManager.prepareBalances();
  }
}
