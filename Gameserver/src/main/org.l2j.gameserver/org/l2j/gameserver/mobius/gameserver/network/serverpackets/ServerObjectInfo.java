package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author devScarlet, mrTJO
 */
public final class ServerObjectInfo implements IClientOutgoingPacket
{
	private final L2Npc _activeChar;
	private final int _x;
	private final int _y;
	private final int _z;
	private final int _heading;
	private final int _idTemplate;
	private final boolean _isAttackable;
	private final double _collisionHeight;
	private final double _collisionRadius;
	private final String _name;
	
	public ServerObjectInfo(L2Npc activeChar, L2Character actor)
	{
		_activeChar = activeChar;
		_idTemplate = _activeChar.getTemplate().getDisplayId();
		_isAttackable = _activeChar.isAutoAttackable(actor);
		_collisionHeight = _activeChar.getCollisionHeight();
		_collisionRadius = _activeChar.getCollisionRadius();
		_x = _activeChar.getX();
		_y = _activeChar.getY();
		_z = _activeChar.getZ();
		_heading = _activeChar.getHeading();
		_name = _activeChar.getTemplate().isUsingServerSideName() ? _activeChar.getTemplate().getName() : "";
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.SERVER_OBJECT_INFO.writeId(packet);
		
		packet.writeD(_activeChar.getObjectId());
		packet.writeD(_idTemplate + 1000000);
		packet.writeS(_name); // name
		packet.writeD(_isAttackable ? 1 : 0);
		packet.writeD(_x);
		packet.writeD(_y);
		packet.writeD(_z);
		packet.writeD(_heading);
		packet.writeF(1.0); // movement multiplier
		packet.writeF(1.0); // attack speed multiplier
		packet.writeF(_collisionRadius);
		packet.writeF(_collisionHeight);
		packet.writeD((int) (_isAttackable ? _activeChar.getCurrentHp() : 0));
		packet.writeD(_isAttackable ? _activeChar.getMaxHp() : 0);
		packet.writeD(0x01); // object type
		packet.writeD(0x00); // special effects
		return true;
	}
}
