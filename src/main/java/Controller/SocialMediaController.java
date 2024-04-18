package Controller;

import DAO.AccountDAO;
import DAO.MessageDAO;

import Model.Account;
import Model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import com.fasterxml.jackson.core.JsonProcessingException;




import java.util.List;

public class SocialMediaController {

    private final AccountDAO accountDAO;
    private final MessageDAO messageDAO;

 
    public SocialMediaController() {
        this.accountDAO = new AccountDAO();
        this.messageDAO = new MessageDAO();
    }

    
    public SocialMediaController(AccountDAO accountDAO, MessageDAO messageDAO) {
        this.accountDAO = accountDAO;
        this.messageDAO = messageDAO;
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();

        // Endpoints
        app.post("/register", this::register);
        app.post("/login", this::login);
        app.post("/messages", this::createMessage);
        app.get("/messages", this::getAllMessages);
        app.get("/messages/{message_id}", this::getMessageById); 
        app.delete("/messages/{message_id}", this::deleteMessage);
        app.patch("/messages/{message_id}", this::updateMessage);
        app.get("/accounts/{account_id}/messages", this::getMessagesByUser);

        return app;
    }

    private void register(Context context) {
        try {
            Account account = new ObjectMapper().readValue(context.body(), Account.class);

            // Validate username and password
            if (account.getUsername() == null || account.getUsername().isEmpty() ||
                    account.getPassword() == null || account.getPassword().length() < 4) {
                context.status(400).json(""); 
                return;
            }

            Account registeredAccount = accountDAO.register(account);
            if (registeredAccount != null) {
                context.json(registeredAccount);
            } else {
                context.status(400).json(""); 
            }
        } catch (Exception e) {
            context.status(400).json(""); 
        }
    }

    private void login(Context context) {
        try {
            Account account = new ObjectMapper().readValue(context.body(), Account.class);
            Account loggedInAccount = accountDAO.login(account.getUsername(), account.getPassword());
            if (loggedInAccount != null) {
                context.json(loggedInAccount);
            } else {
                context.status(401).json("");
            }
        } catch (Exception e) {
            context.status(400).json("");
        }
    }
    private void createMessage(Context context) {
        try {
            System.out.println("Received request to create message");
            
        
            System.out.println("Request body: " + context.body());
    
          
            Message message = new ObjectMapper().readValue(context.body(), Message.class);
            System.out.println("Deserialized message object: " + message);
    
         
            if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
                System.out.println("Message text is blank");
                context.status(400).json("");
                return;
            } else if (message.getMessage_text().length() > 255) {
                System.out.println("Message text exceeds character limit");
                context.status(400).json(""); 
                return;
            }
    
         
            Account user = accountDAO.getAccountById(message.getPosted_by());
            System.out.println("Retrieved user from database: " + user);
            if (user == null) {
                System.out.println("User does not exist");
                context.status(400).json(""); 
                return;
            }
    
           
            Message createdMessage = messageDAO.createMessage(message);
            System.out.println("Created message: " + createdMessage);
            if (createdMessage != null) {
                context.json(createdMessage).status(200);
            } else {
                System.out.println("Failed to create message");
                context.status(400).json(""); 
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error processing JSON: " + e.getMessage());
            context.status(400).json(""); 
        } catch (Exception e) {
            System.out.println("Internal server error");
            context.status(500).json(""); 
        }
    }
    
    
    
    


    
    private void getAllMessages(Context context) {
        List<Message> messages = messageDAO.getAllMessages();
        context.json(messages);
    }

  private void getMessageById(Context context) {
    try {
        int messageId = Integer.parseInt(context.pathParam("message_id"));
        Message message = messageDAO.getMessageById(messageId);

        if (message != null) {
            context.json(message);  
        } else {
            context.status(200).result("");
        }
    } catch (NumberFormatException e) {
        context.status(400).json(new ErrorResponse("Invalid message ID")); 
    }
}


public void updateMessage(Context ctx) {
    try {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        String body = ctx.body();
        String messageText = extractMessageText(body);

        if (messageText == null) {
            ctx.status(400).result("");
            return;
        }

        if (messageText.isEmpty()) {
            ctx.status(400).result("");
            return;
        }

        if (messageText.length() > 255) {
            ctx.status(400).result("");
            return;
        }

        Message existingMessage = messageDAO.getMessageById(messageId);
        if (existingMessage == null) {
            ctx.status(400).result("");
            return;
        }

        existingMessage.setMessage_text(messageText);
        boolean updateSuccess = messageDAO.updateMessage(messageId, existingMessage);
        if (updateSuccess) {
            ctx.status(200).json(existingMessage);
        } else {
            ctx.status(500).result("");
        }
    } catch (NumberFormatException e) {
        ctx.status(400).result("");
    } catch (Exception e) {
        ctx.status(500).result("" + e.getMessage());
    }
}


private String extractMessageText(String json) {
    String key = "\"message_text\":";
    int startIndex = json.indexOf(key);
    if (startIndex == -1) {
        return null;
    }
    startIndex += key.length();
    int startQuote = json.indexOf('"', startIndex);
    int endQuote = json.indexOf('"', startQuote + 1);
    if (startQuote == -1 || endQuote == -1) {
        return null;
    }
    return json.substring(startQuote + 1, endQuote);
}
    

    private void deleteMessage(Context context) {
        try {
            int messageId = Integer.parseInt(context.pathParam("message_id"));
            Message messageToDelete = messageDAO.getMessageById(messageId);
    
            if (messageToDelete != null) {
        
                messageDAO.deleteMessageById(messageId);
             
                context.json(messageToDelete);
            } else {
              
                context.status(200).result(""); 
            }
        } catch (NumberFormatException e) {
            context.status(400).json("");
        } catch (Exception e) {
            context.status(500).json("");
        }
    }

    
    


    private void getMessagesByUser(Context context) {
        try {
            int accountId = Integer.parseInt(context.pathParam("account_id"));
            List<Message> messages = messageDAO.getMessagesByUser(accountId);
            context.json(messages);
        } catch (NumberFormatException e) {
            context.status(400).json("");
        } catch (Exception e) {
            context.status(400).json("");
        }
    }

    public class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
