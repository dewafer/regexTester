package wyq.tool.regexTester;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import wyq.appengine.component.file.TextFile;
import wyq.swing.ui.util.CountDownKeyTypedEventAdapter;
import wyq.swing.ui.util.LongRunDialog;

public class RegexTester {

	private JFrame frame;
	private JTextField textFieldRegex;
	private JTextPane textPane;
	private JTextPane textPaneOutput;
	private static String NEW_LINE = System.getProperty("line.separator");
	private JLabel lblStatusLabel;
	private JCheckBoxMenuItem chckbxmntmShowMatches;
	/**
	 * @wbp.nonvisual location=484,19
	 */
	private final JFileChooser fileChooser = new JFileChooser();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager
							.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					RegexTester window = new RegexTester();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RegexTester() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		fileChooser.setApproveButtonText("Open");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		JMenuItem mntmNewMenuItem = new JMenuItem("Open file");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int result = fileChooser.showOpenDialog(frame);

				if (JFileChooser.APPROVE_OPTION == result) {

					LongRunDialog.run(frame, new Runnable() {

						@Override
						public void run() {

							String content = null;
							try {
								File selectedFile = fileChooser
										.getSelectedFile();
								TextFile file = new TextFile(selectedFile
										.getAbsolutePath());
								content = file.readAll();
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (content != null) {
								textPane.setText(content);
							}

						}
					});
				}
			}
		});
		mnFile.add(mntmNewMenuItem);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);
		mnFile.add(mntmExit);

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		chckbxmntmShowMatches = new JCheckBoxMenuItem("Show matches");
		chckbxmntmShowMatches.setSelected(true);
		mnSettings.add(chckbxmntmShowMatches);

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblInputRegex = new JLabel("Input Regex:");
		panel.add(lblInputRegex, BorderLayout.NORTH);

		textFieldRegex = new JTextField();
		textFieldRegex.addKeyListener(new CountDownKeyTypedEventAdapter(
				new CountDownKeyTypedEventAdapter.KeyTypedEvent() {

					@Override
					public void keyTyped(KeyEvent[] arg0) {
						LongRunDialog.run(frame, new Runnable() {

							@Override
							public void run() {
								String inputText = textPane.getText();
								String strPattern = textFieldRegex.getText();
								if (inputText != null && strPattern != null
										&& inputText.length() > 0
										&& strPattern.length() > 0) {
									if (NEW_LINE.length() > 1) {
										inputText = inputText.replaceAll(
												NEW_LINE, "\n");
									}
									Pattern pattern = null;
									try {
										pattern = Pattern.compile(strPattern);
										Matcher matcher = pattern
												.matcher(inputText);
										textPaneOutput.setText(inputText);
										StyledDocument document = textPaneOutput
												.getStyledDocument();
										document.setCharacterAttributes(0,
												document.getLength(),
												document.getStyle("regular"),
												true);
										while (matcher.find()) {
											int start = matcher.start();
											int end = matcher.end();
											document.setCharacterAttributes(
													start, end - start,
													document.getStyle("match"),
													true);
										}
										if (chckbxmntmShowMatches.isSelected()) {
											lblStatusLabel.setText("matches: "
													+ matcher.matches());
										} else {
											lblStatusLabel.setText("");
										}
									} catch (Exception e) {
										lblStatusLabel.setText(e.getMessage());
									}
								} else {
									lblStatusLabel.setText("");
									textPaneOutput.setText(inputText);
									StyledDocument document = textPaneOutput
											.getStyledDocument();
									document.setCharacterAttributes(0,
											document.getLength(),
											document.getStyle("regular"), true);
								}
							}
						});
					}
				}));
		// textFieldRegex.addKeyListener(new KeyAdapter() {
		//
		// @Override
		// public void keyTyped(KeyEvent arg0) {
		// System.out.println(arg0);
		// System.out.println("keyChar:" + arg0.getKeyChar());
		// System.out.println("isAction:"+arg0.isActionKey());
		// String inputText = textPane.getText();
		// String strPattern = textFieldRegex.getText();
		// if (inputText != null && strPattern != null
		// && inputText.length() > 0 && strPattern.length() > 0) {
		// if (NEW_LINE.length() > 1) {
		// inputText = inputText.replaceAll(NEW_LINE, "\n");
		// }
		// Pattern pattern = null;
		// try {
		// pattern = Pattern.compile(strPattern);
		// Matcher matcher = pattern.matcher(inputText);
		// textPaneOutput.setText(inputText);
		// StyledDocument document = textPaneOutput
		// .getStyledDocument();
		// document.setCharacterAttributes(0,
		// document.getLength(),
		// document.getStyle("regular"), true);
		// while (matcher.find()) {
		// int start = matcher.start();
		// int end = matcher.end();
		// document.setCharacterAttributes(start, end - start,
		// document.getStyle("match"), true);
		// }
		// if (chckbxmntmShowMatches.isSelected()) {
		// lblStatusLabel.setText("matches: "
		// + matcher.matches());
		// } else {
		// lblStatusLabel.setText("");
		// }
		// } catch (Exception e) {
		// lblStatusLabel.setText(e.getMessage());
		// }
		// } else {
		// lblStatusLabel.setText("");
		// textPaneOutput.setText(inputText);
		// StyledDocument document = textPaneOutput
		// .getStyledDocument();
		// document.setCharacterAttributes(0, document.getLength(),
		// document.getStyle("regular"), true);
		// }
		// }
		// });
		panel.add(textFieldRegex, BorderLayout.CENTER);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setVgap(0);
		flowLayout.setHgap(0);
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel.add(panel_1, BorderLayout.SOUTH);

		lblStatusLabel = new JLabel();
		panel_1.add(lblStatusLabel);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane.setRightComponent(scrollPane_1);

		textPaneOutput = new JTextPane();
		textPaneOutput.setEditable(false);
		scrollPane_1.setViewportView(textPaneOutput);

		addStylesToDocument(textPaneOutput.getStyledDocument());
	}

	protected void addStylesToDocument(StyledDocument doc) {
		// Initialize some styles.
		Style def = StyleContext.getDefaultStyleContext().getStyle(
				StyleContext.DEFAULT_STYLE);
		Style regular = doc.addStyle("regular", def);
		StyleConstants.setFontFamily(def, "SansSerif");

		Style s = doc.addStyle("match", regular);
		StyleConstants.setBold(s, true);
		StyleConstants.setForeground(s, Color.red);
		StyleConstants.setBackground(s, Color.yellow);

		// Style s = doc.addStyle("italic", regular);
		// StyleConstants.setItalic(s, true);
		// s = doc.addStyle("bold", regular);
		// StyleConstants.setBold(s, true);
		// s = doc.addStyle("small", regular);
		// StyleConstants.setFontSize(s, 10);
		// s = doc.addStyle("large", regular);
		// StyleConstants.setFontSize(s, 16);
		// s = doc.addStyle("icon", regular);
		// StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

	}

}
