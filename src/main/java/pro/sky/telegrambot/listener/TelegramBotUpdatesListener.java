package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.sun.istack.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.TelegramBotRepository;


import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private final TelegramBotRepository telegramBotRepository;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");


    public TelegramBotUpdatesListener(TelegramBotRepository telegramBotRepository) {
        this.telegramBotRepository = telegramBotRepository;
    }


    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(@NotNull List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (update.message() != null && update.message().text() != null) {
                Message messageText = update.message();
                String text = messageText.text();


                if ("/info".equals(text)) {
                    sendInfoMessage(messageText.chat().id());
                }
                else if ("/start".equals(text)){
                   sendWelcomeMessage(messageText.chat().id());
                }
                else if (text.startsWith("/remind")) {
                    createNotificationTask(text, messageText.chat().id());
                }else if ("/list".equals(text)) {
                    listRemainingNotifications(messageText.chat().id());
                }

                else {
                    requestNotRecognized(messageText.chat().id());
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
    private void sendWelcomeMessage(Long chatId) {
        logger.info("Processing update: Sending welcome message");
        String welcomeMessage = "Welcome to our bot!" + "Enter /info to help";
        SendMessage request = new SendMessage(chatId, welcomeMessage);
        telegramBot.execute(request);
    }
    private void sendInfoMessage(Long chatId) {
        logger.info("Processing: Sending info message");
        String infoMessage = "To create Notification enter /remind dd.MM.yyyy HH:mm message." +
                "To view the remaining Notification enter /list.";
        SendMessage request = new SendMessage(chatId, infoMessage);
        telegramBot.execute(request);
    }
    private void requestNotRecognized(Long chatId) {
        logger.info("Processing: Request not recognized");
        String notRecognizedMessage = "This is not a recognized message.Refer to /info section.";
        SendMessage request = new SendMessage(chatId, notRecognizedMessage);
        telegramBot.execute(request);
    }
    private void sendNotificationTask(Long chatId) {
        logger.info("Processing: Sending notification task");
        String notificationMessage = "Notification accepted. ";
        SendMessage request = new SendMessage(chatId, notificationMessage);
        telegramBot.execute(request);
    }

    public void createNotificationTask(String text, Long chatId) {
        logger.info("Processing: Create notification task");
        String[] parts = text.split(" ", 3);
        if (parts.length < 3) {
            requestNotRecognized(chatId);
            return;
        }

        String dateTimeStr = parts[1] + " " + parts[2];
        try {
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, formatter);
            String message = parts[3];

            NotificationTask task = new NotificationTask();
            task.setScheduledTime(dateTime);
            task.setMessage(message);
            task.setRecipient(chatId.toString());
            task.setSent(false);

            telegramBotRepository.save(task);
            sendNotificationTask(chatId);

        } catch (DateTimeParseException e) {
            requestNotRecognized(chatId);
        }
    }
    private void listRemainingNotifications(Long chatId) {
        List<NotificationTask> tasks = telegramBotRepository.findByIsSentFalse();
        if (tasks.isEmpty()) {
            telegramBot.execute(new SendMessage(chatId, "No remaining notifications."));
        } else {
            StringBuilder response = new StringBuilder("Remaining notifications:\n");
            tasks.forEach(task -> {
                response.append(formatter.format(task.getScheduledTime()))
                        .append(" - ")
                        .append(task.getMessage())
                        .append("\n");
            });
            telegramBot.execute(new SendMessage(chatId, response.toString()));
        }
    }

}
