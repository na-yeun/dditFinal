package kr.or.ddit.expense.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
public class ExpenseGroupVO {
    @NotBlank
    @Size(max = 20)
    private String groupId;

    @NotBlank
    @Size(max = 20)
    private String docId;

    @NotBlank
    @Size(max = 10)
    private String empId;

    @NotNull
    @PositiveOrZero
    private Long totalAmount;

    @NotBlank
    @Size(max = 2)
    private String expenseType;

    @NotNull
    @Size(max = 1)
    private String groupStatus = "N";

    @NotBlank
    @Size(max = 100)
    private String expenseName;

    @NotBlank
    @Size(max = 20)
    private String departCode;
}
