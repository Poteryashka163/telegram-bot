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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class NotificationScheduler {

    @Autowired
    private TelegramBotRepository telegramBotRepository;

    @Autowired
    private TelegramBot telegramBot;

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Scheduled(fixedRate = 60000)
    public void processNotifications() {
        executorService.submit(this::sendNotifications);
        executorService.submit(this::cleanUpOldNotifications);
    }

    public void sendNotifications() {
        List<NotificationTask> tasks = telegramBotRepository.findByIsSentFalseAndScheduledTimeBefore(LocalDateTime.now());

        tasks.forEach(task -> {
            sendNotification(task);
            task.setSent(true);
            telegramBotRepository.save(task);
        });
    }

    public void cleanUpOldNotifications() {
        List<NotificationTask> oldTasks = telegramBotRepository.findByIsSentTrue();
        oldTasks.forEach(task -> telegramBotRepository.delete(task));
    }

    private void sendNotification(NotificationTask task) {
        Long chatId = Long.parseLong(task.getRecipient());
        String message = task.getMessage();
        telegramBot.execute(new SendMessage(chatId, message));
    }
}
