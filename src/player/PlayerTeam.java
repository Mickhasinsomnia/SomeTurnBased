package player;

import java.util.ArrayList;

import character.GameCharacter;

public class PlayerTeam {

	private static ArrayList<GameCharacter> team;

	public static ArrayList<GameCharacter> player() {
	    if (team == null) {
	        team = new ArrayList<>();
	    }
	    return team;
	}
}
