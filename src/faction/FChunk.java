package faction;

import org.bukkit.Location;

import core.Core;

public class FChunk {

	private int x, z;

	public FChunk(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public FChunk(Location location) {
		this.x = (int) (location.getX() / 16);
		this.z = (int) (location.getZ() / 16);
	}

	public int getX() {
		return x;
	}

	public int getZ() {
		return z;
	}

	public boolean isInChunk(Location location) {
		double lX = location.getX();
		double lY = location.getY();
		int a = (int) (lX / 16);
		int b = (int) (lY / 16);
		if (a == x && b == z) {
			return true;
		}
		return false;
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
