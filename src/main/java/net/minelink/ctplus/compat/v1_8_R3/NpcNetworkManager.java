package net.minelink.ctplus.compat.v1_8_R3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.SocketAddress;
import javax.crypto.SecretKey;
import net.minecraft.server.v1_8_R3.EnumProtocol;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketListener;

public final class NpcNetworkManager extends NetworkManager {
  public NpcNetworkManager() {
    super(EnumProtocolDirection.SERVERBOUND);
  }
  
  public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {}
  
  public void a(EnumProtocol enumprotocol) {}
  
  public void channelInactive(ChannelHandlerContext channelhandlercontext) {}
  
  public void exceptionCaught(ChannelHandlerContext channelhandlercontext, Throwable throwable) {}
  
  protected void a(ChannelHandlerContext channelhandlercontext, Packet packet) {}
  
  public void a(PacketListener packetlistener) {}
  
  public void handle(Packet packet) {}
  
  public void a(Packet packet, GenericFutureListener genericfuturelistener, GenericFutureListener... agenericfuturelistener) {}
  
  public SocketAddress getSocketAddress() {
    return new SocketAddress() {
      
      };
  }
  
  public boolean c() {
    return false;
  }
  
  public void a(SecretKey secretkey) {}
  
  public boolean g() {
    return true;
  }
  
  public void k() {}
  
  public void a(int i) {}
  
  public void l() {}
  
  protected void channelRead0(ChannelHandlerContext channelhandlercontext, Packet object) throws Exception {}
}
