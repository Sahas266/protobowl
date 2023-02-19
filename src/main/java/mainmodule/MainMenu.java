package mainmodule;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.json.simple.JSONObject;


public class MainMenu extends JDialog implements ActionListener, ItemListener {
	public static JMenuBar menuBar;
	public static JMenu menu, submenu, menuItemSubjects, menuItemLoad;
	public static List<JMenuItem> menuItems;
	public static JRadioButtonMenuItem rbMenuItem;
	public static JCheckBoxMenuItem cbMenuItem;
	public static JFrame frame;
	public static JFrame logFrame;
	public static JMenuItem importItemImport;
	private static JMenuItem mntmScience;
	private JMenuItem menuItemExit;
	private JMenuItem mntmHistory;
	private JMenuItem mntmCategory;
	private static JMenuItem mntmLoadMany;
	private static JMenuItem mntmLoadOne;
	
	public static ManageImports ma;
	public static int questionsCounter = 0;
	public static int correctAnswersCounter = 0;
	public static int wrongAnswersCounter = 0;
	public static int skippedCounter = 0;
	


	public static void main(String[] args) throws Exception {

		if (!isAdmin()) {
			JOptionPane.showMessageDialog(null, "Only Admin users are allowed to run this application!",
					"Protobowl - Lets have fun", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		menuItems = new ArrayList<JMenuItem>();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu window = new MainMenu();
					// window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws Exception
	 */
	public MainMenu() throws Exception {
		initialize();
	}

	public static boolean isAdmin() {
		String groups[] = (new com.sun.security.auth.module.NTSystem()).getGroupIDs();
		for (String group : groups) {
			if (group.equals("S-1-5-32-544"))
				return true;
		}
		return false;
	}

	public void initialize() throws Exception {
		ma = new ManageImports();
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame = new JFrame("Protobowl - Lets have fun");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		createUI(frame);
		frame.setPreferredSize(new Dimension(600, 500));
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.pack();
		String appPath = Paths.get(".").toAbsolutePath().normalize().toString();
		String splashScreenPath = appPath.concat("\\ProtobowlSplash01.png");
		System.out.println(splashScreenPath);
		frame.setContentPane(new JLabel(new ImageIcon(ImageIO.read(new File(splashScreenPath)))));
		for (int i = 0; i < menuItems.size(); i++) {
			JMenuItem item = menuItems.get(i);
			item.addActionListener(this);
		}
		frame.setVisible(true);
	}

	public void createUI(final JFrame frame) {
		menuBar = new JMenuBar();
		menu = new JMenu("Main Menu");
		menu.getAccessibleContext().setAccessibleDescription("Main Menu");
		menuBar.add(menu);

		menuItemSubjects = new JMenu("Subjects");
		menuItemSubjects.setFont(new Font("Calibri", Font.ITALIC, 14));
		menu.add(menuItemSubjects);

		
		mntmScience = new JMenuItem("Science");
		mntmScience.setFont(new Font("Calibri", Font.ITALIC, 14));
		mntmScience.setName("science");
		menuItemSubjects.add(mntmScience);
		menuItems.add(mntmScience);


		mntmHistory = new JMenuItem("History");
		mntmHistory.setName("history");
		mntmHistory.setFont(new Font("Calibri", Font.ITALIC, 14));
		menuItemSubjects.add(mntmHistory);
		menuItems.add(mntmHistory);
		
		mntmCategory = new JMenuItem("Choose By Category");
		mntmCategory.setName("category");
		mntmCategory.setFont(new Font("Calibri", Font.ITALIC, 14));
		menuItemSubjects.add(mntmCategory);
		menuItems.add(mntmCategory);

		importItemImport = new JMenuItem("Import");
		importItemImport.setFont(new Font("Calibri", Font.ITALIC, 14));
		importItemImport.setName("import");
		menu.add(importItemImport);
		menuItems.add(importItemImport);
		
		menuItemLoad = new JMenu("Load");
		menuItemLoad.setFont(new Font("Calibri", Font.ITALIC, 14));
		menu.add(menuItemLoad);

		
		mntmLoadOne = new JMenuItem("LoadSingle");
		mntmLoadOne.setFont(new Font("Calibri", Font.ITALIC, 14));
		mntmLoadOne.setName("loadSingle");
		menuItemLoad.add(mntmLoadOne);
		menuItems.add(mntmLoadOne);

		mntmLoadMany = new JMenuItem("LoadMany");
		mntmLoadMany.setFont(new Font("Calibri", Font.ITALIC, 14));
		mntmLoadMany.setName("loadMany");
		menuItemLoad.add(mntmLoadMany);
		menuItems.add(mntmLoadMany);

		menuItemExit = new JMenuItem("Exit");
		menuItemExit.setName("exit");
		menuItemExit.setFont(new Font("Calibri", Font.ITALIC, 14));
		menu.add(menuItemExit);
		menuItems.add(menuItemExit);

		frame.setJMenuBar(menuBar);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String menuItemChosen = null;

		if (e.getSource() instanceof JMenuItem) {
			menuItemChosen = ((((JMenuItem) (e.getSource())).getName()));
		} else if (e.getSource() instanceof JButton) {
			menuItemChosen = ((((JButton) (e.getSource())).getName()));
		}

		Object[] options = { "Yes, please", "No way!" };
		int userConfirm = 0;
		switch (menuItemChosen.toUpperCase()) {
		case "SCIENCE":
			try {
				askScienceQuestions();
			} catch (SQLException | IOException e3) {
				e3.printStackTrace();
			}
			break;
		case "HISTORY":
			try {
				askHistoryQuestions();
			} catch (SQLException | IOException e3) {
				e3.printStackTrace();
			}
			break;		
		case "CATEGORY":
			try {
				askQuestionsByCategory();
			} catch (SQLException | IOException e3) {
				e3.printStackTrace();
			}
			break;				
		case "IMPORT":
			try {
				performImport();
			} catch (SQLException | IOException e1) {
				e1.printStackTrace();
			}
			break;
		case "LOADSINGLE":
			try {
				performSingleImport();
			} catch (SQLException | IOException e1) {
				e1.printStackTrace();
			}
			break;
		case "LOADMANY":
			try {
				performManyImport();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;			
		case "EXIT":
			System.exit(0);
			break;
		default:
			System.out.println("");
		}
	}

	public void askScienceQuestions() throws FileNotFoundException, SQLException, IOException {
		ModeratorManager.askQuestionsByCategory("Science");
	}
	
	public void askHistoryQuestions() throws FileNotFoundException, SQLException, IOException {
		ModeratorManager.askQuestionsByCategory("History");
	}
	
	public void askQuestionsByCategory() throws FileNotFoundException, SQLException, IOException {
		ModeratorManager.initialize();
		ModeratorManager.askQuestionsByCategory("prompt");
	}
	
	public void performImport() throws FileNotFoundException, SQLException, IOException {
		LoadPacketsManager.initialize();
		LoadPacketsManager.performImport();
	}
	
	public void performSingleImport() throws FileNotFoundException, SQLException, IOException {
		LoadPacketsManager.initialize();
		LoadPacketsManager.performSingleImport();
	}
	
	public void performManyImport() throws FileNotFoundException, SQLException, IOException {
		LoadPacketsManager.initialize();
		LoadPacketsManager.performManyImport();
	}

	public void showUnderConstructionDialog() {
		String infoMessage = "Feature under construction.";
		String titleBar = "Quiz Bowl";
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
	
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
	}

}
