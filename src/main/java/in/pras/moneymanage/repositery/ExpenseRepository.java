package in.pras.moneymanage.repositery;

import in.pras.moneymanage.entity.ExpenseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity,Long> {


    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.profile.id =:profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

   List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
      Long profileId,
      LocalDate startDate,
      LocalDate endDate,
      String keyword,
      Sort sort
    );
   List<ExpenseEntity> findByProfileIdAndDateBetween(Long ProfileId,LocalDate startDate, LocalDate endDate);
   //select * from tbl _ expenses where profileId = and date =
    List<ExpenseEntity> findByProfileIdAndDate(Long profileId, LocalDate date);

}
