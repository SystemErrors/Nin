/*
            This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sf.odinms.net.channel.handler;

import java.rmi.RemoteException;
import java.util.Collection;
import net.sf.odinms.client.MapleCharacter;
import net.sf.odinms.client.MapleClient;
import net.sf.odinms.net.channel.ChannelServer;
import net.sf.odinms.net.world.MapleParty;
import net.sf.odinms.net.world.MaplePartyCharacter;
import net.sf.odinms.net.world.PartyOperation;
import net.sf.odinms.net.world.remote.WorldChannelInterface;
import net.sf.odinms.server.maps.MapleMap;
import net.sf.odinms.server.maps.MapleMapObject;
import net.sf.odinms.tools.MaplePacketCreator;
import net.sf.odinms.tools.data.input.SeekableLittleEndianAccessor;

/**
 *
 * @author Admin
 */
public class PartyHandler {
public static final void DenyPartyRequest(final SeekableLittleEndianAccessor slea, final MapleClient c) {
	final MapleCharacter cfrom = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
	final String to = slea.readMapleAsciiString();

	if (cfrom != null) {
	    cfrom.getClient().getSession().write(MaplePacketCreator.partyStatusMessage(23, c.getPlayer().getName()));
	}
    }

    public static final void PartyOperatopn(final SeekableLittleEndianAccessor slea, final MapleClient c) {
	final int operation = slea.readByte();
	final WorldChannelInterface wci = ChannelServer.getInstance(c.getChannel()).getWorldInterface();
	MapleParty party = c.getPlayer().getParty();
	MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());

	switch (operation) {
	    case 1: // create
		if (c.getPlayer().getParty() == null) {
		    try {
			party = wci.createParty(partyplayer);
			c.getPlayer().setParty(party);
		    } catch (RemoteException e) {
			c.getChannelServer().reconnectWorld();
		    }
		    c.getSession().write(MaplePacketCreator.partyCreated());
		} else {
		    c.getPlayer().dropMessage(5, "You can't create a party as you are already in one");
		}
		break;
	    case 2: // leave
		if (party != null) { //are we in a party? o.O"
		    try {
			if (partyplayer.equals(party.getLeader())) { // disband
			    wci.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
			    if (c.getPlayer().getEventInstance() != null) {
				c.getPlayer().getEventInstance().disbandParty();
			    }
			} else {
			    wci.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
			    if (c.getPlayer().getEventInstance() != null) {
				c.getPlayer().getEventInstance().leftParty(c.getPlayer());
			    }
			}
		    } catch (RemoteException e) {
			c.getChannelServer().reconnectWorld();
		    }
		    c.getPlayer().setParty(null);
		}
		break;
	    case 3: // accept invitation
		final int partyid = slea.readInt();
		if (c.getPlayer().getParty() == null) {
		    try {
			party = wci.getParty(partyid);
			if (party != null) {
			    if (party.getMembers().size() < 6) {
				wci.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
				c.getPlayer().receivePartyMemberHP();
				c.getPlayer().updatePartyMemberHP();
			    } else {
				c.getSession().write(MaplePacketCreator.partyStatusMessage(17));
			    }
			} else {
			    c.getPlayer().dropMessage(5, "The party you are trying to join does not exist");
			}
		    } catch (RemoteException e) {
			c.getChannelServer().reconnectWorld();
		    }
		} else {
		    c.getPlayer().dropMessage(5, "You can't join the party as you are already in one");
		}
		break;
	    case 4: // invite
		// TODO store pending invitations and check against them
		final MapleCharacter invited = c.getChannelServer().getPlayerStorage().getCharacterByName(slea.readMapleAsciiString());
		if (invited != null && c.getPlayer().canFuck(invited)) {
		    if (invited.getParty() == null) {
			if (party.getMembers().size() < 6) {
			    invited.getClient().getSession().write(MaplePacketCreator.partyInvite(c.getPlayer()));
			} else {
			    c.getSession().write(MaplePacketCreator.partyStatusMessage(16));
			}
		    } else {
			c.getSession().write(MaplePacketCreator.partyStatusMessage(17));
		    }
		} else {
		    c.getSession().write(MaplePacketCreator.partyStatusMessage(19));
		}
		break;
	    case 5: // expel
		if (partyplayer.equals(party.getLeader())) {
		    final MaplePartyCharacter expelled = party.getMemberById(slea.readInt());
		    if (expelled != null) {
			try {
			    wci.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
			    if (c.getPlayer().getEventInstance() != null) {
				/*if leader wants to boot someone, then the whole party gets expelled
				TODO: Find an easier way to get the character behind a MaplePartyCharacter
				possibly remove just the expellee.*/
				if (expelled.isOnline()) {
				    c.getPlayer().getEventInstance().disbandParty();
				}
			    }

			} catch (RemoteException e) {
			    c.getChannelServer().reconnectWorld();
			}
		    }
		}
		break;
	    case 6: // change leader
		final MaplePartyCharacter newleader = party.getMemberById(slea.readInt());
		try {
		    wci.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newleader);
		} catch (RemoteException e) {
		    c.getChannelServer().reconnectWorld();
		}
		break;
	    default:
		System.out.println("Unhandled Party function." + operation + "");
		break;
	}
    }

    public static final void partySearchRegister(SeekableLittleEndianAccessor slea, MapleClient c) {
        MapleCharacter chr = c.getPlayer();
        int min = slea.readInt();
        int max = slea.readInt();
        if (chr.getLevel() < min || chr.getLevel() > max || (max - min) > 30 || min > max) { // Client editing
            return;
        }
    }

    public static final void partySearchStart(SeekableLittleEndianAccessor slea, MapleClient c) {
        int min = slea.readInt();
        int max = slea.readInt();
        slea.readInt(); // members
        int jobs = slea.readInt();
        MapleCharacter chr = c.getPlayer();
        MapleMap map = chr.getMap();
        Collection<MapleMapObject> mapobjs = map.getAllPlayer();
        for (MapleMapObject mapobj : mapobjs) {
            if (chr.getParty().getMembers().size() > 5) {
                break;
            }
            if (mapobj instanceof MapleCharacter) {
                MapleCharacter tchar = (MapleCharacter) mapobj;
                int charlvl = tchar.getLevel();
                if (charlvl >= min && charlvl <= max && isValidJob(tchar.getJob(), jobs)) {
                    if (c.getPlayer().getParty() == null) {
                        try {
                            WorldChannelInterface wci = c.getChannelServer().getWorldInterface();
                            MapleParty party = c.getPlayer().getParty();
                            int partyid = party.getId();
                            party = wci.getParty(partyid);
                            if (party != null) {
                                if (party.getMembers().size() < 6) {
                                    MaplePartyCharacter partyplayer = new MaplePartyCharacter(tchar);
                                    wci.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                                    c.getPlayer().receivePartyMemberHP();
                                    c.getPlayer().updatePartyMemberHP();
                                } else {
                                    c.getSession().write(MaplePacketCreator.partyStatusMessage(17));
                                }
                            }
                        } catch (Exception e) {
                            c.getChannelServer().reconnectWorld();
                        }
                    }
                }
            }
        }
    }

    private static boolean isValidJob(int jobid, int jobs) {
        if (jobid == 0) {
            return ((jobs & 2) > 0);
        } else if (jobid == 100) {
            return ((jobs & 4) > 0);
        } else if (jobid > 100 && jobid < 113) {
            return ((jobs & 8) > 0);
        } else if (jobid > 110 && jobid < 123) {
            return ((jobs & 16) > 0);
        } else if (jobid > 120 && jobid < 133) {
            return ((jobs & 32) > 0);
        } else if (jobid == 200) {
            return ((jobs & 64) > 0);
        } else if (jobid > 209 && jobid < 213) {
            return ((jobs & 128) > 0);
        } else if (jobid > 219 && jobid < 223) {
            return ((jobs & 256) > 0);
        } else if (jobid > 229 && jobid < 233) {
            return ((jobs & 512) > 0);
        } else if (jobid == 500) {
            return ((jobs & 1024) > 0);
        } else if (jobid > 509 && jobid < 513) {
            return ((jobs & 2048) > 0);
        } else if (jobid > 519 && jobid < 523) {
            return ((jobs & 4096) > 0);
        } else if (jobid == 400) {
            return ((jobs & 8192) > 0);
        } else if (jobid > 400 && jobid < 413) {
            return ((jobs & 16384) > 0);
        } else if (jobid > 419 && jobid < 423) {
            return ((jobs & 32768) > 0);
        } else if (jobid == 300) {
            return ((jobs & 65536) > 0);
        } else if (jobid > 300 && jobid < 313) {
            return ((jobs & 131072) > 0);
        } else if (jobid > 319 && jobid < 323) {
            return ((jobs & 262144) > 0);
        }
        return false;
    }
}

