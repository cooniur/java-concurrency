import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import java.awt.Font;
import java.awt.SystemColor;
import java.awt.BorderLayout;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTextField;

public class MainWindow implements ProducerEventHandler, ConsumerEventHandler {

	private JFrame	_frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// ProductBuffer pb = new ProductBuffer();
		// Producer p = new Producer(pb);
		// Consumer c = new Consumer(pb);
		// p.setEventHandler(handler);
		// c.setEventHandle(handler);
		// Thread producer_t = new Thread(p);
		// Thread consumer_t = new Thread(c);
		// producer_t.start();
		// consumer_t.start();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window._frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	ProductBuffer	_product;
	Producer		_producer;
	Consumer		_consumer;
	Thread			_producer_t	= null;
	Thread			_consumer_t	= null;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();

		_product = new ProductBuffer();
		_producer = new Producer(_product, 10);
		_consumer = new Consumer(_product);
		_producer.setEventHandler(this);
		_consumer.setEventHandle(this);
	}

	JPanel				pnlProducer, pnlConsumer;
	JLabel				lblProducerValue, lblConsumerValue;
	JButton				btnStart;
	JButton				btnStop;
	private JTextField	txtMax;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		_frame = new JFrame();
		_frame.setTitle("Producer-Consumer");
		_frame.setBounds(100, 100, 297, 218);
		_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		_frame.getContentPane().setLayout(null);

		pnlProducer = new JPanel();
		pnlProducer.setForeground(SystemColor.window);
		pnlProducer.setBounds(6, 6, 124, 138);
		_frame.getContentPane().add(pnlProducer);
		pnlProducer.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel lblProducer = new JLabel("Producer");
		lblProducer.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		lblProducer.setHorizontalAlignment(SwingConstants.CENTER);
		pnlProducer.add(lblProducer);

		lblProducerValue = new JLabel("-");
		lblProducerValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblProducerValue.setFont(new Font("Lucida Grande", Font.PLAIN, 32));
		pnlProducer.add(lblProducerValue);

		pnlConsumer = new JPanel();
		pnlConsumer.setForeground(SystemColor.window);
		pnlConsumer.setBounds(167, 6, 124, 138);
		_frame.getContentPane().add(pnlConsumer);
		pnlConsumer.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel lblConsumer = new JLabel("Consumer");
		lblConsumer.setHorizontalAlignment(SwingConstants.CENTER);
		lblConsumer.setFont(new Font("Lucida Grande", Font.PLAIN, 20));
		pnlConsumer.add(lblConsumer);

		lblConsumerValue = new JLabel("-");
		lblConsumerValue.setHorizontalAlignment(SwingConstants.CENTER);
		lblConsumerValue.setFont(new Font("Lucida Grande", Font.PLAIN, 32));
		pnlConsumer.add(lblConsumerValue);

		btnStart = new JButton("Start");
		btnStart.setBounds(123, 151, 86, 29);
		_frame.getContentPane().add(btnStart);
		btnStart.addActionListener(new ActionListener() {

			@Override 
			public void actionPerformed(ActionEvent arg0) {
				_producer_t = new Thread(_producer);
				_consumer_t = new Thread(_consumer);
				_producer.setMaxProductNum(Integer.parseInt(txtMax.getText()));
				_producer_t.start();
				_consumer_t.start();
				btnStart.setEnabled(false);
				btnStop.setEnabled(true);
				lblProducerValue.setText("-");
				lblConsumerValue.setText("-");
			}
		});

		JLabel lblMax = new JLabel("Max:");
		lblMax.setBounds(6, 156, 61, 16);
		_frame.getContentPane().add(lblMax);

		txtMax = new JTextField();
		lblMax.setLabelFor(txtMax);
		txtMax.setText("10");
		txtMax.setBounds(42, 150, 61, 28);
		_frame.getContentPane().add(txtMax);
		txtMax.setColumns(10);

		btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnStop.setBounds(205, 151, 86, 29);
		_frame.getContentPane().add(btnStop);
		btnStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				_producer.stop();
				_producer_t.interrupt();
				_consumer.stop();
				_consumer_t.interrupt();
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
			}
		});
	}

	@Override
	public void onConsumerDisplay(int value) {
		final JLabel lbl = this.lblConsumerValue;
		final int v = (value + 1) * 2;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				lbl.setText(String.valueOf(v));
				lbl.validate();
				lbl.repaint();
			}
		});
	}

	@Override
	public void onConsumerInterrupted(InterruptedException e) {
		final JButton btnStart = this.btnStart;
		final JButton btnStop = this.btnStop;
		final InterruptedException ex = e;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
				ex.printStackTrace(System.err);
			}
		});
	}

	@Override
	public void onProducerDisplay(int value) {
		final JLabel lbl = this.lblProducerValue;
		final int v = (value + 1);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				lbl.setText(String.valueOf(v));
				lbl.validate();
				lbl.repaint();
			}
		});
	}

	@Override
	public void onProducerInterrupted(InterruptedException e) {
		final JButton btnStart = this.btnStart;
		final JButton btnStop = this.btnStop;
		final InterruptedException ex = e;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
				ex.printStackTrace(System.err);
			}
		});
	}

	@Override
	public void onConsumerFinished() {
		final JButton btnStart = this.btnStart;
		final JButton btnStop = this.btnStop;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
			}
		});
	}

	@Override
	public void onProducerFinished() {
		final JButton btnStart = this.btnStart;
		final JButton btnStop = this.btnStop;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
			}
		});
	}
}
