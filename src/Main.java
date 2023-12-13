import project.ManagerView;

public class Main {
	public static void main(String args[]) {

		try {
			ManagerView view = new ManagerView();
			view.startManager();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}