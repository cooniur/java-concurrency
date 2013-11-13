import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class MainFrame implements ConsoleEventHandler, ProcessEventHandler {

	private JFrame	_frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window._frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	Console							_console;
	DefaultListModel				_userProcessListModel;
	DefaultListModel				_sysProcessListModel;
	private Map<String, Process>	_processDict	= new HashMap<String, Process>();
	private Map<String, Thread>		_threadDict		= new HashMap<String, Thread>();
	int								_processId		= 0;

	/**
	 * Create the application.
	 */
	public MainFrame() {
		this._userProcessListModel = new DefaultListModel();
		this._sysProcessListModel = new DefaultListModel();
		this._console = new Console();
		_console.setAccess(Console.Access.ALLOW);
		_console.setHandler(this);

		initialize();
	}

	JCheckBox	chkAccess;
	JTextArea	txtConsole;
	JList		lstUser;
	JList		lstSystem;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		_frame = new JFrame();
		_frame.setBounds(100, 100, 422, 442);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.getContentPane().setLayout(null);

		JLabel lblUserProcesses = new JLabel("User Processes");
		lblUserProcesses.setBounds(42, 6, 107, 16);
		_frame.getContentPane().add(lblUserProcesses);

		JLabel lblSystemProcesses = new JLabel("System Processes");
		lblSystemProcesses.setBounds(232, 6, 143, 16);
		_frame.getContentPane().add(lblSystemProcesses);

		JLabel lblConsole = new JLabel("Console");
		lblConsole.setBounds(6, 191, 107, 16);
		_frame.getContentPane().add(lblConsole);

		txtConsole = new JTextArea();
		txtConsole.setEditable(false);
		txtConsole.setBounds(16, 301, 385, 128);
		JScrollPane scpConsole = new JScrollPane(txtConsole);
		scpConsole.setLocation(6, 219);
		scpConsole.setSize(395, 182);
		scpConsole
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		_frame.getContentPane().add(scpConsole);

		JButton btnUserAdd = new JButton("Add");
		btnUserAdd.setBounds(42, 29, 71, 29);
		_frame.getContentPane().add(btnUserAdd);
		btnUserAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_createProcess(Process.Type.USER);
			}
		});

		JButton btnUserDel = new JButton("Del");
		btnUserDel.setBounds(108, 29, 71, 29);
		_frame.getContentPane().add(btnUserDel);
		btnUserDel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_delFromList(_userProcessListModel, lstUser);
			}
		});

		JButton btnSystemAdd = new JButton("Add");
		btnSystemAdd.setBounds(232, 29, 71, 29);
		_frame.getContentPane().add(btnSystemAdd);
		btnSystemAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_createProcess(Process.Type.SYSTEM);
			}
		});

		JButton btnSystemDel = new JButton("Del");
		btnSystemDel.setBounds(298, 29, 71, 29);
		_frame.getContentPane().add(btnSystemDel);
		btnSystemDel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_delFromList(_sysProcessListModel, lstSystem);
			}
		});

		chkAccess = new JCheckBox("Allow User to access console");
		chkAccess.setSelected(true);
		chkAccess.setBounds(176, 191, 225, 23);
		_frame.getContentPane().add(chkAccess);

		JScrollPane scpUser = new JScrollPane();
		scpUser.setBounds(42, 57, 143, 122);
		_frame.getContentPane().add(scpUser);

		lstUser = new JList(this._userProcessListModel);
		scpUser.setViewportView(lstUser);

		JScrollPane scpSystem = new JScrollPane();
		scpSystem.setBounds(232, 57, 143, 122);
		_frame.getContentPane().add(scpSystem);

		lstSystem = new JList(this._sysProcessListModel);
		scpSystem.setViewportView(lstSystem);
		chkAccess.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getSource() == chkAccess) {
					if (chkAccess.isSelected()) {
						_console.setAccess(Console.Access.ALLOW);
					} else {
						_console.setAccess(Console.Access.DENY);
					}
				}
			}
		});
	}

	private void _createProcess(Process.Type type) {
		String name;
		DefaultListModel m;
		Process p;
		int cur_num = this._processId++;
		if (type == Process.Type.USER) {
			name = String.format("User%d", cur_num);
			m = this._userProcessListModel;
			p = new UserProcess(name, _console);
		} else {
			name = String.format("System%d", cur_num);
			m = this._sysProcessListModel;
			p = new SystemProcess(name, _console);
		}

		p.setHandler(this);
		_processDict.put(name, p);
		this._addToList(m, p);

		Thread t = new Thread(p);
		_threadDict.put(name, t);
		t.start();
	}

	private void _addToList(DefaultListModel m, Process p) {
		m.addElement(p);
	}

	private void _delFromList(DefaultListModel m, JList lst) {
		Object[] objList = lst.getSelectedValues();
		for (Object o : objList) {
			Process p = (Process) o;
			Thread t = this._threadDict.get(p.getName());
			if (t != null) {
				p.stop();
				t.interrupt();
			}
			m.removeElement(o);
			this._threadDict.remove(p.getName());
			this._processDict.remove(p.getName());
		}
	}

	@Override
	public void onInterrupted(Process sender, InterruptedException e) {
		final JTextArea txt = this.txtConsole;
		final String s = String.format("%s interrupted.\n", sender.getName());
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				txt.append(s);
				txt.validate();
				txt.repaint();
			}
		});
	}

	@Override
	public void onFinished(Process sender) {
		final JTextArea txt = this.txtConsole;
		final String s = String.format("%s finished.\n", sender.getName());
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				txt.append(s);
				txt.validate();
				txt.repaint();
			}
		});
	}

	@Override
	public void onWrite(Console sender, String str) {
		final JTextArea txt = this.txtConsole;
		final String s = str;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				txt.append(s);
				txt.validate();
				txt.repaint();
			}
		});
	}
}
