package pro.sky.telegrambot.listener;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.TelegramBotRepository;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationScheduler {

    @Autowired
    private TelegramBotRepository telegramBotRepository;

    @Autowired
    private TelegramBot telegramBot;

    @Scheduled(fixedRate = 60000)
    public void sendNotifications() {
        List<NotificationTask> tasks = telegramBotRepository.findByIsSentFalseAndScheduledTimeBefore(LocalDateTime.now());

        tasks.forEach(task -> {
            sendNotification(task);
            task.setSent(true);
            telegramBotRepository.save(task);
        });
    }

    private void sendNotification(NotificationTask task) {
        Long chatId = Long.parseLong(task.getRecipient());
        String message = task.getMessage();
        SendMessage request = new SendMessage(chatId, message);
        telegramBot.execute(request);
    }
}
