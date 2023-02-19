package mainmodule;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

public class LoadPacketsManager {
	public static int questionsCounter = 0;
	public static int correctAnswersCounter = 0;
	public static int wrongAnswersCounter = 0;
	public static int skippedCounter = 0;
	public static ManageImports ma;
	public static JFrame frame;
	
	public static void initialize() {
		ma = MainMenu.ma;
		frame = MainMenu.frame;
	}
	
	public static void performImport() throws FileNotFoundException, SQLException, IOException {
		String infoMessage = "Please, select a question bank file.";
		String titleBar = "Quiz Bowl";
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Select a JSON file");
		jfc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
		jfc.addChoosableFileFilter(filter);
		int returnValue = jfc.showOpenDialog(null);
		File selectedFile = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = jfc.getSelectedFile();
		}
		//System.out.println(selectedFile);
		if (selectedFile != null) {
//			ManageImports ma = new ManageImports();
			ma.initialize();
			ma.importQuestions(selectedFile);
			ma.setCurrentPacket(selectedFile);
			ma.findCategories();
			ma.fillCategories();
			ma.findCategoriesWithCount();
		}		
	}
	
	public static void performSingleImport() throws FileNotFoundException, SQLException, IOException {
		String infoMessage = "Please, select a question bank file.";
		String titleBar = "Quiz Bowl";
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setDialogTitle("Select a JSON file");
		jfc.setAcceptAllFileFilterUsed(false);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JSON Files", "json");
		jfc.addChoosableFileFilter(filter);
		int returnValue = jfc.showOpenDialog(null);
		File selectedFile = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFile = jfc.getSelectedFile();
		}
		//System.out.println(selectedFile);
		if (selectedFile != null) {
//			ManageImports ma = new ManageImports();
			ma.initialize();
			ma.importQuestions(selectedFile);
			ma.setCurrentPacket(selectedFile);
			ma.findCategories();
			ma.fillCategories();
			ma.findCategoriesWithCount();
		}		
	}
	
	public static void performManyImport() throws FileNotFoundException, SQLException, IOException {
		String infoMessage = "Please, select a folder containing questions bank files.";
		String titleBar = "Quiz Bowl";
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setDialogTitle("Select a Folder");
		int returnValue = jfc.showOpenDialog(null);
		File selectedFolder = null;
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			selectedFolder = jfc.getSelectedFile();
		}
	    Set<String> fileSet = new HashSet<>();
	    Set<String> fileSetPackets = new HashSet<>();

	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(selectedFolder.toString()))) {
	        for (Path path : stream) {
	            if (!Files.isDirectory(path)) {
	                fileSet.add(path.getFileName()
	                    .toString());
	            }
	        }
	    }
		for (String entry : fileSet) {
			if (entry.contains(".json")) {
				fileSetPackets.add(entry);
			}
		}

		ma.initialize();
		for (String entry : fileSetPackets) {
			String selectedFile = selectedFolder + "\\" + entry;
			File file = new File(selectedFile);
			ma.importQuestionsAppend(file);
		}
		ma.findCategories();
		ma.fillCategories();
		ma.findCategoriesWithCount();
	}
	
}
