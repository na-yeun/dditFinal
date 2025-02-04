package kr.or.ddit.expense.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class ExpenseHistoryVO {
    @NotBlank(message = "지출 아이템 ID는 필수입니다")
    @Size(max = 20)
    private String expenseItemId;

    @NotBlank(message = "그룹 ID는 필수입니다")
    @Size(max = 20)
    private String groupId;

    @NotNull(message = "지출 일자는 필수입니다")
    private LocalDate expenseDate;

    @Size(max = 200)
    private String expenseDetail;

    @PositiveOrZero(message = "수량은 0 이상이어야 합니다")
    private Long quantity;

    @PositiveOrZero(message = "단가는 0 이상이어야 합니다")
    private Long unitPrice;

    @PositiveOrZero(message = "금액은 0 이상이어야 합니다")
    private Long expenseAmount;

    @NotBlank
    private String expenseCategories;

    @NotBlank
    private String paymentMethod;
}