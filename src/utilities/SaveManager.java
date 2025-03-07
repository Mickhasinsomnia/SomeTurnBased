package utilities;

import java.io.*;

public class SaveManager {
	private static final String FILE_PATH = "unlock.txt";

	public static int loadUnlock() {
		try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
			return Integer.parseInt(reader.readLine());
		} catch (IOException e) {
			return 1;
		}
	}

	public static void saveUnlock(int unlock) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
			writer.write(String.valueOf(unlock));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
