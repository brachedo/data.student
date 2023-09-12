package com.grandp.data.entity.authority;

import java.util.List;

import com.grandp.data.exception.notfound.entity.SimpleAuthorityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class SimpleAuthorityService {

    @Autowired
    private SimpleAuthorityRepository simpleAuthorityRepository;

    public SimpleAuthority createAuthority(SimpleAuthority simpleAuthority) {
        return simpleAuthorityRepository.save(simpleAuthority);
    }

    public SimpleAuthority getAuthorityById(Long id) {
        return simpleAuthorityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("SimpleAuthority not found with id " + id));
    }

    public List<SimpleAuthority> getAllAuthorities() {
        return simpleAuthorityRepository.findAll();
    }

    public SimpleAuthority getAuthorityByName(String roleName) throws SimpleAuthorityNotFoundException {
        return simpleAuthorityRepository.getSimpleAuthorityByAuthorityName(roleName).orElseThrow(() -> new EntityNotFoundException("SimpleAuthority with name: '" + roleName + "' not found"));
    }

    public SimpleAuthority updateAuthority(SimpleAuthority authority, String newName, String description) {
        setAuthorityName(authority, newName);

        if (description != null) {
            authority.setDescription(description);
        }

        SimpleAuthority updatedAuthority = simpleAuthorityRepository.save(authority);

        return updatedAuthority;
    }

     /** Sets the name of an Authority.
      * If the Authority is SystemAuthority:
      * - it cannot be changed;
      * - returns false;
      *
      * If the new name is null: return false
      * If the new name is valid: return true
     */
     private boolean setAuthorityName(SimpleAuthority authority, String newName) {
        if (isSystemAuthority(authority)) {
            return false;
        }

        if (newName != null) {
            authority.setName(newName);
        } else {
            return false;
        }

        return true;
    }

    private boolean isSystemAuthority(SimpleAuthority authority) {
        return authority.equals(SimpleAuthority.ADMINISTRATOR)
                || authority.equals(SimpleAuthority.GUEST)
                || authority.equals(SimpleAuthority.STUDENT)
                || authority.equals(SimpleAuthority.TEACHER);
    }
}