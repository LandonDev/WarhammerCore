package landon.jurassiccore.utils;

import java.lang.reflect.Method;
import org.apache.commons.lang.WordUtils;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
  private static Class<?> CraftItemStackClass = NMSUtil.getCraftClass("inventory.CraftItemStack");
  
  private static Class<?> ItemStackClass = NMSUtil.getNMSClass("ItemStack");
  
  private static Class<?> NBTTagCompoundClass = NMSUtil.getNMSClass("NBTTagCompound");
  
  public static String getItemName(ItemStack is) {
    try {
      Object NMSItemStack = CraftItemStackClass.getMethod("asNMSCopy", new Class[] { ItemStack.class }).invoke(null, new Object[] { is });
      return (String)NMSItemStack.getClass().getMethod("getName", new Class[0]).invoke(NMSItemStack, new Object[0]);
    } catch (NoSuchMethodException|SecurityException|IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException e) {
      e.printStackTrace();
      return WordUtils.capitalize(is.getType().name().replace("_", " ").toLowerCase());
    } 
  }
  
  public static String convertItemStackToJson(ItemStack itemStack) {
    try {
      Method asNMSCopyMethod = CraftItemStackClass.getMethod("asNMSCopy", new Class[] { ItemStack.class });
      Method saveMethod = ItemStackClass.getMethod("save", new Class[] { NBTTagCompoundClass });
      Object NBTTagCompound = NBTTagCompoundClass.newInstance();
      Object NMSItemStack = asNMSCopyMethod.invoke(null, new Object[] { itemStack });
      Object Json = saveMethod.invoke(NMSItemStack, new Object[] { NBTTagCompound });
      return Json.toString();
    } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|SecurityException|InstantiationException e) {
      e.printStackTrace();
      return null;
    } 
  }
}
