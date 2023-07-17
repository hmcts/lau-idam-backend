package uk.gov.hmcts.reform.laubackend.idam.serenityfunctionaltests.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeletedAccountsResponse {

    private List<DeletedAccount> deletionLogs;
}
