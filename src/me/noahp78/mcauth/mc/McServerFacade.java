package me.noahp78.mcauth.mc;

import java.net.Proxy;
import java.util.HashMap;

import org.spacehq.mc.auth.data.GameProfile;
import org.spacehq.mc.protocol.MinecraftConstants;
import org.spacehq.mc.protocol.MinecraftProtocol;
import org.spacehq.mc.protocol.ServerLoginHandler;
import org.spacehq.mc.protocol.data.SubProtocol;
import org.spacehq.mc.protocol.data.game.entity.player.GameMode;
import org.spacehq.mc.protocol.data.game.setting.Difficulty;
import org.spacehq.mc.protocol.data.game.world.WorldType;
import org.spacehq.mc.protocol.data.message.ChatColor;
import org.spacehq.mc.protocol.data.message.ChatFormat;
import org.spacehq.mc.protocol.data.message.Message;
import org.spacehq.mc.protocol.data.message.MessageStyle;
import org.spacehq.mc.protocol.data.message.TextMessage;
import org.spacehq.mc.protocol.data.status.PlayerInfo;
import org.spacehq.mc.protocol.data.status.ServerStatusInfo;
import org.spacehq.mc.protocol.data.status.VersionInfo;
import org.spacehq.mc.protocol.data.status.handler.ServerInfoBuilder;
import org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerChatPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import org.spacehq.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import org.spacehq.packetlib.Server;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.event.server.ServerAdapter;
import org.spacehq.packetlib.event.server.SessionAddedEvent;
import org.spacehq.packetlib.event.server.SessionRemovedEvent;
import org.spacehq.packetlib.event.session.PacketReceivedEvent;
import org.spacehq.packetlib.event.session.SessionAdapter;
import org.spacehq.packetlib.tcp.TcpSessionFactory;

public class McServerFacade {

	
	
	
	/**
	 * First parameter = POLL TOKEN
	 * Second Paramter = HOSTNAME
	 */
	public static HashMap<String,String> token_url_connection = new HashMap<>();
	
	public static HashMap<String,String> hostname_token = new HashMap<>();
	
	/**
	 * First Paramter = POLL_TOKEN
	 * Second Parameter = Authentication Result
	 */
	public static HashMap<String, MCUserData> authenticated_users = new HashMap<>();
	private static final boolean SPAWN_SERVER = true;
    private static final boolean VERIFY_USERS = true;
    private static final String HOST = "0.0.0.0";
    private static final int PORT = 25565;
    private static final Proxy PROXY = Proxy.NO_PROXY;
    private static final Proxy AUTH_PROXY = Proxy.NO_PROXY;
    private static final String USERNAME = "Username";
    private static final String PASSWORD = "Password";
	public static void startServer(){
		
		Server server = new Server(HOST, PORT, MinecraftProtocol.class, new TcpSessionFactory(PROXY));
        server.setGlobalFlag(MinecraftConstants.AUTH_PROXY_KEY, AUTH_PROXY);
        server.setGlobalFlag(MinecraftConstants.VERIFY_USERS_KEY, VERIFY_USERS);
        
        server.setGlobalFlag(MinecraftConstants.SERVER_INFO_BUILDER_KEY, new ServerInfoBuilder() {
        
        	@Override
            public ServerStatusInfo buildInfo(Session session) {
                
            	return new ServerStatusInfo(new VersionInfo(MinecraftConstants.GAME_VERSION, MinecraftConstants.PROTOCOL_VERSION), new PlayerInfo(100, 0, new GameProfile[0]), new TextMessage("I authenticate or something, I guess"), null);
            }
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_LOGIN_HANDLER_KEY, new ServerLoginHandler() {
            @Override
            public void loggedIn(Session session) {
            	GameProfile profile = session.getFlag(MinecraftConstants.PROFILE_KEY);
            	MCUserData dat = new MCUserData();
            	dat.uuid=profile.getId().toString();
            	dat.username=profile.getName();
            	String host = session.getFlag("HOSTNAME");
            	//FML fix.
            	if(host.contains(("\0FML\0"))){
            		host = host.split("\0")[0];
            	}
            	System.out.println("Connection from host " + host);
            	if(hostname_token.containsKey(host)){
            		String token = hostname_token.get(host);
            		authenticated_users.put(token, dat);
            		hostname_token.remove(host);
            		System.out.println("Verified " + token);
            	session.send(new ServerDisconnectPacket("Verified"));
            	}else{
            		session.send(new ServerDisconnectPacket("No verify request for "+ host));
            	}
            	//session.send(new ServerJoinGamePacket(0, false, GameMode.SURVIVAL, 0, Difficulty.PEACEFUL, 10, WorldType.DEFAULT, false));
            //
            }
        });

        server.setGlobalFlag(MinecraftConstants.SERVER_COMPRESSION_THRESHOLD, 100);
        server.addListener(new ServerAdapter() {
            @Override
            public void sessionAdded(SessionAddedEvent event) {
            	
                event.getSession().addListener(new SessionAdapter() {
                    @Override
                    public void packetReceived(PacketReceivedEvent event) {
                        if(event.getPacket() instanceof ClientChatPacket) {
                            ClientChatPacket packet = event.getPacket();
                            GameProfile profile = event.getSession().getFlag(MinecraftConstants.PROFILE_KEY);
                            System.out.println(profile.getName() + ": " + packet.getMessage());
                            Message msg = new TextMessage("Hello, ").setStyle(new MessageStyle().setColor(ChatColor.GREEN));
                            Message name = new TextMessage(profile.getName()).setStyle(new MessageStyle().setColor(ChatColor.AQUA).addFormat(ChatFormat.UNDERLINED));
                            Message end = new TextMessage("!");
                            msg.addExtra(name);
                            msg.addExtra(end);
                            event.getSession().send(new ServerChatPacket(msg));
                        }
                    }
                });
            }

            
        });

        server.bind();
	}
}
