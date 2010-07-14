package net.sf.odinms.server;

import net.sf.odinms.net.CashShop.CashShopServer;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.login.LoginServer;
import net.sf.odinms.net.world.WorldServer;


public class Start {

    public final static void main(final String args[]) {
	if (args[0].equals("CHANNEL")) {
	    ChannelServer.startChannel_Main();
	} else if (args[0].equals("LOGIN")) {
	    LoginServer.startLogin_Main();
	} else if (args[0].equals("WORLD")) {
	    WorldServer.startWorld_Main();
	} else if (args[0].equals("CASHSHOP")) {
	    CashShopServer.startCashShop_main();
	} else {
	    System.out.println("Invalid input for selected servers: 'CASHSHOP', 'CHANNEL', 'LOGIN' and 'WORLD'.");
	}
    }
}
