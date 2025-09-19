package com.quintonc.vs_sails.client;

import com.quintonc.vs_sails.WindManager;
import com.quintonc.vs_sails.networking.WindModNetworking;
//import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//import team.lodestar.lodestone.handlers.ScreenshakeHandler;
//import team.lodestar.lodestone.systems.screenshake.PositionedScreenshakeInstance;
//import team.lodestar.lodestone.systems.screenshake.ScreenshakeInstance;

public class ClientWindManager extends WindManager {


    public static void InitializeWind() {

        windStrength = 0;
        windDirection = 0;
        windGustiness = 0.125f;
        windShear = 10;

//        assert WindModNetworking.WINDSTRENGTHS2CPACKET != null;
//        ClientPlayNetworking.registerGlobalReceiver(WindModNetworking.WINDSTRENGTHS2CPACKET, (client, handler, buf, responseSender) -> {
//
//            if (buf.readableBytes() >= 4) { // Ensure there are enough bytes to read a float
//                float strength = buf.readFloat();
//
//                client.execute(() -> {
//                    windStrength = strength;
//                });
//            } else {
//                System.out.println("Client: Buffer does not have enough bytes to read a float");
//            }
//        });
//
//        assert WindModNetworking.WINDDIRECTIONS2CPACKET != null;
//        ClientPlayNetworking.registerGlobalReceiver(WindModNetworking.WINDDIRECTIONS2CPACKET, (client, handler, buf, responseSender) -> {
//
//            if (buf.readableBytes() >= 4) { // Ensure there are enough bytes to read a float
//                float direction = buf.readFloat();
//
//                client.execute(() -> {
//                    windDirection = direction;
////                    client.inGameHud.getChatHud().addMessage(Text.of("Direction: " + direction));
//                });
//            } else {
//                System.out.println("Client: Buffer does not have enough bytes to read a float");
//            }
//        });
    }
}
