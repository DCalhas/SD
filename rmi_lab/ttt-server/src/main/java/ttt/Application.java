package ttt;

import java.rmi.registry.*;

public class Application {
	public static void main(String args[]) {
		System.out.println("Main OK");
		try {
			TTTService ttt = new TTT();
			System.out.println("After create.");

			Registry rmiregistry = LocateRegistry.createRegistry(1099);
			rmiregistry.rebind("TTT", ttt);

			System.out.println("TTT server ready");

			System.out.println("Awaiting connections");
            System.out.println("Press enter to shutdown");
            System.in.read();
			System.exit(0);
		} catch(Exception e) {
			System.out.println("TTT server main: " + e.getMessage());
		}
	}
}