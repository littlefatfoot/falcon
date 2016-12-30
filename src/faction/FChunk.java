package faction;

import java.util.ArrayList;

import org.bukkit.Location;

import core.Core;

public class FChunk {

	private int x, z;
	
	private boolean nX = false, nZ = false;

	public FChunk(int x, int z) {
		this.x = x;
		this.z = z;
		this.nX = x < 0;
		this.nZ = z < 0;
	}

	public FChunk(Location location) {
		this.x = (int) (location.getX() / 16);
		this.z = (int) (location.getZ() / 16);
		this.nX = location.getX() < 0;
		this.nZ = location.getZ() < 0;
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public boolean isInChunk(Location location) {
		double lX = location.getBlock().getLocation().getX();
		double lZ = location.getBlock().getLocation().getZ();
		int a = (int) (lX / 16);
		int b = (int) (lZ / 16);
		if (a == x && b == z && lX < 0 == nX && lZ < 0 == nZ) {
			return true;
		}
		return false;
	}
	
	public boolean isNearOtherFaction(Faction yours){
		for(FChunk fChunk: getNearbyChunks()){
			if(fChunk.isClaimed()){
				if(!fChunk.whoOwns().equals(yours)){
					return true;
				}
			}
		}
		return false;
	}
	
	public ArrayList<FChunk> getNearbyChunks(){
		ArrayList<FChunk> nearby = new ArrayList<FChunk>();
		for(int x = -1; x <= 1; x++){
			for(int z = -1; z <= 1; z++){
				if(x != 0 || z != 0){
					FChunk fc = new FChunk(this.x + x, this.z + z);
					nearby.add(fc);
				}
			}
		}
		return nearby;
	}

	public boolean isClaimed() {
		for (Faction faction : Core.getInstance().getFactionManager()
				.getFactions()) {
			for(FChunk chunk : faction.getLand()){
				if(chunk.getX() == this.x && chunk.getZ() == this.z){
					return true;
				}
			}
		}
		return false;
	}

	public Faction whoOwns() {
		for (Faction faction : Core.getInstance().getFactionManager()
				.getFactions()) {
			for(FChunk chunk : faction.getLand()){
				if(chunk.getX() == this.x && chunk.getZ() == this.z){
					return faction;
				}
			}
		}
		return null;
	}

	public boolean isNextTo(FChunk chunk) {
		if (x + 1 == chunk.getX() || x - 1 == chunk.getX() || x == chunk.getX()) {
			if(z + 1 == chunk.getZ() || z - 1 == chunk.getZ() || z == chunk.getZ()){
				if(Math.abs(x - chunk.getX()) + Math.abs(z - chunk.getZ()) <= 1){
					return true;
				}
			}
		}
		return false;
	}

}
