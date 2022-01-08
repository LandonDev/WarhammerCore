package net.minelink.ctplus.compat.v1_8_R3;

import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInAbilities;
import net.minecraft.server.v1_8_R3.PacketPlayInArmAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockDig;
import net.minecraft.server.v1_8_R3.PacketPlayInBlockPlace;
import net.minecraft.server.v1_8_R3.PacketPlayInChat;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;
import net.minecraft.server.v1_8_R3.PacketPlayInCloseWindow;
import net.minecraft.server.v1_8_R3.PacketPlayInCustomPayload;
import net.minecraft.server.v1_8_R3.PacketPlayInEnchantItem;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInHeldItemSlot;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayInResourcePackStatus;
import net.minecraft.server.v1_8_R3.PacketPlayInSetCreativeSlot;
import net.minecraft.server.v1_8_R3.PacketPlayInSettings;
import net.minecraft.server.v1_8_R3.PacketPlayInSpectate;
import net.minecraft.server.v1_8_R3.PacketPlayInSteerVehicle;
import net.minecraft.server.v1_8_R3.PacketPlayInTabComplete;
import net.minecraft.server.v1_8_R3.PacketPlayInTransaction;
import net.minecraft.server.v1_8_R3.PacketPlayInUpdateSign;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayInWindowClick;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public final class NpcPlayerConnection extends PlayerConnection {
  public NpcPlayerConnection(EntityPlayer entityplayer) {
    super(MinecraftServer.getServer(), new NpcNetworkManager(), entityplayer);
  }
  
  public void c() {}
  
  public void disconnect(String s) {}
  
  public void a(PacketPlayInSteerVehicle packetplayinsteervehicle) {}
  
  public void a(PacketPlayInFlying packetplayinflying) {}
  
  public void a(PacketPlayInBlockDig packetplayinblockdig) {}
  
  public void a(PacketPlayInBlockPlace packetplayinblockplace) {}
  
  public void a(PacketPlayInSpectate packetplayinspectate) {}
  
  public void a(PacketPlayInResourcePackStatus packetplayinresourcepackstatus) {}
  
  public void a(IChatBaseComponent ichatbasecomponent) {}
  
  public void sendPacket(Packet packet) {}
  
  public void a(PacketPlayInHeldItemSlot packetplayinhelditemslot) {}
  
  public void a(PacketPlayInChat packetplayinchat) {}
  
  public void chat(String s, boolean async) {}
  
  public void a(PacketPlayInArmAnimation packetplayinarmanimation) {}
  
  public void a(PacketPlayInEntityAction packetplayinentityaction) {}
  
  public void a(PacketPlayInUseEntity packetplayinuseentity) {}
  
  public void a(PacketPlayInClientCommand packetplayinclientcommand) {}
  
  public void a(PacketPlayInCloseWindow packetplayinclosewindow) {}
  
  public void a(PacketPlayInWindowClick packetplayinwindowclick) {}
  
  public void a(PacketPlayInEnchantItem packetplayinenchantitem) {}
  
  public void a(PacketPlayInSetCreativeSlot packetplayinsetcreativeslot) {}
  
  public void a(PacketPlayInTransaction packetplayintransaction) {}
  
  public void a(PacketPlayInUpdateSign packetplayinupdatesign) {}
  
  public void a(PacketPlayInKeepAlive packetplayinkeepalive) {}
  
  public void a(PacketPlayInAbilities packetplayinabilities) {}
  
  public void a(PacketPlayInTabComplete packetplayintabcomplete) {}
  
  public void a(PacketPlayInSettings packetplayinsettings) {}
  
  public void a(PacketPlayInCustomPayload packetplayincustompayload) {}
}
