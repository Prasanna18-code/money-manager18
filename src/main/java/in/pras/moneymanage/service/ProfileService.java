package in.pras.moneymanage.service;


import in.pras.moneymanage.dto.AuthDTO;
import in.pras.moneymanage.dto.ProfileDTO;
import in.pras.moneymanage.entity.ProfileEntity;
import in.pras.moneymanage.repositery.ProfileRepository;
import in.pras.moneymanage.util.JwtUtil;
import lombok.RequiredArgsConstructor;
//
import org.springframework.beans.factory.annotation.Value;   // ✅ CORRECT

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final  JwtUtil jwtUtil;

   @Value("${app.activation.url}")
    private String activationURL;

    public ProfileDTO registerProfile(ProfileDTO profileDTO){
        ProfileEntity newProfile =  toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());

       newProfile= profileRepository.save(newProfile);

       String activationLink=activationURL+"/api/v1.0/activate?token="+newProfile.getActivationToken();
       String subject ="Activate Your Money manager Acc";
       String body ="Click"+activationLink;
//       emailService.sendEmail(newProfile.getEmail(),subject,body);
//

        return toDTO(newProfile);
    }
    public ProfileEntity toEntity(ProfileDTO profileDTO){
        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }
    public static ProfileDTO toDTO(ProfileEntity profileEntity) {
        return ProfileDTO.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }
    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
        .map( profile->{
            profile.setIsActive(true);
            profileRepository.save(profile);
            return true;

        })
                .orElse(false);
    }
    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }
    public ProfileEntity getCurrentProfile(){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(()-> new UsernameNotFoundException("profile Not found"));

    }
    public ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser=null;
        if(email==null){
         currentUser =getCurrentProfile();
        }else{
          currentUser = profileRepository.findByEmail(email)
                  .orElseThrow(()-> new UsernameNotFoundException("profile Not found"));

        }
        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }
    public Map<String,Object> authenticateAndGenerateToken(AuthDTO authDTO){
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(),authDTO.getPassword()));
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token",token,
                    "user",getPublicProfile(authDTO.getEmail())
            );
        }catch (Exception e){
            e.printStackTrace();
           throw new RuntimeException("invalid email od pass");
        }
    }

}
