package mrtjp.projectred.integration2;

import mrtjp.projectred.ProjectRedIntegration;
import mrtjp.projectred.core.BasicUtils;
import mrtjp.projectred.integration2.GateLogic.ITimerGuiLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetServerHandler;
import net.minecraft.world.World;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IServerPacketHandler;
import codechicken.lib.vec.BlockCoord;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;

public class IntegrationSPH implements IServerPacketHandler {
    
    public static Object channel = ProjectRedIntegration.instance;
    
    @Override
    public void handlePacket(PacketCustom packet, NetServerHandler nethandler, EntityPlayerMP player) {
        switch (packet.getType()) {
            case 1:
                incrTimer(player.worldObj, packet);
        }
    }
    
    private void incrTimer(World world, PacketCustom packet) {
        TMultiPart part = readPartIndex(world, packet);
        if(part instanceof GatePart) {
            GatePart gate = (GatePart)part;
            if(gate.getLogic() instanceof ITimerGuiLogic) {
                ITimerGuiLogic t = (ITimerGuiLogic)gate.getLogic();
                t.setTimerMax(gate, t.getTimerMax()+packet.readShort());
            }
        }
    }

    public static PacketCustom writePartIndex(PacketCustom out, TMultiPart part) {
        return out.writeCoord(new BlockCoord(part.getTile()))
                .writeByte(part.tile().jPartList().indexOf(part));
    }
    
    public static TMultiPart readPartIndex(World world, PacketCustom in) {
        TileMultipart tile = BasicUtils.getMultipartTile(world, in.readCoord());
        try
        {
            return tile.jPartList().get(in.readUByte());
        }
        catch(IndexOutOfBoundsException e)
        {
            return null;
        }
    }

    public static void openTimerGui(EntityPlayer player, GatePart part) {
        PacketCustom packet = new PacketCustom(channel, 1);
        writePartIndex(packet, part);
        packet.sendToPlayer(player);
    }
}