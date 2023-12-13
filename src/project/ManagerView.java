package project;

import java.awt.Color;
import java.awt.EventQueue;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;


public class ManagerView {
	
	private ManagerController controller = new ManagerController();
	DefaultTableModel queueTableModel = new DefaultTableModel();
    private JFrame frame;
	private JTable queueTable;
	private JPanel queue;	

	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void startManager() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					initialize();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * @wbp.parser.entryPoint
	 */
	public ManagerView() throws RemoteException {
		try {
            setupManagerController();
        } catch (JMSException e) {
            e.printStackTrace();
        }
		
	}
	
	void setupManagerController() throws JMSException, RemoteException {
//		controller = new ManagerController();
		controller.initialize();

	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 621, 428);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		updateQueueTable();
		
		// ------------ SETUP QUEUE  -------------
		queue = new JPanel();
		queue.setBounds(0, 0, 600, 359);
		frame.getContentPane().add(queue);
		queue.setLayout(null);
		
		
		JButton removeQueue = new JButton("remover fila");
		removeQueue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeQueueItem();
			}
		});
		removeQueue.setBounds(447, 49, 153, 29);
		queue.add(removeQueue);
		
		queueTable = new JTable();
		queueTable.setBorder(null);
		queueTable.setBackground(Color.WHITE);
		queueTable.setCellSelectionEnabled(true);
		queueTable.setDefaultEditor(Object.class, null);
		queueTable.setBounds(21, 39, 420, 287);
		queue.add(queueTable);
		
		JLabel lblNewLabel = new JLabel("Filas");
		lblNewLabel.setFont(new Font("PT Serif", Font.BOLD, 20));
		lblNewLabel.setBounds(179, 16, 52, 16);
		queue.add(lblNewLabel);
		
				
		startPeriodicUpdateTableTask();
	
	}
	
	private void updateQueueTable() {
		queueTableModel.setRowCount(0);
		if (queueTableModel.getColumnCount() == 0) {
		    queueTableModel.addColumn("Nome da Fila");
		    queueTableModel.addColumn("Mensagens Pendentes");
		}

		for (String queueName : controller.getUserNames()) {
		    int pendingMessages = controller.getUserMessageAmount(queueName);

		    queueTableModel.addRow(new Object[]{queueName, pendingMessages});
		}
		
		if (this.queueTable != null) {

			queueTable.setModel(queueTableModel);	
		}
	}
	
	
	
	private void removeQueueItem() {
		int selectedRow = queueTable.getSelectedRow();
		int selectedColumn = queueTable.getSelectedColumn();

		if (selectedRow >= 0 && selectedColumn >= 0) {
		    Object selectedValue = queueTable.getValueAt(selectedRow, 0);
		    String queueName = selectedValue.toString();
			controller.deleteUser(queueName);
			updateQueueTable();
		}
	}
	 
//	 background
	 public void startPeriodicUpdateTableTask() {
		    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		    
		    executor.scheduleAtFixedRate(() -> {
		        try {
		            updateTableInBackground();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }, 0, 5, TimeUnit.SECONDS); 
	}

	private void updateTableInBackground() {
		    SwingUtilities.invokeLater(() -> {
		        try {
		            updateQueueTable(); 
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    });
	}	

}