package resenkov.work.todobackend.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskSearchValues {

    private String title;
    private Integer completed;
    private Long priorityId;
    private Long categoryId;
    private String email;
    private Date dateFrom; //для задания периода по датам
    private Date dateTo;


    private Integer pageNumber;
    private Integer pageSize;

    private String sortColumn;
    private String sortDirection;

    //такие же названия должны быть на фронтэнде
}
