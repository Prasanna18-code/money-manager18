package in.pras.moneymanage.service;

import in.pras.moneymanage.entity.ProfileEntity;
import in.pras.moneymanage.repositery.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUseDetailsService implements UserDetailsService {
    private final ProfileRepository profileRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
      ProfileEntity exsistingProfile = profileRepository.findByEmail(email)
               .orElseThrow(()-> new UsernameNotFoundException("Profile not found with email"));
      return User.builder()
              .username(exsistingProfile.getEmail())
              .password(exsistingProfile.getPassword())
              .authorities(Collections.emptyList())
              .build();
    }
}
