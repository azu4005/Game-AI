package apg;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class APGPlayer {

	private String ID;
	private boolean Team;
	private String displayName;
	
	private int totalAddedHP = 0;
	private int initHPBoost = 0;
	
	private int totalAddedEnergy = 0;
	private int initEnergyBoost = 0;
	
	private int critPosibility = 0;
	private int totalCritPosibility = 0;
	
	private static List<APGPlayer> players = new ArrayList<APGPlayer>();
	
	public APGPlayer(String ID, boolean team, String displayName) {
		setID(ID);
		setTeam(team);
		setDisplayName(displayName);
	}
	


	public static APGPlayer getOrCreatePlayer(String ID, int team, String displayName) {

        for (APGPlayer player : APGPlayer.players)
            if (player.getID().equals(ID))
                return player;
		
		APGPlayer newPlayer = new APGPlayer(ID, team != 0, displayName);
		players.add(newPlayer);
		return newPlayer;
		
	}
	
	/** Get total team's participation
	 * 
	 * @param boolean team
	 * 
	 * @return int[] {HP, Energy, Crit}
	 */
	public static int[] GetTeamStats(boolean team) {
		
		int teamHP = 0;
		int teamEnergy = 0;
		int teamCrit = 0;
		
		for (int i = 0; i < getPlayers().size(); i++) {
			teamHP += getPlayers().get(i).getTotalAddedHP();
			teamEnergy += getPlayers().get(i).getTotalAddedEnergy();
			teamCrit += getPlayers().get(i).getTotalCritPosibility();
		}
		
		
		return new int[]{teamHP, teamEnergy, teamCrit};
	}
	
	public static void setAndClearInitData(fighting.Character character, int delay_s) {
		
		getPlayers().forEach(p -> {	
			if (p.getTeam() != character.isPlayerNumber()) // Ignore if APG player does not belong to the team
				return;

			// APG participation addition(s)
			character.setHp(character.getHp() + p.getHPBoost());

			// Create a CompletableFuture
	        CompletableFuture<Void> resetTask = CompletableFuture.runAsync(() -> {
	            // Delay before resetting the value
	        	// Create a ScheduledExecutorService
	            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	            // Schedule the value reset after the specified delay
	            scheduler.schedule(() -> {
	                p.setHPBoost(0);
	            }, delay_s, TimeUnit.SECONDS);

	            // Shutdown the scheduler to release resources when done
	            scheduler.shutdown();
	        });

	        // Keep the program running to allow the asynchronous task to complete
	        resetTask.join();
		}
		);
	}
	
	private static List<APGPlayer> getPlayers(){
		return players;
	}
	
	public void addEnergyBoost(int energy) {
		setTotalAddedEnergy(getTotalAddedEnergy() + energy);
		initEnergyBoost += energy;
	}
	
	public int getEnergyBoost() {
		return initEnergyBoost;
	}
	
	public void setEnergyBoost(int energy) {
		initEnergyBoost = energy;
	}
	
	public void addHPBoost(int hp) {
		setTotalAddedHP(getTotalAddedHP() + hp);
		initHPBoost += hp;
	}
	
	public int getHPBoost() {
		return initHPBoost;
	}
	
	public void setHPBoost(int hp) {
		initHPBoost = hp;
	}
	
	@Deprecated
	public int extractHPBoost() {
		int tmp = initHPBoost;
		initHPBoost = 0;
		return tmp;
	}
	
	public boolean getTeam() {
		return Team;
	}
	
	private void setTeam(boolean team) {
		Team = team;
	}

	public String getDisplayName() {
		return displayName;
	}

	private void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public String getID() {
		return this.ID;
	}
	
	private void setID(String ID) {
		this.ID = ID;
	}



	public int getCritPosibility() {
		return critPosibility;
	}



	public void setCritPosibility(int critPosibility) {
		this.critPosibility = critPosibility;
	}



	public int getTotalCritPosibility() {
		return totalCritPosibility;
	}



	public void setTotalCritPosibility(int totalCritPosibility) {
		this.totalCritPosibility = totalCritPosibility;
	}



	public int getTotalAddedEnergy() {
		return totalAddedEnergy;
	}



	public void setTotalAddedEnergy(int totalAddedEnergy) {
		this.totalAddedEnergy = totalAddedEnergy;
	}



	public int getTotalAddedHP() {
		return totalAddedHP;
	}



	public void setTotalAddedHP(int totalAddedHP) {
		this.totalAddedHP = totalAddedHP;
	}
}
