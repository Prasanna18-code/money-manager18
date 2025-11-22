package in.pras.moneymanage.service;

import in.pras.moneymanage.dto.ExpenseDTO;
import in.pras.moneymanage.entity.ProfileEntity;
import in.pras.moneymanage.repositery.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    // ⏰ Daily reminder to update expenses
   // @Scheduled(cron = " 0 * * * * *",zone = "Asia/Kolkata")
   @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Kolkata")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job Started: sendDailyIncomeExpenseReminder()");

        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {
            String htmlContent =
                    "<p>Hi " + profile.getFullName() + ",</p>" +
                            "<p>This is a friendly reminder to add your income and expenses for today in Money Manager.</p>" +
                            "<a href=\"" + frontendUrl +
                            "\" style=\"padding:10px 20px;background-color:#007BFF;color:white;text-decoration:none;" +
                            "border-radius:8px;\">Go to Money Manager</a>" +
                            "<br/><p>Best regards,<br>Money Manager Team</p>";

            // ✉️ Send email via Brevo
            emailService.sendEmail(
                    profile.getEmail(),
                    "Daily Reminder: Add Your Income & Expenses",
                    htmlContent
            );

        }
    }

    // ⏰ Daily summary email
   // @Scheduled(cron = " 0 * * * * *",zone = "Asia/Kolkata")
   @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Kolkata")
    public void sendDailyExpenseSummary() {
        log.info("Job Started: sendDailyExpenseSummary()");

        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todaysExpenses =
                    expenseService.getExpensesForUserOnDate(profile.getId(),
                            LocalDate.now(ZoneId.of("Asia/Kolkata")));

            if (!todaysExpenses.isEmpty()) {

                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse:collapse;width:100%;font-family:Arial,sans-serif;'>")
                        .append("<tr style='background-color:#f2f2f2;'>")
                        .append("<th>#</th><th>Category</th><th>Amount</th><th>Name</th><th>Date</th>")
                        .append("</tr>");

                int i = 1;
                for (ExpenseDTO expense : todaysExpenses) {
                    table.append("<tr>")
                            .append("<td>").append(i++).append("</td>")
                            .append("<td>").append(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A").append("</td>")
                            .append("<td>₹").append(expense.getAmount()).append("</td>")
                            .append("<td>").append(expense.getName()).append("</td>")
                            .append("<td>").append(expense.getDate()).append("</td>")
                            .append("</tr>");
                }

                table.append("</table>");

                String htmlContent =
                        "<p>Hi " + profile.getFullName() + ",</p>" +
                                "<p>Here is the summary of your expenses for today:</p>" +
                                table +
                                "<br/><p>Best regards,<br>Money Manager Team</p>";

                // ✉️ Send summary email via Brevo
                emailService.sendEmail(
                        profile.getEmail(),
                        "Your Daily Expense Summary",
                        htmlContent
                );

            }
        }

        log.info("Job completed: sendDailyExpenseSummary()");
    }
}
