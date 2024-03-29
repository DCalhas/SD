package ttt;

import java.rmi.*;
import java.util.Scanner;

public class Game {
	TTT ttt;
	Scanner keyboardSc;
	int winner = 0;
	int player = 1;

	public Game() {
		try {
			ttt = new TTT();
			} catch(RemoteException e) {
				System.err.println("RemoteException: " + e.getMessage());
			}
		keyboardSc = new Scanner(System.in);
	}

	public int readPlay() {
		int play;
		do {
			System.out.printf(
					"\nPlayer %d, please enter the number of the square "
							+ "where you want to place your %c (or 0 to refresh the board): \n",
					player, (player == 1) ? 'X' : 'O');
			play = keyboardSc.nextInt();
		} while (play > 9 || play < 0);
		return play;
	}

	public void playGame() {
		int play;
		boolean playAccepted = false;

		do {
			player = ++player % 2;
			do {
				try {
					System.out.println(ttt.currentBoard());
					} catch(RemoteException e) {
						System.err.println("RemoteException: " + e.getMessage());
					}
				play = readPlay();
				if (play != 0) {
					try {
						playAccepted = ttt.play(--play / 3, play % 3, player);
						} catch(RemoteException e) {
							System.err.println("RemoteException: " + e.getMessage());
						}
					if (!playAccepted)
						System.out.println("Invalid play! Try again.");
				} else
					playAccepted = false;
			} while (!playAccepted);
			try {
				winner = ttt.checkWinner();
				} catch(RemoteException e) {
					System.err.println("RemoteException: " + e.getMessage());
				}
		} while (winner == -1);
		try {
			System.out.println(ttt.currentBoard());
			} catch(RemoteException e) {
				System.err.println("RemoteException: " + e.getMessage());
			}
	}

	public void congratulate() {
		if (winner == 2)
			System.out.printf("\nHow boring, it is a draw\n");
		else
			System.out.printf("\nCongratulations, player %d, YOU ARE THE WINNER!\n", winner);
	}

	public static void main(String[] args) {
		Game g = new Game();
		g.playGame();
		g.congratulate();
	}
}
