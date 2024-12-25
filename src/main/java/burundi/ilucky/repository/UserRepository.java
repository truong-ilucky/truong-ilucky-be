package burundi.ilucky.repository;

import burundi.ilucky.model.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import burundi.ilucky.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);

    @Query("select u from User u order by u.totalStar desc limit 5")
    List<User> findTop5();

}
