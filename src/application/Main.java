package application;

import java.util.ArrayList;

import character.GameCharacter;
import character.Swordman;

public class Main {

	public static void main(String[] args) {
		ArrayList<GameCharacter>player=new ArrayList<>();
		player.add(new Swordman());
		
		System.out.println(player.get(0));

	}

}
