package org.example.sor_mrp.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

  // Spring Data JPA automatically implements this from the method name!
  Optional<User> findByEmail(String email);

  // Note: save(), findById(), deleteById(), and update logic
  // are now entirely handled out-of-the-box by JpaRepository!
}
