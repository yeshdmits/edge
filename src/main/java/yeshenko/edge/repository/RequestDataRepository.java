package yeshenko.edge.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import yeshenko.edge.model.RequestData;

@Repository
public interface RequestDataRepository extends JpaRepository<RequestData, Long> {
}
