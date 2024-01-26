package apg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import enumerate.GameSceneName;
import gamescene.GameScene;
import gamescene.Play;
import setting.LaunchSetting;
import struct.CharacterData;

public class APGManager {

	private static int port = 5000;
	private static NonBlockingTCPListener server;
	
	private static GameScene currentGameScene;
	private static Play playScene;
	
	public static int[] initHP;
	public static int[] agpAdditionalHP = {200, 0};
	public static int[] maxEnergy;
	public static int[] agpAdditionalMaxEnergy = { 0, 0 };
	
	private static fighting.Character character0;
	private static fighting.Character character1;
	
	
	/**
     * Copy of default values
     */
	public static void Init(GameScene scene) {
		currentGameScene = scene;
		
		try {
			setServer(new NonBlockingTCPListener(port));
			server.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		initHP = LaunchSetting.maxHp;
		maxEnergy = LaunchSetting.maxEnergy;
	}
	
	public static void setCurrentGameScene(GameScene scene) {
		currentGameScene = scene;
		if (scene.getCurrentSceneName() == GameSceneName.PLAY)
			playScene = (Play) scene;
	}
	
	
	
	/**
	 * Called synchronous from the main loop
	 */
	public static void update() {
		//System.out.println(currentGameScene.getCurrentSceneName());
		if (currentGameScene.getCurrentSceneName() != GameSceneName.PLAY)
			return;
		
		if (currentGameScene.isTransition())
			return;
		
		CharacterData cd = playScene.frameData.getCharacter(false); // Returns false if the player don't exist, yet
		//if (cd != null)
			//System.out.println("false: " + cd.getHp());
		
		cd = playScene.frameData.getCharacter(true); // Returns false if the player don't exist, yet
		//if (cd != null)
			//System.out.println("true: " + cd.getHp());
	}
	
	public static boolean isWaitingToReset = true;
	/**
     * Run after initial round initial has been executed to override previous values.
     */
	public static void roundInitLate(fighting.Character character){
		
		int characterNum = character.isPlayerNumber() ? 0 : 1;

		if (characterNum == 0)
			character0 = character;
		else
			character1 = character;
		
		
		APGPlayer.setAndClearInitData(character, 3);
	}

	public static NonBlockingTCPListener getServer() {
		return server;
	}

	public static void setServer(NonBlockingTCPListener server) {
		APGManager.server = server;
	}
	
	public static void handleConnection(Socket clientSocket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream outputStream = clientSocket.getOutputStream()
            ) {
                // Read input from the client in a loop
                String clientInput;
                while ((clientInput = reader.readLine()) != null) {
                    System.out.println("Received from client: " + clientInput);
                    APGManager.OnClientInput(clientInput);
                    // Process the client input (replace this with your own logic)
                    String response = "Server received: " + clientInput + "\n";

                    // Send a response back to the client
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // The loop will exit when the client disconnects
                System.out.println("Client disconnected.");

                // Close the socket when done
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
	    
	    
	private static void OnClientInput(String s) {
	    System.out.println(s);
	    if (s.split(";").length < 2)
	    {
	    	System.out.println("ClientData " + s + " is missing either mandatory or optional data.");
	    	return;
	    }
	    	
	    String mandatoryData = s.split(";")[0];
	    System.out.println(mandatoryData);
	    String[] mData = mandatoryData.split("/");
	    	
	    System.out.println(mData.length);
	    System.out.println(mData[mData.length-1]);

	    	
	    APGPlayer player = null;
	    String ID = null;
	    int team = 2;
	    String displayName = null;

        for (String mDatum : mData) {
            String mKey = mDatum.split(":")[0];
            String value = mDatum.split(":")[1];
            switch (mKey) {
                case "ID":
                    ID = value;
                    break;
                case "Team":
                    team = Integer.parseInt(value);
                    break;
                case "DisplayName":
                    displayName = value;
                    break;
                default:
                    System.out.println("Unknown mandatory data " + mKey + ". Data is ignoring.");
                    break;
            }
        }
	    	
	    // Safety is some information was missing in the message.
	    if (ID == null || team == 2 || displayName == null) {
	    	System.out.println("Missing mandatory data.");
	    	return;
	    }
	    player = APGPlayer.getOrCreatePlayer(ID, team, displayName);

	    String optionalData = s.split(";")[1];
	    	
	    String[] data = optionalData.split("/");
	    if (data == null || data.length == 0)
	    {
	    	System.out.println("Missing optional data. Ignoring");
	    	return;
	    }

        for (String datum : data) {
            String dKey = datum.split(":")[0];
            String dvalue = datum.split(":")[1];

            switch (dKey) {
                case "addInitHP":
                    player.addHPBoost(Integer.parseInt(dvalue));
                    break;
                case "setHP":
                    break;
                default:
                    System.out.println("Data pack with key " + dKey + " is not supported. Ignoring.");
                    break;
            }
        }
	}
}

