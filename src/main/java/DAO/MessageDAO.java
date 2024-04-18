package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public Message createMessage(Message message) {
        String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, message.getPosted_by());
            preparedStatement.setString(2, message.getMessage_text());
            preparedStatement.setLong(3, message.getTime_posted_epoch());
    
            int rowsInserted = preparedStatement.executeUpdate();
            System.out.println("Rows inserted: " + rowsInserted);
            if (rowsInserted > 0) {
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    int messageId = rs.getInt(1); 
                    message.setMessage_id(messageId);
                }
                return message;
            }
        } catch (SQLException e) {
            System.out.println("SQL exception: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    


    public int getNextMessageId() {
        String sql = "SELECT MAX(message_id) AS max_id FROM message";
        try {
            Connection connection = ConnectionUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                int maxId = resultSet.getInt("max_id");
           
                return maxId + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
      
        return 1;
    }

    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message";
        try {
            Connection connection = ConnectionUtil.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int messageId = resultSet.getInt("message_id");
                int postedBy = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long timePostedEpoch = resultSet.getLong("time_posted_epoch");
                Message message = new Message(messageId, postedBy, messageText, timePostedEpoch);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    public Message getMessageById(int messageId) {
        String sql = "SELECT * FROM message WHERE message_id = ?";
        try {
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int postedBy = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long timePostedEpoch = resultSet.getLong("time_posted_epoch");

                return new Message(messageId, postedBy, messageText, timePostedEpoch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteMessageById(int messageId) {
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM message WHERE message_id = ?")) {
            preparedStatement.setInt(1, messageId);
            preparedStatement.executeUpdate();
         
        } catch (SQLException e) {
            e.printStackTrace();
          
        }
    }
    
    
    
    public boolean updateMessage(int messageId, Message newMessage) {
        String sqlUpdate = "UPDATE message SET message_text = ? WHERE message_id = ?";
    
        try (Connection connection = ConnectionUtil.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(sqlUpdate)) {
    
            updateStatement.setString(1, newMessage.getMessage_text());
            updateStatement.setInt(2, messageId);
    
            int rowsAffected = updateStatement.executeUpdate();
            return rowsAffected > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }
    

    public List<Message> getMessagesByUser(int accountId) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        try {
            Connection connection = ConnectionUtil.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, accountId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int messageId = resultSet.getInt("message_id");
                int postedBy = resultSet.getInt("posted_by");
                String messageText = resultSet.getString("message_text");
                long timePostedEpoch = resultSet.getLong("time_posted_epoch");
                Message message = new Message(messageId, postedBy, messageText, timePostedEpoch);
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
