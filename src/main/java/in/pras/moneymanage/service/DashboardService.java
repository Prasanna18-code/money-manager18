package in.pras.moneymanage.service;

import in.pras.moneymanage.dto.ExpenseDTO;
import in.pras.moneymanage.dto.IncomeDTO;
import in.pras.moneymanage.dto.RecentTransactionDTO;
import in.pras.moneymanage.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();

        // ✅ Get latest 5 incomes and expenses
        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();

        // ✅ Merge both into one stream of RecentTransactionDTO
        List<RecentTransactionDTO> recentTransactions = Stream.concat(
                        latestIncomes.stream().map(income ->
                                RecentTransactionDTO.builder()
                                        .id(income.getId())
                                        .profileId(profile.getId())
                                        .icon(income.getIcon())
                                        .name(income.getName()) // fixed
                                        .amount(income.getAmount())
                                        .date(income.getDate())
                                        .createdAt(income.getCreatedAt())
                                        .updatedAt(income.getUpdatedAt())
                                        .type("income")
                                        .build()
                        ),
                        latestExpenses.stream().map(expense ->
                                RecentTransactionDTO.builder()
                                        .id(expense.getId())
                                        .profileId(profile.getId())
                                        .icon(expense.getIcon())
                                        .name(expense.getName())
                                        .amount(expense.getAmount())
                                        .date(expense.getDate())
                                        .createdAt(expense.getCreatedAt())
                                        .updatedAt(expense.getUpdatedAt())
                                        .type("expense")
                                        .build()))
                .sorted((a, b) -> {
                    if (a.getDate() == null && b.getDate() == null) return 0;
                    if (a.getDate() == null) return 1;  // put nulls last
                    if (b.getDate() == null) return -1; // put nulls last

                    int cmp = b.getDate().compareTo(a.getDate());
                    if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).collect(Collectors.toList());

        returnValue.put("totalBalance",incomeService.getTotalIncomeForCurrentUser().subtract(expenseService.getTotalExpenseForCurrentUser()));
        returnValue.put("totL INCOME ",incomeService.getTotalIncomeForCurrentUser());
        returnValue.put("Total expenses",expenseService.getTotalExpenseForCurrentUser());
        returnValue.put("recent5Expenses",latestExpenses);
        returnValue.put("recent5Incomes",latestIncomes);
        returnValue.put("recentTransactions",recentTransactions);
        return returnValue;
    }


}
