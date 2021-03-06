package self.principal;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.UIManager;

import self.especial.Especial;
import self.input.ListenerManager;
import self.menu.Menu;
import self.util.Variator;

public class Principal implements Runnable {

	public static Relogio relogio;
	public static Janela janela;
	private Tela tela;
	private static ListenerManager listener;
	
	public static boolean pintar = false;
	
	public static int tickTotal = 1;

	private Principal() {
		relogio = new Relogio();
		janela = new Janela();
		tela = new Tela();
		listener = new ListenerManager();

		new Thread(this).start();
	}

	private void init() {
		tela.init(relogio);
		janela.init(tela, "Smart Clock");
		listener.init(janela);
		Especial.init();
		Menu.init();
		relogio.init();
		
	}
	
	private void update() {
		relogio.update();
		updateVariator();
		Menu.update();
		janela.update();
		Especial.update();
	}

	private void updateVariator() {
		for (int x = 0; x < Variator.todosVariator.size(); x++) {
			Variator.todosVariator.get(x).update();
		}
		
	}

	public void run() {
		init();
		
		long timeOld;
		
		long targetTime =(long) (1000/50.0);
		long timer = System.currentTimeMillis();
		int tick = 0;

		while (true) {
			timeOld = System.nanoTime();
			
			update();
			pintar = true;
			tela.repaint();
			tick++;
			tickTotal++;
			
			if (System.currentTimeMillis() - timer >= 1000) {
				System.out.println("TPS: " + tick);

				tick = 0;
				timer = System.currentTimeMillis();
			}
			
			long timePast = System.nanoTime() - timeOld;
			
			if (timePast > targetTime) timePast = targetTime;

			try {
				Thread.sleep(targetTime - timePast/1000000l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("fonts/DS-DIGII.TTF");
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();	
		try {
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, is));
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new Principal();
	}
}
