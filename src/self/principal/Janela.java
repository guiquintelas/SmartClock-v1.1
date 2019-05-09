package self.principal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

import self.GUI.HoverArea;
import self.especial.Especial;
import self.input.KeyListener;
import self.input.ListenerManager;
import self.input.MouseListener;
import self.menu.Menu;
import self.util.Util;
import self.util.Variator;
import self.util.VariatorNumero;



@SuppressWarnings("serial")
public class Janela extends JFrame {
	public static final int WIDTH = 200;
	public static final int HEIGHT = 70;
	public static final int MENU_HEIGHT = 145;
	public static final int WIDTH_TELA = Toolkit.getDefaultToolkit().getScreenSize().width;
	public static final int HEIGHT_TELA = Toolkit.getDefaultToolkit().getScreenSize().height;
	
	public boolean pintar = false;
	private Point mouse = null;
	
	private HoverArea botaoX;
	private HoverArea botaoMin;
	
	public static int x;
	public static int y;
	
	private static Variator varX;
	private static Variator varY;
	private static int xTemp;
	private static int yTemp;
	private static boolean updateXY = false;
	
	public static int xMouseTela;
	public static int yMouseTela;
	
	
	
	public void init(Tela telas, String titulo) {
		setUndecorated(true);
		setBackground(new Color(0,0,0,0));
		setResizable(false);
		setLayout(null);
		setTitle(titulo);
		setIconImage(Util.carregarImg("/clockicon.png"));
		setContentPane(telas);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setAlwaysOnTop(true);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		botaoX = new HoverArea(Janela.WIDTH - 16, 0, 16, 16, 5);
		botaoMin = new HoverArea(Janela.WIDTH - 30, 0, 16, 16, 5);
		botaoX.setOffSet(-5);
		botaoMin.setOffSet(-5);
		
		initListener();
		initVariator();
		
		x = xTemp = getX();
		y = yTemp = getY();
		
	}
	
	private void initVariator() {
		varX = new Variator(new VariatorNumero() {
			public void setNumero(double numero) {
				xTemp = (int)numero;
				updateXY = true;
			}
			
			public double getNumero() {
				return x;
			}
			
			public boolean devoContinuar() {
				return true;
			}
		});
		
		varY = new Variator(new VariatorNumero() {
			public void setNumero(double numero) {
				yTemp = (int)numero;
				updateXY = true;
			}
			
			public double getNumero() {
				return y;
			}
			
			public boolean devoContinuar() {
				return true;
			}
		});
		
	}

	private void initListener() {
		ListenerManager.addListener(ListenerManager.MOUSE_PRESSED, new MouseListener() {
			int lastTick = 0;
			public void acao(MouseEvent e) {
				if (Principal.tickTotal < lastTick +15) {
					return;
				}
				lastTick = Principal.tickTotal;
				
				if (botaoX.isAtivo()) {
					System.exit(0);
				}
				
				if (Especial.rodando) return;

				if (e.getButton() != 1) {
					if (Menu.aberto) {
						Menu.fechar();
					} else {
						Menu.abrir();
					}
					mouse = null;
					return;
				}
				
				if (e.getY() > Janela.HEIGHT) {
					mouse = null;
					return;
				}
				
				if (botaoMin.isAtivo()) {
					setState(ICONIFIED);
				}
				
				mouse = e.getPoint();	
			}
		});
		
		ListenerManager.addListener(ListenerManager.MOUSE_DRAG, new MouseListener() {
			public void acao(MouseEvent e) {
				if (Especial.rodando || mouse == null || e.getY() > Janela.HEIGHT) return;
				setLocation(e.getXOnScreen() - (int)mouse.getX(), e.getYOnScreen() - (int)mouse.getY());
				
			}
		});
		
		ListenerManager.addListener(ListenerManager.KEY_PRESSED, new KeyListener() {
			public void acao(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE && !Especial.rodando) {
					if (Menu.aberto) {
						Menu.fechar();
					} else {
						Menu.abrir();
					}
				}
				
				//top-right numpad off 9
				if (e.getKeyCode() == 33) moverJanela(WIDTH_TELA - WIDTH - 5, 25, 50);
				//top-left numpad off 7
				if (e.getKeyCode() == 36) moverJanela(5, 25, 50);
				//bottom-left numpad off 1
				if (e.getKeyCode() == 35) moverJanela(5,HEIGHT_TELA - HEIGHT - 40, 50);
				//bottom-right numpad off 3
				if (e.getKeyCode() == 34) moverJanela(WIDTH_TELA - WIDTH - 5, HEIGHT_TELA - HEIGHT - 40, 50);
			}
		});
		
		ListenerManager.addListener(ListenerManager.MOUSE_CLICKED, new MouseListener() {
			int doubleClick = 0;
			public void acao(MouseEvent e) {
				if (Especial.rodando) return;
				
				if (Principal.tickTotal <= doubleClick + 10 && e.getButton() == 1 && !Menu.aberto) {
					if (e.getX() < Janela.WIDTH/2 && e.getY() < Janela.HEIGHT/2) {
						//System.out.println("top left");
						moverJanela(5, 25, 50);
					}
					
					if (e.getX() >= Janela.WIDTH/2 && e.getY() < Janela.HEIGHT/2) {
						//System.out.println("top right");
						moverJanela(WIDTH_TELA - WIDTH - 5, 25, 50);
					}
					
					if (e.getX() < Janela.WIDTH/2 && e.getY() >= Janela.HEIGHT/2 && e.getY() < Janela.HEIGHT) {
						//System.out.println("botton left");
						moverJanela(5,HEIGHT_TELA - HEIGHT - 40, 50);
					}
					
					if (e.getX() >= Janela.WIDTH/2 && e.getY() >= Janela.HEIGHT/2 && e.getY() < Janela.HEIGHT) {
						//System.out.println("botton right");
						moverJanela(WIDTH_TELA - WIDTH - 5, HEIGHT_TELA - HEIGHT - 40, 50);
					}
				}
				
				
				doubleClick = Principal.tickTotal;
			}
		});
	}
	
	public static void moverJanela(int x, int y, int tickDelay) {
		varX.clearFila();
		varX.variar(false);
		varY.clearFila();
		varY.variar(false);
		
		if (Janela.x != x) {
			if (Janela.x > x) {
				System.out.println("janela x " + Janela.x);
				varX.fadeOutSin(Janela.x, x, tickDelay);
			} else {
				varX.fadeInSin(Janela.x, x, tickDelay);
			}
			varX.variar(true);
		}
		
		if (Janela.y != y) {
			if (Janela.y > y) {
				varY.fadeOutSin(Janela.y, y, tickDelay);
			} else {
				varY.fadeInSin(Janela.y, y, tickDelay);
			}
			varY.variar(true);
		}
	}
	
	public void updateXY() {	
		if (updateXY) {
			setLocation(xTemp, yTemp);
			xTemp = x = getX();
			yTemp = y = getY();
			updateXY = false;
		}	else {
			x = getX();
			y = getY();
		}	
		
	}
	
	public void update() {
		updateXY();
		updateXYMouse();
	}
	
	private void updateXYMouse() {
		xMouseTela = MouseInfo.getPointerInfo().getLocation().x;
		yMouseTela = MouseInfo.getPointerInfo().getLocation().y;
		
	}

	public static boolean isMovendo() {
		return varX.isVariando() || varY.isVariando();
	}
	
	public void paint (Graphics g) {


	}
	
}