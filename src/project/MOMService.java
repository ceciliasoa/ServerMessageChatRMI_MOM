package project;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;

import Message.MessageModel;

public class MOMService {

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
	ConnectionFactory connectionFactory;
	Connection connection;
	Session session;
	
	 public void startConnection() throws JMSException {
	        connectionFactory = new ActiveMQConnectionFactory(url);
	        connection = connectionFactory.createConnection();
	        connection.start();
	        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	        
	    }
	 
	 public void stopConnection() throws JMSException {
			try {
				session.close();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new JMSException(e.getMessage());
			}
		}
	
	public void createQueue(String name) throws JMSException {
	    Destination destination = session.createQueue(name);      
	    MessageProducer producer = session.createProducer(destination);
	    producer.close();
	}
	
	public void deleteQueue(String name) throws JMSException {
	      Destination destination = session.createQueue(name);      
	      ((ActiveMQConnection) connection).destroyDestination((ActiveMQDestination) destination);
	}
	
	public List<String> getExistingQueuesNames() throws JMSException {
		Set<ActiveMQQueue> queues = ((ActiveMQConnection) connection).getDestinationSource().getQueues();
		List<String> queueNames = new ArrayList<>();
		queues.forEach(fila -> {
			String nome = fila.getPhysicalName();
			queueNames.add(nome);

		});
		return queueNames;
	}
	
	int getAmountOfPendingMessagesFromQueue(String queueName) throws JMSException {
		ActiveMQQueue queue = (ActiveMQQueue) session.createQueue(queueName);
		QueueBrowser browser = session.createBrowser((ActiveMQQueue) queue);
		Enumeration<?> enumeration = browser.getEnumeration();
		int count = 0;
		while (enumeration.hasMoreElements()) {
			enumeration.nextElement();
			count++;
		}
		return count;
	}
	
	public void sendMessageToQueue(String sender, String recipient, String message) throws JMSException {
         Destination destination = session.createQueue(recipient);

         MessageProducer producer = session.createProducer(destination);
         TextMessage messageText = session.createTextMessage(message);
         messageText.setStringProperty("remetente", sender);
         messageText.setStringProperty("fila", recipient);
         producer.send(messageText);
         producer.close();
	}
	
	public List<MessageModel> getPendingQueueMessage(String queueName) throws JMSException {
		Queue queue = session.createQueue(queueName);
		QueueBrowser browser = session.createBrowser(queue);
		List<MessageModel> messages = new ArrayList<>();

		try {
			Enumeration<?> enumeration = browser.getEnumeration();

			while (enumeration.hasMoreElements()) {
				TextMessage message = (TextMessage) enumeration.nextElement();

				String sender = message.getStringProperty("remetente");
				String receiver = queueName;
				String text = message.getText();

				MessageModel messageObj = new MessageModel(sender, receiver, text);
				messages.add(messageObj);
			}

			return messages;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	

	public List<MessageModel> consumePendingQueueMessage(String queueName) throws JMSException {
		if (queueName == null || queueName.equals("")) {
			return null;
		}

		Destination destination = session.createQueue(queueName);
		MessageConsumer consumer = session.createConsumer(destination);
		List<MessageModel> messages = new ArrayList<>();
		TextMessage message;

		do {
			message = (TextMessage) consumer.receive(10);

			if (message != null) {
				String sender = message.getStringProperty("remetente");
				String text = message.getText();

				MessageModel messageObj = new MessageModel(sender, queueName, text);
				messages.add(messageObj);
			}
		} while (message != null);

		consumer.close();

		return messages;
	}


	    
	
}
