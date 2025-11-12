package in.pras.moneymanage.dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileDTO {

    private Long id;
    private String fullName;
    
    private String email;
    private String password;
    private String profileImageUrl;

    private String createdAt;
    private String updatedAt;
}
