package com.ofss.fc.utilities.deploytool;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class DepToolUtilityGUI {

	private JFrame frame;
	private JTextField textField;
	private JTextArea conSole;

	static String targetFile = null;
	static String deploymentTag = null;
	static int errorCode = 0;
	static int unitsToDeploy = 0; // 1- DB units, 2- Shell units, 0- Both

	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * Create the application.
	 */
	public DepToolUtilityGUI() {
		initialize();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DepToolUtilityGUI window = new DepToolUtilityGUI();					
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Deployment tool- For Jibun Bank Data Migration");
		frame.setBounds(200, 200, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);	

		conSole= new JTextArea();

		//browse field
		textField = new JTextField();
		textField.setBounds(151, 8, 450, 25);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		textField.setEditable(false);

		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select the release sheet");
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setAcceptAllFileFilterUsed(true);
				int returnVal = chooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					targetFile = chooser.getSelectedFile().getAbsolutePath();
					textField.setText(targetFile);
				}
			}
		});
		btnBrowse.setBounds(620, 8, 100, 25);
		frame.getContentPane().add(btnBrowse);


		JLabel lblTargetFolder = new JLabel("Release Sheet   :");
		lblTargetFolder.setBounds(37, 11, 104, 14);
		frame.getContentPane().add(lblTargetFolder);

		JLabel lblUnitsToDeploy = new JLabel("Choose units to deploy   :");
		lblUnitsToDeploy.setBounds(37, 45, 152, 14);
		frame.getContentPane().add(lblUnitsToDeploy);

		JRadioButton rdbtnDbUnits = new JRadioButton("DB Units");
		buttonGroup.add(rdbtnDbUnits);
		rdbtnDbUnits.setBounds(179, 45, 88, 23);
		frame.getContentPane().add(rdbtnDbUnits);

		JRadioButton rdbtnShellUnits = new JRadioButton("Shell Units");
		buttonGroup.add(rdbtnShellUnits);
		rdbtnShellUnits.setBounds(269, 45, 88, 23);
		frame.getContentPane().add(rdbtnShellUnits);

		JRadioButton rdbtnBoth = new JRadioButton("Both");
		buttonGroup.add(rdbtnBoth);
		rdbtnBoth.setBounds(369, 45, 109, 23);
		frame.getContentPane().add(rdbtnBoth);
		rdbtnBoth.setSelected(true);

		JLabel lblConsole = new JLabel("Console :");
		lblConsole.setBounds(250, 100, 100, 14);
		frame.getContentPane().add(lblConsole);

		JButton btnDeploy = new JButton("Deploy");
		btnDeploy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				//1- DB units 2-shell/java units 0-Both DB and shell/java
				unitsToDeploy = rdbtnDbUnits.isSelected()? 1 : (rdbtnShellUnits.isSelected() ? 2 : 0);
				deploymentTag= "Default";		// to put release sheet tag here
				conSole.setText(null);
				try {
					if (targetFile != null) {
						errorCode = DepToolProcessor.startToolProcess(deploymentTag, targetFile, unitsToDeploy);
						if (errorCode == 0) {
							JOptionPane.showMessageDialog(frame, "Deployment Successful!");
						} else {
							JOptionPane.showMessageDialog(frame, "Error while deployment!");
						} 
					} else {
						if (deploymentTag == null || deploymentTag.equals("")) {
							JOptionPane.showMessageDialog(frame, "Deployment tag can't be black.");
						} else if (targetFile == null || targetFile.equals("")) {
							JOptionPane.showMessageDialog(frame, "Please select release sheet.");
						}
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		btnDeploy.setBounds(37, 100, 170, 30);
		frame.getContentPane().add(btnDeploy);

		JButton btnViewLogs = new JButton("View Deployment Logs");
		btnViewLogs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DepToolProcessor.displayDeploymentLog();				
			}
		});
		btnViewLogs.setBounds(37, 150, 170, 30);
		frame.getContentPane().add(btnViewLogs);

		JButton btnExecute = new JButton("Execute");
		btnExecute.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				deploymentTag= "execute";		// to put release sheet tag here
				conSole.setText(null);
				try {
					if (targetFile != null) {
						errorCode = DepToolProcessor.startToolProcess(deploymentTag, targetFile, unitsToDeploy);
						if (errorCode == 0) {
							JOptionPane.showMessageDialog(null, "Execution Successful.");
						}else  JOptionPane.showMessageDialog(null, "Error while Execution.");
					} else {
						if (deploymentTag == null || deploymentTag.equals("")) {
							JOptionPane.showMessageDialog(frame, "Deployment tag can't be blank.");
						} else if (targetFile == null || targetFile.equals("")) {
							JOptionPane.showMessageDialog(frame, "Please select release sheet.");
						}
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		btnExecute.setBounds(37, 200, 170, 30);
		frame.getContentPane().add(btnExecute);

		JButton btnExecuteLogs = new JButton("View Execution log");
		btnExecuteLogs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {					
					DepToolProcessor.displayExecLog();
				}catch(Exception exp) {
					exp.printStackTrace();
				}

			}
		});
		btnExecuteLogs.setBounds(37, 250, 170, 30);
		frame.getContentPane().add(btnExecuteLogs);

		//View property file button
		JButton btnProperties = new JButton("View PropertiesFile");
		btnProperties.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {					
					DepToolProcessor.displayPropertiesFile();
				}catch(Exception exp) {
					exp.printStackTrace();
				}

			}
		});
		btnProperties.setBounds(37, 300, 170, 30);
		frame.getContentPane().add(btnProperties);

		//Clear Console button
		JButton btnClearConsole = new JButton("Clear Console");
		btnClearConsole.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {					
					conSole.setText(null);
				}catch(Exception exp) {
					exp.printStackTrace();
				}
			}
		});
		btnClearConsole.setBounds(37, 400, 170, 30);
		frame.getContentPane().add(btnClearConsole);


		//console logs
		conSole.setEditable(false);		
		JScrollPane scroll = new JScrollPane(conSole, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(250, 120, 500, 330);
		frame.getContentPane().add(scroll);

		JTextAreaOutputStream out= new JTextAreaOutputStream(conSole);
		try {
			System.setOut(new PrintStream(out));
			System.setErr(new PrintStream(out));
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		//Clear Console button
		JButton btnHighlight = new JButton("Highlight Errors");
		btnHighlight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {					
					highLightErros(conSole, "Error");
				}catch(Exception exp) {
					exp.printStackTrace();
				}
			}
		});
		btnHighlight.setBounds(37, 350, 170, 30);
		frame.getContentPane().add(btnHighlight);
	}

	Highlighter.HighlightPainter myhighlighter = new CustomHighlighter(Color.orange);
	public void highLightErros(JTextArea console, String text) {

		try {
			Highlighter h=console.getHighlighter();
			h.removeAllHighlights();
			int pos=0;					

			while((pos=console.getText().indexOf(text,pos))>=0) {
				h.addHighlight(pos, pos+text.length(), myhighlighter);
				pos+=text.length();
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

	}	

	private class CustomHighlighter extends DefaultHighlighter.DefaultHighlightPainter{
		public CustomHighlighter(Color color) {
			super(color);
		}
	}}