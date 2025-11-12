package in.pras.moneymanage.service;

import in.pras.moneymanage.dto.ExpenseDTO;
import in.pras.moneymanage.dto.IncomeDTO;
import in.pras.moneymanage.entity.CategoryEntity;
import in.pras.moneymanage.entity.ExpenseEntity;
import in.pras.moneymanage.entity.IncomeEntity;
import in.pras.moneymanage.entity.ProfileEntity;
import in.pras.moneymanage.repositery.CategoryRepository;

import in.pras.moneymanage.repositery.IncomeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    public IncomeDTO addIncome(IncomeDTO dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category= categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Catgeory not found") );
        IncomeEntity newIncome = toEntity(dto,profile,category);
        newIncome=incomeRepository.save(newIncome);
        return toDTO(newIncome);

    }
    public List<IncomeDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now =LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list =  incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        return list.stream().map((this::toDTO)).toList();
    }
    public void deleteIncome(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(expenseId)
                .orElseThrow(()-> new RuntimeException("Expense Not found"));
        if(!entity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("unauthorized to delte this expense");

        }
        incomeRepository.delete(entity);
    }

    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map((this::toDTO)).toList();
    }


    public BigDecimal getTotalIncomeForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalExpenseByProfileId(profile.getId());
        return total !=null ?total :BigDecimal.ZERO;
    }
    //filter incomes
    public List<IncomeDTO> filterExpenses(LocalDate startDate, LocalDate endDate,String keyword, Sort sort){
        ProfileEntity profile = profileService.getCurrentProfile();

        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profile.getId(),startDate,endDate,keyword,sort);
        return list.stream().map(this::toDTO).toList();
    }
    private IncomeEntity toEntity(IncomeDTO dto , ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category)
                .build();

    }
    private IncomeDTO toDTO(IncomeEntity entity) {

        return IncomeDTO.builder()
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
