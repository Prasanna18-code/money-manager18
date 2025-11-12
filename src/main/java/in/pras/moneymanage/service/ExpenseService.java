package in.pras.moneymanage.service;


import in.pras.moneymanage.dto.ExpenseDTO;
import in.pras.moneymanage.entity.CategoryEntity;
import in.pras.moneymanage.entity.ExpenseEntity;
import in.pras.moneymanage.entity.ProfileEntity;
import in.pras.moneymanage.repositery.CategoryRepository;
import in.pras.moneymanage.repositery.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    public ExpenseDTO addExpense(ExpenseDTO dto){
        ProfileEntity profile = profileService.getCurrentProfile();
       CategoryEntity category= categoryRepository.findById(dto.getCategoryId())
               .orElseThrow(()-> new RuntimeException("Catgeory not found") );
       ExpenseEntity newExpense = toEntity(dto,profile,category);
       newExpense=expenseRepository.save(newExpense);
       return toDTO(newExpense);

    }

    //Retrive all expenses from current month/based from start and end date

    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
      List<ExpenseEntity> list =  expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
      return list.stream().map((this::toDTO)).toList();
    }
  public void deleteExpense(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(expenseId)
                .orElseThrow(()-> new RuntimeException("Expense Not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("unauthorix=zed to delte this expense");

        }
        expenseRepository.delete(entity);
  }
  public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
      return list.stream().map((this::toDTO)).toList();
  }

  public BigDecimal getTotalExpenseForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total !=null ?total :BigDecimal.ZERO;
  }
  //filter expenses
  public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate,String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
        return list.stream().map(this::toDTO).toList();
  }
  //Notifications
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId,LocalDate date){
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(profileId,date);
        return list.stream().map(this::toDTO).toList();
    }
    private ExpenseEntity toEntity(ExpenseDTO dto , ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();

    }
    private ExpenseDTO toDTO(ExpenseEntity entity) {

        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .amount(entity.getAmount())
                .date(entity.getDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .categoryId(entity.getCategory()!=null ? entity.getCategory().getId(): null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .build();
    }

}
